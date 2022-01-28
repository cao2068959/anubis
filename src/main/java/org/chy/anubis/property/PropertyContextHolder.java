package org.chy.anubis.property;

import org.chy.anubis.property.mapping.AnubisProperty;

public class PropertyContextHolder {

    public static PropertyContext context = new PropertyContext();

    public static AnubisProperty getAnubisProperty(){
        return context.anubis;
    }


}
