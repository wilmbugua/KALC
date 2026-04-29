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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.CustomJTextField;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.globals.IconFactory;
import ke.kalc.osk.KeyBoard;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.sales.CustomerDeliveryInfo;

/**
 * @author John Lewis
 */
public class DeliveryDialog extends JDialog {

    private final Font txtFont = KALCFonts.DEFAULTFONT.deriveFont(18f);
    private final Font btnFont = KALCFonts.DEFAULTBUTTONFONT;
    private final Font lblFont =  KALCFonts.DEFAULTFONT.deriveFont(16f);
    

    private final JPanel panel = new JPanel(new MigLayout("insets 10 0 0 10 ", "", ""));
    private final JPanel messagePanel = new JPanel(new MigLayout("insets 10 0 0 0 ", "[150:150:150] 5 [300:300:300] 10 [150:150:150] 5 [160:160:160]", ""));
    private final JPanel btnPanel = new JPanel();

    private ExtendedJButton btn = null;
    private static int CHOICE = -1;
    private final CustomJTextField inputName = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField addressLine1 = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField addressLine2 = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField addressLine3 = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField postCode = new CustomJTextField(new Dimension(150, 25), txtFont);
    private final CustomJTextField phone = new CustomJTextField(new Dimension(160, 25), txtFont);
    private final JTextArea comments = new JTextArea(1, 1);
    private final JXDatePicker datePicker = new JXDatePicker(new Date());

    private JPanel keyBoard;

    //entry point for inputbox
    protected DeliveryDialog() {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard(KeyBoard.Layout.QWERTY);
        deliveryPane();
        pack();
    }

    protected void deliveryPane() {
        setButtonPanel(new Dimension(100, 35));

        //Create the layout
        datePicker.setPreferredSize(new Dimension(300, 26));

        comments.setText("");
        comments.setFont(txtFont);
        comments.setLineWrap(true);
        comments.setWrapStyleWord(true);
        comments.setBorder(BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 1));

        comments.setPreferredSize(new Dimension(700, 70));
        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.deliverydate"), lblFont), "align right");
        messagePanel.add(datePicker, "wrap");
        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.name"), lblFont), "align right");
        messagePanel.add(inputName, "wrap");
        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.address"), lblFont), "align right");
        messagePanel.add(addressLine1, "wrap");
        messagePanel.add(new CustomJLabel("", lblFont), "align right");
        messagePanel.add(addressLine2, "wrap");
        messagePanel.add(new CustomJLabel("", lblFont), "align right");
        messagePanel.add(addressLine3, "wrap");
        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.postcode"), lblFont), "align right");
        messagePanel.add(postCode);
        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.phone"), lblFont));
        messagePanel.add(phone, "wrap");
        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.comments"), lblFont), "align right");
        messagePanel.add(comments, "span");

        panel.add(messagePanel, "wrap");
        panel.add(btnPanel, "span,  align center, wrap");

        setResizable(false);
        setModal(true);

        panel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Delivery Details");
        getContentPane().add(panel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void setButtonPanel(Dimension dimension) {
        btn = new ExtendedJButton(AppLocal.getIntString("button.ok"), JAlertPane.OK);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btn);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btn);

        JButton kbButton = new JButton();
        kbButton.setBorderPainted(false);
        kbButton.setOpaque(false);
        kbButton.setPreferredSize(new Dimension(75, 35));
        kbButton.setIcon(IconFactory.getResizedIcon("keyboard.png", new Dimension(75, 35)));
        kbButton.addActionListener((ActionEvent e) -> {
            kbButton.setEnabled(false);
            panel.add(keyBoard);
            int x = (this.getX() + (this.getWidth() / 2)) - 400;
            int y = this.getY() + this.getHeight() + 10;
            this.setLocation(x, this.getY());
            this.pack();
        });
        btnPanel.add(kbButton);
    }

    private void setTextAreaParameters(JTextArea textArea) {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setDisabledTextColor(CustomColour.getEnabledColour(textArea.getBackground()));
        textArea.setEnabled(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setRequestFocusEnabled(false);
    }

    protected int getChoice() {
        return CHOICE;
    }

    protected CustomerDeliveryInfo getDeliveryInfo() {
        return new CustomerDeliveryInfo(
                null,
                inputName.getText(),
                addressLine1.getText(),
                addressLine2.getText(),
                addressLine3.getText(),
                postCode.getText(),
                phone.getText(),
                datePicker.getDate(),
                false,
                comments.getText()
        );
    }

}
