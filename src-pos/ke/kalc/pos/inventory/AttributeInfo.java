/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
*/


package ke.kalc.pos.inventory;

import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.loader.SerializerRead;

public class AttributeInfo implements IKeyed {

    private String id;
    private String name;
    private String siteGuid;

    public AttributeInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public AttributeInfo(String id, String name, String siteGuid) {
        this.id = id;
        this.name = name;
        this.siteGuid = siteGuid;
    }

    @Override
    public Object getKey() {
        return id;
    }

    public String getSiteGuid() {
        return siteGuid;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                String id = dr.getString(1);
                String name = dr.getString(2);
                String siteGuid = dr.getString(3);
                return new AttributeInfo(id, name, siteGuid);
            }
        };
    }

}
