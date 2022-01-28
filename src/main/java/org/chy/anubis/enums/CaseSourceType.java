package org.chy.anubis.enums;

import org.chy.anubis.property.PropertyEnums;

public enum CaseSourceType implements PropertyEnums {

    /**
     * leetcode
     */
    LEETCODE("leetcode", 0);

    private String name;
    private int code;

    CaseSourceType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }
}
