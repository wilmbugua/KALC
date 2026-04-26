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
package ke.kalc.pos.catalog;

import ke.kalc.basic.BasicException;
import ke.kalc.data.gui.JMessageDialog;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.sales.TaxesLogic;
import ke.kalc.pos.ticket.ProductInfoExt;
import ke.kalc.pos.ticket.TaxInfo;
import ke.kalc.pos.util.ThumbNailBuilder;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.globals.IconFactory;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.JRootApp;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;

public class JCatalogFull extends JPanel implements ListSelectionListener, CatalogSelector {

    protected EventListenerList listeners = new EventListenerList();
    private DataLogicSales m_dlSales;
    private DataLogicSystem m_dlSystem;
    private TaxesLogic taxeslogic;
    private boolean pricevisible;
    private boolean taxesincluded;
    private final Map<String, ProductInfoExt> m_productsset = new HashMap<>();
    private final Set<String> m_categoriesset = new HashSet<>();
    private ThumbNailBuilder tnbbutton;
    private Object newColour;
    private String siteGuid;

    public JCatalogFull(DataLogicSales dlSales, String siteGuid) {
        this(dlSales, false, false, siteGuid);
    }

    //  public JCatalogFull(DataLogicSales dlSales, boolean pricevisible, boolean taxesincluded, int width, int height, String siteGuid) {   
    public JCatalogFull(DataLogicSales dlSales, boolean pricevisible, boolean taxesincluded, String siteGuid) {
        this.siteGuid = siteGuid;
        m_dlSales = dlSales;
        m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(SessionFactory.getSession());
        this.pricevisible = pricevisible;
        this.taxesincluded = taxesincluded;
        initComponents();
        tnbbutton = new ThumbNailBuilder(SystemProperty.PRODUCTBUTTONWIDTH, SystemProperty.PRODUCTBUTTONHEIGHT, IconFactory.getIcon("package.png"));

    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void showCatalogPanel(String id) {
    }

    @Override
    public void loadCatalog(String siteGuid) throws BasicException {
        m_jProducts.removeAll();
        m_productsset.clear();
        m_categoriesset.clear();

        // Load the taxes logic
        taxeslogic = new TaxesLogic(m_dlSales.getTaxList(siteGuid).list());

        buildProductPanel();
    }

    @Override
    public void setComponentEnabled(boolean value) {

        m_jProducts.setEnabled(value);
        synchronized (m_jProducts.getTreeLock()) {
            int compCount = m_jProducts.getComponentCount();
            for (int i = 0; i < compCount; i++) {
                m_jProducts.getComponent(i).setEnabled(value);
            }
        }
        this.setEnabled(value);
    }

    @Override
    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
    }

    protected void fireSelectedProduct(ProductInfoExt prod) {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (EventListener l1 : l) {
            if (e == null) {
                e = new ActionEvent(prod, ActionEvent.ACTION_PERFORMED, prod.getID());
            }
            ((ActionListener) l1).actionPerformed(e);
        }
    }

    private void buildProductPanel() {
        try {
            JCatalogTab jcurrTab = new JCatalogTab();
            m_jProducts.add(jcurrTab, "");

            java.util.List< ProductInfoExt> prods;

            prods = (SystemProperty.NEWSCREEN) ? m_dlSales.getAllProductCatalogByCatOrder(siteGuid) : m_dlSales.getAllProductCatalog(siteGuid);

            for (ProductInfoExt prod : prods) {
                jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new MouseTimer(prod));
            }
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));
        }
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

    private void showProductPanel(String id) {
        ProductInfoExt product = m_productsset.get(id);
        if (product == null) {
            if (m_productsset.containsKey(id)) {
            } else {
                try {
                    List<ProductInfoExt> products = m_dlSales.getProductComments(id, siteGuid);

                    if (products.isEmpty()) {
                        m_productsset.put(id, null);
                    } else {
                        product = m_dlSales.getProductInfo(id);
                        m_productsset.put(id, product);

                        JCatalogTab jcurrTab = new JCatalogTab();
                        jcurrTab.applyComponentOrientation(getComponentOrientation());
                        m_jProducts.add(jcurrTab, "PRODUCT." + id);

                        // Add products
                        for (ProductInfoExt prod : products) {
                            jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new MouseTimer(prod));
                        }
                        CardLayout cl = (CardLayout) (m_jProducts.getLayout());
                        cl.show(m_jProducts, "PRODUCT." + id);
                    }
                } catch (BasicException eb) {
                    m_productsset.put(id, null);
                }
            }
        } else {
            CardLayout cl = (CardLayout) (m_jProducts.getLayout());
            cl.show(m_jProducts, "PRODUCT." + id);
        }
    }

    private class MouseTimer extends MouseAdapter {

        private final ProductInfoExt prod;
        private long start;

        public MouseTimer(ProductInfoExt prod) {
            this.prod = prod;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            start = System.currentTimeMillis();
            ((JButton) e.getSource()).setPressedIcon(IconFactory.getIcon("empty.png"));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            long duration = ((System.currentTimeMillis()) - start) / 1000;
            if (duration < 1) {
                fireSelectedProduct(prod);
            } else {
                if (!SystemProperty.PRODUCTSCRIPT || !JRootApp.getPricipalApp().getUser().hasPermission("access.productheld")) {
                    fireSelectedProduct(prod);
                    return;
                }
                try {
                    evalScript("script.productheld", prod);
                } catch (ScriptException ex) {
                    System.out.println("Not found");
                }
            }
        }
    }

    private Object evalScript(String code, ProductInfoExt prod) throws ScriptException {
        ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
        script.put("product", prod);
        script.put("dlSales", m_dlSales);
        script.put("dlSystem", m_dlSystem);
        return script.eval(m_dlSystem.getResourceAsXML(code));
    }

    private class SelectedAction implements ActionListener {

        private final ProductInfoExt prod;

        public SelectedAction(ProductInfoExt prod) {
            this.prod = prod;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireSelectedProduct(prod);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jProducts = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        m_jProducts.setLayout(new java.awt.CardLayout());
        add(m_jProducts, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel m_jProducts;
    // End of variables declaration//GEN-END:variables

}
