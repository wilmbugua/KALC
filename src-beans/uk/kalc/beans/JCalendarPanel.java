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
package uk.kalc.beans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import uk.kalc.format.Formats;
import uk.kalc.globals.IconFactory;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.KALCFonts;

/**
 *
 *
 */
public class JCalendarPanel extends javax.swing.JPanel {

    private static LocaleResources m_resources;

    private static GregorianCalendar m_CalendarHelper = new GregorianCalendar();

    private Date m_date;
    private JButtonDate[] m_ListDates;
    private JLabel[] m_jDays;

    private JButtonDate m_jCurrent;
    private JButtonDate m_jBtnMonthInc;
    private JButtonDate m_jBtnMonthDec;
    private JButtonDate m_jBtnYearInc;
    private JButtonDate m_jBtnYearDec;
    private JButtonDate m_jBtnToday;

    private int iCurrentMonth;

    private Boolean showHistoric = true;

    private DateFormat fmtMonthYear = new SimpleDateFormat("MMMMM yyyy");

    /**
     * Creates new form JCalendarPanel2
     */
    public JCalendarPanel() {
        this(new Date());
    }

    public JCalendarPanel(Boolean showHistoric) {
        this(new Date());
        this.showHistoric = showHistoric;
    }

    /**
     *
     * @param dDate
     */
    public JCalendarPanel(Date dDate) {
        super();

        initComponents();
        initComponents2();

        m_date = dDate;

        renderMonth();
        renderDay();
    }

    /**
     *
     * @param dNewDate
     */
    public void setDate(Date dNewDate) {

        Date dOldDate = m_date;
        m_date = dNewDate;

        renderMonth();
        renderDay();

        firePropertyChange("Date", dOldDate, dNewDate);
    }

    /**
     *
     * @return
     */
    public Date getDate() {
        return m_date;
    }

    public void setEnabled(boolean bValue) {

        super.setEnabled(bValue);

        renderMonth();
        renderDay();
    }

    private void renderMonth() {
        Date today = new Date();
        Date yesterday = new Date(today.getTime() - 3600000L);

        for (int j = 0; j < 7; j++) {
            m_jDays[j].setEnabled(isEnabled());
        }

        for (int i = 0; i < 42; i++) {
            JButtonDate jAux = m_ListDates[i];
            jAux.DateInf = null;
            jAux.setEnabled(false);
            jAux.setText(null);
            jAux.setPreferredSize(new Dimension(56, 30));
            jAux.setFont(KALCFonts.DEFAULTFONT.deriveFont(18f));
            jAux.setForeground((Color) UIManager.getDefaults().get("TextPane.foreground"));
            jAux.setBackground((Color) UIManager.getDefaults().get("TextPane.background"));
            jAux.setBorder(null);
        }

        if (m_date == null) {
            m_jLblMonth.setEnabled(isEnabled());
            m_jLblMonth.setText(null);
        } else {
            m_CalendarHelper.setTime(m_date);
            m_jLblMonth.setEnabled(isEnabled());
            m_jLblMonth.setText(fmtMonthYear.format(m_CalendarHelper.getTime()));

            iCurrentMonth = m_CalendarHelper.get(Calendar.MONTH);
            m_CalendarHelper.set(Calendar.DAY_OF_MONTH, 1);

            while (m_CalendarHelper.get(Calendar.MONTH) == iCurrentMonth) {
                JButtonDate jAux = getLabelByDate(m_CalendarHelper.getTime());
                jAux.DateInf = m_CalendarHelper.getTime();
                if (showHistoric) {
                    jAux.setEnabled(isEnabled());
                } else if (yesterday.before(m_CalendarHelper.getTime())) {
                    jAux.setEnabled(isEnabled());
                }
                jAux.setText(String.valueOf(m_CalendarHelper.get(Calendar.DAY_OF_MONTH)));
                m_CalendarHelper.add(Calendar.DATE, 1);
            }
        }
        m_jCurrent = null;
    }

    
    public String getSelectedDate(){
        return Formats.DATE.formatValue(m_date);
    }
    
