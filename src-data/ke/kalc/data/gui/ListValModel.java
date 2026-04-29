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


package ke.kalc.data.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import ke.kalc.data.loader.IKeyGetter;
import ke.kalc.data.loader.KeyGetterBuilder;


public class ListValModel extends AbstractListModel implements ListModel {  
   
    private List m_aData;
    private IKeyGetter m_keygetter;
    
    /** Creates a new instance of ComboBoxValModel
     * @param aData
     * @param keygetter */
    public ListValModel(List aData, IKeyGetter keygetter) {
        m_aData = aData;
        m_keygetter = keygetter;
    }

    /**
     *
     * @param aData
     */
    public ListValModel(List aData) {
        this(aData, KeyGetterBuilder.INSTANCE);
    }

    /**
     *
     * @param keygetter
     */
    public ListValModel(IKeyGetter keygetter) {
        this(new ArrayList(), keygetter);
    }

    /**
     *
     */
    public ListValModel() {
        this(new ArrayList(), KeyGetterBuilder.INSTANCE);
    }
    
    /**
     *
     * @param c
     */
    public void add(Object c) {
        m_aData.add(c);
    }

    /**
     *
     * @param c
     */
    public void del(Object c) {
        m_aData.remove(c);
    }

    /**
     *
     * @param index
     * @param c
     */
    public void add(int index, Object c) {
        m_aData.add(index, c);
    }
    
    /**
     *
     * @param aData
     */
    public void refresh(List aData) {
        m_aData = aData;
    }
  
    /**
     *
     * @param aKey
     * @return
     */
    public Object getElementByKey(Object aKey) {
        if (aKey != null) {
            Iterator it = m_aData.iterator();
            while (it.hasNext()) {
                Object value = it.next();
                if (aKey.equals(m_keygetter.getKey(value))) {
                    return value;
                }
            }           
        }
        return null;
    }
    
    @Override
    public Object getElementAt(int index) {
        return m_aData.get(index);
    }
    
    @Override
    public int getSize() {
        return m_aData.size();
    }
    
}
