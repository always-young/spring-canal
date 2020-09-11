package com.spy.spring.canal;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kevin Liu
 * @date 2020/9/10 2:34 下午
 */
@ConfigurationProperties(prefix = "canal")
public class CanalProperties {

    private String host;

    private Integer port;

    private String dbName;

    private String userName;

    private String password;

    private String destination;

    private CanalClusterProperties cluster;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public CanalClusterProperties getCluster() {
        return cluster;
    }

    public void setCluster(CanalClusterProperties cluster) {
        this.cluster = cluster;
    }

    public static class CanalClusterProperties {
        private Boolean enable;

        private String zkHosts;

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getZkHosts() {
            return zkHosts;
        }

        public void setZkHosts(String zkHosts) {
            this.zkHosts = zkHosts;
        }
    }
}
