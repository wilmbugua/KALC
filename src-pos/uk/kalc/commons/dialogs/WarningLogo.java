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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.KALCFonts;

/**
 *
 * @author John
 */
public class WarningLogo extends JDialog {

    private final JPanel splashPanel = new JPanel(new MigLayout("insets 3 0 3 0", "[][]", "[]0[50:20:50]"));
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int centreX = screenSize.width / 2;
    private final int centreY = screenSize.height / 2;

    public WarningLogo() {
        super((Window) null);
        setModal(true);
        createPage();
        setLocation(centreX - 280, centreY - 135);
        setSize(580, 280);
        setUndecorated(true);
        add(splashPanel);
        setVisible(true);

    }

    private void createPage() {
        String currentPath = System.getProperty("user.dir");
        JLabel imageLabel = new JLabel();
        JLabel textLabel = new JLabel();
        JButton btnExit = new JButton("Exit");
        btnExit.setFont(KALCFonts.DEFAULTBUTTONFONT);
        btnExit.setFocusPainted(false);
        JLabel version = new JLabel();
        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/verkalclogo.png")));
        textLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/runningtext.png")));

        version.setText("V" + AppLocal.APP_VERSION + "       ");
        version.setForeground(new Color(29, 15, 191));

        version.setFont(KALCFonts.KALCFONTBOLD);

        splashPanel.add(imageLabel, "height ::157, span, wrap");
        splashPanel.add(version, "height ::20, span, wrap, align right");
        splashPanel.add(textLabel, "width ::480 , height :45:");

        splashPanel.add(btnExit, "width ::200, height :40:");
        splashPanel.setBackground(Color.white);
        splashPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        btnExit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
    }

    public void deleteSplashLogo() {
        this.setVisible(false);
        this.dispose();
    }
}
