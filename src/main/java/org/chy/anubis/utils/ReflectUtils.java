package org.chy.anubis.utils;

import org.chy.anubis.exception.ReflectExecException;
import org.chy.anubis.property.PropertyEnums;

import java.lang.reflect.Field;

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




}
