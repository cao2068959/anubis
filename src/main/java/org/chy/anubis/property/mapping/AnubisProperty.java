package org.chy.anubis.property.mapping;

import org.chy.anubis.enums.TreasuryType;

public class AnubisProperty {

    @Property("treasury")
    public  TreasuryProperty treasury = new TreasuryProperty();

    @Property("localcode")
    public LocalCodeProperty localcode = new LocalCodeProperty();
}
