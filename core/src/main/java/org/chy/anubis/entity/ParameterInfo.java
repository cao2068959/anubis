package org.chy.anubis.entity;

import lombok.Data;

import java.util.Set;

@Data
public class ParameterInfo {
    public String type;
    public String name;
    public Set<String> annotations;

    public ParameterInfo(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getSimpleType() {
        int index = type.lastIndexOf(".");
        if (index < 0) {
            return type;
        }
        return type.substring(index + 1);

    }

}
