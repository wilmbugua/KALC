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


package uk.kalc.pos.panels;

import java.awt.Component;
import java.util.Date;
import java.util.UUID;
import uk.kalc.basic.BasicException;
import uk.kalc.data.gui.ComboBoxValModel;
import uk.kalc.data.loader.IKeyed;
import uk.kalc.data.user.DirtyManager;
import uk.kalc.data.user.EditorRecord;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.AppView;
import uk.kalc.pos.forms.KALCFonts;


public final class PaymentsEditor extends javax.swing.JPanel implements EditorRecord {
    
    private final ComboBoxValModel m_ReasonModel;
    
    private String m_sId;
    private String m_sPaymentId;
    private Date datenew;
   
    private final AppView m_App;
    private String m_sNotes;
    
    /** Creates new form JPanelPayments
     * @param oApp
     * @param dirty */
    public PaymentsEditor(AppView oApp, DirtyManager dirty) {
        
        m_App = oApp;
        
        initComponents();
       
        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(new PaymentReasonPositive("cashin", AppLocal.getIntString("paymentdescription.cashin")));
        m_ReasonModel.add(new PaymentReasonNegative("cashout", AppLocal.getIntString("paymentdescription.cashout")));              
        m_jreason.setModel(m_ReasonModel);
        
        jTotal.addEditorKeys(m_jKeys);
        
        m_jreason.addActionListener(dirty);
        jTotal.addPropertyChangeListener("Text", dirty);
        m_jNotes.addPropertyChangeListener("Text", dirty);
        m_jNotes.addEditorKeys(m_jKeys);        
        
        writeValueEOF();
    }
    
    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_sId = null;
        m_sPaymentId = null;
        datenew = null;
        setReasonTotal(null, null);
        m_jreason.setEnabled(false);
        jTotal.setEnabled(false);
        m_sNotes = null;
        m_jNotes.setEnabled(false);

    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {

        m_sId = null;
        m_sPaymentId = null;
        datenew = null;
        setReasonTotal("cashin", null);
        m_jreason.setEnabled(true);
        jTotal.setEnabled(true);   
        jTotal.activate();
        m_sNotes = null;
        m_jNotes.setEnabled(true);
        m_jNotes.setText(m_sNotes);
    }
    
    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] payment = (Object[]) value;
        m_sId = (String) payment[0];
        datenew = (Date) payment[2];
        m_sPaymentId = (String) payment[3];
        setReasonTotal(payment[4], payment[5]);
        m_jreason.setEnabled(false);
        jTotal.setEnabled(false);
        m_sNotes = (String) payment[6];
        m_jNotes.setEnabled(false);
    }
    
    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] payment = (Object[]) value;
        m_sId = (String) payment[0];
        datenew = (Date) payment[2];
        m_sPaymentId = (String) payment[3];
        setReasonTotal(payment[4], payment[5]);
        m_jreason.setEnabled(false);
        jTotal.setEnabled(false);
        jTotal.activate();
        m_sNotes = (String) payment[6];
        m_jNotes.setEnabled(false);
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        Object[] payment = new Object[7];
        payment[0] = m_sId == null ? UUID.randomUUID().toString() : m_sId;
        payment[1] = m_App.getActiveCashIndex();
        payment[2] = datenew == null ? new Date() : datenew;
        payment[3] = m_sPaymentId == null ? UUID.randomUUID().toString() : m_sPaymentId;
        payment[4] = m_ReasonModel.getSelectedKey();
        PaymentReason reason = (PaymentReason) m_ReasonModel.getSelectedItem();
        Double dtotal = jTotal.getDoubleValue();
        payment[5] = reason == null ? dtotal : reason.addSignum(dtotal);
        String snotes = "";
        m_sNotes = m_jNotes.getText();
        payment[6] = m_sNotes == null ? snotes : m_sNotes;
        return payment;
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
     */
    @Override
    public void refresh() {
    }  
    
    private void setReasonTotal(Object reasonfield, Object totalfield) {
        
        m_ReasonModel.setSelectedKey(reasonfield);
             
        PaymentReason reason = (PaymentReason) m_ReasonModel.getSelectedItem();     
        
        if (reason == null) {
            jTotal.setDoubleValue((Double) totalfield);
        } else {
            jTotal.setDoubleValue(reason.positivize((Double) totalfield));
        }  
    }

    @Override
    public void refreshGuid(String siteGuid) {     
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
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        m_jreason = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTotal = new uk.kalc.editor.JEditorCurrency();
        m_jNotes = new uk.kalc.editor.JEditorString();
        jPanel2 = new javax.swing.JPanel();
        m_jKeys = new uk.kalc.editor.JEditorKeys();

        setLayout(new java.awt.BorderLayout());

        jLabel5.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        jLabel5.setText(AppLocal.getIntString("label.paymentreason")); // NOI18N

        m_jreason.setFont(KALCFonts.DEFAULTFONT);
        m_jreason.setFocusable(false);

        jLabel3.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        jLabel3.setText(AppLocal.getIntString("label.paymenttotal")); // NOI18N

        jTotal.setFont(KALCFonts.DEFAULTFONT);

        m_jNotes.setFont(KALCFonts.DEFAULTFONT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_jNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .addComponent(m_jreason, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jreason, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_jNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(266, Short.MAX_VALUE))
        );

        add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        m_jKeys.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                m_jKeysPropertyChange(evt);
            }
        });
        jPanel2.add(m_jKeys, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jKeysPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_m_jKeysPropertyChange

    }//GEN-LAST:event_m_jKeysPropertyChange
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private uk.kalc.editor.JEditorCurrency jTotal;
    private uk.kalc.editor.JEditorKeys m_jKeys;
    private uk.kalc.editor.JEditorString m_jNotes;
    private javax.swing.JComboBox m_jreason;
    // End of variables declaration//GEN-END:variables
    
}
