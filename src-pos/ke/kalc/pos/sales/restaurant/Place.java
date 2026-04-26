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
package ke.kalc.pos.sales.restaurant;

import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import ke.kalc.basic.BasicException;
import ke.kalc.data.gui.NullIcon;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.globals.IconFactory;
import ke.kalc.globals.SystemProperty;

/**
 *
 *
 */
public class Place implements SerializableRead, java.io.Serializable {

    private static final long serialVersionUID = 8652254694281L;
    private static final Icon ICO_OCU = IconFactory.getIcon("edit_group.png");
    private static final Icon ICO_FRE = new NullIcon(22, 22);

    private String m_sId;
    private String m_sName;
    private int m_ix;
    private int m_iy;
    private int width;
    private int height;
    private int m_diffx;
    private int m_diffy;
    private String m_sfloor;
    private String m_customer;
    private String m_waiter;
    private String m_ticketId;
    private Boolean m_tableMoved;
    private Boolean m_changed = false;
    private int covers = 0;

    private boolean m_bPeople;
    private JButton m_btn;

    /**
     * Creates a new instance of TablePlace
     */
    public Place() {
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sId = dr.getString(1);
        m_sName = dr.getString(2);
        m_ix = dr.getInt(3);
        m_iy = dr.getInt(4);
        width = dr.getInt(5);
        height = dr.getInt(6);
        m_sfloor = dr.getString(7);
        m_customer = dr.getString(7);
        m_waiter = dr.getString(9);
        m_ticketId = dr.getString(10);
        m_tableMoved = dr.getBoolean(11);
        covers = dr.getInt(12);

        m_bPeople = false;
        m_btn = new JButton();

        m_btn.setFocusPainted(false);
        m_btn.setFocusable(false);
        m_btn.setRequestFocusEnabled(false);
        m_btn.setHorizontalTextPosition(SwingConstants.CENTER);
        
        if (!SystemProperty.SHOWWAITERDETAILS || !SystemProperty.SHOWCUSTOMERDETAILS) {
           m_btn.setVerticalTextPosition(SwingConstants.BOTTOM); 
        }
        m_btn.setIcon(ICO_FRE);
        m_btn.setText(m_sName + " (" + covers + ")");
        m_btn.setMargin(new Insets(2, 5, 2, 5));

        m_diffx = 0;
        m_diffy = 0;
    }

    public String getId() {
        return m_sId;
    }

    public String getTicketID() {
        return m_ticketId;
    }

    public String getName() {
        if (covers == 0) {
            return m_sName;
        }
        return m_sName + " (" + covers + ")";
    }

    public int getX() {
        return m_ix;
    }

    public int getY() {
        return m_iy;
    }

    public void setX(int x) {
        this.m_ix = x;
    }

    public void setY(int y) {
        this.m_iy = y;
    }

    public int getDiffX() {
        return m_diffx;
    }

    public int getDiffY() {
        return m_diffy;
    }

    public void setDiffX(int x) {
        this.m_diffx = x;
    }

    public void setDiffY(int y) {
        this.m_diffy = y;
    }

    public Boolean getChanged() {
        return m_changed;
    }

    public void setChanged(Boolean changed) {
        this.m_changed = changed;
    }

    public String getFloor() {
        return m_sfloor;
    }

    public JButton getButton() {
        return m_btn;
    }

    public String getCustomer() {
        return m_customer;
    }

    public String getWaiter() {
        return m_waiter;
    }

    public boolean hasPeople() {
        return m_bPeople;
    }

    public void setPeople(boolean bValue) {
        m_bPeople = bValue;
        m_btn.setIcon(bValue ? ICO_OCU : ICO_FRE);
    }

    public void setButtonBounds() {
        m_btn.setBounds(m_ix, m_iy, width, height);
    }

    public void setButtonText(String btnText) {
        m_btn.setText(btnText);
    }

}
