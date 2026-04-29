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


package ke.kalc.pos.epm;


public class EmployeeInfoExt extends EmployeeInfo {
    
    /**
     *
     */
    protected boolean visible;
    
    /** Creates a new instance of EmployeeInfoExt
     * @param id */
    public EmployeeInfoExt(String id) {
        super(id);
    } 

    /**
     *
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
