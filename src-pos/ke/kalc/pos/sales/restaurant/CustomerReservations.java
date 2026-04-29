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

import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.NumberFormatter;
import net.miginfocom.swing.MigLayout;
import ke.kalc.basic.BasicException;
import ke.kalc.beans.JCalendarPanel;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.CustomJTextField;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.globals.IconFactory;
import ke.kalc.globals.SystemProperty;
import ke.kalc.osk.KeyBoard;
import ke.kalc.pos.customers.JCustomerFinder;
import ke.kalc.pos.datalogic.DataLogicCustomers;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.forms.JRootFrame;

public class CustomerReservations extends JDialog {

    //Set the fonts to be used
    private final Font lblFont = KALCFonts.DEFAULTFONTBOLD;
    private final Font txtFont = KALCFonts.DEFAULTFONT.deriveFont(18f);
    private final Font btnFont = KALCFonts.DEFAULTFONTBOLD;

    //Main panels to be used by miglayout
    private final JPanel mainPanel = new JPanel(new MigLayout("insets 5 5  5 ", "", ""));
    private final JPanel resevationDetailsPanel = new JPanel(new MigLayout("insets 0 0 0 10 ", "[100::]10[]", "[]"));
    private final JPanel leftPanel = new JPanel(new MigLayout("insets 0 10 0 0 ", "[440::]", "[]"));
    private final JPanel rightPanel = new JPanel(new MigLayout("insets 0 0 0 0 ", "[100::][][25::]", "[][][][][]40"));
    private final JPanel keyboardPanel = new JPanel();

    private JCalendarPanel datePanel2 = new JCalendarPanel(false);
    private TimePicker timePicker;

    private JPanel btnMainPanel;

    private ExtendedJButton btn;
    private JButton btnSave;
    private final JButton customerBtn = new JButton();

    private JFormattedTextField covers;
    private final CustomJTextField customerName = new CustomJTextField(new Dimension(260, 25), txtFont);
    private final JTextArea comments = new JTextArea(1, 1);
    private String id = null;
    private String taxID = null;

    private final JPanel keyBoard;

    private DataLogicCustomers dlCustomers;

    public CustomerReservations(DataLogicCustomers dlCustomers) {
        super(new JFrame());
        this.dlCustomers = dlCustomers;
        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.initialTime = LocalTime.of(15, 30);
        timeSettings.generatePotentialMenuTimes(TimeIncrement.FifteenMinutes, null, null);
        timeSettings.setInitialTimeToNow();
        timePicker = new TimePicker(timeSettings);

        datePanel2.setEnabled(true);

        keyBoard = KeyBoard.getKeyboard2(KeyBoard.Layout.QWERTY);
        reservationPane();
        pack();
        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + 50;
        setLocation(x, y);
    }

