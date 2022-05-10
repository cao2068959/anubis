package org.chy.anubis.dynamic.template;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.chy.anubis.exception.NoSuchFieldException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.chy.anubis.Constant.ALGORITHM_TEMPLATE_NAME;

/**
 * 模版管理器
 */
public class CtTemplateContext {
    public static CtTemplateContext instance = new CtTemplateContext();

    private Map<String, CtTemplate> datas = new HashMap<>();

    public CtTemplateContext() {
        loadTemplate();
    }

    @SneakyThrows
    private void loadTemplate() {
        loadCtTemplate(ALGORITHM_TEMPLATE_NAME);
    }

    private void loadCtTemplate(String name) throws IOException {
        URL algorithmTemplateResource = Thread.currentThread().getContextClassLoader().getResource("template/" + name + ".ct");
        if (algorithmTemplateResource == null) {
            throw new NoSuchFieldException("无法加载名称为 [" + name + ".ct] 的文件,请检查路径 [template] 下是否有对应的文件");
        }
        String content = new String(IOUtils.toByteArray(algorithmTemplateResource));
        CtTemplate ctTemplate = new CtTemplate(name, content);
        datas.put(name, ctTemplate);
    }

    /**
     * 获取算法模版
     */
    public CtTemplate getAlgorithmTemplate() {
        return getTemplate(ALGORITHM_TEMPLATE_NAME);
    }

    public CtTemplate getTemplate(String name) {
        CtTemplate ctTemplate = datas.get(name);
        if (ctTemplate == null) {
            throw new NoSuchFieldException("无法找到名称为 [" + name + "] 的模版文件");
        }


        return new CtTemplate(ctTemplate.getName(), ctTemplate.getTemplateContent());
    }

}
