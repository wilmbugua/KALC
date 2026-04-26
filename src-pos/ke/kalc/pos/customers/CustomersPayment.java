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
package ke.kalc.pos.customers;

import ke.kalc.pos.datalogic.DataLogicCustomers;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import net.miginfocom.swing.MigLayout;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.commons.dialogs.Receipt;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.CustomJTextField;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.data.user.DirtyManager;
import ke.kalc.format.Formats;
import ke.kalc.globals.CompanyInfo;
import ke.kalc.globals.IconFactory;
import ke.kalc.globals.SystemProperty;
import static ke.kalc.globals.SystemProperty.USERCOUNTRY;
import static ke.kalc.globals.SystemProperty.USERLANGUAGE;
import ke.kalc.osk.KeyBoard;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.forms.JRootFrame;
import ke.kalc.pos.payment.JPaymentSelect;
import ke.kalc.pos.payment.JPaymentSelectCustomer;
import ke.kalc.pos.payment.PaymentInfo;
import ke.kalc.pos.payment.PaymentInfoTicket;
import ke.kalc.pos.printer.IncludeFile;
import ke.kalc.pos.printer.TicketParser;
import ke.kalc.pos.printer.TicketPrinterException;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.pos.ticket.TicketType;

public class CustomersPayment extends JDialog {

    private final AppView app;
    private final DataLogicCustomers dlCustomers;
    private final DataLogicSales dlSales;
    private final DataLogicSystem dlSystem;
    private final TicketParser ttp;
    private final JPaymentSelect paymentdialog;
    private final DirtyManager dirty;
    private CustomerInfoExt customerext;
    private final JCustomerFinder finder;

    //Set the fonts to be used
    private final Font lblFont = KALCFonts.DEFAULTFONTBOLD;
    private final Font txtFont = KALCFonts.DEFAULTFONT.deriveFont(18f);
    private final Font btnFont = KALCFonts.DEFAULTFONTBOLD;

    //Main panels to be used by miglayout
    private final JPanel mainPanel = new JPanel(new MigLayout("insets 10 10 10 10 ", "", ""));
    private final JPanel customerPanel = new JPanel(new MigLayout("insets 0 0 0 0 ", "[120][300]15[180]", "[][][]"));
    private final JPanel keyboardPanel = new JPanel();

    private JPanel actionButtonPanel;
    private JPanel btnPanel;

    private CustomJLabel accountState;

    private ExtendedJButton btnCustomer;
    private ExtendedJButton btnSave;
    private ExtendedJButton btnPay;
    private ExtendedJButton btnReset;
    private ExtendedJButton btn;

    private final JPanel keyBoard;

    private final CustomJTextField txtTaxId = new CustomJTextField(new Dimension(200, 25), txtFont);
    private final CustomJTextField txtName = new CustomJTextField(new Dimension(200, 25), txtFont);
    private final CustomJTextField txtCard = new CustomJTextField(new Dimension(200, 25), txtFont);
    private final CustomJTextField txtMaxdebt = new CustomJTextField(new Dimension(200, 25), txtFont);
    private final CustomJTextField txtDiscount = new CustomJTextField(new Dimension(150, 25), txtFont);
    private final CustomJTextField txtCurdebt = new CustomJTextField(new Dimension(200, 25), txtFont);
    private final CustomJTextField txtCurdate = new CustomJTextField(new Dimension(200, 25), txtFont);
    private final JTextArea txtNotes = new JTextArea(1, 1);

    private DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
    private DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
    //   private char sep = symbols.getDecimalSeparator();
    private Locale currentLocale = new Locale(USERLANGUAGE, USERCOUNTRY);

