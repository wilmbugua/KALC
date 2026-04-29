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


package ke.kalc.pos.forms;

import java.io.File;

public interface AppProperties {

    public File getConfigFile(); 

    public String getHost();    

    public String getProperty(String sKey); 
}
