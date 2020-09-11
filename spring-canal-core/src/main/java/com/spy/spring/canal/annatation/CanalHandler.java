package com.spy.spring.canal.annatation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标明一个类是CanalHandler
 * @author Kevin Liu
 * @date 2020/9/10 1:37 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CanalHandler {

    String tableName() default "";

    Class<?> doClass() default Object.class;
}
