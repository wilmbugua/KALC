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
package uk.kalc.commons.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;
import uk.kalc.basic.BasicException;
import uk.kalc.custom.CustomColour;
import uk.kalc.custom.CustomJLabel;
import uk.kalc.custom.ExtendedJButton;
import uk.kalc.data.loader.SerializerReadClass;
import uk.kalc.data.loader.Session;
import uk.kalc.data.loader.SessionFactory;
import uk.kalc.data.loader.StaticSentence;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.KALCFonts;
import uk.kalc.pos.forms.StartPOS;
import uk.kalc.pos.panels.ClosedCashInfo;

/**
 * @author John Lewis
 */
public class ReloadClosedCashDialog extends JDialog {

  
    private final Font font = KALCFonts.DEFAULTFONTBOLD;

    private final JPanel panel = new JPanel(new MigLayout("insets 10 0 0 10 ", "", ""));
    private final JPanel messagePanel = new JPanel(new MigLayout("insets 10 0 0 0 ", "[150:150:150] 5 [300:300:300] 10 [150:150:150] ", ""));
    private final JPanel btnPanel = new JPanel();

    private ExtendedJButton btn = null;
    private ExtendedJButton btnOK;
    private static int CHOICE = -1;

    private JComboBox host = new JComboBox();
    private JComboBox sequence = new JComboBox();

    private JXDatePicker datePicker = new JXDatePicker(new Date());
    private List<ClosedCashInfo> closedData;

    private Set<String> items;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    //entry point for inputbox
    protected ReloadClosedCashDialog() {
        super(new JFrame());
        try {
            reloadPane();
        } catch (BasicException ex) {
        }
        pack();
    }

    protected void reloadPane() throws BasicException {
        Session session = SessionFactory.getSession();

        closedData = new StaticSentence(session,
                "select datestart, dateend, host, hostsequence, money "
                + "from closedcash where dateend is not null "
                + "order by datestart desc",
                null,
                new SerializerReadClass(ClosedCashInfo.class)
        ).list();
      
        setButtonPanel(new Dimension(100, 35));
        btnOK.setEnabled(false);

        //Create the layout
        datePicker.setPreferredSize(new Dimension(300, 26));

        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.closedcashdate"), font), "align right");
        messagePanel.add(datePicker, "wrap");

        items = closedData.stream()
                .filter(c -> dateFormat.format(c.getStartDate()).equals(dateFormat.format(datePicker.getDate())))
                .map(c -> c.getHost())
                .collect(Collectors.toSet());

        host.removeAllItems();
        items.forEach(c -> {
            host.addItem(c);
        });

        items = closedData.stream()
                .filter(c -> dateFormat.format(c.getStartDate()).equals(dateFormat.format(datePicker.getDate()))
                && c.getHost().equalsIgnoreCase(host.getSelectedItem().toString()))
                .map(c -> Integer.toString(c.getHostSequence()))
                .collect(Collectors.toSet());

        sequence.removeAllItems();
        items.forEach(c -> {
            sequence.addItem(c);
        });

        if (sequence.getSelectedItem() != null){
            btnOK.setEnabled(true);
        }
        
        datePicker.addActionListener((ActionEvent e) -> {
            items = closedData.stream()
                    .filter(c -> dateFormat.format(c.getStartDate()).equals(dateFormat.format(datePicker.getDate())))
                    .map(c -> c.getHost())
                    .collect(Collectors.toSet());
            
            host.removeAllItems();
            sequence.removeAllItems();
            items.forEach(c -> {
                host.addItem(c);
            });
        });

        host.addActionListener((ActionEvent e) -> {
            if (host.getItemCount() != 0) {
                items = closedData.stream()
                        .filter(c -> dateFormat.format(c.getStartDate()).equals(dateFormat.format(datePicker.getDate()))
                                && c.getHost().equalsIgnoreCase(host.getSelectedItem().toString()))
                        .map(c -> Integer.toString(c.getHostSequence()))
                        .collect(Collectors.toSet());
                
                sequence.removeAllItems();
                items.forEach(c -> {
                    sequence.addItem(c);
                });
                btnOK.setEnabled(true);
            } else {
                btnOK.setEnabled(false);
            }
        });

        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.closedhost"), font), "align right");
        host.setPreferredSize(new Dimension(300, 25));
        messagePanel.add(host, "wrap");

        messagePanel.add(new CustomJLabel(AppLocal.getIntString("message.closesequence"), font), "align right");
        sequence.setPreferredSize(new Dimension(300, 25));
        messagePanel.add(sequence, "wrap");

        panel.add(messagePanel, "wrap");
        panel.add(btnPanel, "span,  align center, wrap");

        setResizable(false);
        setModal(true);

        panel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Closed Cash Selector");
        getContentPane().add(panel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void setButtonPanel(Dimension dimension) {
        btnOK = new ExtendedJButton(AppLocal.getIntString("button.ok"), JAlertPane.OK);
        btnOK.setPreferredSize(dimension);
        btnOK.setFont(font);
        btnOK.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btnOK);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(font);
        btn.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
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

    protected int getChoice() {
        return CHOICE;
    }

    protected ClosedCashInfo getClosedData() {
        return new ClosedCashInfo(
                datePicker.getDate(),
                (Date) (closedData.stream()
                        .filter(c -> c.getHost().equalsIgnoreCase(host.getSelectedItem().toString())
                        && c.getHostSequence() == Integer.valueOf(sequence.getSelectedItem().toString()))
                        .map(c -> c.getEndDate())
                        .findFirst()).get(),
                host.getSelectedItem().toString(),
                Integer.valueOf(sequence.getSelectedItem().toString()),
                (closedData.stream()
                        .filter(c -> c.getHost().equalsIgnoreCase(host.getSelectedItem().toString())
                        && c.getHostSequence() == Integer.valueOf(sequence.getSelectedItem().toString()))
                        .map(c -> c.getMoneyGuid())
                        .findFirst()).get()
        );

    }

}
