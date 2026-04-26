/*
**    KALC POS  - Open Source Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**    KALC POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    KALC POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with KALC POS.  If not, see <http://www.gnu.org/licenses/>
**
*/


package ke.kalc.pos.scripting;

import bsh.EvalError;
import bsh.Interpreter;


class ScriptEngineBeanshell implements ScriptEngine {

    private Interpreter i;
    
    /** Creates a new instance of ScriptEngineBeanshell */
    public ScriptEngineBeanshell() {
        i = new Interpreter();
    }
    
    @Override
    public void put(String key, Object value) {
        
        try {
            i.set(key, value);
        } catch (EvalError e) {
        }
    }
    
    @Override
    public Object get(String key) {
        
        try {
            return i.get(key);
        } catch (EvalError e) {
            return null;
        }
    }
    
    @Override
    public Object eval(String src) throws ScriptException {

        try {
            return i.eval(src);  
        } catch (EvalError e) {
            throw new ScriptException(e.getMessage(), e);
        }        
    }   
}
