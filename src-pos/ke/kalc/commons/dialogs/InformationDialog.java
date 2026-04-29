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
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 * @author John Lewis
 */
public class InformationDialog extends JDialog {

    private JPanel mainPanel;
    private JPanel logoPanel;
    private JPanel btnPanel;

    private Image img;
    private JTextArea contextArea;
    private JTextArea headerTextArea;
    private JLabel headerText;
    private JLabel iconLabel = new JLabel();

    private JSeparator separator = new JSeparator();
    private Font font;

    protected InformationDialog(Boolean showLogo, JPanel content, Boolean unDecorated, Boolean border) {
        super(new JFrame());
        infoDialog(showLogo, content, unDecorated, border);
        pack();
    }

    protected InformationDialog(Boolean showLogo, JPanel content, Boolean unDecorated) {
        super(new JFrame());
        infoDialog(showLogo, content, unDecorated, false);
        pack();
    }

//    protected InformationDialog(int type, String strTitle, String strHeaderText, String strContextText, int buttons) {
//        super(new JFrame(), strTitle);
//        separator.setOrientation(JSeparator.HORIZONTAL);
//        alertDialog(type, strHeaderText, strContextText, buttons);
//        pack();
//    }
//    private void alertDialog(int type, String headerText, String contextText, int buttons) {
//        alertDialog(type, headerText, contextText, buttons, false);
//    }
    private void infoDialog(Boolean showLogo, JPanel content, Boolean unDecorated, Boolean border) {
        mainPanel = new JPanel(new MigLayout("insets 10 4 5 2"));
        btnPanel = new JPanel();

        if (border) {
            mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        }

        if (showLogo) {
            logoPanel = new JPanel(new MigLayout("", "[500]"));
            ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/uk/KALC/fixedimages/kalclogo_small.png"));
            JLabel imageLabel = new JLabel(imageIcon);
            logoPanel.add(imageLabel, "align left");
            mainPanel.add(logoPanel, "wrap");
        }

        mainPanel.add(content, "width 500, wrap");

        JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        mainPanel.add(exit, "width 500, wrap, align center");

        //Set the dialog with no minimize or expand icons on title bar
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
            }
        });

        setAlwaysOnTop(true);
        setResizable(false);
        setModal(true);
        setUndecorated(unDecorated);

        String laf = UIManager.getLookAndFeel().getClass().toString();
        if ((!UIManager.getLookAndFeel().getClass().toString().contains("com.jtattoo.plaf"))
                && (!UIManager.getLookAndFeel().getClass().toString().contains("com.alee.laf"))) {
            if (unDecorated) {
                logoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            }
        }

        getContentPane().add(mainPanel);

    }

}
