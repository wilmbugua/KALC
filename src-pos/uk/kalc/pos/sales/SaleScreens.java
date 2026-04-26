/*
**    KALC Administration  - Open Source Point of Sale
**
**    This file is part of KALC Administration Version KALC V1.5.3
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
package uk.kalc.pos.sales;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import uk.kalc.globals.SystemProperty;
import uk.kalc.pos.catalog.JCatalog;

/**
 *
 * @author John
 */
public abstract class SaleScreens extends JPanelTicket {

    private static JPanelTicket panel;

    protected static void createLayout(JPanelTicket ticketPanel) {
        if (ticketPanel instanceof JPanelTicketSales) {
            panel = ticketPanel;
            switch (SystemProperty.SALESLAYOUT) {
                case "Layout1":
                    layoutOne();
                    break;
                case "Layout2":
                    layoutTwo();
                    break;
                case "Layout3":
                    layoutThree();
                    break;
            }
        }
    }

    private static void layoutOne() {
        panel.catcontainer.add(panel.southcomponent, BorderLayout.CENTER);
        JPanel southpanel = (JPanel) panel.southcomponent;
        ((JPanel) southpanel.getComponent(0)).setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel fpanel = new JPanel(new BorderLayout());
        fpanel.add(panel.m_jPanEntries, BorderLayout.WEST);
        if (panel.southcomponent instanceof JCatalog) {
            fpanel.add(((JCatalog) southpanel).getCatComponent(), BorderLayout.EAST);
        }

        panel.m_jPanEntriesE.add(panel.ticketActions, BorderLayout.WEST);
        panel.m_jPanEntriesE.add(fpanel, BorderLayout.EAST);

        panel.m_jTicketId.setPreferredSize(new Dimension(300, 18));
        panel.m_jLblLoyaltyCard.setPreferredSize(new Dimension(300, 18));

        panel.m_jPanTicket.add(panel.jPanel3, BorderLayout.BEFORE_FIRST_LINE);
        panel.jPanel2.setPreferredSize(new Dimension(90, 305));
        panel.ticketActions.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panel.jPanel4.add(panel.m_jPanTotals, BorderLayout.CENTER);
        panel.m_jContEntries.add(panel.m_jPanEntriesE, BorderLayout.NORTH);
        if (panel.southcomponent instanceof JCatalog) {
            JCatalog catpanel = (JCatalog) southpanel;
            panel.m_jContEntries.add(catpanel.getProductComponent(), BorderLayout.CENTER);
            catpanel.setControls("south");
            catpanel.getCatComponent().setPreferredSize(new Dimension(230, 10));
            catpanel.getProductComponent().setPreferredSize(new Dimension(230, 350));
        } else {
            panel.m_jContEntries.add(southpanel, BorderLayout.CENTER);
        }
        panel.southcomponent.setPreferredSize(new Dimension(0, 0));
    }

    private static void layoutTwo() {
        if (panel.catcontainer.getComponent(0) instanceof JCatalog) {
            JCatalog catpanel = (JCatalog) panel.catcontainer.getComponent(0);
            catpanel.setControls("south");
            catpanel.getCatComponent().setPreferredSize(new Dimension(230, 10));
        }
        panel.jPanel4.add(panel.m_jPanTotals, BorderLayout.CENTER);
        panel.m_jPanContainer.add(panel.m_jPanTicket, BorderLayout.EAST);
        panel.m_jPanContainer.add(panel.catcontainer, BorderLayout.CENTER);
        panel.ticketActions.setPreferredSize(new Dimension(80, 0));
        panel.jPanel2.setPreferredSize(new Dimension(90, 310));
        panel.m_jPanEntriesE.add(panel.ticketActions, BorderLayout.CENTER);
        panel.m_jPanEntriesE.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        panel.m_jContEntries.add(panel.m_jPanEntries, BorderLayout.CENTER);
        panel.m_jPanEntries.setPreferredSize(new Dimension(250, 316));
        panel.m_jTicketId.setPreferredSize(new Dimension(300, 18));
        panel.m_jLblLoyaltyCard.setPreferredSize(new Dimension(300, 18));

        panel.m_jPanTicket.add(panel.jPanel3, BorderLayout.BEFORE_FIRST_LINE);
        panel.m_jPanTicket.setPreferredSize(new Dimension(390, 316));
        panel.m_jPanTicket.add(panel.m_jContEntries, BorderLayout.SOUTH);
    }

    private static void layoutThree() {
        JPanel catPanel = null;
        JPanel jpanelA = new JPanel(new BorderLayout());
        if (panel.catcontainer.getComponent(0) instanceof JCatalog) {
            JCatalog myCatpanel = (JCatalog) panel.catcontainer.getComponent(0);
            myCatpanel.setControls("south");
            myCatpanel.getCatComponent().setPreferredSize(new Dimension(230, 10));
            catPanel = (JPanel) myCatpanel.getCatComponent();
            catPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            panel.m_jPanContainer.add(jpanelA, BorderLayout.EAST);
            jpanelA.add(catPanel, BorderLayout.CENTER);
        }
        panel.jPanel4.add(panel.m_jPanTotals, BorderLayout.CENTER);
        panel.m_jPanContainer.add(panel.m_jPanTicket, BorderLayout.EAST);
        panel.m_jPanContainer.add(panel.catcontainer, BorderLayout.CENTER);
        panel.ticketActions.setPreferredSize(new Dimension(80, 0));
        panel.jPanel2.setPreferredSize(new Dimension(90, 310));
        panel.m_jPanEntriesE.add(panel.ticketActions, BorderLayout.CENTER);
        panel.m_jPanEntriesE.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        panel.m_jContEntries.add(panel.m_jPanEntries, BorderLayout.CENTER);
        panel.m_jPanEntries.setPreferredSize(new Dimension(250, 316));
        panel.m_jTicketId.setPreferredSize(new Dimension(300, 16));
        panel.m_jTicketId.setPreferredSize(new Dimension(300, 18));
        panel.m_jLblLoyaltyCard.setPreferredSize(new Dimension(300, 18));

        panel.m_jPanTicket.add(panel.jPanel3, BorderLayout.BEFORE_FIRST_LINE);
        if (catPanel != null) {
            panel.m_jPanTicket.setPreferredSize(new Dimension(500, 316));
            jpanelA.add(panel.m_jContEntries, BorderLayout.EAST);
            panel.m_jPanTicket.add(jpanelA, BorderLayout.SOUTH);
        } else {
            panel.m_jPanTicket.setPreferredSize(new Dimension(390, 316));
            panel.m_jPanTicket.add(panel.m_jContEntries, BorderLayout.SOUTH);
        }
    }

}
