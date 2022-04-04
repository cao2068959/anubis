package org.chy.anubis.entity;

import lombok.Data;

@Data
public class parameterInfo {
    public String type;
    public String name;

    public parameterInfo(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
