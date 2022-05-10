package org.chy.anubis.enums;

import org.chy.anubis.property.PropertyEnums;

public enum TreasuryType implements PropertyEnums {

    /**
     * 自定义的本地服务器
     */
    ANUBIS_SERVICE("anubisService", 0),
    /**
     * 使用 github
     */
    GITHUB("github", 1),
    /**
     * 使用 码云
     */
    GITEE("gitee", 2);

    private String name;
    private int code;

    TreasuryType(String name, int code) {
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
