package com.beyond.mq;

import java.lang.reflect.Method;

public class UnHandleMethodData {
    private Method method;
    private Object[] args;
    private Object source;

    public UnHandleMethodData(Method method, Object[] args, Object source) {
        this.method = method;
        this.args = args;
        this.source = source;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}
