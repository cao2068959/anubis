package org.chy.anubis.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.chy.anubis.exception.ReflectExecException;

import java.util.HashMap;
import java.util.Map;

public class TypeUtils {

    static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    /**
     * 类型匹配, 如果是基本数据类型, 都会转成包装类去比较
     *
     * @param type   要对比的类型, 类的全路径,如果是 数组类型那么 aa.bb.AA[]
     * @param target 同target
     * @return true 匹配上
     */
    public static boolean typeMatch(String type, String target) {
        if (type.equals(target)) {
            return true;
        }
        type = toArrayWrapperType(type);
        target = toArrayWrapperType(target);
        return type.equals(target);
    }

    /**
     * 如果是基本类型,就转成包装类型, 数组类型也可以转
     */
    private static String toArrayWrapperType(String type) {
        boolean isArray = false;
        if (type.endsWith("[]")) {
            isArray = true;
            type = type.substring(0, type.length() - 3);
        }
        type = toWrapperType(type);
        if (isArray) {
            type = type + "[]";
        }
        return type;
    }


    private static String toWrapperType(String type) {
        if ("int".equals(type)) {
            return Integer.class.getTypeName();
        }

        if ("long".equals(type)) {
            return Long.class.getTypeName();
        }

        if ("double".equals(type)) {
            return Double.class.getTypeName();
        }

        if ("float".equals(type)) {
            return Float.class.getTypeName();
        }
        if ("short".equals(type)) {
            return Short.class.getTypeName();
        }

        if ("byte".equals(type)) {
            return Byte.class.getTypeName();
        }
        if ("boolean".equals(type)) {
            return Boolean.class.getTypeName();
        }

        if ("char".equals(type)) {
            return Character.class.getTypeName();
        }
        return type;
    }

    @SneakyThrows
    public static <T> T convert(Object data, Class<T> type) {

        String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        return objectMapper.readValue(jsonString, type);
    }

    @SneakyThrows
    public static <T> T convert(Object data, TypeReference<T> typeReference) {
        String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        return objectMapper.readValue(jsonString, typeReference);
    }

    /**
     * 生成对应的通用转换表达式
     *
     * @param oldIntanceName 要转换对象的名称
     * @param newType        要转换的类型
     * @param newIntanceName 转换后对象的名称
     * @return 对应的转换表达式
     */
    public static String genConvertExpression(String oldIntanceName, String newType, String newIntanceName) {
        String converTemplate = "${converType} ${newIntance} = org.chy.anubis.utils.TypeUtils.convert(${convertValue}, new com.fasterxml.jackson.core.type.TypeReference<${converType}>(){});";
        Map<String, String> params = new HashMap<>();
        params.put("newIntance", newIntanceName);
        params.put("converType", toWrapperType(newType));
        params.put("convertValue", oldIntanceName);
        return PlaceholderUtils.replacePlaceholder(converTemplate, params, "${", "}");
    }

    /**
     * 覆盖对象的值
     *
     * @param victim 需要被覆盖的对象
     * @param value  对应覆盖的值
     */
    public static void coverObject(Object victim, Object value) {
        Class<?> victimClass = victim.getClass();
        Class<?> valueClass = victim.getClass();

        if (victimClass.isArray()) {
            coverArrayObject(victim, victimClass, value, valueClass);
            return;
        }
        coverObject(victim, victimClass, value, valueClass);
    }

    /**
     * 生成对应的覆盖表达式
     *
     * @param victimName 要被覆盖的对象名称
     * @param coverName  用什么对象去覆盖
     */
    public static String genCoverObjectExpression(String victimName, String coverName) {
        String converTemplate = "org.chy.anubis.utils.TypeUtils.coverObject(${victimName}, ${coverName});";
        Map<String, String> params = new HashMap<>();
        params.put("victimName", victimName);
        params.put("coverName", coverName);
        return PlaceholderUtils.replacePlaceholder(converTemplate, params, "${", "}");
    }

    /**
     * 数组对象的覆盖
     *
     * @param victim 要被覆盖的数组
     * @param value  对应覆盖的值
     */
    private static void coverArrayObject(Object victim, Class<?> victimClass, Object value, Class<?> valueClass) {
        if (!victimClass.isArray() || !valueClass.isArray()) {
            throw new ReflectExecException("非法的参数: 入参仅仅只支持数组");
        }

        Object[] victimArray = (Object[]) victim;
        Object[] valueArray = (Object[]) value;
        // 数组长度不同, 先去把长度统一
        if (valueArray.length != victimArray.length) {
            System.arraycopy(victimArray, 0, valueArray, 0, valueArray.length);
        }

        for (int i = 0; i < valueArray.length; i++) {
            Object valueItem = valueArray[i];
            if (valueItem == null) {
                victimArray[i] = null;
                continue;
            }
            Object item = victimArray[i];
            // 类型完全相同 直接赋值就行了
            if (typeMatch(valueItem.getClass().getTypeName(), item.getClass().getName())) {
                victimArray[i] = valueItem;
            }
            coverObject(item, item.getClass(), valueItem, valueItem.getClass());
        }

    }

    private static void coverObject(Object victim, Class<?> victimClass, Object value, Class<?> valueClass) {
        //TODO 暂时没场景
    }


}
