/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
*/


package ke.kalc.pos.inventory;

import ke.kalc.data.loader.IKeyed;

public class AttributeSetInfo implements IKeyed {

    private String id;
    private String name;
    private String siteGuid;

    public AttributeSetInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public AttributeSetInfo(String id, String name, String siteGuid) {
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
