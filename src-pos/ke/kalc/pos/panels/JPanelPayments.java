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


package ke.kalc.pos.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.commons.utils.JNumberField;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.data.gui.ComboBoxValModel;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.user.DirtyManager;
import ke.kalc.globals.IconFactory;
import ke.kalc.osk.KeyBoard;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.forms.JRootFrame;
import ke.kalc.pos.printer.IncludeFile;
import ke.kalc.pos.printer.TicketParser;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;


/**
 *
 * @author John
 */
public class JPanelPayments extends JDialog {

    private final AppView app;
    private final DataLogicSales dlSales;
    private final DataLogicSystem dlSystem;
    private final TicketParser ttp;
    private final DirtyManager dirty;

    //Set the fonts to be used
    private final Font lblFont = KALCFonts.DEFAULTFONT;
    private final Font txtFont = KALCFonts.DEFAULTFONT;
    private final Font btnFont = KALCFonts.DEFAULTFONT;

    //Main panels to be used by miglayout
    private final JPanel mainPanel = new JPanel(new MigLayout("insets 10 10 10 10 ", "", ""));
    private final JPanel paymentPanel = new JPanel(new MigLayout("insets 0 0 0 0 ", "[120][300]15[120]", "[][][]"));
    private final JPanel keyboardPanel = new JPanel();

    private JPanel btnPanel;

    private ExtendedJButton btnSave;
    private ExtendedJButton btnReset;
    private ExtendedJButton btn;

    private final JPanel keyBoard;

    private JNumberField inputValue = new JNumberField();

    private final JComboBox paymentReason = new JComboBox();
    private ComboBoxValModel m_ReasonModel;

    private final JTextArea txtNotes = new JTextArea(1, 1);

    public JPanelPayments(AppView app) {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard2(KeyBoard.Layout.QWERTY);

        this.app = app;
        dlSales = (DataLogicSales) app.getBean("ke.kalc.pos.datalogic.DataLogicSales");
        dlSystem = (DataLogicSystem) app.getBean("ke.kalc.pos.datalogic.DataLogicSystem");
        
        
        ttp = new TicketParser(app.getDeviceTicket(), dlSystem);
        dirty = new DirtyManager();
        txtNotes.addPropertyChangeListener("Text", dirty);

        m_ReasonModel = new ComboBoxValModel();
        // m_ReasonModel.add(new JPanelPayments.PaymentReasonPositive("floatin", AppLocal.getIntString("transpayment.floatin")));
        // m_ReasonModel.add(new JPanelPayments.PaymentReasonNegative("floatout", AppLocal.getIntString("transpayment.floatout")));
        m_ReasonModel.add(new JPanelPayments.PaymentReasonPositive("cashin", AppLocal.getIntString("paymentdescription.cashin")));
        m_ReasonModel.add(new JPanelPayments.PaymentReasonNegative("cashout", AppLocal.getIntString("paymentdescription.cashout")));
        paymentReason.setModel(m_ReasonModel);

        showPaymentPanel();
        pack();

        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + 50;
        setLocation(x, y);
        setVisible(true);

    }

    private void showPaymentPanel() {
        setButtonPanel(new Dimension(105, 35));

        txtNotes.setText("");
        txtNotes.setFont(txtFont);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setBorder(BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 1));
        txtNotes.setPreferredSize(new Dimension(550, 100));

        paymentPanel.add(new CustomJLabel(AppLocal.getIntString("label.paymentreason"), lblFont));
        paymentReason.setFont(KALCFonts.DEFAULTFONTBOLD);
        paymentReason.setPreferredSize(new Dimension(150, 25));
        paymentPanel.add(paymentReason, "width 298:298:298, wrap");
        paymentPanel.add(new CustomJLabel(AppLocal.getIntString("label.paymenttotal"), lblFont));

