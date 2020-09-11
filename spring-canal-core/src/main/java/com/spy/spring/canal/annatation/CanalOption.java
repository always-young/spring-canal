package com.spy.spring.canal.annatation;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Kevin Liu
 * @date 2020/9/10 5:32 下午
 */
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalOption {

    CanalEntry.EventType[] method() default {};
}
