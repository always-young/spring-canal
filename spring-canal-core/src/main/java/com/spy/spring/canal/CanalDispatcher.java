package com.spy.spring.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin Liu
 * @date 2020/9/10 2:01 下午
 */
@Component
@EnableConfigurationProperties(CanalProperties.class)
public class CanalDispatcher extends Thread {

    private CanalConnector connector;

    private Boolean shutDown = false;

    private static final Logger CANAL_LOGGER = LoggerFactory.getLogger(CanalDispatcher.class);

    private final Gson gson = new Gson();

    private final CanalProperties canalProperties;

    private final CanalHandlerContainer canalHandlerContainer;

    /**
     * 服务是否终止监视
     */
    private volatile boolean serviceHasEnd = true;

    /**
     * 优雅停止
     */
    public void shutDown() {
        shutDown = true;
    }

    public CanalDispatcher(CanalProperties canalProperties, CanalHandlerContainer canalHandlerContainer) {
        this.canalProperties = canalProperties;
        this.canalHandlerContainer = canalHandlerContainer;
    }



    @Override
    public void run() {
        int batchSize = 50;
        serviceHasEnd = false;
        while (!shutDown) {
            long batchId = -1;
            try {
                if (this.connector == null || !this.connector.checkValid()) {
                    this.rebuildConnector();
                }
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
                batchId = message.getId();
                int size = message.getEntries().size();
                CANAL_LOGGER.debug("从canal获取变更数据，size= {}", size);
                if (batchId == -1 || size == 0) {
                    if (CANAL_LOGGER.isDebugEnabled()) {
                        CANAL_LOGGER.info("Canal receive empty message : {}", message.getEntries());
                    }
                    connector.ack(batchId);
                    // 为空休息一会儿
                    Thread.sleep(200);
                } else {
                    try {
                        dispatcher(message.getEntries());
                    } finally {
                        connector.ack(batchId);
                    }
                }
            } catch (Throwable e) {
                CANAL_LOGGER.error(e.getMessage(), e);
                if (e instanceof CanalClientException) {
                    connector.disconnect();
                    rebuildConnector();
                }
            }
        }
        try {
            // 关闭连接
            connector.disconnect();
        } catch (Throwable throwable) {
            CANAL_LOGGER.error("CanalDispatcher:connector结束 error.", throwable);
        } finally {
            serviceHasEnd = true;
            CANAL_LOGGER.info(
                    "***********************************  CanalDispatcher->run()结束运行。  ***********************************");
        }
    }

    public boolean isServiceHasEnd() {
        return serviceHasEnd;
    }


    /**
     * 调度
     *
     * @param entries 实体
     */
    private void dispatcher(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            try {
                RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                String schemaName = entry.getHeader().getSchemaName();
                String tableName = entry.getHeader().getTableName();
                CanalEntry.EventType eventType = rowChange.getEventType();
                List<RowData> rowDataList = rowChange.getRowDatasList();
                CANAL_LOGGER.info("canalProperties.dbName= {},tableName = {}", canalProperties.getDbName(), tableName);
                if (canalProperties.getDbName().equals(schemaName)) {
                    for (CanalEntry.RowData rowData : rowDataList) {
                        if (eventType == CanalEntry.EventType.DELETE) {
                            String json = toJsonString(rowData.getBeforeColumnsList());
                            canalHandlerContainer.deal(tableName, eventType, json);
                            CANAL_LOGGER.info("canal callback eventType=DELETE,data={}", json);
                        } else if (eventType == CanalEntry.EventType.INSERT || eventType == EventType.UPDATE) {
                            String json = toJsonString(rowData.getAfterColumnsList());
                            canalHandlerContainer.deal(tableName, eventType, json);
                            CANAL_LOGGER.info("canal callback eventType={},data={}", eventType, json);
                        }
                    }
                }
            } catch (Throwable e) {
                CANAL_LOGGER.error("self service has an error , data:" + entry.toString(), e);
            }
        }
    }

    private String toJsonString(List<CanalEntry.Column> columns) {
        Map<String, Object> jsonMap = new HashMap<>();
        String columnName;
        for (CanalEntry.Column column : columns) {
            columnName = column.getName();
            if ("json".equalsIgnoreCase(column.getMysqlType())) {
                jsonMap.put(NameConvertUtils.lineToHump(columnName), JSON.parseObject(column.getValue()));
            } else {
                jsonMap.put(NameConvertUtils.lineToHump(columnName), column.getValue());
            }
        }

        return gson.toJson(jsonMap);
    }


    /**
     * connector链接
     */
    private void rebuildConnector() {
        for (; ; ) {
            try {
                if (canalProperties.getCluster().getEnable()) {
                    connector = CanalConnectors.newClusterConnector(canalProperties.getCluster().getZkHosts(), canalProperties.getDestination(),
                            canalProperties.getUserName(), canalProperties.getPassword());
                } else {
                    connector = CanalConnectors.newSingleConnector(
                            new InetSocketAddress(canalProperties.getHost(), canalProperties.getPort()), canalProperties.getDestination(),
                            canalProperties.getUserName(), canalProperties.getPassword());
                }
                connector.connect();
                connector.subscribe(canalProperties.getDbName() + "\\..*");
                connector.rollback();
                break;
            } catch (Exception e) {
                connector.disconnect();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

}
