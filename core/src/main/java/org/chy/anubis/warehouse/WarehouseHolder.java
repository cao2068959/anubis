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

        throw new RuntimeException("数据源["+type.getName()+"] 暂时不支持, 请重新设置 参数 anubis.treasury.type来指定数据源");
    }


}
