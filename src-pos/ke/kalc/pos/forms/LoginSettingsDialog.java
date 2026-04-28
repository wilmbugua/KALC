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

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.CustomJTextField;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.util.PropertyUtil;

/**
 * Dialog for configuring login settings
 */
public class LoginSettingsDialog extends JDialog {

    private final Font txtFont = KALCFonts.DEFAULTFONT.deriveFont(18f);
    private final Font btnFont = KALCFonts.DEFAULTBUTTONFONT;
    private final Font lblFont = KALCFonts.DEFAULTFONT.deriveFont(16f);

    private final JPanel panel = new JPanel(new MigLayout("insets 10 0 0 10 ", "", ""));
    private final JPanel btnPanel = new JPanel();

    private ExtendedJButton btnSave = null;
    private ExtendedJButton btnCancel = null;
    private ExtendedJButton btnBrowse = null;
    private static int CHOICE = -1;
    private final CustomJTextField imagePathField = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField statusField = new CustomJTextField(new Dimension(300, 25), txtFont);

    //entry point
    protected LoginSettingsDialog() {
        super(new JFrame());
        initComponents();
        loadCurrentSettings();
        pack();
    }

    private void initComponents() {
        setButtonPanel(new Dimension(100, 35));

        // Load current settings
        statusField.setEditable(false);
        statusField.setBackground(Color.LIGHT_GRAY);

        //Create the layout
        panel.add(new CustomJLabel(AppLocal.getIntString("label.loginimagepath"), lblFont), "align right");
        panel.add(imagePathField, "wrap");
        panel.add(new CustomJLabel(AppLocal.getIntString("label.status"), lblFont), "align right");
        panel.add(statusField, "wrap");
        panel.add(btnBrowse, "wrap");
        panel.add(btnPanel, "span, align center, wrap");

        setResizable(false);
        setModal(true);
        panel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle(AppLocal.getIntString("title.loginsettings"));
        getContentPane().add(panel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void loadCurrentSettings() {
        String currentPath = PropertyUtil.getProperty("login.image.path");
        if (currentPath != null) {
            imagePathField.setText(currentPath);
            statusField.setText(AppLocal.getIntString("message.loaded") + ": " + currentPath);
            
            // Validate if file exists
            File imageFile = new File(currentPath);
            if (imageFile.exists()) {
                statusField.setText(AppLocal.getIntString("message.valid") + ": " + currentPath);
                statusField.setForeground(Color.GREEN);
            } else {
                statusField.setText(AppLocal.getIntString("message.notfound") + ": " + currentPath);
                statusField.setForeground(Color.RED);
            }
        } else {
            statusField.setText(AppLocal.getIntString("message.notset"));
            statusField.setForeground(Color.BLACK);
        }
    }

    private void setButtonPanel(Dimension dimension) {
        btnSave = new ExtendedJButton(AppLocal.getIntString("button.save"), JAlertPane.OK);
        btnSave.setPreferredSize(dimension);
        btnSave.setFont(btnFont);
        btnSave.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            
            // Save the image path
            String path = imagePathField.getText().trim();
            if (!path.isEmpty()) {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    statusField.setText(AppLocal.getIntString("message.savedvalid") + ": " + path);
                    statusField.setForeground(Color.GREEN);
                } else {
                    statusField.setText(AppLocal.getIntString("message.savednotfound") + ": " + path);
                    statusField.setForeground(Color.ORANGE);
                }
                // Save to properties using AppConfig
                AppConfig.put("login.image.path", path);
            } else {
                statusField.setText(AppLocal.getIntString("message.emptypath"));
                statusField.setForeground(Color.RED);
            }
            
            dispose();
        });
        btnPanel.add(btnSave);

        btnBrowse = new ExtendedJButton(AppLocal.getIntString("button.browse"), JAlertPane.OK);
        btnBrowse.setPreferredSize(dimension);
        btnBrowse.setFont(btnFont);
        btnBrowse.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(AppLocal.getIntString("title.selectimage"));
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                AppLocal.getIntString("message.imagefiles"), 
                "jpg", "jpeg", "png", "gif", "bmp"));
            
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePathField.setText(selectedFile.getAbsolutePath());
                statusField.setText(AppLocal.getIntString("message.selected") + ": " + selectedFile.getName());
                statusField.setForeground(Color.BLUE);
            }
        });
        btnPanel.add(btnBrowse);

        btnCancel = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btnCancel.setPreferredSize(dimension);
        btnCancel.setFont(btnFont);
        btnCancel.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btnCancel);
    }

    protected int getChoice() {
        return CHOICE;
    }
}