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
package ke.kalc.pos.ticket;

import java.awt.Component;
import java.awt.Image;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import ke.kalc.format.Formats;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.util.ThumbNailBuilder;

public class ProductSearchRenderer extends DefaultListCellRenderer {

    ThumbNailBuilder tnbprod;
    private Double stockCount;

    /**
     * Creates a new instance of ProductRenderer
     */
    public ProductSearchRenderer(double stockCount) {
        tnbprod = new ThumbNailBuilder(48, 48, IconFactory.getIcon("package.png"));
        this.stockCount = stockCount;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);

        ProductInfoExt prod = (ProductInfoExt) value;
        if (prod != null) {

//            setText("<html>" + prod.getReference() + " - " + prod.getName() + "<br>&nbsp;&nbsp;&nbsp;&nbsp;" + Formats.CURRENCY.formatValue(prod.getPriceSell()));
            setText("<html>" + prod.getReference() + " - " + prod.getName() + "<br> "
                    + "<b>" + AppLocal.getIntString("label.stockunits") + ":</b> " + Formats.DOUBLE.formatValue(stockCount) + "<b>     "
                    + AppLocal.getIntString("label.prodpricesell") + ":</b>  "
                    + Formats.CURRENCY.formatValue(prod.getPriceSell()));
            Image img = tnbprod.getThumbNail(prod.getImage());
            setIcon(img == null ? null : new ImageIcon(img));
        }
        return this;
    }
}
