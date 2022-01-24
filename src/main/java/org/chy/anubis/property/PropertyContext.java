package org.chy.anubis.property;

import org.chy.anubis.log.Logger;
import org.chy.anubis.property.mapping.AnubisProperty;
import org.chy.anubis.property.mapping.Property;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class PropertyContext {

    Map<String, Object> data;

    public static AnubisProperty anubis = new AnubisProperty();


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


    }

    private void doMappingAnubisProperty(Map<String, Object> data, Object mappingStructure) {
        if (data == null){
            return;
        }

        Class<?> mappingType = mappingStructure.getClass();
        Arrays.stream(mappingType.getDeclaredFields()).forEach(field -> {
            Property propertyAnnotation = field.getDeclaredAnnotation(Property.class);
            if (propertyAnnotation == null){
                return;
            }
            String key = propertyAnnotation.value();
            Object nextData = data.get(key);
            if (nextData == null){
                return;
            }

            Class<?> nextDataType = nextData.getClass();
            if (nextDataType == field.getType()){

            }

        });
    }


}
