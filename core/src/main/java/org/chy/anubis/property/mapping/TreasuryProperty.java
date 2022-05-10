package org.chy.anubis.property.mapping;

import org.chy.anubis.enums.TreasuryType;

public class TreasuryProperty {

    /**
     * 远程服务器的类型
     */
    @Property("type")
    public TreasuryType type = TreasuryType.GITHUB;

    /**
     * 自定义的本地服务器 的配置, 需要上面 type=anubisService 才会生效
     */
    @Property("anubisService")
    public AnubisServiceProperty anubisService = new AnubisServiceProperty();

    @Property("gitee")
    public GiteeProperty gitee = new GiteeProperty();

}