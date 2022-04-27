package org.chy.anubis.property.mapping;

public class AnubisProperty {

    @Property("treasury")
    public  TreasuryProperty treasury = new TreasuryProperty();

    @Property("localcode")
    public LocalCodeProperty localcode = new LocalCodeProperty();

    @Property("logger")
    public LoggerProperty logger = new LoggerProperty();
}
