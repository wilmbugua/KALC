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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BeanFactoryData implements BeanFactoryApp {
    
    private BeanFactoryApp bf;
    
    /** Creates a new instance of BeanFactoryData */
    public BeanFactoryData() {
    }
    
    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {  
        
        try {
            
            String sfactoryname = this.getClass().getName();
            if (sfactoryname.endsWith("Create")) {
                sfactoryname = sfactoryname.substring(0, sfactoryname.length() - 6);
            }
            bf = (BeanFactoryApp) Class.forName(sfactoryname + app.getSession().DB.getName()).getDeclaredConstructor().newInstance();
            bf.init(app);                     
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | BeanFactoryException ex) {
            throw new BeanFactoryException(ex);
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(BeanFactoryData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Object getBean() {
        return bf.getBean();
    }         
}
