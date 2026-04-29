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
package ke.kalc.pos.sales.restaurant;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import ke.kalc.globals.SystemProperty;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.data.gui.NullIcon;
import ke.kalc.data.loader.SentenceList;
import ke.kalc.data.loader.SerializerReadClass;
import ke.kalc.data.loader.StaticSentence;
import ke.kalc.pos.customers.CustomerInfo;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.JPrincipalApp;
import ke.kalc.pos.forms.JRootApp;
import ke.kalc.pos.forms.LocalResource;
import ke.kalc.pos.datalogic.DataLogicReceipts;
import ke.kalc.pos.sales.JPanelTicket;
import ke.kalc.pos.sales.JTicketsBag;
import ke.kalc.pos.sales.SharedTicketInfo;
import ke.kalc.pos.sales.TicketsEditor;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.pos.ticket.TicketLineInfo;
import ke.kalc.pos.util.AutoLogoff;
import ke.kalc.pos.util.AutoRefresh;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.auditing.Audit;
import ke.kalc.pos.forms.AppUser;
import ke.kalc.pos.forms.KALCFonts;

/**
 *
 *
 */
public class JTicketsBagRestaurantMap extends JTicketsBag {

    @Override
    public void getTicketByCode(String id) {

    }

    /**
     *
     */
    private static class ServerCurrent {

        public ServerCurrent() {
        }
    }

    private java.util.List<Place> m_aplaces;
    private java.util.List<Floor> m_afloors;

    private JTicketsBagRestaurant m_restaurantmap;
    private JTicketsBagRestaurantRes m_jreservations;

    private Place m_PlaceCurrent;
    private ServerCurrent m_ServerCurrent;
    private Place m_PlaceClipboard;
    private CustomerInfo customer;

    private DataLogicReceipts dlReceipts = null;
    private DataLogicSales dlSales = null;
    private DataLogicSystem dlSystem = null;
    private final RestaurantDBUtils restDB;
    private static final Icon ICO_OCU_SM = IconFactory.getIcon("edit_group_sm.png");
    private static final Icon ICO_OCU = IconFactory.getIcon("edit_group.png");
    private static final Icon ICO_WAITER = new NullIcon(1, 1);
    private static final Icon ICO_FRE = new NullIcon(22, 22);
    private String waiterDetails;
    private String customerDetails;
    private String tableName;
    private Boolean transparentButtons;
    private Boolean actionEnabled = true;
    private int newX;
    private int newY;
    private JPrincipalApp principalApp;
    private JPanelTicket panelTicket;
    private Boolean overrideActive = false;

