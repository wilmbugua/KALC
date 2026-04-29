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

public class AppLocal implements Versions {

    public static final String APP_NAME = "KALC POS";
    public static final String APP_ID = "kalc";
    public static final String APP_DEMO = "";

    public static String LIST_BY_RIGHTS = "";

    /**
     *
     * @param sKey
     * @return
     */
    public static String getIntString(String sKey) {
        return LocalResource.getString(sKey);
    }

    /**
     *
     * @param sKey
     * @param sValues
     * @return
     */
    public static String getIntString(String sKey, Object... sValues) {
        return  LocalResource.getString(sKey, sValues);
    }
}