    public CustomersPayment(AppView app) {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard2(KeyBoard.Layout.QWERTY);

        this.app = app;
        dlCustomers = (DataLogicCustomers) app.getBean("ke.kalc.pos.datalogic.DataLogicCustomers");
        dlSales = (DataLogicSales) app.getBean("ke.kalc.pos.datalogic.DataLogicSales");
        dlSystem = (DataLogicSystem) app.getBean("ke.kalc.pos.datalogic.DataLogicSystem");
        ttp = new TicketParser(app.getDeviceTicket(), dlSystem);
        dirty = new DirtyManager();
        txtNotes.addPropertyChangeListener("Text", dirty);
        finder = new JCustomerFinder(dlCustomers);
        paymentdialog = JPaymentSelectCustomer.getDialog(JRootFrame.PARENTFRAME);
        paymentdialog.init(app);

        showCustomerPayment();
        pack();

        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + 50;
        setLocation(x, y);
        setVisible(true);

    }

    private void showCustomerPayment() {
        setButtonPanel(new Dimension(105, 35));
        setActionButtonsPanel(new Dimension(150, 35));

        txtNotes.setText("");
        txtNotes.setFont(txtFont);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setBorder(BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 1));
        txtNotes.setPreferredSize(new Dimension(550, 100));

        mainPanel.add(actionButtonPanel, "span 4,  align left, wrap");
        mainPanel.add(new JSeparator(), "span 4, wrap");

        customerPanel.add(new CustomJLabel(AppLocal.getIntString("label.taxid"), lblFont));
        customerPanel.add(txtTaxId);
        txtTaxId.setEditable(false);
        customerPanel.add(new CustomJLabel(AppLocal.getIntString("label.maxdebt"), lblFont));
        customerPanel.add(txtMaxdebt, "wrap");
        txtMaxdebt.setEditable(false);
        customerPanel.add(new CustomJLabel(AppLocal.getIntString("label.name"), lblFont));
        customerPanel.add(txtName);
        txtName.setEditable(false);
        accountState = new CustomJLabel(AppLocal.getIntString("label.customerDebt"), lblFont);
        customerPanel.add(accountState);
        customerPanel.add(txtCurdebt, "wrap");
        txtCurdebt.setEditable(false);
        customerPanel.add(new CustomJLabel(AppLocal.getIntString("label.card"), lblFont));
        customerPanel.add(txtCard);
        txtCard.setEditable(false);
        customerPanel.add(new CustomJLabel(AppLocal.getIntString("label.curdate"), lblFont));
        customerPanel.add(txtCurdate, "wrap");
        txtCurdate.setEditable(false);
        customerPanel.add(new CustomJLabel(AppLocal.getIntString("label.discount"), lblFont));
        customerPanel.add(txtDiscount, "wrap");
        txtDiscount.setEditable(false);
        customerPanel.add(new CustomJLabel(AppLocal.getIntString("label.notes"), lblFont));
        customerPanel.add(txtNotes, "span 4, wrap");
        txtNotes.setEditable(false);

        customerPanel.add(btnPanel, "span 4,  align right, wrap");
        mainPanel.add(customerPanel, "align center, wrap");
        mainPanel.add(keyboardPanel);

        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Customer Payment");
        getContentPane().add(mainPanel);

