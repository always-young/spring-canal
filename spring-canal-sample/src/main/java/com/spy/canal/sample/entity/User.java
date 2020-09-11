package com.spy.canal.sample.entity;

import java.io.Serializable;

/**
 * @author Kevin Liu
 * @date 2020/9/10 8:35 下午
 */
public class User implements Serializable {

    private Integer id;

    private String username;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String
    toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
