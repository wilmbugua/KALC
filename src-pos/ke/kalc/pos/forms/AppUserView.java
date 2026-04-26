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


package ke.kalc.pos.forms;

public interface AppUserView {

    public static final String ACTION_TASKNAME = "taskname";

    public AppUser getUser(); 

    public void showTask(String sTaskClass);

    public void executeTask(String sTaskClass);
}
