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


package ke.kalc.data.user;

/**
 *
 *   
 */
public interface BrowseListener extends java.util.EventListener {
    
    /**
     *
     * @param iIndex
     * @param iCounter
     */
    public void updateIndex(int iIndex, int iCounter);
}
