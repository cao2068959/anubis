package org.chy.anubis.dynamic.template;

import lombok.Getter;
import org.chy.anubis.utils.PlaceholderUtils;

import java.util.HashMap;
import java.util.Map;

public class CtTemplate {

    @Getter
    private String name;
    @Getter
    private String templateContent;

    private Map<String, String> params = new HashMap<>();

    public CtTemplate(String name, String templateContent) {
        this.name = name;
        this.templateContent = templateContent;
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public String executeTemplate() {
        return PlaceholderUtils.replacePlaceholder(templateContent, params, "${", "}");
    }
}
