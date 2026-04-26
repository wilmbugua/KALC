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
package ke.kalc.commons.dialogs;

import javax.swing.*;

/**
 * @author John Lewis
 */
public class InformationPane extends JDialog {

    public static void showInformationDialog(Boolean showLogo, JPanel content, Boolean undecorated, JPanel frame) {//, Boolean border) {
        InformationDialog jInfo = new InformationDialog(showLogo,
                content,
                undecorated);
        jInfo.setLocationRelativeTo(frame);
        jInfo.setModal(true);
        jInfo.setVisible(true);

    }

    public static void showInformationDialog(Boolean showLogo, JPanel content, Boolean undecorated, Boolean border, JPanel frame) {
        InformationDialog jInfo = new InformationDialog(showLogo,
                content,
                undecorated,
                border);
        jInfo.setLocationRelativeTo(frame);
        jInfo.setModal(true);
        jInfo.setVisible(true);

    }

}
