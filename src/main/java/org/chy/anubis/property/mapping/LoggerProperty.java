package org.chy.anubis.property.mapping;

public class LoggerProperty {

    /**
     * 日志所用的实现, 需要自己去实现接口  org.chy.anubis.treasury.log.ILogger
     */
    @Property("using")
    public String using = "org.chy.anubis.log.LoggerImp";

}
