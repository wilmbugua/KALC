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
package ke.kalc.commons.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.CustomJPasswordField;  
import ke.kalc.custom.CustomJTextField;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.osk.KeyBoard;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;

/**
 * @author John Lewis
 */
public final class LoginDialog extends JDialog {

    private final Font txtFont = KALCFonts.DEFAULTFONT.deriveFont(18f);
    private final Font btnFont = KALCFonts.DEFAULTBUTTONFONT;
    private final Font lblFont = KALCFonts.DEFAULTFONT.deriveFont(16f);

    private final JPanel panel = new JPanel(new MigLayout("insets 10 0 0 10 ", "", ""));
    private final JPanel messagePanel = new JPanel(new MigLayout("insets 10 0 0 0 ", "[150:150:150] 5 [300:300:300] 10 [150:150:150] 5 [160:160:160]", ""));
    private final JPanel btnPanel = new JPanel();

    private ExtendedJButton btnExit = null;
    private ExtendedJButton btnLogin = null;
    private static int CHOICE = -1;
    private final CustomJTextField userName = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJPasswordField password = new CustomJPasswordField(new Dimension(300, 25), txtFont);

    private final JPanel keyBoard;

    //entry point for inputbox
    protected LoginDialog() {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard(KeyBoard.Layout.QWERTY);
        loginPane();
        pack();
    }

    protected void loginPane() {
        setButtonPanel(new Dimension(100, 35));

        userName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });

        password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });
 
        //Create the layout
        messagePanel.add(new CustomJLabel(AppLocal.getIntString("label.username"), lblFont), "align right");
        messagePanel.add(userName, "wrap");
        messagePanel.add(new CustomJLabel(AppLocal.getIntString("label.password"), lblFont), "align right");
        messagePanel.add(password, "wrap");

        panel.add(messagePanel, "wrap");
        panel.add(btnPanel, "span,  align center, wrap");

        panel.add(keyBoard);
        int x = (this.getX() + (this.getWidth() / 2)) - 400;
        this.setLocation(x, this.getY());
        this.pack();

        setResizable(false);
        //  setUndecorated(true);
        setModal(true);

        panel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Login");
        getContentPane().add(panel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void setButtonPanel(Dimension dimension) {
        btnLogin = new ExtendedJButton(AppLocal.getIntString("button.login"), JAlertPane.OK);
        btnLogin.setPreferredSize(dimension);
        btnLogin.setFont(btnFont);
        btnLogin.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btnLogin);

        btnExit = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btnExit.setPreferredSize(dimension);
        btnExit.setFont(btnFont);
        btnExit.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btnExit);

    }

    protected int getChoice() {
        return CHOICE;
    }

    protected String getUserName() {
        return userName.getText();
    }

    protected String getPassword() {
        return new String(password.getPassword());
    }
}
