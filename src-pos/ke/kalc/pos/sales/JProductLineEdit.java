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


package ke.kalc.pos.sales;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import net.miginfocom.swing.MigLayout;
import ke.kalc.globals.SystemProperty;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.CustomJTextField;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.data.loader.ConnectionFactory;
import ke.kalc.globals.IconFactory;
import ke.kalc.osk.KeyBoard;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.forms.JRootFrame;
import ke.kalc.pos.ticket.TicketLineInfo;

public class JProductLineEdit extends JDialog {

    private TicketLineInfo returnLine;
    private TicketLineInfo m_oLine;
    private String productID;
    private Connection connection;
    private PreparedStatement pstmt;

    //Set the fonts to be used
    private final Font lblFont = KALCFonts.DEFAULTFONT;
    private final Font txtFont = KALCFonts.DEFAULTFONT;
    private final Font btnFont = KALCFonts.DEFAULTFONT;

    private CustomJTextField m_jName = new CustomJTextField(new Dimension(400, 25), txtFont);
    private CustomJTextField m_jUnits = new CustomJTextField(new Dimension(110, 25), txtFont);
    private CustomJTextField m_jPrice = new CustomJTextField(new Dimension(110, 25), txtFont);
    private CustomJTextField m_jPriceTax = new CustomJTextField(new Dimension(110, 25), txtFont);

    //Main panels to be used by miglayout
    private final JPanel mainPanel = new JPanel(new MigLayout("insets 10 10 10 10 ", "", ""));
    private final JPanel productPanel = new JPanel(new MigLayout("insets 0 0 0 0 ", "[120][400]15[200]", "[][][]"));

    private final JPanel btnPanel = new JPanel();
    private ExtendedJButton btn;
    private ExtendedJButton btnUpdate;
    private ExtendedJButton btnOK;

    private AppView app;
    private TicketLineInfo oLine;
    private JPanel keyBoard;

    /**
     * Creates new form JProductLineEdit
     */
    public JProductLineEdit(AppView app, TicketLineInfo oLine) {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard(KeyBoard.Layout.QWERTY);
        this.app = app;
        this.oLine = oLine;
        productID = oLine.getProductID();
        showLineEditor();
        pack();
        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + 80;
        setLocation(x, y);
        setVisible(true);
    }

