package org.chy.anubis.property;

import org.chy.anubis.exception.PropertyMappingException;
import org.chy.anubis.exception.ReflectExecException;
import org.chy.anubis.log.Logger;
import org.chy.anubis.property.mapping.AnubisProperty;
import org.chy.anubis.property.mapping.Property;
import org.chy.anubis.utils.ReflectUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class PropertyContext {

    Map<String, Object> data;

    public AnubisProperty anubis = new AnubisProperty();


    public PropertyContext() {
        init();
        mappingAnubisProperty();
    }


    private void init() {
        //读取配置文件
        Optional<File> propertyFile = readPropertyFile();
        if (!propertyFile.isPresent()) {
            Logger.info("没有读取到配置文件,使用默认的配置");
            return;
        }
        //解析配置文件
        analysisPropertyFile(propertyFile.get());


    }

    private void analysisPropertyFile(File file) {
        Yaml yaml = new Yaml();
        try {
            this.data = yaml.loadAs(new FileInputStream(file), Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("读取配置文件[" + file.getName() + "]失败,使用默认的配置");
        }
    }

    /**
     * 读取配置文件
     *
     * @return
     */
    private Optional<File> readPropertyFile() {
        URL configFileUrl = Thread.currentThread().getContextClassLoader().getResource("anubis.yml");
        if (configFileUrl == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new File(configFileUrl.toURI()));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }


    private void mappingAnubisProperty() {
        doMappingAnubisProperty((Map<String, Object>) data.get("anubis"), anubis, "anubis");
    }

    private void doMappingAnubisProperty(Map<String, Object> data, Object mappingStructure, String completeKey) {
        if (data == null) {
            return;
        }

        Class<?> mappingType = mappingStructure.getClass();
        Arrays.stream(mappingType.getDeclaredFields()).forEach(field -> {
            Property propertyAnnotation = field.getDeclaredAnnotation(Property.class);
            if (propertyAnnotation == null) {
                return;
            }
            String key = propertyAnnotation.value();
            Object nextData = data.get(key);
            if (nextData == null) {
                return;
            }
            String currentCompleteKey = completeKey + "." + key;
            boolean isVoluation = voluationIfLastLayer(nextData, field, mappingStructure, currentCompleteKey);
            //如果已经赋值成功的就不处理了
            if (isVoluation) {
                return;
            }
            //还没赋值说明这层不是基础类型,继续递归去赋值

            //如果接下来的值已经不是 map了,那么也没必要去递归赋值了
            if (!(nextData instanceof Map)) {
                return;
            }

            Object nextMappingStructure = ReflectUtils.getFiledValue(field, mappingStructure);
            //递归赋值
            doMappingAnubisProperty((Map<String, Object>) nextData, nextMappingStructure, currentCompleteKey);
        });
    }


    public PropertyEnums getPropertyEnumInstance(Class<Enum> enumClass, String enumName, String completeKey) {
        Enum[] enumConstants = enumClass.getEnumConstants();
        StringBuilder allEnumName = new StringBuilder();
        boolean first = true;
        for (Enum enumConstant : enumConstants) {
            if (!(enumConstant instanceof PropertyEnums)) {
                throw new PropertyMappingException("配置项[" + completeKey + "] 无法映射，因为枚举: [" + enumClass.getName() + "] 并没有实现接口 PropertyEnums");
            }
            PropertyEnums propertyEnums = (PropertyEnums) enumConstant;
            if (enumName.equals(propertyEnums.getName())) {
                return propertyEnums;
            }
            if (!first) {
                allEnumName.append(",");
            } else {
                first = false;
            }
            allEnumName.append(propertyEnums.getName());
        }

        throw new PropertyMappingException("配置项[" + completeKey + "] 不能够配置值[" + enumName + "] ，请从下面列表中选择 [" + allEnumName.toString() + "]");
    }


    /**
     * 如果已经是最后一层配置了,那么赋值
     *
     * @param data     要赋值的对象值
     * @param field    要赋值的字段
     * @param fieldObj 字段所属的对象
     * @return
     */
    private boolean voluationIfLastLayer(Object data, Field field, Object fieldObj, String completeKey) {
        Class<?> dataType = data.getClass();
        //字段类型和数据类型一样,那么就直接赋值了
        if (dataType == field.getType()) {
            ReflectUtils.setFiledValue(field, fieldObj, data);
            return true;
        }
        //字段类型是 string, 那么就把 data转成string类型放进去
        if (field.getType() == String.class) {
            ReflectUtils.setFiledValue(field, fieldObj, data.toString());
            return true;
        }

        //如果要赋值的字段是枚举，那么先转一下枚举
        if (field.getType().isEnum()) {
            PropertyEnums propertyEnumInstance = getPropertyEnumInstance((Class<Enum>) field.getType(), data.toString(), completeKey);
            ReflectUtils.setFiledValue(field, fieldObj, propertyEnumInstance);
            return true;
        }


        //TODO 其他基本类型后续用到了再支持

        return false;
    }


}
