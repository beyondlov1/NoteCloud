package com.beyond.utils;

import com.beyond.entity.fx.Document;

import java.lang.reflect.Field;

public class ReflectUtils {

    public static Object getValueByField(Class c, Document value, String propertyName) {

        Object result=null;
        Field field  = null;
        try {
            field = c.getDeclaredField(propertyName);
            field.setAccessible(true);
            result = field.get(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
