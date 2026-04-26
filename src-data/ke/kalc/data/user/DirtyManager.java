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


package ke.kalc.data.user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DirtyManager implements DocumentListener, ChangeListener, ActionListener, PropertyChangeListener {

    private boolean m_bDirty;
    private boolean m_dataValidated;

    /**
     *
     */
    //  protected Vector listeners = new Vector();
    protected ArrayList listeners = new ArrayList();
    private List synchList = Collections.synchronizedList(listeners);

    /**
     * Creates a new instance of DirtyManager
     */
    public DirtyManager() {
        m_bDirty = false;
        m_dataValidated = false;
    }

    /**
     *
     * @param l
     */
    public void addDirtyListener(DirtyListener l) {
        listeners.add(l);
    }

    /**
     *
     * @param l
     */
    public void removeDirtyListener(DirtyListener l) {
        listeners.remove(l);
    }

    /**
     *
     */
    protected void fireChangedDirty() {
        int j = 0;
        while (synchList.size() > j) {
            DirtyListener l = (DirtyListener) synchList.get(j);
            l.changedDirty(m_bDirty);
            j++;
        }
    }

    /**
     *
     * @param bValue
     */
    public void setDirty(boolean bValue) {
        if (m_bDirty != bValue) {
            m_bDirty = bValue;
            fireChangedDirty();
        }
    }

    
    public boolean isValidated(){
        return m_dataValidated;
    }
    
    public void setValidated(boolean dataValidated){
        m_dataValidated = dataValidated;
    }
    
    /**
     *
     * @return
     */
    public boolean isDirty() {
        return m_bDirty;
    }

    public void changedUpdate(DocumentEvent e) {
        setDirty(true);
    }

    public void insertUpdate(DocumentEvent e) {
        setDirty(true);
    }

    public void removeUpdate(DocumentEvent e) {
        setDirty(true);
    }

    public void stateChanged(ChangeEvent e) {
        setDirty(true);
    }

    public void actionPerformed(ActionEvent e) {
        setDirty(true);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        //   if ("image".equals(evt.getPropertyName())) {
        setDirty(true);
        // }
    }

}