        inputValue = new JNumberField(true);
        inputValue.allowNegativeNumbers(false);
        inputValue.setDecimalPlaces(2);
        inputValue.setText("0.00");
        inputValue.setFont(KALCFonts.DEFAULTFONTBOLD);
        inputValue.setPreferredSize(new Dimension(150, 25));
        paymentPanel.add(inputValue, "wrap");
        inputValue.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            dirty.setDirty(true);
        });

        paymentPanel.add(new CustomJLabel(AppLocal.getIntString("label.notes"), lblFont));
        paymentPanel.add(txtNotes, "span 4, wrap");
        txtNotes.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            dirty.setDirty(true);
        });
        paymentPanel.add(btnPanel, "span 4,  align right, wrap");
        mainPanel.add(paymentPanel, "align center, wrap");
        mainPanel.add(keyboardPanel);

        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Payments In\\Out");
        getContentPane().add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (dirty.isDirty()) {
                    int res = JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.wannasave"), 16, new Dimension(125, 50), JAlertPane.YES_NO_OPTION);
                    if (res == 5) {
                        save();
                    }
                }
            }
        });

        pack();

    }

    private void resetPanel() {
        txtNotes.setText("");
        inputValue.setText("0.00");
        paymentReason.setSelectedItem(null);

    }

    @FunctionalInterface
    public interface SimpleDocumentListener extends DocumentListener {

        void update(DocumentEvent e);

        @Override
        default void insertUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        default void removeUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        default void changedUpdate(DocumentEvent e) {
            update(e);
        }
    }

    private void setButtonPanel(Dimension dimension) {
        btnPanel = new JPanel();

        btnSave = new ExtendedJButton("Save", "filesave.png", 5);
        btnSave.setPreferredSize(dimension);
        btnSave.setFont(btnFont);
        btnSave.setFocusable(false);
        btnSave.addActionListener((ActionEvent e) -> {
            save();
        });
        btnPanel.add(btnSave);

        btnReset = new ExtendedJButton("Reset ", "reload.png", 5);
        btnReset.setPreferredSize(dimension);
        btnReset.setFont(btnFont);
        btnReset.setFocusable(false);
        btnReset.addActionListener((ActionEvent e) -> {
            resetPanel();
        });
        btnPanel.add(btnReset);

        btn = new ExtendedJButton(AppLocal.getIntString("button.exit"), JAlertPane.OK);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            if (dirty.isDirty()) {
                int res = JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.wannasave"), 16, new Dimension(125, 50), JAlertPane.YES_NO_OPTION);
                if (res == 5) {
                    save();
                }
            }
            dispose();
        });
        btnPanel.add(btn);

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

    /**
     *
     * @return
     */
    private void save() {
        Object[] payment = new Object[9];
        payment[0] = UUID.randomUUID().toString();
        payment[1] = app.getActiveCashIndex();
        payment[2] = new Date();
        payment[3] = UUID.randomUUID().toString();
        payment[4] = m_ReasonModel.getSelectedKey();
        PaymentReason reason = (PaymentReason) m_ReasonModel.getSelectedItem();
        Double dtotal = Double.valueOf(inputValue.getText());
        payment[5] = AppLocal.getIntString("paymentdescription." + m_ReasonModel.getSelectedKey());
        payment[6] = reason == null ? dtotal : reason.addSignum(dtotal);
        payment[7] = txtNotes.getText() == null ? "" : txtNotes.getText();
        payment[8] = app.getAppUserView().getUser().getId();
        try {
            dlSales.getPaymentMovementInsert().exec(payment);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelPayments.class.getName()).log(Level.SEVERE, null, ex);
        }

        //printTicket("Printer,CashIn");
        dispose();
    }

    public void printTicket(String sresourcename) {
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
//                if (ticket.getLinesCount() > 0) {
//                    script.put("salesticket", ticket.getLine(0).getProperty("salesticket"));
//                }
//                script.put("ticket", ticket);
//                script.put("ticketpanel", this);
//                script.put("taxincluded", ticket.isTaxInclusive());
//                script.put("customer", customer);
//                script.put("company", new CompanyInfo());
//
//                ttp.printTicket(script.eval(sresource).toString(),null);
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    private static abstract class PaymentReason implements IKeyed {

        private String m_sKey;
        private String m_sText;

        public PaymentReason(String key, String text) {
            m_sKey = key;
            m_sText = text;
        }

        @Override
        public Object getKey() {
            return m_sKey;
        }

        public abstract Double positivize(Double d);

        public abstract Double addSignum(Double d);

        @Override
        public String toString() {
            return m_sText;
        }
    }

    private static class PaymentReasonPositive extends PaymentReason {

        public PaymentReasonPositive(String key, String text) {
            super(key, text);
        }

        @Override
        public Double positivize(Double d) {
            return d;
        }

        @Override
        public Double addSignum(Double d) {
            if (d == null) {
                return null;
            } else if (d.doubleValue() < 0.0) {
                return Double.valueOf(-d.doubleValue());
            } else {
                return d;
            }
        }
    }

    private static class PaymentReasonNegative extends PaymentReason {

        public PaymentReasonNegative(String key, String text) {
            super(key, text);
        }

        @Override
        public Double positivize(Double d) {
            return d == null ? null : Double.valueOf(-d.doubleValue());
        }

        @Override
        public Double addSignum(Double d) {
            if (d == null) {
                return null;
            } else if (d.doubleValue() > 0.0) {
                return Double.valueOf(-d.doubleValue());
            } else {
                return d;
            }
        }
    }
}
