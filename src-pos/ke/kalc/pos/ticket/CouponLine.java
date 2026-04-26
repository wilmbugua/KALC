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


package ke.kalc.pos.ticket;

import java.io.Serializable;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.DataWrite;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.data.loader.SerializableWrite;

/**
 *
 * @author John Barrett
 */

public class CouponLine implements Comparable<CouponLine>, SerializableWrite, SerializableRead, Serializable{

    protected String m_Id;
    protected int m_LineNumber;
    protected String m_LineText;

    public CouponLine() {
        super();            
    }

    public CouponLine(String id, int linenumber, String text) {
        super();
        this.m_Id = id;
        this.m_LineNumber = linenumber;
        this.m_LineText = text;
    }

    public CouponLine(CouponLine o) {
        super();
        this.m_Id = o.m_Id;
        this.m_LineNumber = o.m_LineNumber;
        this.m_LineText = o.m_LineText;
    }

    public String getid() {
        return m_Id;
    }
    public int getlinenumber() {
        return m_LineNumber;
    }
    public String gettext() {
        return m_LineText;
    }

    public int compareTo(CouponLine o) {
        int c = o.m_Id.compareTo(m_Id);

        if( c==0 ) {
            // Same id so compare line numbers
            c = m_LineNumber - o.m_LineNumber;
        }
        return c;
    }        

    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, m_Id);
        dp.setInt(2, m_LineNumber);
        dp.setString(3, m_LineText);
   }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_Id = dr.getString(1);
        m_LineNumber = dr.getInt(2);
        m_LineText = dr.getString(3);
    }
}    

