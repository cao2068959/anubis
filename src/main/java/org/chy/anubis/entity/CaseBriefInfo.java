package org.chy.anubis.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CaseBriefInfo {

    /**
     * 用例的名称
     */
    String name;

    /**
     * 案例所属的url
     */
    String url;

}
