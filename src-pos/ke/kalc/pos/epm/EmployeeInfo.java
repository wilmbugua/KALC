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


package ke.kalc.pos.epm;

import java.io.Serializable;
import ke.kalc.pos.util.StringUtils;

public class EmployeeInfo implements Serializable {
    
    private static final long serialVersionUID = 9083257536541L;

    /**
     *
     */
    protected String id;

    /**
     *
     */
    protected String name;
    
    /** Creates a new instance of EmployeeInfo
     * @param id */
    public EmployeeInfo(String id) {
        this.id = id;
        this.name = null;
    }
    
    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }   

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String printName() {
        return StringUtils.encodeXML(name);
    }
    
    @Override
    public String toString() {
        return getName();
    }    
}

