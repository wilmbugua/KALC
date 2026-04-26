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

import java.util.ArrayList;
import javax.swing.Action;

public class MenuDefinition {

    private String m_sKey;

    private ArrayList m_aMenuElements;

    /**
     * Creates a new instance of MenuDefinition
     *
     * @param skey
     */
    public MenuDefinition(String skey) {
        m_sKey = skey;
        m_aMenuElements = new ArrayList();
    }

    /**
     *
     * @return
     */
    public String getKey() {
        return m_sKey;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return AppLocal.getIntString(m_sKey);
    }

    /**
     *
     * @param act
     */
    public void addMenuItem(Action act) {
        MenuItemDefinition menuitem = new MenuItemDefinition(act);
        m_aMenuElements.add(menuitem);
    }

    /**
     *
     * @param keytext
     */
    public void addMenuTitle(String keytext) {
        MenuTitleDefinition menutitle = new MenuTitleDefinition();
        menutitle.KeyText = keytext;
        m_aMenuElements.add(menutitle);        
    }

    /**
     *
     * @param i
     * @return
     */
    public MenuElement getMenuElement(int i) {
        return (MenuElement) m_aMenuElements.get(i);
    }

    /**
     *
     * @return
     */
    public int countMenuElements() {
        return m_aMenuElements.size();
    }

}
