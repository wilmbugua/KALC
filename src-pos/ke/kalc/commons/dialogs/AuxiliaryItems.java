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
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.globals.IconFactory;
import ke.kalc.globals.SystemProperty;
import ke.kalc.osk.KeyBoard;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.sales.TaxesLogic;
import ke.kalc.pos.ticket.ProductInfoExt;
import ke.kalc.pos.ticket.TaxInfo;
import ke.kalc.pos.util.ThumbNailBuilder;

/**
 * @author John Lewis
 */
public final class AuxiliaryItems extends JDialog {

    private final Font txtFont = KALCFonts.DEFAULTFONT.deriveFont(18f);
    private final Font btnFont = KALCFonts.DEFAULTBUTTONFONT;
    private final Font lblFont = KALCFonts.DEFAULTFONT.deriveFont(16f);

    private final JPanel panel = new JPanel(new MigLayout("insets 10 0 0 10 ", "", ""));
    private final JPanel productsPanel = new JPanel(new MigLayout("insets 10 0 0 0, wrap ", "15[]5[]5[]15", "15[]"));
    private final JPanel btnPanel = new JPanel();
    private ThumbNailBuilder tnbbutton;

    private ExtendedJButton btn = null;
    private static int CHOICE = -1;

    private JPanel keyBoard;
    private TaxesLogic taxeslogic;

    private boolean pricevisible;
    private boolean taxesincluded;

    //entry point for inputbox
    protected AuxiliaryItems(java.util.List<ProductInfoExt> products, Boolean pricevisible, Boolean taxesincluded) {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard(KeyBoard.Layout.QWERTY);
        this.pricevisible = pricevisible;
        this.taxesincluded = taxesincluded;
        auxiliaryPane(products);
        pack();
    }

    private String getProductLabel(ProductInfoExt product) {
        if (pricevisible) {
            if (taxesincluded) {
                TaxInfo tax = taxeslogic.getTaxInfo(product.getTaxCategoryID());
                return "<html><center>" + product.getName() + "<br>" + product.printPriceSellTax(tax);
            } else {
                return "<html><center>" + product.getDisplay() + "<br>" + product.printPriceSell();
            }
        } else {
            return product.getDisplay();
        }
    }

    public JButton getButton(Icon ico, String text) {
        JButton btn = new JButton();
        // Icon ico = new ImageIcon(tnbbutton.getThumbNailText(prod.getImage()));
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
        // btn.addMouseListener(al);
        return btn;
    }

    protected void auxiliaryPane(java.util.List<ProductInfoExt> products) {
        setButtonPanel(new Dimension(100, 35));

        if (SystemProperty.HISTORICPRODUCTICON) {
            tnbbutton = new ThumbNailBuilder(SystemProperty.PRODUCTBUTTONWIDTH, SystemProperty.PRODUCTBUTTONHEIGHT, IconFactory.getIcon("package.png"));
        } else {
            tnbbutton = new ThumbNailBuilder(SystemProperty.PRODUCTBUTTONWIDTH, SystemProperty.PRODUCTBUTTONHEIGHT, IconFactory.getIcon("newproduct.png"));
        }

        for (ProductInfoExt prod : products) {

            productsPanel.add(getButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), "")), getProductLabel(prod)));

        }

        panel.add(productsPanel, "wrap");
        panel.add(btnPanel, "span,  align center, wrap");

        setResizable(false);
        setModal(true);

        //   panel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Auxiliary Items");
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

    }

    protected int getChoice() {
        return CHOICE;
    }

}