    public JProductLineEdit() {
        super(new JFrame());
        showLineEditor();
        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);

    }

    public TicketLineInfo getTicketLine() {
        return returnLine;
    }

    private void showLineEditor() {
        setButtonPanel(new Dimension(145, 35));

        m_oLine = new TicketLineInfo(oLine, false);

        m_jName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                m_oLine.setProperty("product.name", m_jName.getText());
                btnUpdate.setEnabled(SystemProperty.PRODUCTUPDATE);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                m_oLine.setProperty("product.name", m_jName.getText());
                btnUpdate.setEnabled(SystemProperty.PRODUCTUPDATE);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        m_jUnits.setName("m_jUnits");
        m_jPrice.setName("m_jPrice");
        m_jPriceTax.setName("m_jPriceTax");

        m_jName.setText(oLine.getProductName());

        m_jName.setEnabled(app.getAppUserView().getUser().hasPermission("ke.kalc.pos.sales.JPanelTicketEdits"));
        m_jPrice.setEnabled(app.getAppUserView().getUser().hasPermission("ke.kalc.pos.sales.JPanelTicketEdits"));
        m_jPriceTax.setEnabled(app.getAppUserView().getUser().hasPermission("ke.kalc.pos.sales.JPanelTicketEdits"));

        m_jUnits.setText(String.format("%.2f", oLine.getMultiply()));

        if (SystemProperty.TAXINCLUDED) {
            m_jPrice.setText(String.format("%.2f", oLine.getSoldPriceExe()));
            m_jPriceTax.setText(String.format("%.2f", oLine.getPrice()));
        } else {
            m_jPrice.setText(String.format("%.2f", oLine.getPrice()));
            m_jPriceTax.setText(String.format("%.2f", oLine.getPriceTax()));
        }
        // ((AbstractDocument) m_jPrice.getDocument()).setDocumentFilter(new checkPrices());
        // ((AbstractDocument) m_jPriceTax.getDocument()).setDocumentFilter(new checkTaxPrices());

        ((AbstractDocument) m_jUnits.getDocument()).setDocumentFilter(new enableNumericWithDecimal(m_jUnits));
        ((AbstractDocument) m_jPrice.getDocument()).setDocumentFilter(new enableNumericWithDecimal(m_jPrice));
        ((AbstractDocument) m_jPriceTax.getDocument()).setDocumentFilter(new enableNumericWithDecimal(m_jPriceTax));

        enableNumericWithDecimalFocus(m_jUnits);
        enableNumericWithDecimalFocus(m_jPrice);
        enableNumericWithDecimalFocus(m_jPriceTax);

        btnUpdate.setEnabled(false);

        productPanel.add(new CustomJLabel(AppLocal.getIntString("label.item"), lblFont));
        productPanel.add(m_jName, "wrap");
        productPanel.add(new CustomJLabel(AppLocal.getIntString("label.units"), lblFont));
        productPanel.add(m_jUnits, "wrap");
        productPanel.add(new CustomJLabel(AppLocal.getIntString("label.price"), lblFont));
        productPanel.add(m_jPrice, "wrap");
        productPanel.add(new CustomJLabel(AppLocal.getIntString("label.pricetax"), lblFont));
        productPanel.add(m_jPriceTax, "wrap");
        productPanel.add(btnPanel, "span 3,  align right, wrap");

        mainPanel.add(productPanel, "align center, wrap");

        setResizable(false);
        setModal(true);
        mainPanel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Product Editor");
        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(btnOK);
    }

    class enableNumericWithDecimal extends DocumentFilter {

        private JTextField field;

        public enableNumericWithDecimal(JTextField field) {
            super();
            this.field = field;
        }

        private String regex = "^(?:\\d{0,6}(?:[\\.\\,]\\d{0,2})?)?$";

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offset, text);
            if (sb.toString().matches(regex)) {
                super.replace(fb, offset, length, text, attrs);
                updatePrices(field);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            if (offset != -1) {
                super.remove(fb, offset, length);
                updatePrices(field);
            } else {
                super.remove(fb, 0, length);
            }

        }
    }

    private void enableNumericWithDecimalFocus(JTextField field) {
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                String value = (field.getText().isEmpty() && field.getName().equals("m_jUnits")) ? "1.00" : "0.00";
                value = (field.getText().isEmpty()) ? value : String.format("%.2f", Double.valueOf(field.getText()));
                try {
                    field.getDocument().remove(-1, field.getDocument().getLength());
                    field.getDocument().insertString(0, value, null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(JProductLineEdit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );

    }

    private void updatePrices(JTextField field) {
        Double value;
        btnUpdate.setEnabled(SystemProperty.PRODUCTUPDATE);
        switch (field.getName()) {
            case "m_jUnits":
                m_oLine.setMultiply((m_jUnits.getText().isEmpty()) ? 1.00 : Double.valueOf(m_jUnits.getText()));
                break;
            case "m_jPrice":
                value = (m_jPrice.getText().isEmpty()) ? 0.00 : Double.valueOf(m_jPrice.getText());
                try {
                    m_jPriceTax.getDocument().remove(-1, m_jPriceTax.getDocument().getLength());
                    m_jPriceTax.getDocument().insertString(0, String.format("%.2f", value * (1 + m_oLine.getTaxRate())), null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(JProductLineEdit.class.getName()).log(Level.SEVERE, null, ex);
                }
                m_oLine.setChangedPrice(Double.valueOf(m_jPrice.getText()), Double.valueOf(m_jPriceTax.getText()));
                break;
            case "m_jPriceTax":
                value = (m_jPriceTax.getText().isEmpty()) ? 0.00 : Double.valueOf(m_jPriceTax.getText());
                try {
                    m_jPrice.getDocument().remove(-1, m_jPrice.getDocument().getLength());
                    m_jPrice.getDocument().insertString(0, (SystemProperty.TAXINCLUDED) ? String.format("%.2f", m_oLine.getSoldPriceExe())
                            : String.format("%.2f", value / (1.0 + m_oLine.getTaxRate())), null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(JProductLineEdit.class.getName()).log(Level.SEVERE, null, ex);
                }
                m_oLine.setChangedPrice(Double.valueOf(m_jPrice.getText()), Double.valueOf(m_jPriceTax.getText()));
                break;
        }
    }

    private void setButtonPanel(Dimension dimension) {

        btnUpdate = new ExtendedJButton(AppLocal.getIntString("button.updateProduct"), 0);
        btnUpdate.setPreferredSize(dimension);
        btnUpdate.setFont(btnFont);
        btnUpdate.setFocusable(false);
        btnUpdate.setVisible(productID.equals("DefaultProduct") ? false : app.getAppUserView().getUser().hasPermission("db.updatedatabase"));
        btnUpdate.addActionListener((ActionEvent e) -> {
            try {
                connection = ConnectionFactory.getInstance().getConnection();
                pstmt = connection.prepareStatement("update products set pricesell = ?, pricesellinc =? where id = ?");
                pstmt.setDouble(1, Double.valueOf(m_jPrice.getText()));
                pstmt.setDouble(2, Double.valueOf(m_jPriceTax.getText()));
                pstmt.setString(3, productID);
                pstmt.executeUpdate();
                m_oLine.setUpdated(true);
                m_oLine.reflectChangedPrice(Double.valueOf(m_jPrice.getText()), Double.valueOf(m_jPriceTax.getText()));
                btnUpdate.setEnabled(false);
            } catch (SQLException ex) {
                Logger.getLogger(JProductLineEdit.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnPanel.add(btnUpdate);

        btnOK = new ExtendedJButton(AppLocal.getIntString("button.ok"), JAlertPane.OK);
        btnOK.setPreferredSize(dimension);
        btnOK.setFont(btnFont);
        btnOK.setFocusable(false);
        btnOK.addActionListener((ActionEvent e) -> {
            returnLine = m_oLine;
            dispose();

        });
        btnPanel.add(btnOK);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            returnLine = null;
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
            mainPanel.add(keyBoard);
            int x = (this.getX() + (this.getWidth() / 2)) - 400;
            int y = this.getY() + this.getHeight() + 10;
            this.setLocation(x, this.getY());
            this.pack();
        });
        btnPanel.add(kbButton);

    }

}
