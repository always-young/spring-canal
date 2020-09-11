package com.spy.canal.sample.service;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.spy.canal.sample.entity.User;
import com.spy.spring.canal.annatation.*;

/**
 * User表变更处理Service
 * @author Kevin Liu
 * @date 2020/9/10 8:35 下午
 */
@CanalHandler(tableName = "user",doClass = User.class)
public class UserService {


    @InsertOption
    public void insert(User user) {
        System.out.println("测试刷新ES");
        System.out.println(user);
    }

    @InsertOption
    public void update(User user) {
        System.out.println("测试刷新Redis");
        System.out.println(user);
    }

    @UpdateOption
    public void update2(User user) {
        System.out.println(user);
    }

    @DeleteOption
    public void delete(User user) {
        System.out.println(user);
    }

    @CanalOption(method = CanalEntry.EventType.DELETE)
    public void delete2(User user){
        System.out.println(user);
    }

}
