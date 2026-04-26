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


package uk.kalc.pos.loyalty;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.SessionFactory;
import uk.kalc.pos.customers.CustomerInfo;
import uk.kalc.pos.datalogic.DataLogicSales;

/**
 *
 * @author John
 */
public class RegisterLoyaltyCustomer extends JDialog {

    private JPanel panel;
    private JPanel leftPanel;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel btnPanel;

    private JTextField firstName;
    private JTextField lastName;
    private JTextField address1;
    private JTextField address2;
    private JTextField city;
    private JTextField postCode;
    private JTextField email;
    private JCheckBox receiveMarketing;
    private String loyaltyCard;

    public RegisterLoyaltyCustomer() {
        super(new JFrame(), "");
        buildRegisterPanel();
        pack();
    }

    public void showLoyaltyRegister(String loyaltyCard) {
        this.loyaltyCard = loyaltyCard;
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildRegisterPanel() {
        btnPanel = new JPanel();
        btnCancel = new JButton("Continue without Customer Details");
        btnOK = new JButton("Add Customer Details");
        btnOK.setFocusPainted(false);
        btnPanel.add(btnCancel);
        btnPanel.add(btnOK);

        btnCancel.addActionListener((ActionEvent e) -> {
            dispose();
        });

        btnOK.addActionListener((ActionEvent e) -> {
            //create customer record
            StringBuilder name = new StringBuilder();
            name.append(firstName.getText());
            name.append(" ");
            name.append(lastName.getText());
            CustomerInfo customer = new CustomerInfo();
            customer.setCustomerType("loyalty");
            customer.setFirstName(firstName.getText());
            customer.setLastName(lastName.getText());
            customer.setName(name.toString());
            customer.setAddress(address1.getText());
            customer.setAddress2(address2.getText());
            customer.setCity(city.getText());
            customer.setPostal(postCode.getText());
            customer.setEmail(email.getText());
            customer.setMarketable(receiveMarketing.isSelected());
            customer.setLoyaltyCardId(loyaltyCard);
            customer.setActiveCustomer(true);
            customer.setTaxCategory(null);
            customer.setCustomerCard("");
            customer.setNotes("");
            customer.setTaxid("");

            try {
                Object params[] = (Object[]) customer.createParams();
                DataLogicSales dlSales = new DataLogicSales();
                dlSales.init(SessionFactory.getSession());
                dlSales.insertCustomer(params);
            } catch (BasicException ex) {
                Logger.getLogger(RegisterLoyaltyCustomer.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispose();
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
            }
        });

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        leftPanel = new JPanel(new MigLayout("insets 10 25 0 0", "[][250]", "[]2[]2[]2[]2[]2[]2[]2[]25[]"));

        JLabel lblFirstName = new JLabel("First Name :");
        JLabel lblLastName = new JLabel("Last Name :");
        JLabel lblAddress1 = new JLabel("Address line 1 :");
        JLabel lblAddress2 = new JLabel("Address line 2 :");
        JLabel lblCity = new JLabel("City :");
        JLabel lblPostCode = new JLabel("PostCode :");
        JLabel lblEmail = new JLabel("Email Address :");
        JLabel lblReceiveMarketing = new JLabel("Receive Marketing :");
        JLabel lblSpacer = new JLabel();

        firstName = new JTextField();
        lastName = new JTextField();
        address1 = new JTextField();
        address2 = new JTextField();
        city = new JTextField();
        postCode = new JTextField();
        email = new JTextField();
        receiveMarketing = new JCheckBox();

        leftPanel.add(lblFirstName, "align right");
        leftPanel.add(firstName, "width 220, wrap");

        leftPanel.add(lblLastName, "align right");
        leftPanel.add(lastName, "width 220,wrap");

        leftPanel.add(lblAddress1, "align right");
        leftPanel.add(address1, "width 220,wrap");

        leftPanel.add(lblAddress2, "align right");
        leftPanel.add(address2, "width 220,wrap");

        leftPanel.add(lblCity, "align right");
        leftPanel.add(city, "width 220,wrap");

        leftPanel.add(lblPostCode, "align right");
        leftPanel.add(postCode, "width 100,wrap");

        leftPanel.add(lblEmail, "align right");
        leftPanel.add(email, "width 220,wrap");

        leftPanel.add(lblReceiveMarketing, "align right");
        leftPanel.add(receiveMarketing, "wrap");

        leftPanel.add(btnPanel, "span, align right");

        setAlwaysOnTop(true);
        setModal(true);
        getContentPane().add(leftPanel);

    }

}