    public int getDay() {
        return m_CalendarHelper.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth() {        
        return m_CalendarHelper.get(Calendar.MONTH) + 1;
    }

    public int getYear() {
        return m_CalendarHelper.get(Calendar.YEAR);
    }

    private void renderDay() {

        m_jBtnToday.setEnabled(isEnabled());

        if (m_date == null) {
            m_jBtnMonthDec.setEnabled(false);
            m_jBtnMonthInc.setEnabled(isEnabled());
            m_jBtnYearDec.setEnabled(isEnabled());
            m_jBtnYearInc.setEnabled(isEnabled());
        } else {
            m_CalendarHelper.setTime(m_date);

            m_CalendarHelper.add(Calendar.MONTH, -1);
            m_jBtnMonthDec.DateInf = m_CalendarHelper.getTime();
            m_jBtnMonthDec.setEnabled(isEnabled());
            m_CalendarHelper.add(Calendar.MONTH, 2);
            m_jBtnMonthInc.DateInf = m_CalendarHelper.getTime();
            m_jBtnMonthInc.setEnabled(isEnabled());

            m_CalendarHelper.setTime(m_date);
            m_CalendarHelper.add(Calendar.YEAR, -1);
            m_jBtnYearDec.DateInf = m_CalendarHelper.getTime();
            m_jBtnYearDec.setEnabled(isEnabled());
            m_CalendarHelper.add(Calendar.YEAR, 2);
            m_jBtnYearInc.DateInf = m_CalendarHelper.getTime();
            m_jBtnYearInc.setEnabled(isEnabled());

            m_CalendarHelper.setTime(m_date);
            m_CalendarHelper.add(Calendar.YEAR, -10);
            m_CalendarHelper.add(Calendar.YEAR, 20);

            if (m_jCurrent != null) {
                m_jCurrent.setForeground((Color) UIManager.getDefaults().get("TextPane.foreground"));
                m_jCurrent.setBackground((Color) UIManager.getDefaults().get("TextPane.background"));
                m_jCurrent.setBorder(null);
            }

            JButtonDate jAux = getLabelByDate(m_date);
            jAux.setBackground((Color) UIManager.getDefaults().get("TextPane.selectionBackground"));
            jAux.setForeground((Color) UIManager.getDefaults().get("TextPane.selectionForeground"));
            jAux.setBorder(new LineBorder((Color) UIManager.getDefaults().get("TitledBorder.titleColor")));
            m_jCurrent = jAux;
        }
    }

    private JButtonDate getLabelByDate(Date d) {

        GregorianCalendar oCalRender = new GregorianCalendar();
        oCalRender.setTime(d);
        int iDayOfMonth = oCalRender.get(Calendar.DAY_OF_MONTH);

        oCalRender.set(Calendar.DAY_OF_MONTH, 1);

        int iCol = oCalRender.get(Calendar.DAY_OF_WEEK) - oCalRender.getFirstDayOfWeek();
        if (iCol < 0) {
            iCol += 7;
        }
        return m_ListDates[iCol + iDayOfMonth - 1];
    }

    private class DateClick implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JButtonDate oLbl = (JButtonDate) e.getSource();
            if (oLbl.DateInf != null) {
                setDate(oLbl.DateInf);
            }
        }
    }

    private static class JButtonDate extends JButton {

        public Date DateInf;

        public JButtonDate(ActionListener datehandler) {
            super();
            initComponent();
            addActionListener(datehandler);
        }

        public JButtonDate(String sText, ActionListener datehandler) {
            super(sText);
            initComponent();
            addActionListener(datehandler);
        }

        public JButtonDate(Icon icon, ActionListener datehandler) {
            super(icon);
            initComponent();
            setPreferredSize(new Dimension(40, 30));
            addActionListener(datehandler);
        }

        private void initComponent() {
            DateInf = null;
            setRequestFocusEnabled(false);
            setFocusPainted(false);
            setFocusable(false);
        }
    }

    private void initComponents2() {

        ActionListener dateclick = new DateClick();

        m_jBtnYearDec = new JButtonDate(IconFactory.getIcon("2leftarrow.png"), dateclick);
        m_jBtnMonthDec = new JButtonDate(IconFactory.getIcon("1leftarrow.png"), dateclick);
        m_jBtnToday = new JButtonDate(AppLocal.getIntString("button.today"), dateclick);
        m_jBtnMonthInc = new JButtonDate(IconFactory.getIcon("1rightarrow.png"), dateclick);
        m_jBtnYearInc = new JButtonDate(IconFactory.getIcon("2rightarrow.png"), dateclick);

        m_jBtnToday.DateInf = new Date();
        m_jActions.add(m_jBtnYearDec);
        m_jActions.add(m_jBtnMonthDec);
        m_jBtnToday.setPreferredSize(new Dimension(220, 30));
        m_jBtnToday.setFont(KALCFonts.DEFAULTFONTBOLD);
        m_jActions.add(m_jBtnToday);
        m_jActions.add(m_jBtnMonthInc);
        m_jActions.add(m_jBtnYearInc);

        m_ListDates = new JButtonDate[42];
        for (int i = 0; i < 42; i++) {
            JButtonDate jAux = new JButtonDate(dateclick);
            jAux.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jAux.setText(null);
            jAux.setOpaque(true);
            jAux.setForeground((Color) UIManager.getDefaults().get("TextPane.foreground"));
            jAux.setBackground((Color) UIManager.getDefaults().get("TextPane.background"));
            jAux.setBorder(null);
            m_ListDates[i] = jAux;
            m_jDates.add(jAux);
        }

        m_jDays = new JLabel[7];
        for (int iHead = 0; iHead < 7; iHead++) {
            JLabel JAuxHeader = new JLabel();
            JAuxHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            m_jDays[iHead] = JAuxHeader;
            m_jWeekDays.add(JAuxHeader);
        }

        DateFormat fmtWeekDay = new SimpleDateFormat("E");
        Calendar oCalRender = new GregorianCalendar();
        int iCol;
        for (int j = 0; j < 7; j++) {
            oCalRender.add(Calendar.DATE, 1);
            iCol = oCalRender.get(Calendar.DAY_OF_WEEK) - oCalRender.getFirstDayOfWeek();
            if (iCol < 0) {
                iCol += 7;
            }
            m_jDays[iCol].setText(fmtWeekDay.format(oCalRender.getTime()));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        m_jLblMonth1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jLblMonth = new javax.swing.JLabel();
        m_jMonth = new javax.swing.JPanel();
        m_jWeekDays = new javax.swing.JPanel();
        m_jDates = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        m_jActions = new javax.swing.JPanel();

        setFont(KALCFonts.DEFAULTFONT.deriveFont(14f)
        );
        setLayout(new java.awt.BorderLayout());

        m_jLblMonth1.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(18f)
        );
        jPanel4.add(m_jLblMonth1);

        add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setMaximumSize(new java.awt.Dimension(30, 32767));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        m_jLblMonth.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(18f)
        );
        jPanel2.add(m_jLblMonth);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        m_jMonth.setFont(KALCFonts.DEFAULTFONT.deriveFont(12f)
        );
        m_jMonth.setMinimumSize(new java.awt.Dimension(100, 150));
        m_jMonth.setLayout(new java.awt.BorderLayout());

        m_jWeekDays.setLayout(new java.awt.GridLayout(1, 7));
        m_jMonth.add(m_jWeekDays, java.awt.BorderLayout.NORTH);

        m_jDates.setBackground(javax.swing.UIManager.getDefaults().getColor("TextPane.background"));
        m_jDates.setFont(KALCFonts.DEFAULTFONT.deriveFont(14f));
        m_jDates.setLayout(new java.awt.GridLayout(6, 7));
        m_jMonth.add(m_jDates, java.awt.BorderLayout.CENTER);

        jPanel1.add(m_jMonth, java.awt.BorderLayout.CENTER);

        jPanel3.setFont(KALCFonts.DEFAULTFONT.deriveFont(12f)
        );
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 20));

        m_jActions.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        m_jActions.setMaximumSize(new java.awt.Dimension(32767, 25));
        m_jActions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));
        jPanel3.add(m_jActions);

        jPanel1.add(jPanel3, java.awt.BorderLayout.SOUTH);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel m_jActions;
    private javax.swing.JPanel m_jDates;
    private javax.swing.JLabel m_jLblMonth;
    private javax.swing.JLabel m_jLblMonth1;
    private javax.swing.JPanel m_jMonth;
    private javax.swing.JPanel m_jWeekDays;
    // End of variables declaration//GEN-END:variables

}
