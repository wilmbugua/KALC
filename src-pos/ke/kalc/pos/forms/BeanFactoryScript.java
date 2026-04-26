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

import java.io.IOException;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;
import ke.kalc.pos.util.StringUtils;

public class BeanFactoryScript implements BeanFactoryApp {
    
    private BeanFactory bean = null;
    private String script;
    
    /**
     *
     * @param script
     */
    public BeanFactoryScript(String script) {
        this.script = script;
    }
    
    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {
        
        // Resource
        try {
            ScriptEngine eng = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
            eng.put("app", app);
            
            bean = (BeanFactory) eng.eval(StringUtils.readResource(script));
            if (bean == null) {
                // old definition
                bean = (BeanFactory) eng.get("bean");
            }
            
            if (bean instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bean).init(app);
            }
        } catch (ScriptException | IOException e) {
            throw new BeanFactoryException(e);
        }     
    }

    /**
     *
     * @return
     */
    @Override
    public Object getBean() {
        return bean.getBean();
    }
}
