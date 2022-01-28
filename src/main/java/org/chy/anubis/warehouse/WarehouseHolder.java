package org.chy.anubis.warehouse;


import org.chy.anubis.enums.TreasuryType;
import org.chy.anubis.property.PropertyContextHolder;


public class WarehouseHolder {

    public static Warehouse warehouse;

    static {
        initWarehouse();
    }

    private static void initWarehouse() {

        TreasuryType type = PropertyContextHolder.getAnubisProperty().treasury.type;

        if (type == TreasuryType.ANUBIS_SERVICE) {
            warehouse = new AnubisServiceWarehouse();
            return;
        }


    }


}
