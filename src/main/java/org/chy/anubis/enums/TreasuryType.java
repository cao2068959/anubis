package org.chy.anubis.enums;

public enum TreasuryType {

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

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