    /**
     * Creates new form JTicketsBagRestaurant
     *
     * @param app
     * @param panelticket
     */
    public JTicketsBagRestaurantMap(AppView app, TicketsEditor panelticket) {

        super(app, panelticket);
        this.panelTicket = (JPanelTicket) panelticket;

        // create a refresh timer action if required
        if (SystemProperty.AUTOREFRESH) {
            Action refreshTables = new refreshTables();
            AutoRefresh.getInstance().setTimer(5 * 1000, refreshTables);
            AutoRefresh.getInstance().activateTimer();
        }

        restDB = new RestaurantDBUtils();
        transparentButtons = SystemProperty.TRANSPARENTBUTTONS;

        dlReceipts = (DataLogicReceipts) app.getBean("ke.kalc.pos.datalogic.DataLogicReceipts");
        dlSales = (DataLogicSales) m_App.getBean("ke.kalc.pos.datalogic.DataLogicSales");
        dlSystem = (DataLogicSystem) m_App.getBean("ke.kalc.pos.datalogic.DataLogicSystem");

        m_restaurantmap = new JTicketsBagRestaurant(app, this);
        m_PlaceCurrent = null;
        m_PlaceClipboard = null;
        customer = null;

        try {
            //Get the list of floors available
            SentenceList sent = new StaticSentence(
                    app.getSession(),
                    "select id, name, image from floors order by name",
                    null,
                    new SerializerReadClass(Floor.class));
            m_afloors = sent.list();

        } catch (BasicException eD) {
            m_afloors = new ArrayList<>();
        }
        //Get the list of places (tables) available
        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(),
                    "select id, name, x, y, width, height, floor, customer, waiter, ticketid, tablemoved, covers from places order by floor",
                    null,
                    new SerializerReadClass(Place.class));
            m_aplaces = sent.list();
        } catch (BasicException eD) {
            m_aplaces = new ArrayList<>();
        }

        initComponents();

        if (m_afloors.size() > 1) {
            // A tab container for 2 or more floors
            JTabbedPane jTabFloors = new JTabbedPane();
            jTabFloors.applyComponentOrientation(getComponentOrientation());
            jTabFloors.setBorder(new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)));
            jTabFloors.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            jTabFloors.setFocusable(false);
            jTabFloors.setRequestFocusEnabled(false);
            m_jPanelMap.add(jTabFloors, BorderLayout.CENTER);

            for (Floor f : m_afloors) {
                f.getContainer().applyComponentOrientation(getComponentOrientation());

                JScrollPane jScrCont = new JScrollPane();
                jScrCont.applyComponentOrientation(getComponentOrientation());
                JPanel jPanCont = new JPanel();
                jPanCont.applyComponentOrientation(getComponentOrientation());

                jTabFloors.addTab(f.getName(), f.getIcon(), jScrCont);
                jScrCont.setViewportView(jPanCont);
                jPanCont.add(f.getContainer());
            }
        } else if (m_afloors.size() == 1) {
            // Just a frame for 1 floor
            Floor f = m_afloors.get(0);
            f.getContainer().applyComponentOrientation(getComponentOrientation());

            JPanel jPlaces = new JPanel();
            jPlaces.applyComponentOrientation(getComponentOrientation());
            jPlaces.setLayout(new BorderLayout());
            jPlaces.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)),
                    new javax.swing.border.TitledBorder(f.getName())));

            JScrollPane jScrCont = new JScrollPane();
            jScrCont.applyComponentOrientation(getComponentOrientation());
            JPanel jPanCont = new JPanel();
            jPanCont.applyComponentOrientation(getComponentOrientation());

            // jPlaces.setLayout(new FlowLayout());           
            m_jPanelMap.add(jPlaces, BorderLayout.CENTER);
            jPlaces.add(jScrCont, BorderLayout.CENTER);
            jScrCont.setViewportView(jPanCont);
            jPanCont.add(f.getContainer());
        }

        // Add all the Table buttons.
        Floor currfloor = null;

        for (Place pl : m_aplaces) {
            int iFloor = 0;

            if (currfloor == null || !currfloor.getID().equals(pl.getFloor())) {
                do {
                    currfloor = m_afloors.get(iFloor++);
                } while (!currfloor.getID().equals(pl.getFloor()));
            }

            currfloor.getContainer().add(pl.getButton());
            //Apply compensation from Admin app co-ordinates
            //     pl.setX(pl.getX() + SystemProperty.SWINGXCALIBRATION);
            //     pl.setY(pl.getY() + SystemProperty.SWINGYCALIBRATION);

            pl.setButtonBounds();

            if (SystemProperty.TRANSPARENTBUTTONS) {
                pl.getButton().setOpaque(false);
                pl.getButton().setContentAreaFilled(false);
                pl.getButton().setBorderPainted(false);
            }

            pl.getButton().addMouseMotionListener(new MouseAdapter() {

                public void mouseDragged(MouseEvent E) {
                    if (!actionEnabled) {
                        if (pl.getDiffX() == 0) {
                            pl.setDiffX(pl.getButton().getX() - pl.getX());
                            pl.setDiffY(pl.getButton().getY() - pl.getY());
                        }
                        newX = E.getX() + pl.getButton().getX();
                        newY = E.getY() + pl.getButton().getY();
                        pl.getButton().setBounds(newX + pl.getDiffX(), newY + pl.getDiffY(), pl.getButton().getWidth(), pl.getButton().getHeight());
                        // pl.setChanged(true);
                        pl.setX(newX);
                        pl.setY(newY);
                    }
                }
            }
            );

            pl.getButton().addActionListener(new MyActionListener(pl));
        }

        // Add the reservations panel
        m_jreservations = new JTicketsBagRestaurantRes(app, this);
        add(m_jreservations, "res");
        m_btnSavePlaces.setVisible(false);
        m_btnSetupMode.setVisible(AppUser.hasPermission("TableLayout"));

        m_jbtnReservations.setVisible(SystemProperty.RESERVATIONS);

        btnLogout.addActionListener((ActionEvent e) -> {
            jbtnLogout();
        });
    }

    public JPanelTicket getSalesPanel(){
        return panelTicket;
    }
            
            
    private class refreshTables extends AbstractAction {

        public refreshTables() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            m_PlaceClipboard = null;
            customer = null;
            loadTickets();
            printState();
            //    m_jbtnRefreshActionPerformed(null);
            //  AutoRefresh.getInstance().activateTimer();
        }
    }

    /**
     *
     */
    @Override
    public void activate() {
        btnOverride.setVisible(AppUser.hasPermission("access.lockedtables"));
        principalApp = JRootApp.getPricipalApp();
        m_PlaceClipboard = null;
        customer = null;
        loadTickets();
        printState();

        m_panelticket.setActiveTicket(null, null);
        m_restaurantmap.activate();

        showView("map");
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {

        AutoRefresh.getInstance().deactivateTimer();
        // precondicion es que tenemos ticket activado aqui y ticket en el panel
        if (viewTables()) {
            m_PlaceClipboard = null;
            customer = null;

            if (m_PlaceCurrent != null) {

                try {
                    dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket(),
                            m_panelticket.getActiveTicket().getPickupId(), "P" + m_panelticket.getActiveTicket().getId().substring(24), null);
                } catch (BasicException e) {
                    new MessageInf(e).show(this);
                }

                m_PlaceCurrent = null;
            }
            printState();
            m_panelticket.setActiveTicket(null, null);

            AutoLogoff.getInstance().deactivateTimer();

            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getBagComponent() {
        return m_restaurantmap;
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getNullComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    public TicketInfo getActiveTicket() {
        return m_panelticket.getActiveTicket();
    }

    /**
     *
     */
    public void moveTicket() {
        if (m_PlaceCurrent != null) {

            try {
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket(),
                        m_panelticket.getActiveTicket().getPickupId(), "P" + m_panelticket.getActiveTicket().getId().substring(24), null);
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }

            m_PlaceClipboard = m_PlaceCurrent;

            customer = null;
            m_PlaceCurrent = null;
        }

        printState();
        m_panelticket.setActiveTicket(null, null);
    }

    /**
     *
     * @param c
     * @return
     */
    public boolean viewTables(CustomerInfo c) {
        if (m_jreservations.deactivate()) {
            showView("map");
            m_PlaceClipboard = null;
            customer = c;
            printState();
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public boolean viewTables() {
        return viewTables(null);
    }

    /**
     *
     */
    public void newTicket() {
        AutoRefresh.getInstance().activateTimer();
        if (SystemProperty.CREATEORDER && m_panelticket.getActiveTicket().getArticlesCount() == 0) {
            deleteTicket();
        } else if (m_PlaceCurrent != null) {
            try {
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket(),
                        m_panelticket.getActiveTicket().getPickupId(), "P" + m_panelticket.getActiveTicket().getId().substring(24), null);
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
            m_PlaceCurrent = null;
        }
        printState();
        m_panelticket.setActiveTicket(null, null);
    }

    /**
     *
     * @return
     */
    public String getTable() {
        String id = null;
        if (m_PlaceCurrent != null) {
            id = m_PlaceCurrent.getId();
        }
        return (id);
    }

    /**
     *
     * @return
     */
    public String getTableName() {
        String tableName = null;
        if (m_PlaceCurrent != null) {
            tableName = m_PlaceCurrent.getName();
        }
        return (tableName);
    }

    public void deleteFullTicket() {
        Boolean remote = false;
        if (m_PlaceCurrent != null) {
            String id = m_PlaceCurrent.getId();
            try {
                for (TicketLineInfo line : m_panelticket.getActiveTicket().getLines()) {
                    Audit.itemRemoved(m_panelticket.getActiveTicket(), line, "Full Ticket Removed");
                    if ("OK".equals(line.getProperty("sendstatus"))) {
                        line.setProperty("sendstatus", "Cancel");
                        remote = true;
                    }
                }

                if (remote) {
                    panelTicket.printTicket("Printer.TicketKitchen", m_panelticket.getActiveTicket(), m_panelticket.getActiveTicket().getPlace(), true);
                    JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.orderCancellation"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
                }

                dlReceipts.deleteSharedTicket(id);
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }

            m_PlaceCurrent.setPeople(false);

            m_PlaceCurrent = null;
        }

        printState();
        m_panelticket.setActiveTicket(null, null);
    }

    /**
     *
     */
    @Override
    public void deleteTicket() {
        if (m_PlaceCurrent != null) {
            String id = m_PlaceCurrent.getId();
            try {
                dlReceipts.deleteSharedTicket(id);
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }

            m_PlaceCurrent.setPeople(false);

            m_PlaceCurrent = null;
        }

        printState();
        m_panelticket.setActiveTicket(null, null);
    }

    /**
     *
     */
    public void changeServer() {

        if (m_ServerCurrent != null) {

//          Show list of Users
//          Allow Users - CurrentUsers select
//          Compare Users
//          If newServer equal.currentUser
//              Msg NoChange
//          else
//              m_ServerCurrent.setPeople(newServer);
//              Msg Changed to NewServer
        }
    }

    /**
     *
     */
    public void loadTickets() {
        AutoRefresh.getInstance().activateTimer();
        Set<String> atickets = new HashSet<>();

        try {
            java.util.List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
            for (SharedTicketInfo ticket : l) {
                atickets.add(ticket.getId());
            }
        } catch (BasicException e) {
            new MessageInf(e).show(this);
        }

        for (Place table : m_aplaces) {
            table.setPeople(atickets.contains(table.getId()));
        }
    }

    private void printState() {
        if (m_PlaceClipboard == null) {
            if (customer == null) {
                // Select a table
                m_jText.setText(null);
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(true);
                    // get the customer details form the database
                    // We have set the option show details on table.  
                    tableName = "<style=font-size:9px;font-weight:bold;><font color =" + SystemProperty.TABLECOLOUR + ">" + place.getName() + "</font></style>";

                    if (SystemProperty.SHOWWAITERDETAILS) {
                        waiterDetails = (restDB.getWaiterNameInTable(place.getName()) == null) ? "" : "<style=font-size:9px;font-weight:bold;><font color ="
                                + SystemProperty.WAITERCOLOUR + ">" + restDB.getWaiterNameInTableById(place.getId()) + "</font></style><br>";
                        place.getButton().setIcon(ICO_OCU_SM);
                    } else {
                        waiterDetails = "";
                    }

                    if (SystemProperty.SHOWCUSTOMERDETAILS) {
                        place.getButton().setIcon((SystemProperty.SHOWWAITERDETAILS && (restDB.getCustomerNameInTable(place.getName()) != null)) ? ICO_WAITER : ICO_OCU_SM);
                        customerDetails = (restDB.getCustomerNameInTable(place.getName()) == null) ? "" : "<style=font-size:9px;font-weight:bold;><font color ="
                                + SystemProperty.CUSTOMERCOLOUR + ">" + restDB.getCustomerNameInTableById(place.getId()) + "</font></style><br>";

                    } else {
                        customerDetails = "";
                    }

                    if (SystemProperty.SHOWWAITERDETAILS || SystemProperty.SHOWCUSTOMERDETAILS) {
                        place.getButton().setText("<html><center>" + customerDetails + waiterDetails + tableName + "</html>");
                    } else {
                        tableName = "<style=font-size:10px;font-weight:bold;><font color =" + SystemProperty.TABLECOLOUR + ">" + place.getName() + "</font></style>";
                        place.getButton().setText("<html><center>" + tableName + "</html>");

                    }
                    if (!place.hasPeople()) {
                        place.getButton().setIcon(ICO_FRE);
                    }                   
                }

                m_jbtnReservations.setEnabled(true);
            } else {
                // receive a customer
                JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("label.restaurantcustomer", new Object[]{customer.getName()}), 16,
                        new Dimension(125, 50), JAlertPane.OK_OPTION);

                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(!place.hasPeople());
                }
                m_jbtnReservations.setEnabled(false);
            }
        } else {
            // Moving or merging the receipt to another table
            m_jText.setText(AppLocal.getIntString("label.restaurantmove", new Object[]{m_PlaceClipboard.getName()}));
            // Enable all empty tables and origin table.
            for (Place place : m_aplaces) {
                place.getButton().setEnabled(true);
            }
            m_jbtnReservations.setEnabled(false);
        }

    }

    private TicketInfo getTicketInfo(Place place) {

        try {
            return dlReceipts.getSharedTicket(place.getId());
        } catch (BasicException e) {
            new MessageInf(e).show(JTicketsBagRestaurantMap.this);
            return null;
        }
    }

    private void setActivePlace(Place place, TicketInfo ticket) {
        //check table status
        if (restDB.getTableLock(place.getId())) {
            if (!overrideActive) {
                JAlertPane.messageBox(JAlertPane.INFORMATION, LocalResource.getString("message.tableopen", restDB.getTableOpenedBy(place.getId())), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
                return;
            }

            if (AppUser.hasPermission("access.lockedtables")) {
                if (JAlertPane.messageBox(JAlertPane.INFORMATION, LocalResource.getString("message.overridelockedtables",
                        restDB.getTableOpenedBy(place.getId())), 16, new Dimension(125, 50), JAlertPane.YES_NO_OPTION) != 5) {
                    btnOverride.setEnabled(true);
                    overrideActive = false;
                    return;
                }
                overrideActive = false;
                btnOverride.setEnabled(true);
            }
        }

        if (ticket.getTicketOwner() == null) {
            ticket.setTicketOwner(m_App.getAppUserView().getUser().getId());
        }

        if (SystemProperty.SHAREDTICKETBYUSER
                && (ticket.getTicketOwner().equalsIgnoreCase(m_App.getAppUserView().getUser().getId()) || AppUser.hasPermission("access.alltickets"))) {
            m_PlaceCurrent = place;
            m_panelticket.setActiveTicket(ticket, m_PlaceCurrent.getName());
            ticket.setPlace(place.getName());
        } else if (!SystemProperty.SHAREDTICKETBYUSER) {
            m_PlaceCurrent = place;
            m_panelticket.setActiveTicket(ticket, m_PlaceCurrent.getName());
            ticket.setPlace(place.getName());
        } else {
            JAlertPane.messageBox(JAlertPane.INFORMATION, LocalResource.getString("message.tableOpenNotAllowed"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
        }
        return;
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (getLayout());
        cl.show(this, view);
    }

    private class MyActionListener implements ActionListener {

        private final Place m_place;

        public MyActionListener(Place place) {
            m_place = place;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
//            if (!restDB.getTableLock(m_place.getId())) {
            if (!actionEnabled) {
                m_place.setDiffX(0);
            }
            // disable the action if edit mode
            if (actionEnabled) {
                //disable table refresh
                AutoRefresh.getInstance().deactivateTimer();

                if (m_PlaceClipboard == null) {

                    if (customer == null) {
                        // check if the sharedticket is the same
                        TicketInfo ticket = getTicketInfo(m_place);

                        // check
                        if (ticket == null && !m_place.hasPeople()) {
                            // Empty table and checked

                            // table occupied
                            ticket = new TicketInfo();

                            try {
//Create a new pickup code because this is a new ticket                            
                                dlReceipts.insertSharedTicket(m_place.getId(), ticket, ticket.getPickupId(), "P" + ticket.getId().substring(24), null);
                            } catch (BasicException e) {
                                new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                            }
                            m_place.setPeople(true);
                            setActivePlace(m_place, ticket);

                        } else if (ticket == null && m_place.hasPeople()) {
                            // The table is now available
                            JAlertPane.messageBox(JAlertPane.INFORMATION, LocalResource.getString("message.tableempty"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
                            m_PlaceClipboard = null;
                            customer = null;
                            loadTickets();
                            printState();
                        } else if (ticket != null && !m_place.hasPeople()) {
                            // The table is now occupied
                            JAlertPane.messageBox(JAlertPane.INFORMATION, LocalResource.getString("message.tablefull"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
                            m_PlaceClipboard = null;
                            customer = null;
                            loadTickets();
                            printState();
                        } else { // both != null
                            // Full table                
                            // m_place.setPeople(true); // already true                           
                            setActivePlace(m_place, ticket);
                        }
                    } else {
                        // receiving customer.
                        // check if the sharedticket is the same
                        TicketInfo ticket = getTicketInfo(m_place);
                        if (ticket == null) {
                            // receive the customer
                            // table occupied
                            ticket = new TicketInfo();

                            try {
                                ticket.setCustomer(customer.getId() == null
                                        ? null
                                        : dlSales.loadCustomerExt(customer.getId()));

                            } catch (BasicException e) {
                                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), e);
                                msg.show(JTicketsBagRestaurantMap.this);
                            }

                            try {
                                dlReceipts.insertSharedTicket(m_place.getId(), ticket, ticket.getPickupId(), "P" + ticket.getId().substring(24), null);
                            } catch (BasicException e) {
                                new MessageInf(e).show(JTicketsBagRestaurantMap.this);
                            }
                            m_place.setPeople(true);
                            m_PlaceClipboard = null;
                            customer = null;

                            setActivePlace(m_place, ticket);
                        } else {
                            // TODO: msg: The table is now full
                            new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JTicketsBagRestaurantMap.this);
                            m_place.setPeople(true);
                            m_place.getButton().setEnabled(false);
                        }
                    }
                } else {
                    // check if the sharedticket is the same
                    TicketInfo ticketclip = getTicketInfo(m_PlaceClipboard);

                    if (ticketclip == null) {
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                        m_PlaceClipboard.setPeople(false);
                        m_PlaceClipboard = null;
                        customer = null;
                        printState();
                    } else // tenemos que copiar el ticket del clipboard
                    {
                        if (m_PlaceClipboard == m_place) {
                            // the same button. Canceling.
                            Place placeclip = m_PlaceClipboard;
                            m_PlaceClipboard = null;
                            customer = null;
                            printState();
                            setActivePlace(placeclip, ticketclip);
                        } else if (!m_place.hasPeople()) {
                            // Moving the receipt to an empty table
                            TicketInfo ticket = getTicketInfo(m_place);
                            if (ticket == null) {
                                try {
                                    dlReceipts.insertSharedTicket(m_place.getId(), ticketclip, ticketclip.getPickupId(), null, null);
                                    m_place.setPeople(true);
                                    dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
                                    m_PlaceClipboard.setPeople(false);
                                } catch (BasicException e) {
                                    new MessageInf(e).show(JTicketsBagRestaurantMap.this);
                                }

                                m_PlaceClipboard = null;
                                customer = null;
                                printState();

                                // No hace falta preguntar si estaba bloqueado porque ya lo estaba antes
                                // activamos el ticket seleccionado
                                setActivePlace(m_place, ticketclip);

                            } else {
                                // Full table
                                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JTicketsBagRestaurantMap.this);
                                m_PlaceClipboard.setPeople(true);
                                printState();
                            }
                        } else {
                            // Merge the lines with the receipt of the table
                            TicketInfo ticket = getTicketInfo(m_place);

                            if (ticket == null) {
                                // The table is now empty
                                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                                m_place.setPeople(false); // fixed                        
                            } else //asks if you want to merge tables
                            {
                                if (JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.mergetablequestion"), 16,
                                        new Dimension(100, 50), JAlertPane.YES_NO_OPTION) == 5) {
                                    // merge lines ticket
                                    try {
                                        dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
                                        m_PlaceClipboard.setPeople(false);
                                        if (ticket.getCustomer() == null) {
                                            ticket.setCustomer(ticketclip.getCustomer());
                                        }
                                        for (TicketLineInfo line : ticketclip.getLines()) {
                                            ticket.addLine(line);
                                        }
                                        dlReceipts.updateSharedTicket(m_place.getId(), ticket, ticket.getPickupId(), "P" + ticket.getId().substring(24), null);
                                        //restDB.clearTableLockByTicket(tableName);
                                    } catch (BasicException e) {
                                        new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                                    }

                                    m_PlaceClipboard = null;
                                    customer = null;
//clear the original table data
                                    restDB.clearCustomerNameInTable(restDB.getTableDetails(ticketclip.getId()));
                                    restDB.clearWaiterNameInTable(restDB.getTableDetails(ticketclip.getId()));
                                    restDB.clearTableMovedFlag(restDB.getTableDetails(ticketclip.getId()));
                                    restDB.clearTicketIdInTable(restDB.getTableDetails(ticketclip.getId()));
                                    printState();
                                    setActivePlace(m_place, ticket);
                                } else {
                                    // Cancel merge operations
                                    Place placeclip = m_PlaceClipboard;
                                    m_PlaceClipboard = null;
                                    customer = null;
                                    printState();
                                    setActivePlace(placeclip, ticketclip);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param btnText
     */
    public void setButtonTextBags(String btnText) {
        m_PlaceClipboard.setButtonText(btnText);
    }

    public void deleteAllShared() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnActionMenu = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        m_jbtnReservations = new javax.swing.JButton();
        m_jbtnRefresh = new javax.swing.JButton();
        m_jText = new javax.swing.JLabel();
        m_btnSetupMode = new javax.swing.JButton();
        m_btnSavePlaces = new javax.swing.JButton();
        btnOverride = new javax.swing.JButton();

        setLayout(new java.awt.CardLayout());

        m_jPanelMap.setFont(KALCFonts.DEFAULTFONT);
        m_jPanelMap.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnActionMenu.setIcon(IconFactory.getIcon("menu.png"));
        btnActionMenu.setFocusPainted(false);
        btnActionMenu.setFocusable(false);
        btnActionMenu.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnActionMenu.setMaximumSize(new java.awt.Dimension(50, 40));
        btnActionMenu.setMinimumSize(new java.awt.Dimension(50, 40));
        btnActionMenu.setPreferredSize(new java.awt.Dimension(52, 40));
        btnActionMenu.setRequestFocusEnabled(false);
        btnActionMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActionMenubtnMenu(evt);
            }
        });
        jPanel2.add(btnActionMenu);
        
        // Add Recall button
        btnRecall = new javax.swing.JButton();
        btnRecall.setIcon(IconFactory.getIcon("recall.png"));
        btnRecall.setText(AppLocal.getIntString("button.recall"));
        btnRecall.setFocusPainted(false);
        btnRecall.setFocusable(false);
        btnRecall.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnRecall.setMaximumSize(new java.awt.Dimension(80, 40));
        btnRecall.setMinimumSize(new java.awt.Dimension(80, 40));
        btnRecall.setPreferredSize(new java.awt.Dimension(82, 40));
        btnRecall.setRequestFocusEnabled(false);
        btnRecall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecallActionPerformed(evt);
            }
        });
        jPanel2.add(btnRecall);
        
        // Add Modify button
        btnModify = new javax.swing.JButton();
        btnModify.setIcon(IconFactory.getIcon("modify.png"));
        btnModify.setText(AppLocal.getIntString("button.modify"));
        btnModify.setFocusPainted(false);
        btnModify.setFocusable(false);
        btnModify.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnModify.setMaximumSize(new java.awt.Dimension(80, 40));
        btnModify.setMinimumSize(new java.awt.Dimension(80, 40));
        btnModify.setPreferredSize(new java.awt.Dimension(82, 40));
        btnModify.setRequestFocusEnabled(false);
        btnModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifyActionPerformed(evt);
            }
        });
        jPanel2.add(btnModify);
        
        // Add Merge button
        btnMerge = new javax.swing.JButton();
        btnMerge.setIcon(IconFactory.getIcon("merge.png"));
        btnMerge.setText(AppLocal.getIntString("button.merge"));
        btnMerge.setFocusPainted(false);
        btnMerge.setFocusable(false);
        btnMerge.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnMerge.setMaximumSize(new java.awt.Dimension(80, 40));
        btnMerge.setMinimumSize(new java.awt.Dimension(80, 40));
        btnMerge.setPreferredSize(new java.awt.Dimension(82, 40));
        btnMerge.setRequestFocusEnabled(false);
        btnMerge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMergeActionPerformed(evt);
            }
        });
        jPanel2.add(btnMerge);

        btnLogout.setIcon(IconFactory.getIcon("logout.png"));
        btnLogout.setFocusPainted(false);
        btnLogout.setFocusable(false);
        btnLogout.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnLogout.setMaximumSize(new java.awt.Dimension(50, 40));
        btnLogout.setMinimumSize(new java.awt.Dimension(50, 40));
        btnLogout.setPreferredSize(new java.awt.Dimension(52, 40));
        btnLogout.setRequestFocusEnabled(false);
        jPanel2.add(btnLogout);

        m_jbtnReservations.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jbtnReservations.setIcon(IconFactory.getIcon("date.png"));
        m_jbtnReservations.setText(AppLocal.getIntString("button.reservations")); // NOI18N
        m_jbtnReservations.setFocusPainted(false);
        m_jbtnReservations.setFocusable(false);
        m_jbtnReservations.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnReservations.setMaximumSize(new java.awt.Dimension(133, 40));
        m_jbtnReservations.setMinimumSize(new java.awt.Dimension(133, 40));
        m_jbtnReservations.setPreferredSize(new java.awt.Dimension(150, 40));
        m_jbtnReservations.setRequestFocusEnabled(false);
        m_jbtnReservations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnReservationsActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnReservations);

        m_jbtnRefresh.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jbtnRefresh.setIcon(IconFactory.getIcon("reload.png"));
        m_jbtnRefresh.setText(AppLocal.getIntString("button.reloadticket")); // NOI18N
        m_jbtnRefresh.setFocusPainted(false);
        m_jbtnRefresh.setFocusable(false);
        m_jbtnRefresh.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnRefresh.setMaximumSize(new java.awt.Dimension(100, 40));
        m_jbtnRefresh.setMinimumSize(new java.awt.Dimension(100, 40));
        m_jbtnRefresh.setPreferredSize(new java.awt.Dimension(150, 40));
        m_jbtnRefresh.setRequestFocusEnabled(false);
        m_jbtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnRefreshActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnRefresh);
        jPanel2.add(m_jText);

        m_btnSetupMode.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_btnSetupMode.setIcon(IconFactory.getIcon("movetable.png"));
        m_btnSetupMode.setText(AppLocal.getIntString("button.layout")); // NOI18N
        m_btnSetupMode.setFocusPainted(false);
        m_btnSetupMode.setFocusable(false);
        m_btnSetupMode.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_btnSetupMode.setMaximumSize(new java.awt.Dimension(170, 40));
        m_btnSetupMode.setMinimumSize(new java.awt.Dimension(170, 40));
        m_btnSetupMode.setPreferredSize(new java.awt.Dimension(170, 40));
        m_btnSetupMode.setRequestFocusEnabled(false);
        m_btnSetupMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnSetupModeActionPerformed(evt);
            }
        });
        jPanel2.add(m_btnSetupMode);

        m_btnSavePlaces.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_btnSavePlaces.setIcon(IconFactory.getIcon("filesave.png"));
        m_btnSavePlaces.setText(AppLocal.getIntString("button.save")); // NOI18N
        m_btnSavePlaces.setToolTipText("");
        m_btnSavePlaces.setFocusPainted(false);
        m_btnSavePlaces.setFocusable(false);
        m_btnSavePlaces.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_btnSavePlaces.setMaximumSize(new java.awt.Dimension(100, 40));
        m_btnSavePlaces.setMinimumSize(new java.awt.Dimension(100, 40));
        m_btnSavePlaces.setPreferredSize(new java.awt.Dimension(100, 40));
        m_btnSavePlaces.setRequestFocusEnabled(false);
        m_btnSavePlaces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnSavePlacesActionPerformed(evt);
            }
        });
        jPanel2.add(m_btnSavePlaces);

        btnOverride.setFont(KALCFonts.DEFAULTBUTTONFONT);
        btnOverride.setIcon(IconFactory.getIcon("filesave.png"));
        btnOverride.setText(AppLocal.getIntString("button.overridelock")); // NOI18N
        btnOverride.setToolTipText("");
        btnOverride.setFocusPainted(false);
        btnOverride.setFocusable(false);
        btnOverride.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnOverride.setMaximumSize(new java.awt.Dimension(160, 40));
        btnOverride.setMinimumSize(new java.awt.Dimension(160, 40));
        btnOverride.setPreferredSize(new java.awt.Dimension(160, 40));
        btnOverride.setRequestFocusEnabled(false);
        btnOverride.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOverrideActionPerformed(evt);
            }
        });
        jPanel2.add(btnOverride);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_START);

        m_jPanelMap.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        add(m_jPanelMap, "map");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnRefreshActionPerformed
        m_PlaceClipboard = null;
        customer = null;
        loadTickets();
        printState();
    }//GEN-LAST:event_m_jbtnRefreshActionPerformed

    private void m_jbtnReservationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnReservationsActionPerformed
        showView("res");
        m_jreservations.activate();
    }//GEN-LAST:event_m_jbtnReservationsActionPerformed

    private void m_btnSetupModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnSetupModeActionPerformed
        if (AppLocal.getIntString("button.layout").equals(m_btnSetupMode.getText())) {
            actionEnabled = false;
            m_btnSavePlaces.setVisible(true);
            m_btnSetupMode.setText(AppLocal.getIntString("button.disablelayout"));

            for (Place pl : m_aplaces) {
                if (transparentButtons) {
                    pl.getButton().setOpaque(true);
                    pl.getButton().setContentAreaFilled(true);
                    pl.getButton().setBorderPainted(true);
                }
            }
        } else {
            actionEnabled = true;
            m_btnSavePlaces.setVisible(false);
            m_btnSetupMode.setText(AppLocal.getIntString("button.layout"));

            for (Place pl : m_aplaces) {
                if (transparentButtons) {
                    pl.getButton().setOpaque(false);
                    pl.getButton().setContentAreaFilled(false);
                    pl.getButton().setBorderPainted(false);
                }
            }
        }
    }//GEN-LAST:event_m_btnSetupModeActionPerformed

    private void m_btnSavePlacesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnSavePlacesActionPerformed
        for (Place pl : m_aplaces) {
            try {
                dlSystem.updatePlaces(pl.getX(), pl.getY(), pl.getId());

            } catch (BasicException ex) {
                Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_m_btnSavePlacesActionPerformed

    private void jbtnLogout() {
        AutoLogoff.getInstance().deactivateTimer();
        deactivate();
        try {
            ((JRootApp) m_App).closeAppView();
        } catch (Exception ex) {
        }
    }

    private void btnActionMenubtnMenu(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActionMenubtnMenu
        principalApp.setMenuVisible(btnActionMenu);
    }//GEN-LAST:event_btnActionMenubtnMenu

    private void btnOverrideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOverrideActionPerformed
        overrideActive = true;
        btnOverride.setEnabled(false);
    }//GEN-LAST:event_btnOverrideActionPerformed

    private void btnRecallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecallActionPerformed
        // TODO: Implement recall ticket functionality
        JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.feature.not.implemented"), 16, 
                new Dimension(125, 50), JAlertPane.OK_OPTION);
    }//GEN-LAST:event_btnRecallActionPerformed

    private void btnModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifyActionPerformed
        // TODO: Implement modify ticket functionality
        JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.feature.not.implemented"), 16, 
                new Dimension(125, 50), JAlertPane.OK_OPTION);
    }//GEN-LAST:event_btnModifyActionPerformed

    private void btnMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMergeActionPerformed
        // TODO: Implement merge ticket functionality (similar to existing move/merge logic)
        JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.feature.not.implemented"), 16, 
                new Dimension(125, 50), JAlertPane.OK_OPTION);
    }//GEN-LAST:event_btnMergeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActionMenu;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnOverride;
    private javax.swing.JButton btnRecall;
    private javax.swing.JButton btnModify;
    private javax.swing.JButton btnMerge;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton m_btnSavePlaces;
    private javax.swing.JButton m_btnSetupMode;
    private javax.swing.JPanel m_jPanelMap;
    private javax.swing.JLabel m_jText;
    private javax.swing.JButton m_jbtnRefresh;
    private javax.swing.JButton m_jbtnReservations;
    // End of variables declaration//GEN-END:variables

}
