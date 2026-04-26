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
package uk.kalc.commons.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.jdatepicker.JDatePicker;
import uk.kalc.commons.utils.JNumberField;
import uk.kalc.custom.CustomColour;
import uk.kalc.globals.IconFactory;
import uk.kalc.osk.KeyBoard;
import uk.kalc.pos.sales.CustomerDeliveryInfo;

/**
 * @author John Lewis
 */
public class AlertDialog extends JDialog {

    private JPanel mainPanel;
    private JPanel contextPanel;
    private JPanel headerPanel;
    private JPanel btnPanel;

    private ImageIcon img;
    private JTextArea contextTextArea;
    private JTextArea headerTextArea;
    private JLabel iconLabel = new JLabel();

    private ExtendedJButton btn = null;
    private static int CHOICE = -1;
    private JSeparator separator = new JSeparator();
    private Font font;
    private String result = null;
    private JNumberField inputValue = new JNumberField();
    private JTextField inputName = new JTextField();
    private JTextField addressLine1 = new JTextField();
    private JTextField addressLine2 = new JTextField();
    private JTextField addressLine3 = new JTextField();
    private JTextField postCode = new JTextField();
    private JTextField phone = new JTextField();
    private JTextArea comments = new JTextArea(1, 1);
    private JDatePicker datePicker = new JDatePicker(new Date());
    private JNumberField inputAmount = new JNumberField();
    private JPanel keyBoard;

    protected AlertDialog(int type, String strTitle, String strHeaderText, String strContextText, int buttons, Boolean unDecorated) {
        super(new JFrame(), strTitle);
        separator.setOrientation(JSeparator.HORIZONTAL);
        alertDialog(type, strHeaderText, strContextText, buttons, unDecorated);
        pack();
    }

    protected AlertDialog(int type, String strTitle, String strHeaderText, String strContextText, int buttons) {
        super(new JFrame(), strTitle);
        separator.setOrientation(JSeparator.HORIZONTAL);
        alertDialog(type, strHeaderText, strContextText, buttons);
        pack();
    }

    protected AlertDialog(String strTitle, String strHeaderText, String messageText, String strExceptionText) {
        super(new JFrame(), strTitle);
        separator.setOrientation(JSeparator.HORIZONTAL);
        exceptionDialog(strHeaderText, messageText, strExceptionText);
        pack();
    }

    //entry point for messagebox
    protected AlertDialog(Dimension panelSize, int type, String message, int fontSize, Dimension dimension, int buttons) {
        super(new JFrame());
        optionPane(panelSize, type, message, fontSize, dimension, buttons);
        pack();
    }

    //entry point for inputbox
    protected AlertDialog(Dimension panelSize, String message, int fontSize, Dimension dimension, int buttons, String value, Boolean numberOnly) {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard(KeyBoard.Layout.QWERTY);
        inputPane(panelSize, message, fontSize, dimension, buttons, value, numberOnly);
        pack();
    }

    protected void inputPane(Dimension panelSize, String message, int fontSize, Dimension dimension, int buttons, String value, Boolean numberOnly) {
        setButtonPanel(buttons, dimension);

        //set some default sizes
        Integer width = 400;
        Integer height = 100;

        if (panelSize != null) {
            width = (int) panelSize.getWidth();
            height = (int) panelSize.getHeight();

        }
        //Create the layout
        mainPanel = new JPanel(new MigLayout("insets 0 0 5 0 ", "[" + Integer.toString(width) + "]", "[][]"));

        fontSize = (fontSize != 0) ? fontSize : 12;

        double charPerRow = ((width - 60) / 6) * (12 / (double) fontSize);
        double rows = (message.length() / charPerRow) + 2.5 + StringUtils.countMatches(message, "\n");

        JPanel messagePanel = new JPanel(new MigLayout("insets 10 5 5 5 ", "[150:150:150] 5 [300:300:300]", "[:" + String.valueOf(((int) rows * fontSize) + 5) + ":]"));

        JLabel inputLabel = new JLabel(message);
        inputLabel.setFont(new Font("Arial", Font.BOLD, fontSize));

        inputValue = new JNumberField(numberOnly);
        if (numberOnly) {
            inputValue.allowNegativeNumbers(false);
            inputValue.setDecimalPlaces(2);
        }

        inputValue.setText(value);
        inputValue.setFont(new Font("Arial", Font.BOLD, fontSize));
        inputValue.setPreferredSize(new Dimension((numberOnly) ? 150 : 300, 35));
        messagePanel.add(inputLabel, "align right");
        messagePanel.add(inputValue, "align center");
        //Add the elements to panel
        mainPanel.add(messagePanel, "wrap");
        mainPanel.add(btnPanel, "span,  align right, wrap");

        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setUndecorated(true);
        getContentPane().add(mainPanel);

        JButton kbButton = new JButton();
        kbButton.setBorderPainted(false);
        kbButton.setOpaque(false);
        kbButton.setPreferredSize(new Dimension(75, 35));
        kbButton.setIcon(IconFactory.getResizedIcon("keyboard.png", new Dimension(75, 35)));
        kbButton.addActionListener((ActionEvent e) -> {
            kbButton.setEnabled(false);
            mainPanel.add(keyBoard);
            int x = (this.getX() + (this.getWidth() / 2)) - 400;
            int y = this.getY() + this.getHeight() + 10;
            this.setLocation(x, this.getY());
            this.pack();
        });
        btnPanel.add(kbButton);

    }

