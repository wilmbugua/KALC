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
package ke.kalc.commons.dbmanager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import ke.kalc.pos.forms.AppConfig;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;

public class UpdatePanel extends JDialog {

    private final JPanel updatePanel = new JPanel(new MigLayout("insets 3 0 3 0", "[][][]", "[]0[50:20:25]5[25][25]"));
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int centreX = screenSize.width / 2;
    private final int centreY = screenSize.height / 2;

    public UpdatePanel() {
        super((Window) null);
        setModal(true);
        createPage();
        setLocation(centreX - 280, centreY - 165);
        setSize(560, 330);
        setUndecorated(true);
        add(updatePanel);
        setVisible(true);

    }

    private void createPage() {
        String currentPath = System.getProperty("user.dir");
        JLabel imageLabel = new JLabel();
        JLabel textLabel = new JLabel();
        JButton btnExit = new JButton("Exit");
        JLabel version = new JLabel();
        JLabel dbVersion = new JLabel();
        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/verkalclogo.png")));
        textLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/KALC/fixedimages/updaterequired.png")));

        version.setText("Current Version  : " + AppLocal.APP_VERSION + "       ");
        version.setForeground(new Color(29, 15, 191));
        dbVersion.setText("Database Version : " + AppConfig.getVersion() + "       ");
        dbVersion.setForeground(new Color(29, 15, 191));

        version.setFont(KALCFonts.KALCFONT.deriveFont(16f));
        dbVersion.setFont(KALCFonts.KALCFONT.deriveFont(16f));
        btnExit.setFont(KALCFonts.KALCFONT.deriveFont(16f));

        updatePanel.add(imageLabel, "height ::157, span, wrap");
        updatePanel.add(version, "height ::20, span, wrap, align right");
        updatePanel.add(dbVersion, "height ::20, span, wrap, align right");
        updatePanel.add(textLabel, "width ::480 , span, wrap, height :35:");

        updatePanel.add(btnExit, "span, width :200:,  align right ");

        updatePanel.setBackground(Color.white);
        updatePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        btnExit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

    }

    public void deleteUpdateLogo() {
        this.setVisible(false);
        this.dispose();
    }
}