        pack();

    }

    private void setActionButtonsPanel(Dimension dimension) {
        actionButtonPanel = new JPanel();

        btnCustomer = new ExtendedJButton("Customer", "customer_sml.png", 6);
        btnCustomer.setPreferredSize(dimension);
        btnCustomer.setFont(btnFont);
        btnCustomer.setFocusable(false);
        btnCustomer.addActionListener((ActionEvent e) -> {
            getCustomer();
        });
        actionButtonPanel.add(btnCustomer);

        btnSave = new ExtendedJButton("Save", "filesave.png", 5);
        btnSave.setPreferredSize(dimension);
        btnSave.setFont(btnFont);
        btnSave.setFocusable(false);
        btnSave.addActionListener((ActionEvent e) -> {
            save();
        });
        actionButtonPanel.add(btnSave);

        btnPay = new ExtendedJButton("Make Payment", "pay.png", 5);
        btnPay.setPreferredSize(dimension);
        btnPay.setFont(btnFont);
        btnPay.setFocusable(false);
        btnPay.addActionListener((ActionEvent e) -> {
            payAction();
        });
        actionButtonPanel.add(btnPay);

        btnReset = new ExtendedJButton("Reset", "reload.png", 5);
        btnReset.setPreferredSize(dimension);
        btnReset.setFont(btnFont);
        btnReset.setFocusable(false);
        btnReset.addActionListener((ActionEvent e) -> {
            resetCustomer();
        });
        actionButtonPanel.add(btnReset);

        btnSave.setEnabled(false);
        btnPay.setEnabled(false);

        JButton kbButton = new JButton();
        kbButton.setBorderPainted(false);
        kbButton.setOpaque(false);
        kbButton.setPreferredSize(new Dimension(75, 35));
        kbButton.setIcon(IconFactory.getResizedIcon("keyboard.png", new Dimension(75, 35)));
        kbButton.addActionListener((ActionEvent e) -> {
            kbButton.setEnabled(false);
            keyboardPanel.add(keyBoard);
            int x = (this.getX() + (this.getWidth() / 2)) - 411;
            this.setLocation(x, this.getY());
            this.pack();
        });
        btnPanel.add(kbButton);

    }

    private void setButtonPanel(Dimension dimension) {
        btnPanel = new JPanel();

        btn = new ExtendedJButton(AppLocal.getIntString("button.exit"), JAlertPane.OK);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            dispose();
        });
        btnPanel.add(btn);
    }

    private void setTextAreaParameters(JTextArea textArea) {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setDisabledTextColor(CustomColour.getEnabledColour(textArea.getBackground()));
        textArea.setEnabled(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setRequestFocusEnabled(false);
    }

    public boolean deactivate() {
        if (true) {
            int res = JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.wannasave"), 16, new Dimension(125, 50), JAlertPane.YES_NO_CANCEL_OPTION);
            if (res == 5) {
                save();
                return true;
            } else {
                return res == 6;
            }
        } else {
            return true;
        }
    }

    /**
     *
     * @return
     */
    private void editCustomer(CustomerInfoExt customer) {

        customerext = customer;

        txtTaxId.setText(customer.getTaxid());
        txtName.setText(customer.getName());
        txtCard.setText(customer.getCustomerCard());
        txtNotes.setText(customer.getNotes());
        txtMaxdebt.setText(Formats.CURRENCY.formatValue(customer.getMaxDebt()));
        txtCurdebt.setText(Formats.CURRENCY.formatValue(Math.abs(customer.getCurrentDebt())));
        txtCurdate.setText(Formats.DATE.formatValue(customer.getCurDate()));
        txtDiscount.setText(Formats.PERCENT.formatValue(customer.getCustomerDiscount()));
        txtNotes.setEditable(true);

        accountState.setText((customer.getCurrentDebt() < 0) ? AppLocal.getIntString("label.customerCredit") : AppLocal.getIntString("label.customerDebt"));

        btnSave.setEnabled(true);
        btnPay.setEnabled(true);

        btnPay.setEnabled(customer.getCurrentDebt() != null && customer.getCurrentDebt() > 0.0);
    }

    private void resetCustomer() {
        customerext = null;
        txtTaxId.setText(null);
        txtName.setText(null);
        txtCard.setText(null);
        txtMaxdebt.setText(null);
        txtCurdebt.setText(null);
        txtCurdate.setText(null);
        txtDiscount.setText(null);
        txtNotes.setText(null);
        txtNotes.setEditable(false);

        btnSave.setEnabled(false);
        btnPay.setEnabled(false);

    }

    private void getCustomer() {
        this.setVisible(false);
        finder.search(null);
        finder.setVisible(true);

        CustomerInfo customer = finder.getSelectedCustomer();
        if (customer != null) {
            try {
                CustomerInfoExt c = dlSales.loadCustomerExt(customer.getId());
                if (c == null) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"));
                    msg.show(this);
                } else {
                    editCustomer(c);
                }
            } catch (BasicException ex) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), ex);
                msg.show(this);
            }
        }
        this.setVisible(true);
    }

    private void save() {
        customerext.setNotes(txtNotes.getText());
        try {
            dlCustomers.updateCustomerExt(customerext);
            editCustomer(customerext);
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosave"), e);
            msg.show(this);
        }
    }

    private void payAction() {
        //Get the amount being paid      
        this.setVisible(false);

        df.setDecimalFormatSymbols(new DecimalFormatSymbols(currentLocale));
        String num = df.format(Math.abs(customerext.getCurrentDebt()));

        Object[] result = JAlertPane.inputBox(new Dimension(300, 100), "Payment Amount ", 16, new Dimension(75, 30),
                JAlertPane.OK_CANCEL_OPTION, num, true);

        NumberFormat format = NumberFormat.getInstance(currentLocale);
        Number number;
        try {
            number = format.parse((String) result[1]);
        } catch (ParseException ex) {
            number = 0.00;
        }
        double d = number.doubleValue();

        if ((int) result[0] == 4 || d == 0.00) {
            this.setVisible(true);
            return;
        }

        if (d > customerext.getCurrentDebt()) {
            System.out.println("Bad amount");
        }

        if (paymentdialog.showDialog((d > customerext.getCurrentDebt())
                ? customerext.getCurrentDebt() : d, null, null)) {

            // Save the ticket
            TicketInfo ticket = new TicketInfo();
            ticket.setTicketType(TicketType.PAYMENT);

            List<PaymentInfo> payments = paymentdialog.getSelectedPayments();

            double total = 0.0;
            for (PaymentInfo p : payments) {
                total += p.getTotal();
            }

            payments.add(new PaymentInfoTicket(-total, "debtpaid"));

            ticket.setPayments(payments);

            ticket.setUser(app.getAppUserView().getUser().getUserInfo());
            ticket.setActiveCash(app.getActiveCashIndex());
            ticket.setDate(new Date());
            ticket.setCustomer(customerext);
            

            try {
                dlSales.saveTicket(ticket, app.getInventoryLocation(), null, null, null);
            } catch (BasicException eData) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), eData);
                msg.show(this);
            }

            // reload customer
            CustomerInfoExt c;
            try {
                c = dlSales.loadCustomerExt(customerext.getId());
                if (c == null) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"));
                    msg.show(this);
                } else {
                    editCustomer(c);
                }
            } catch (BasicException ex) {
                c = null;
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), ex);
                msg.show(this);
            }

            boolean isCash = false;
            String change = "";
            Double tendered = 0.00;

            for (PaymentInfo p : ticket.getPayments()) {
                tendered = tendered + p.getTendered();
                if ("cash".equals(p.getName())) {
                    isCash = true;
                    if (p.getTotal() < 0.00) {
                        change = Formats.CURRENCY.formatValue(Double.valueOf(p.getTotal()));
                    } else {
                        change = Formats.CURRENCY.formatValue(Double.valueOf(p.getChange()));
                    }
                }
            }
            
            printTicket("Display.CustomerTotal", ticket, c);
            
            paymentdialog.setReceiptRequired(Receipt.required(Formats.CURRENCY.formatValue(d),
                    Formats.CURRENCY.formatValue(tendered), change, SystemProperty.RECEIPTPRINTOFF));
                        
            if (paymentdialog.printReceipt()) {
                printTicket("Printer.CustomerPaid", ticket, c);
            } 
            
            printTicket("display.Message", ticket, c);
            
        }

        this.setVisible(true);
    }

    public void printTicket(String sresourcename, TicketInfo ticket, CustomerInfoExt customer) {
        //StringBuilder source = new StringBuilder(dlSystem.getResourceAsXML(sresourcename));
        String source = dlSystem.getResourceAsXML(sresourcename);
        String sresource;
        IncludeFile incFile = new IncludeFile(source, dlSystem);
        if (source.isEmpty()) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            sresource = incFile.processInclude();
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                if (ticket.getLinesCount() > 0) {
                    script.put("salesticket", ticket.getLine(0).getProperty("salesticket"));
                }               
                script.put("ticket", ticket);
                script.put("ticketpanel", this);
                script.put("taxincluded", ticket.isTaxInclusive());
                script.put("customer", customer);
                script.put("company", new CompanyInfo());

                ttp.printTicket(script.eval(sresource).toString(), ticket);
            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }
}
