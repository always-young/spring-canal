package com.spy.spring.canal.annatation;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * canal删除事件
 * @author Kevin Liu
 * @date 2020/9/10 1:43 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CanalOption(method = {CanalEntry.EventType.DELETE})
public @interface DeleteOption {


}
