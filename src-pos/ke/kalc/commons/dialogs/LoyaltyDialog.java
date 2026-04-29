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


package ke.kalc.commons.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;

/**
 * @author John Lewis
 */
public class LoyaltyDialog extends JDialog {

    private JPanel panel;
    private JPanel btnPanel;

    private JTextArea contextArea;
    private JLabel iconLabel = new JLabel();

    private ExtendedJButton btn = null;
    private static int CHOICE = -1;
    private Font font;

    private String result = null;

    private JTextField inputName = new JTextField();
    private JTextField email = new JTextField();
    private JCheckBox market = new JCheckBox();

    //entry point for inputbox
    protected LoyaltyDialog(String message) {
        super(new JFrame());
        registerLoyaltyCustomer(message);
        pack();
    }

    protected void registerLoyaltyCustomer(String message) {
        setButtonPanel(new Dimension(100, 35));

        //set some default sizes
        Integer width = 400;
        Integer height = 100;

        //Create the layout
        panel = new JPanel(new MigLayout("insets 5 0 0 10 ", "[" + Integer.toString(width) + "]", "[][]"));
        //    fontSize = 16;

        double charPerRow = ((width - 60) / 6) * (12 / (double) 16);
        double rows = (message.length() / charPerRow) + 2.5 + StringUtils.countMatches(message, "\n");

        JPanel messagePanel = new JPanel(new MigLayout("insets 5 5 5 5 ", "[55:55:55][]", "[10:" + String.valueOf(((int) rows * 16) + 5) + ":]"));
        JPanel loyaltyPanel = new JPanel(new MigLayout("insets 10 0 0 0 ", "[150:150:150] 5 [300:300:300]", ""));

        try {
            iconLabel.setIcon(new ImageIcon(ImageIO.read(LoyaltyDialog.class.getResource("confirmation.png"))));
        } catch (IOException ex) {
            iconLabel.setIcon(null);
        }
        messagePanel.add(iconLabel, "top");

        contextArea = new JTextArea(1, 1);

        setTextAreaParameters(contextArea);
        contextArea.setPreferredSize(new Dimension(width, height));
        contextArea.setText(message);
        contextArea.setFont(KALCFonts.DEFAULTFONTBOLD);

        messagePanel.add(contextArea, "top");

        inputName.setText("");
        inputName.setFont(KALCFonts.DEFAULTFONTBOLD);
        inputName.setPreferredSize(new Dimension(300, 30));

        email.setText("");
        email.setFont(KALCFonts.DEFAULTFONTBOLD);
        email.setPreferredSize(new Dimension(300, 30));

        loyaltyPanel.add(new customJLabel(AppLocal.getIntString("message.name"), 16), "align right");
        loyaltyPanel.add(inputName, "wrap");
        loyaltyPanel.add(new customJLabel(AppLocal.getIntString("message.email"), 16), "align right");
        loyaltyPanel.add(email, "wrap");
        loyaltyPanel.add(new customJLabel(AppLocal.getIntString("message.receivemarketing"), 16), "align right");
        loyaltyPanel.add(market, "left wrap");

        panel.add(messagePanel, "wrap");
        panel.add(loyaltyPanel, "wrap");

        panel.add(btnPanel, "span,  align right");
        setResizable(false);
        setModal(true);

        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));

        setUndecorated(true);
        getContentPane().add(panel);
    }

    class ExtendedJButton extends JButton {

        private int btnChoice = -1;

        private ExtendedJButton(String text) {
            super(text);
        }

        private ExtendedJButton(String text, int choice) {
            super(text);
            this.btnChoice = choice;
        }

        private int getBtnChoice() {
            return btnChoice;
        }
    }

    private static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    private void setButtonPanel(Dimension dimension) {
        btnPanel = new JPanel();

        btn = new ExtendedJButton(AppLocal.getIntString("button.ok"), JAlertPane.OK);
        btn.setPreferredSize(dimension);
        btn.setFont(KALCFonts.DEFAULTFONTBOLD);
        btn.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            if (email.getText().isBlank()) {
                market.setSelected(false);
                CHOICE = extBtn.getBtnChoice();
                dispose();
                return;
            }
            if (isEmailValid(email.getText())) {
                CHOICE = extBtn.getBtnChoice();
                dispose();
                return;
            }
            JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.invalidemail"), 16,
                    new Dimension(125, 50), JAlertPane.OK_OPTION);

        });
        
        btnPanel.add(btn);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(KALCFonts.DEFAULTFONTBOLD);
        btn.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btn);
    }

    private void setTextAreaParameters(JTextArea textArea) {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        try {
            if (SystemProperty.LAF.equalsIgnoreCase("com.jtattoo.plaf.hifi.HiFiLookAndFeel")) {
                textArea.setDisabledTextColor(new java.awt.Color(255, 255, 255));
            } else {
                textArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
            }
        } catch (NoClassDefFoundError ex) {
            textArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        }
        textArea.setEnabled(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setRequestFocusEnabled(false);
    }

    protected int getChoice() {
        return CHOICE;
    }

    protected String getMemberName() {
        return inputName.getText();
    }

    protected String getEmail() {
        return email.getText();
    }

    protected Boolean getMarketing() {
        return market.isSelected();
    }

    private class customJLabel extends JLabel {

        public customJLabel(String text, int fontSize) {
            super();
            this.setText(text);
            this.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(fontSize));
        }
    }

}
