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


package ke.kalc.pos.sales;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ke.kalc.globals.SystemProperty;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.utils.TerminalInfo;
import ke.kalc.pos.catalog.CatalogSelector;
import ke.kalc.pos.catalog.JCatalog;
import ke.kalc.pos.catalog.JCatalogFull;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.ticket.ProductInfoExt;

/**
 *
 *
 */
public class JPanelTicketSales extends JPanelTicket {

    private CatalogSelector m_cat;
    private DataLogicSystem dlSystem;

    public JPanelTicketSales() {
    }

    @Override
    public void init(AppView app) {
        super.init(app);
        dlSystem = (DataLogicSystem) app.getBean("ke.kalc.pos.datalogic.DataLogicSystem");
        ticketLines.addListSelectionListener(new CatalogSelectionListener());
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    protected Component getSouthComponent() {

        if (SystemProperty.NEWSCREEN) {
            m_cat = new JCatalogFull(dlSales,
                    "true".equals(m_jbtnconfig.getProperty("pricevisible")),
                    "true".equals(m_jbtnconfig.getProperty("taxesincluded")),
                    siteGuid);
        } else {
            m_cat = new JCatalog(dlSales,
                    "true".equals(m_jbtnconfig.getProperty("pricevisible")),
                    "true".equals(m_jbtnconfig.getProperty("taxesincluded")),
                    siteGuid);
        }

        m_cat.addActionListener(new CatalogListener());
        m_cat.getComponent().setPreferredSize(new Dimension(
                0,
                Integer.parseInt(m_jbtnconfig.getProperty("cat-height", "245"))));
        return m_cat.getComponent();
    }

    @Override
    protected void resetSouthComponent() {
        m_cat.showCatalogPanel(null);
    }

    @Override
    protected JTicketsBag getJTicketsBag() {
        return JTicketsBag.createTicketsBag(TerminalInfo.getPosType(), m_App, this);
    }

    @Override
    public void activate() throws BasicException {
        super.activate();
        m_cat.loadCatalog(siteGuid);
    }

    @Override
    public void reLoadCatalog() {
        try {
            m_cat.loadCatalog(siteGuid);
        } catch (BasicException ex) {
        }

    }

    private class CatalogListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            buttonTransition((ProductInfoExt) e.getSource());
        }
    }

    private class CatalogSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int i = ticketLines.getSelectedIndex();
                
                if (i >= 0) {
                    // Look for the first non auxiliar product.
                    while (i >= 0 && m_oTicket.getLine(i).isProductCom()) {
                        i--;
                    }

                    // Show the accurate catalog panel...
                    if (i >= 0) {
                        m_cat.showCatalogPanel(m_oTicket.getLine(i).getProductID());
                    } else {
                        m_cat.showCatalogPanel(null);
                    }
                }
            }
        }
    }    
}
