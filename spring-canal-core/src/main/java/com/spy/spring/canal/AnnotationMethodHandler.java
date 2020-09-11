package com.spy.spring.canal;

import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Kevin Liu
 * @date 2020/9/10 1:36 下午
 */
public class AnnotationMethodHandler {

    private Object object;

    private Method method;

    private Class<?> aClass;

    private Gson gson = new Gson();

    public AnnotationMethodHandler() {
    }

    public AnnotationMethodHandler(Object object, Method method,Class<?> aClass) {
        this.object = object;
        this.method = method;
        this.aClass = aClass;
    }

    public void execute(String json){
        final Object args = gson.fromJson(json, aClass);
        try {
            method.invoke(object,args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("invoke the method error cause" + e.getMessage());
        }
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
