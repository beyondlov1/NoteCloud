package com.beyond.utils;

import com.beyond.controller.MainController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

public class SortUtils {
    public static <T> void sort(List list, Class clazz, String propertyName, MainController.SortType sortType) {
        //排序
        Comparator<T> comparator = new Comparator<T>() {
            @Override
            public int compare(T t1, T t2) {
                Field[] declaredFields = clazz.getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    String fieldName = declaredFields[i].getName();
                    if (fieldName.equalsIgnoreCase(propertyName)) {
                        try {
                            Method method = clazz.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                            Object t1Field = method.invoke(t1);
                            Object t2Field = method.invoke(t2);
                            if (t1Field instanceof Comparable && t2Field instanceof Comparable) {
                                Comparable t1FieldComparable = (Comparable) t1Field;
                                Comparable t2FieldComparable = (Comparable) t2Field;
                                return t1FieldComparable.compareTo(t2FieldComparable) * (sortType == MainController.SortType.ASC ? 1 : -1);
                            }
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return 1;
            }
        };
        list.sort(comparator);
    }
}
