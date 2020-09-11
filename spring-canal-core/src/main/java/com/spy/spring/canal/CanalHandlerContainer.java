package com.spy.spring.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.spy.spring.canal.annatation.CanalHandler;
import com.spy.spring.canal.annatation.CanalOption;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author Kevin Liu
 * @date 2020/9/10 2:46 下午
 */
@Component
public class CanalHandlerContainer implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private final Multimap<String, AnnotationMethodHandler> handlerMap = HashMultimap.create();

    public void deal(String tableName, CanalEntry.EventType eventType, String jsonStr) {
        final Collection<AnnotationMethodHandler> methodHandlers = handlerMap.get(buildKey(tableName, eventType));
        methodHandlers.forEach(t -> {
            t.execute(jsonStr);
        });
    }

    private String buildKey(String tableName, CanalEntry.EventType eventType) {
        return tableName + "/" + eventType.getNumber();
    }

    @Override
    public void afterPropertiesSet() {
        final Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(CanalHandler.class);
        beansWithAnnotation.forEach((k, v) -> {
            final CanalHandler annotation = v.getClass().getAnnotation(CanalHandler.class);
            String tableName = annotation.tableName();
            if (StringUtils.isEmpty(tableName)) {
                throw new RuntimeException("tableName can't be null  beanName:" + k);
            }
            Class<?> doClass = annotation.doClass();
            ReflectionUtils.doWithMethods(v.getClass(), method -> {
                final CanalOption canalOption = AnnotatedElementUtils.findMergedAnnotation(method, CanalOption.class);
                if (canalOption != null) {
                    final CanalEntry.EventType[] eventTypes = canalOption.method();
                    if (eventTypes.length == 0) {
                        throw new RuntimeException("canal eventTypes can't be empty  beanName:" + k);
                    }
                    final Parameter[] parameters = method.getParameters();
                    if (parameters == null || parameters.length != 1) {
                        throw new RuntimeException("canal method  param can't be empty or multi   method:" + k + "  " + method.getName());
                    }
                    if (parameters[0].getClass().equals(doClass)) {
                        throw new RuntimeException("canal method  param's classType can't equal canalHandler annotation's class");
                    }
                    Arrays.stream(eventTypes).forEach(t -> {
                        handlerMap.put(buildKey(tableName, t), new AnnotationMethodHandler(v, method, doClass));
                    });
                }
            });
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