    private void reservationPane() {

        NumberFormat integerFieldFormatter = NumberFormat.getIntegerInstance();
        integerFieldFormatter.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(integerFieldFormatter) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.length() == 0) {
                    return 1;
                }
                return super.stringToValue(text);
            }
        };
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(1);
        formatter.setCommitsOnValidEdit(true);

        covers = new JFormattedTextField(formatter);
        covers.setPreferredSize(new Dimension(75, 25));
        covers.setText("2");

        //Add the calender panel
        JPanel calenderPanel = new JPanel();
        calenderPanel.add(datePanel2);
        leftPanel.add(calenderPanel, "wrap");

        comments.setText("");
        comments.setFont(txtFont);
        comments.setLineWrap(true);
        comments.setWrapStyleWord(true);
        comments.setBorder(BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 1));
        comments.setPreferredSize(new Dimension(300, 90));

        customerBtn.setPreferredSize(new Dimension(27, 27));
        customerBtn.setMaximumSize(new Dimension(27, 27));
        customerBtn.setIcon(IconFactory.getResizedIcon("customer_sml.png", new Dimension(25, 25)));
        customerBtn.setFocusPainted(false);

        timePicker.setPreferredSize(new Dimension(100, 28));
        timePicker.setTimeToNow();

        rightPanel.add(new CustomJLabel(AppLocal.getIntString("label.time"), lblFont));
        rightPanel.add(timePicker, "gaptop 0, wrap");
        rightPanel.add(new CustomJLabel(AppLocal.getIntString("label.customer"), lblFont));
        rightPanel.add(customerName);
        rightPanel.add(customerBtn, "wrap");
        rightPanel.add(new CustomJLabel(AppLocal.getIntString("label.covers"), lblFont));
        rightPanel.add(covers, "wrap");
        rightPanel.add(new CustomJLabel(AppLocal.getIntString("label.notes"), lblFont));
        rightPanel.add(comments, "span, wrap");

        resevationDetailsPanel.add(leftPanel);
        resevationDetailsPanel.add(rightPanel, "wrap");

        setButtonPanel(new Dimension(105, 35));

        customerBtn.addActionListener((ActionEvent e) -> {
            this.setVisible(false);
            JCustomerFinder finder = new JCustomerFinder(dlCustomers);
            finder.search(null);
            finder.setVisible(true);
            customerName.setText(finder.getSelectedCustomer() == null
                    ? ""
                    : finder.getSelectedCustomer().getName().toString());
            id = (finder.getSelectedCustomer() == null
                    ? null
                    : finder.getSelectedCustomer().getId().toString());
            taxID = (finder.getSelectedCustomer() == null
                    ? null
                    : finder.getSelectedCustomer().getTaxid().toString());
            this.setVisible(true);
        });
        btnSave.setEnabled(true);

        mainPanel.add(resevationDetailsPanel, "wrap");
        mainPanel.add(btnMainPanel, "span, align right, wrap");
        mainPanel.add(keyboardPanel);

        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(getBorderColour(), 2));
        setTitle("Add Reservation");
        getContentPane().add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setAlwaysOnTop(true);

    }

    private void setButtonPanel(Dimension dimension) {

        btnMainPanel = new JPanel();

        btnSave = new ExtendedJButton(AppLocal.getIntString("button.save"), JAlertPane.OK);
        btnSave.setPreferredSize(dimension);
        btnSave.setFont(btnFont);
        btnSave.setFocusable(false);
        btnSave.addActionListener((ActionEvent e) -> {
            if (customerName.getText().isBlank()) {
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.nocustomername"), 16,
                        new Dimension(125, 50), JAlertPane.OK_OPTION);
                return;
            }
            Date resDate = new Date();
            try {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(datePanel2.getDate());
                    resDate = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(
                            calendar.get(Calendar.YEAR) + "-"
                            + (calendar.get(Calendar.MONTH) + 1) + "-"
                            + calendar.get(Calendar.DAY_OF_MONTH) + " "
                            + timePicker.getTime());
                } catch (ParseException ew) {
                    System.out.println("ParseException occured: " + ew.getMessage());
                }

                Object[] res = new Object[10];
                res[0] = UUID.randomUUID().toString();
                res[1] = new Date();
                res[2] = resDate;
                res[3] = id;
                res[4] = taxID;
                res[5] = customerName.getText();
                res[6] = Integer.valueOf(covers.getText());
                res[7] = false;
                res[8] = comments.getText();

                dlCustomers.getReservationsInsert().exec(res);
                JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.reservationsaved"), 16,
                        new Dimension(125, 50), JAlertPane.OK_OPTION);

                datePanel2.setDate(new Date());
                timePicker.setTimeToNow();
                customerName.setText("");
                covers.setText("2");
                comments.setText("");
                id = null;
                taxID = null;

                dispose();
            } catch (BasicException ex) {
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.unabletosavereservation"), 16,
                        new Dimension(125, 50), JAlertPane.OK_OPTION);
            }

        });
        btnMainPanel.add(btnSave);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            dispose();
        });
        btnMainPanel.add(btn);

        JButton kbButton = new JButton();
        kbButton.setBorderPainted(false);
        kbButton.setOpaque(false);
        kbButton.setPreferredSize(new Dimension(75, 35));
        kbButton.setIcon(IconFactory.getResizedIcon("keyboard.png", new Dimension(75, 35)));
        kbButton.addActionListener((ActionEvent e) -> {
            kbButton.setEnabled(false);
            keyboardPanel.add(keyBoard);
            int x = (this.getX() + (this.getWidth() / 2)) - 455;
            int y = this.getY() + this.getHeight() + 10;
            this.setLocation(x, this.getY());
            this.pack();
        });

        btnMainPanel.add(kbButton);
    }

    private Color getBorderColour() {
        if (SystemProperty.LAF.equalsIgnoreCase("com.jtattoo.plaf.hifi.HiFiLookAndFeel")) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }
}
