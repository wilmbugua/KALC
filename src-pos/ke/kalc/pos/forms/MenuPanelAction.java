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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import ke.kalc.globals.IconFactory;

public class MenuPanelAction extends AbstractAction {

    private final AppView m_App;
    private final String m_sMyView;

    /**
     * Creates a new instance of MenuPanelAction
     *
     * @param app
     * @param icon
     * @param keytext
     * @param sMyView
     */
    public MenuPanelAction(AppView app, String icon, String keytext, String sMyView) {
        //  putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));

        if (icon.lastIndexOf("/") > 0) {
            icon = icon.substring(icon.lastIndexOf("/") + 1);
        }
        putValue(Action.SMALL_ICON, IconFactory.getIcon(icon));
        putValue(Action.NAME, AppLocal.getIntString(keytext));
        putValue(AppUserView.ACTION_TASKNAME, sMyView);
        m_App = app;
        m_sMyView = sMyView;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        m_App.getAppUserView().showTask(m_sMyView);
    }
}
