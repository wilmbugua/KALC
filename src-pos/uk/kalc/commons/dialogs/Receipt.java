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
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import net.miginfocom.swing.MigLayout;
import uk.kalc.format.Formats;
import uk.kalc.globals.SystemProperty;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.KALCFonts;
import uk.kalc.pos.forms.JRootFrame;

/**
 * @author John Lewis
 */
public class Receipt extends JDialog {

    private JPanel panel;
    private JPanel mainPanel;
    private JPanel buttonPanel;

    private final JButton btnYes = new JButton();
    private final JButton btnNo = new JButton();
    private final JButton btnOK = new JButton();

    private final JLabel lblReceiptMsg = new JLabel();
    private final JLabel lblChange = new JLabel();
    private final JLabel lblTotal = new JLabel();
    private final JLabel lblTendered = new JLabel();
    private final JLabel lblChangeDue = new JLabel();
    private final JLabel lblTotalAmount = new JLabel();
    private final JLabel lblTenderedAmount = new JLabel();
    private final JLabel lblColon = new JLabel(":");
    private final JLabel lblColon1 = new JLabel(":");
    private final JLabel lblColon2 = new JLabel(":");

    private static Boolean choice = false;
    private static Boolean receiptRequired = true;
    private static String totalAmount;
    private static String tenderedAmount;
    private static String changeDue;
    private final JSeparator separator = new JSeparator();

    protected Receipt() {
        super(new JFrame(), "");
        separator.setOrientation(JSeparator.HORIZONTAL);
        buildPanel();       
        pack();
    }

    public static Boolean required(String total, String tendered, String change, Boolean required) {
        totalAmount = total;
        tenderedAmount = tendered;
        changeDue = (change == null || change.isBlank()) ? Formats.CURRENCY.formatValue(0.00) : change;
        receiptRequired = required;

        Receipt receipt = new Receipt();

        if (SystemProperty.RECEIPTAUTOCLOSE != 0) {
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(SystemProperty.RECEIPTAUTOCLOSE * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    choice = SystemProperty.RECEIPTPRINTOFF;
                    receipt.dispose();
                }
            });
            t1.start();
        }

        receipt.setLocationRelativeTo(JRootFrame.PARENTFRAME);
        receipt.setVisible(true);
        return choice;
    }

    private void buildPanel() {
        btnYes.setText(AppLocal.getIntString("button.yes"));
        btnYes.setBackground(new Color(122, 230, 134));
        btnYes.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createRaisedBevelBorder()));
        btnYes.setPreferredSize(new Dimension(175, 60));
        btnYes.setFont(KALCFonts.DEFAULTBUTTONFONT);

        btnNo.setText(AppLocal.getIntString("button.no"));
        btnNo.setBackground(new Color(252, 93, 93));
        btnNo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createRaisedBevelBorder()));
        btnNo.setPreferredSize(new Dimension(175, 60));
        btnNo.setFont(KALCFonts.DEFAULTBUTTONFONT);

        btnOK.setText(AppLocal.getIntString("button.ok"));
        btnOK.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createRaisedBevelBorder()));
        btnOK.setPreferredSize(new Dimension(350, 80));
        btnOK.setFont(KALCFonts.DEFAULTBUTTONFONT);

        lblReceiptMsg.setText(AppLocal.getIntString("label.receipt"));
        lblReceiptMsg.setPreferredSize(new Dimension(175, 60));
        lblReceiptMsg.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));

        lblChange.setText(AppLocal.getIntString("label.changeDue"));
        lblChange.setPreferredSize(new Dimension(175, 60));
        lblChange.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblChange.setHorizontalAlignment(SwingConstants.RIGHT);

        lblTotal.setText(AppLocal.getIntString("label.total"));
        lblTotal.setPreferredSize(new Dimension(175, 60));
        lblTotal.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        lblTendered.setText(AppLocal.getIntString("label.tendered"));
        lblTendered.setPreferredSize(new Dimension(175, 60));
        lblTendered.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblTendered.setHorizontalAlignment(SwingConstants.RIGHT);

        lblColon.setPreferredSize(new Dimension(175, 60));
        lblColon.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblColon.setHorizontalAlignment(SwingConstants.CENTER);

        lblColon1.setPreferredSize(new Dimension(175, 60));
        lblColon1.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblColon1.setHorizontalAlignment(SwingConstants.CENTER);

        lblColon2.setPreferredSize(new Dimension(175, 60));
        lblColon2.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblColon2.setHorizontalAlignment(SwingConstants.CENTER);

        lblTotalAmount.setText(totalAmount);
        lblTotalAmount.setPreferredSize(new Dimension(175, 60));
        lblTotalAmount.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblTotalAmount.setHorizontalAlignment(SwingConstants.LEFT);

        lblChangeDue.setText(changeDue);
        lblChangeDue.setPreferredSize(new Dimension(175, 60));
        lblChangeDue.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblChangeDue.setHorizontalAlignment(SwingConstants.LEFT);

        lblTenderedAmount.setText(tenderedAmount);
        lblTenderedAmount.setPreferredSize(new Dimension(175, 60));
        lblTenderedAmount.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(32f));
        lblTenderedAmount.setHorizontalAlignment(SwingConstants.LEFT);

        btnNo.addActionListener((ActionEvent e) -> {
            choice = false;
            dispose();
        });

        btnYes.addActionListener((ActionEvent e) -> {
            choice = true;
            dispose();
        });

        btnOK.addActionListener((ActionEvent e) -> {
            choice = true;
            dispose();
        });

        //Set the dialog with no minimize or expand icons on title bar
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
            }
        });

        panel = new JPanel(new MigLayout("insets 10 30 20 20", "[200:200:200][10:10:10][200:200:200]", "[][][]60[]"));
        panel.add(lblTotal, "align right");
        panel.add(lblColon, "align center");
        panel.add(lblTotalAmount, "align left,  wrap");

        panel.add(lblTendered, "align right");
        panel.add(lblColon1, "align center");
        panel.add(lblTenderedAmount, "align left,  wrap");

        panel.add(lblChange, "align right");
        panel.add(lblColon2, "align center");
        panel.add(lblChangeDue, "align left,  wrap");

        // buttonPanel = new JPanel(new MigLayout("insets 0 30 0 0"));
        if (receiptRequired) {
            mainPanel = new JPanel(new MigLayout("insets 0 35 0 35", "[150:150:150][150:150:150]", "[85:85:85]"));
            panel.setPreferredSize(new Dimension(450, 340));
            mainPanel.setPreferredSize(new Dimension(480, 90));
            mainPanel.add(btnOK, "span, align center");
        } else {
            mainPanel = new JPanel(new MigLayout("insets 0 35 0 35", "[150:150:150][150:150:150]"));
            panel.setPreferredSize(new Dimension(450, 400));
            mainPanel.setPreferredSize(new Dimension(480, 250));
            mainPanel.add(lblReceiptMsg, "span, align center, wrap ");
            mainPanel.add(btnYes, "width 150, align right");
            mainPanel.add(btnNo, "width 150, align left, wrap");
            mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        }

        panel.add(mainPanel, "align center");

        //  
        // JRootFrame.PARENTFRAME.setLocation(WIDTH, WIDTH);
        setAlwaysOnTop(true);
        setResizable(false);
        setModal(true);

        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        setUndecorated(true);
        getContentPane().add(panel);
    }

}
