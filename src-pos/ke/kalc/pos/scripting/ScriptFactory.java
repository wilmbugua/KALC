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


package ke.kalc.pos.scripting;


public class ScriptFactory {
    
    /**
     *
     */
    public static final String VELOCITY = "velocity";

    /**
     *
     */
    public static final String BEANSHELL = "beanshell";

    /**
     *
     */
    public static final String RHINO = "rhino";
    
    /** Creates a new instance of ScriptFactory */
    private ScriptFactory() {
    }
    
    /**
     *
     * @param name
     * @return
     * @throws ScriptException
     */
    public static ScriptEngine getScriptEngine(String name) throws ScriptException {
        switch (name) {
            case VELOCITY:
                return new ScriptEngineVelocity();
            case BEANSHELL:
                return new ScriptEngineBeanshell();
            default:
                throw new ScriptException("Script engine not found: " + name);
        }
    }    
}
