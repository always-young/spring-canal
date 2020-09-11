package com.spy.spring.canal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Kevin Liu
 * @date 2020/9/10 2:55 下午
 */
@Component
public class CanalApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger CANAL_LOGGER = LoggerFactory.getLogger(CanalDispatcher.class);


    private final CanalDispatcher canalDispatcher;

    public CanalApplicationListener(CanalDispatcher canalDispatcher) {
        this.canalDispatcher = canalDispatcher;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        canalDispatcher.start();
    }

    @PostConstruct
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                // 关闭自定义服务线程
                canalDispatcher.shutDown();
                //
                int count = 0;
                int latch = 10;
                while (!canalDispatcher.isServiceHasEnd()) {
                    try {
                        if (count > latch) {
                            break;
                        }
                        CANAL_LOGGER.info(
                                "********************* CanalDispatcherService还未结束运行，请耐心等待1秒......  *******************");
                        Thread.sleep(1000);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                CANAL_LOGGER.info(
                        "*********************  CanalDispatcherService shutdown complete!!!!!!  *******************");
            }
        }));
    }
}
