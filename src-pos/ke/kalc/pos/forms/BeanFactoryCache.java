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


public abstract class BeanFactoryCache implements BeanFactoryApp {
    
    private Object bean = null;

    /**
     *
     * @param app
     * @return
     * @throws BeanFactoryException
     */
    public abstract Object constructBean(AppView app) throws BeanFactoryException;
           
    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {
        bean = constructBean(app);
    }
    
    /**
     *
     * @return
     */
    @Override
    public Object getBean() {
        return bean;
    }
}
