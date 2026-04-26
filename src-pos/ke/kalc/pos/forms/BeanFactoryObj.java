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

public class BeanFactoryObj implements BeanFactory {
    
    private Object bean = null;
    
    /** Creates a new instance of BeanFactoryObj
     * @param bean */
    public BeanFactoryObj(Object bean) {
        this.bean = bean;
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