    protected void optionPane(Dimension panelSize, int type, String message, int fontSize, Dimension dimension, int buttons) {
        setButtonPanel(buttons, dimension);
        setIconImage(type);

        //set some default sizes
        Integer width = 400;
        Integer height = 100;

        if (panelSize != null) {
            width = (int) panelSize.getWidth();
            height = (int) panelSize.getHeight();

        }
        //Create the layout
        mainPanel = new JPanel(new MigLayout("insets 0 0 5 0 ", "[" + Integer.toString(width) + "]", "[40::][]"));

        fontSize = (fontSize != 0) ? fontSize : 12;

        double charPerRow = ((width - 60) / 6) * (12 / (double) fontSize);
        double rows = (message.length() / charPerRow) + 2.5 + StringUtils.countMatches(message, "\n");

        JPanel messagePanel = new JPanel(new MigLayout("insets 10 5 5 5 ", "[55:55:55][]", "[45:" + String.valueOf(((int) rows * fontSize) + 5) + ":]"));

        //add the icon
        iconLabel.setIcon(img);
        messagePanel.add(iconLabel, "top");
        //add the context area
        contextTextArea = new JTextArea(1, 1);

        setTextAreaParameters(contextTextArea);
        contextTextArea.setPreferredSize(new Dimension(width, height));
        contextTextArea.setText(message);
        contextTextArea.setFont(new Font("Arial", Font.BOLD, fontSize));

        if (message.length() - charPerRow < 1) {
            messagePanel.add(contextTextArea, "gapy 15");
        } else {
            messagePanel.add(contextTextArea, "gapy 5");
        }

        //Add the elements to panel
        mainPanel.add(messagePanel, "wrap");
        mainPanel.add(btnPanel, "span,  align center");
        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));

        setUndecorated(true);
        getContentPane().add(mainPanel);

    }

    class ExtendedJButton extends JButton {

        private int btnChoice = -1;

        private ExtendedJButton(String text) {
            super(text);
        }

        private ExtendedJButton(String text, int choice) {
            super(text);
            this.btnChoice = choice;
            this.setFocusable(false);
        }

        private int getBtnChoice() {
            return btnChoice;
        }
    }

    private void alertDialog(int type, String headerText, String contextText, int buttons) {
        alertDialog(type, headerText, contextText, buttons, false);
    }

    private void setButtonPanel(int buttons, Dimension dimension) {
        btnPanel = new JPanel();

        for (int i = 0; i < 8; i++) {
            switch (buttons & (int) Math.pow(2, i)) {
                case 1:
                    btn = new ExtendedJButton("Yes", JAlertPane.YES);
                    break;
                case 2:
                    btn = new ExtendedJButton("No", JAlertPane.NO);
                    break;
                case 4:
                    btn = new ExtendedJButton("OK", JAlertPane.OK);
                    break;
                case 8:
                    btn = new ExtendedJButton("Save", JAlertPane.SAVE);
                    break;
                case 16:
                    btn = new ExtendedJButton("Retry", JAlertPane.RETRY);
                    break;
                case 32:
                    btn = new ExtendedJButton("Exit", JAlertPane.EXIT);
                    break;
                case 64:
                    btn = new ExtendedJButton("Configuration", JAlertPane.CONFIGURE);
                    break;
                case 128:
                    btn = new ExtendedJButton("Cancel", JAlertPane.CANCEL);
                    break;
            }

            if (btn != null) {
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                if (dimension != null & btn != null) {
                    btn.setPreferredSize(dimension);
                }
                btn.addActionListener((ActionEvent e) -> {
                    ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
                    CHOICE = extBtn.getBtnChoice();
                    result = inputValue.getText();
                    dispose();
                });
                btnPanel.add(btn);
            }
        }

    }

    private void setIconImage(int type) {

        switch (type) {
            case -1:
                img = null;
                break;
            case 0:
                img = new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/warning.png"));
                break;
            case 1:
                img = new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/information.png"));
                break;
            case 3:
                img = new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/confirmation.png"));
                break;
            case 5:
                img = new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/success.png"));
                break;
            default:
                img = new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/error.png"));
                break;
        }
    }

    private void alertDialog(int type, String headerText, String contextText, int buttons, Boolean unDecorated) {
        setButtonPanel(buttons, null);
        setIconImage(type);

        //Set the dialog with no minimize or expand icons on title bar
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
            }
        });

        if (headerText != null) {
            mainPanel = new JPanel(new MigLayout("insets 10 4 10 8", "[]", "[][][50:50:50]"));
            headerPanel = new JPanel(new MigLayout("insets 5 8 0 8", "[310:310:310][45]", "[]"));
            contextPanel = new JPanel(new MigLayout("insets 5 8 0 8", "[350:350:350]", "[]"));
            headerTextArea = new JTextArea(1, 1);
            setTextAreaParameters(headerTextArea);
            headerTextArea.setText(headerText);
            headerPanel.add(headerTextArea, "left,  pushx, growx");
            iconLabel.setIcon(img);
            headerPanel.add(iconLabel, "wrap, width :45:, align right");
            headerPanel.add(separator, "span, center, growx, wrap");
            mainPanel.add(headerPanel, "wrap");
            contextTextArea = new JTextArea(1, 1);
            setTextAreaParameters(contextTextArea);
            contextTextArea.setText(contextText);
            contextPanel.add(contextTextArea, "left,  pushx, growx");
            mainPanel.add(contextPanel, "wrap");
        } else {
            mainPanel = new JPanel(new MigLayout("insets 10 4 10 8", "[]", "[][50:50:50]"));
            headerPanel = new JPanel(new MigLayout("insets 5 8 0 8", "[310:310:310][45]", "[]"));
            contextTextArea = new JTextArea(1, 1);
            setTextAreaParameters(contextTextArea);
            contextTextArea.setText(contextText);
            headerPanel.add(contextTextArea, "left,  pushx, growx");
            iconLabel.setIcon(img);
            headerPanel.add(iconLabel, "wrap, width :45:, align right");
            iconLabel.setIcon(img);
            mainPanel.add(headerPanel, "wrap");
        }
        mainPanel.add(btnPanel, "span, gapy 5, height 32:32:32, right");

        setAlwaysOnTop(true);
        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        setUndecorated(unDecorated);
        getContentPane().add(mainPanel);

    }

    private void exceptionDialog(String headerText, String messageText, String exceptionStack) {
        img = new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/error.png"));

        btnPanel = new JPanel();
        btn = new ExtendedJButton("OK", JAlertPane.OK);
        btn.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btn);

        //Set the dialog with no minimize or expand icons on title bar
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
            }
        });

        mainPanel = new JPanel(new MigLayout("insets 2 4 5 2", "[450!][]", "[]0[][][][]"));

        headerTextArea = new JTextArea(1, 1);
        setTextAreaParameters(headerTextArea);
        font = headerTextArea.getFont();
        float size = font.getSize() + 4.0f;
        headerTextArea.setFont(font.deriveFont(size));
        mainPanel.add(headerTextArea, "left,  pushx, growx, split 2");
        iconLabel.setIcon(img);
        mainPanel.add(iconLabel, "wrap, width :45:, align right");
        mainPanel.add(separator, "span, center, gapy 5, growx, wrap");

        JTextArea messageArea = new JTextArea(2, 40);
        setTextAreaParameters(messageArea);
        mainPanel.add(messageArea, "left,  pushx, growx, wrap");
        font = messageArea.getFont();
        size = font.getSize() - 2.0f;
        messageArea.setFont(font.deriveFont(size));
        messageArea.setText(messageText);

        JLabel stackText = new JLabel();
        mainPanel.add(stackText, "left,  pushx, growx, wrap");
        font = stackText.getFont();
        size = font.getSize() - 1.0f;
        stackText.setFont(new Font(font.getName(), Font.PLAIN, (int) size));
        stackText.setText("The exception stacktrace was:");

        contextTextArea = new JTextArea(10, 42);
        setTextAreaParameters(contextTextArea);

        JScrollPane sPane = new JScrollPane(contextTextArea);
        mainPanel.add(sPane, "span");
        headerTextArea.setText(headerText);
        contextTextArea.setText(exceptionStack);
        mainPanel.add(btnPanel, "span, gapy 5, height 32:32:32, right");

        setAlwaysOnTop(true);
        setResizable(false);
        setModal(true);
        getContentPane().add(mainPanel);

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

    protected String getInput() {
        return inputValue.getText();
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
                (Date) datePicker.getModel().getValue(),
                false,
                comments.getText()
        );
    }

}
