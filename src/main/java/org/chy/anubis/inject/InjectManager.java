package org.chy.anubis.inject;

import org.chy.anubis.exception.InjectException;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.property.mapping.AnubisProperty;
import org.chy.anubis.treasury.annotations.Inject;
import org.chy.anubis.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 注入管理器, 动态编译, 动态编译之后的代码如果有 static 字段存在注解 @Inject 则会被注入对应的实例, 具体注入什么对象
 * 由 InjectManager 来管理
 */
public class InjectManager {

    static Map<String, String> mapping = new HashMap<>();

    static {
        AnubisProperty anubisProperty = PropertyContextHolder.getAnubisProperty();
        mapping.put("org.chy.anubis.treasury.log.ILogger", anubisProperty.logger.using);
    }

    public static void inject(Class<?> target) {
        Field[] declaredFields = target.getDeclaredFields();
        for (Field field : declaredFields) {
            Inject inject = field.getDeclaredAnnotation(Inject.class);
            if (inject == null) {
                continue;
            }

            if (!Modifier.isStatic(field.getModifiers())) {
                throw new InjectException("类:[" + target.getName() + "] 中字段[" + field.getName() + "] 注入失败, 当前只支持静态变量注入");
            }
            //获取要注入的实现是什么
            String injectType = mapping.get(field.getType().getName());
            if (injectType == null) {
                throw new InjectException("类:[" + target.getName() + "] 中字段[" + field.getName() + "] 注入失败, 没找到合适的注入类型");
            }
            // 实例化要注入的类
            Object instance = ReflectUtils.getInstance(injectType);
            ReflectUtils.setFiledValue(field, null, instance);
        }


    }


}
