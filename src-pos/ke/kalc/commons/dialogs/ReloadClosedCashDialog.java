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
package ke.kalc.commons.dialogs;

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
import ke.kalc.basic.BasicException;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.data.loader.SerializerReadClass;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.data.loader.StaticSentence;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.forms.StartPOS;
import ke.kalc.pos.panels.ClosedCashInfo;

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
