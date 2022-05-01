package org.chy.anubis.utils;

import org.chy.anubis.exception.ReflectExecException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ReflectUtils {

    public static void setFiledValue(Field field, Object fieldObj, Object data) {
        field.setAccessible(true);
        try {
            field.set(fieldObj, data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new ReflectExecException("设置属性值失败 field: [" + field.getName() + "] data-type : [" + data.getClass() + "]");
        }
    }

    public static Object getFiledValue(Field field, Object fieldObj) {
        field.setAccessible(true);
        try {
            return field.get(fieldObj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new ReflectExecException("获取 field: [" + field.getName() + "] 属性值失败 data-type : [" + fieldObj.getClass() + "]");
        }
    }

    /**
     * 生成实例,只能使用无参构造器
     */
    public static <T> T getInstance(Class<T> type) {
        Constructor<?>[] constructors = type.getConstructors();
        Constructor<?> useConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                useConstructor = constructor;
                break;
            }
        }
        if (useConstructor == null) {
            throw new ReflectExecException("类[" + type.getTypeName() + "] 没有无参构造器,无法实例化");
        }
        try {
            return (T) useConstructor.newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new ReflectExecException("类[" + type.getTypeName() + "] 实例化失败 原因 [" + e.getMessage() + "]", e);
        }
    }

    public static Object getInstance(String type,ClassLoader classLoader) {
        Class<?> aClass;
        try {
            aClass = classLoader.loadClass(type);
        } catch (ClassNotFoundException e) {
            throw new ReflectExecException("类[" + type + "]不存在 ", e);
        }
        return getInstance(aClass);
    }


}
