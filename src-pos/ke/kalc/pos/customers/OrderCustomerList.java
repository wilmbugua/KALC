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


package ke.kalc.pos.customers;

import ke.kalc.pos.datalogic.DataLogicCustomers;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import ke.kalc.basic.BasicException;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.pos.catalog.JCatalogTab;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.datalogic.DataLogicReceipts;
import ke.kalc.pos.sales.SharedTicketInfo;
import ke.kalc.pos.sales.TicketsEditor;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.pos.util.ThumbNailBuilder;

/**
 *
 *    - outline/prep for uniCenta mobile + eCommerce connector
 */
public class OrderCustomerList extends JPanel implements TicketSelector {

    /**
     * Source origin and Ticket
     */
    protected AppView application;
    private String currentTicket;

    /**
     * This instance interface
     */
    protected TicketsEditor panelticket;

    /**
     * Set listeners for new/change Customer/Receipt events
     */
    protected EventListenerList listeners = new EventListenerList();
    private final DataLogicCustomers dataLogicCustomers;
    private final DataLogicReceipts dataLogicReceipts;
    private final ThumbNailBuilder tnbbutton;

    /**
     * Logging / Monitor
     */
    protected static final Logger LOGGER = Logger.getLogger("ke.kalc.pos.customers.CustomersList");

    /**
     * Creates new form CustomersList
     * @param dlCustomers
     * @param app
     * @param panelticket
     */
    public OrderCustomerList(DataLogicCustomers dlCustomers, AppView app, TicketsEditor panelticket) {
        this.application = app;
        this.panelticket = panelticket;
        this.dataLogicCustomers = dlCustomers;
        this.dataLogicReceipts = (DataLogicReceipts) application.getBean("ke.kalc.pos.datalogic.DataLogicReceipts");
        tnbbutton = new ThumbNailBuilder(90, 98);

        initComponents();
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     *
     * @throws BasicException
     */
    public void reloadCustomers() throws BasicException {
//        synchroniseData();
        loadCustomers();
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void loadCustomers() throws BasicException {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                long time = System.currentTimeMillis();
                jPanelCustomers.removeAll();

                JCatalogTab flowTab = new JCatalogTab();
                jPanelCustomers.add(flowTab);

                List<CustomerInfoExt> customers = null;
                List<SharedTicketInfo> ticketList = null;
                try {

                    LOGGER.log(Level.INFO, "Time of getCustomersWithOutImage {0}", (System.currentTimeMillis() - time));
                    time = System.currentTimeMillis();

                    ticketList = dataLogicReceipts.getSharedTicketList();
                    LOGGER.log(Level.INFO, "Time of getSharedTicketList {0}", (System.currentTimeMillis() - time));
                    time = System.currentTimeMillis();


                } catch (BasicException ex) {
                    Logger.getLogger(OrderCustomerList.class.getName()).log(Level.SEVERE, null, ex);
                }
                HashMap<SharedTicketInfo, CustomerInfoExt> orderMap = new HashMap<>();

                for (SharedTicketInfo sharedTicketInfo : ticketList) {

                    String ticketName = sharedTicketInfo.getName().trim();

                    if (ticketName.contains("[") && ticketName.contains("]")) {

                        // found order
                        if (ticketName.startsWith("[")) {
                            // order without customer
                            orderMap.put(sharedTicketInfo, null);
                        } else if (!customers.isEmpty()) {
                            // find customer to ticket
                            for (CustomerInfoExt customer : customers) {
                                if (customer != null) {
                                    String name = customer.getName().trim();
                                    if (ticketName.startsWith(name)) {
                                        orderMap.put(sharedTicketInfo, customer);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                // sort
                CustomerComparator bvc = new CustomerComparator(orderMap);
                TreeMap<SharedTicketInfo, CustomerInfoExt> sortedMap = new TreeMap<>(bvc);
                sortedMap.putAll(orderMap);

                LOGGER.log(Level.INFO, "Time of orderMap {0}", (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();

                // set button list
                for (Map.Entry<SharedTicketInfo, CustomerInfoExt> entry : sortedMap.entrySet()) {
                    SharedTicketInfo ticket = entry.getKey();
                    CustomerInfoExt customer = entry.getValue();

                    String name = ticket.getName();
                    BufferedImage image = null;

                    if (image == null) {
                        try {
                            InputStream is = getClass().getResourceAsStream("/uk/KALC/images/no_image.png");
                            if (is != null) {
                                image = ImageIO.read(is);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(OrderCustomerList.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    String username;
                    if (name.indexOf("[") != 0) {
                        username = name.substring(0, name.indexOf("[") - 1);
                        username = username.replace("-", "");
                    } else {
                        username = "unknown";
                    }
                    String orderId = name.substring(name.indexOf("["), name.indexOf("]") + 1);
                    String text = "<html><center>" + username.trim() + "<br/>" + orderId.trim() + "</center></html>";

                    ImageIcon icon = new ImageIcon(tnbbutton.getThumbNailText(image, text));
                }
                LOGGER.log(Level.INFO, "Time of finished loadCustomerOrders {0}", (System.currentTimeMillis() - time));
            }
        });
    }

    /**
     *
     * @param value
     */
    @Override
    public void setComponentEnabled(boolean value) {
        jPanelCustomers.setEnabled(value);

        synchronized (jPanelCustomers.getTreeLock()) {
            int compCount = jPanelCustomers.getComponentCount();
            for (int i = 0; i < compCount; i++) {
                jPanelCustomers.getComponent(i).setEnabled(value);
            }
        }
        this.setEnabled(value);
    }

    /**
     *
     * @param l
     */
    @Override
    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }

    /**
     *
     * @param l
     */
    @Override
    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    private void setActiveTicket(String id) throws BasicException {

        currentTicket = panelticket.getActiveTicket().getId();

        // set ticket
        // BEGIN TRANSACTION
        TicketInfo ticket = dataLogicReceipts.getSharedTicket(id);
        if (ticket == null) {
            // Does not exists ???
            throw new BasicException(AppLocal.getIntString("message.noticket"));
        } else {
            dataLogicReceipts.deleteSharedTicket(id);
            currentTicket = id;
            panelticket.setActiveTicket(ticket, null);
            fireTicketSelectionChanged(ticket.getId());
        }
        // END TRANSACTION                 
    }


    private void fireTicketSelectionChanged(String ticketId) {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (EventListener l1 : l) {
            if (e == null) {
                e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ticketId);
            }
            ((ActionListener) l1).actionPerformed(e);
        }
    }

    private class SelectedCustomerAction implements ActionListener {

        private final String ticketId;

        public SelectedCustomerAction(String ticketId) {
            this.ticketId = ticketId;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (ticketId != null) {
                    setActiveTicket(ticketId);
                }
            } catch (BasicException ex) {
                new MessageInf(ex).show(OrderCustomerList.this);
            }
        }
    }

    class CustomerComparator implements Comparator<SharedTicketInfo> {

        Map<SharedTicketInfo, CustomerInfoExt> base;

        public CustomerComparator(Map<SharedTicketInfo, CustomerInfoExt> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.    
        @Override
        public int compare(SharedTicketInfo a, SharedTicketInfo b) {
            String nameA = base.get(a).getName();
            String nameB = base.get(b).getName();

            if (nameA.compareToIgnoreCase(nameB) < 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelCustomers = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(256, 560));
        setPreferredSize(new java.awt.Dimension(256, 560));
        setLayout(new java.awt.BorderLayout());

        jPanelCustomers.setLayout(new java.awt.CardLayout());
        add(jPanelCustomers, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelCustomers;
    // End of variables declaration//GEN-END:variables
}
