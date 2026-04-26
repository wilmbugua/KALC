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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 * @author John Lewis
 */
public class MissingDataDialog extends JDialog {

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

    protected MissingDataDialog(Boolean showLogo, JPanel content, Boolean unDecorated) {
        super(new JFrame());
        //  separator.setOrientation(JSeparator.HORIZONTAL);
        infoDialog(showLogo, content, unDecorated);
        pack();
    }

    private void infoDialog(Boolean showLogo, JPanel content, Boolean unDecorated) {
        mainPanel = new JPanel(new MigLayout("insets 10 4 5 2"));
        btnPanel = new JPanel();

        if (showLogo) {
            logoPanel = new JPanel(new MigLayout("", "[500]"));
            ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/uk/KALC/fixedimages/kalclogo_small.png"));
            JLabel imageLabel = new JLabel(imageIcon);
            logoPanel.add(imageLabel, "align left");
            mainPanel.add(logoPanel, "wrap");
        }

        mainPanel.add(content,"width 500, wrap");
        
        
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
            // removed border to undecorated dialog
            if (unDecorated) {
                logoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }
        }

        getContentPane().add(mainPanel);

    }

}
