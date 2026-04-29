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
package ke.kalc.pos.catalog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import ke.kalc.beans.JFlowPanel;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.forms.KALCFonts;

public class JCatalogTab extends javax.swing.JPanel {

    private final JFlowPanel flowpanel;
    private Timer btn2Timer;
    private static final int DELAY = 50;

    /**
     * Creates new form JCategoryProducts
     */
    public JCatalogTab() {
        initComponents();
        flowpanel = new JFlowPanel();
        JScrollPane scroll = new JScrollPane(flowpanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void setEnabled(boolean value) {
        flowpanel.setEnabled(value);
        super.setEnabled(value);        
    }

    //Historic add button code
    public void addButton(Icon ico, ActionListener al) {
        JButton btn = new JButton();
        btn.applyComponentOrientation(getComponentOrientation());
        btn.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
        btn.setIcon(ico);
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.addActionListener(al);
        flowpanel.add(btn);
    }

    public void addButton(Icon ico, MouseListener al) {
        JButton btn = new JButton();
        btn.applyComponentOrientation(getComponentOrientation());
        btn.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
        btn.setIcon(ico);
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.addMouseListener(al);
        flowpanel.add(btn);
    }

    public void addButton(Icon ico, String text, ActionListener al) {
        JButton btn = new JButton();
        btn.applyComponentOrientation(getComponentOrientation());
        btn.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
        btn.setIcon(ico);
        btn.setHorizontalTextPosition(JButton.CENTER);
        btn.setVerticalTextPosition(JButton.CENTER);
        btn.setForeground(new Color((int) Integer.decode(SystemProperty.BUTTONTEXTCOLOUR)));
        btn.setText("<html> <center><b>" + text + "</html>");
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.addActionListener(al);
        flowpanel.add(btn);
    }

    //New add button code
    //public void addButton(Icon ico, String text, ActionListener al) {
    public void addButton(Icon ico, String text, MouseListener al) {
        JButton btn = new JButton();
        btn.applyComponentOrientation(getComponentOrientation());
        btn.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
        btn.setIcon(ico);
        btn.setHorizontalTextPosition(JButton.CENTER);
        btn.setVerticalTextPosition(JButton.CENTER);
        btn.setForeground(new Color((int) Integer.decode(SystemProperty.BUTTONTEXTCOLOUR)));
        btn.setText("<html><center><b>" + text + "</html>");
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.addMouseListener(al);
        flowpanel.add(btn);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setFont(KALCFonts.DEFAULTFONT.deriveFont(14f));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
