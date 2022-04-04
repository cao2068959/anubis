package org.chy.anubis.utils;

public class TypeUtils {


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


}
