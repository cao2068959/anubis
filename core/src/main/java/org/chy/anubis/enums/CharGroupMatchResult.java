package org.chy.anubis.enums;

public enum CharGroupMatchResult {

    /**
     * 成功匹配
     */
    SUCCESS("success", 0),

    /**
     * 失败匹配
     */
    FAIL("fail", 1),

    /**
     * 未匹配完成
     */
    MATCHING("matching", 2);


    private String name;
    private int code;

    CharGroupMatchResult(String name, int code) {
        this.name = name;
        this.code = code;
    }

}
