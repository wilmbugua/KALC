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


package ke.kalc.pos.scale;

import java.awt.Component;
import ke.kalc.beans.JNumberDialog;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.forms.AppLocal;


public class ScaleDialog implements Scale {

    private Component parent;

    /**
     *
     * @param parent
     */
    public ScaleDialog(Component parent) {
        this.parent = parent;
    }

    /**
     *
     * @return
     * @throws ScaleException
     */
    @Override
    public Double readWeight() throws ScaleException {

        return JNumberDialog.showEditNumber(parent, AppLocal.getIntString("label.scale"), AppLocal.getIntString("label.scaleinput"), IconFactory.getIcon("ark2.png"));
    }
}
