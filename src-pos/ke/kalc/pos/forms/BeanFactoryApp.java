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


public interface BeanFactoryApp extends BeanFactory {

    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    public void init(AppView app) throws BeanFactoryException;

}
