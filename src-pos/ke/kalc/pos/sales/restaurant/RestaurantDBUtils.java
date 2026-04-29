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

import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.PreparedSentence;
import ke.kalc.data.loader.SerializerReadBoolean;
import ke.kalc.data.loader.SerializerReadInteger;
import ke.kalc.data.loader.SerializerReadString;
import ke.kalc.data.loader.SerializerWriteBasic;
import ke.kalc.data.loader.SerializerWriteString;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.data.loader.StaticSentence;
import ke.kalc.pos.datalogic.DataLogicSystem;

/**
 *
 * @author JDL
 */
public class RestaurantDBUtils {

    public final static int TRUE = 1;
    public final static int FALSE = 0;
    private final Session m_s;
    private Object m_result;

    /**
     *
     */
    protected DataLogicSystem dlSystem;

    /**
     *
     */
    public RestaurantDBUtils() {
        m_s = SessionFactory.getSession();
    }

    /**
     *
     * @param newTable
     * @param ticketID
     */
    public void moveCustomer(String newTable, String ticketID) {
        String oldTable = getTableDetails(ticketID);
        if (countTicketIdInTable(ticketID) > 1) {
            setCustomerNameInTable(getCustomerNameInTable(oldTable), newTable);
            setWaiterNameInTable(getWaiterNameInTable(oldTable), newTable);
            setTicketIdInTable(ticketID, newTable);
// remove the data for the old table 
            oldTable = getTableMovedName(ticketID);
            if ((oldTable != null) && (oldTable != newTable)) {
                clearCustomerNameInTable(oldTable);
                clearWaiterNameInTable(oldTable);
                clearTicketIdInTable(oldTable);
                clearTableMovedFlag(oldTable);
            } else {
                oldTable = getTableMovedName(ticketID);
                clearTableMovedFlag(oldTable);
            }
        }
    }

    /**
     *
     * @param custName
     * @param tableName
     */
    public void setCustomerNameInTable(String custName, String tableName) {
        try {
            new StaticSentence(m_s, "update places set customer=? where name=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(custName, tableName);

        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param custName
     * @param tableID
     */
    public void setCustomerNameInTableById(String custName, String tableID) {
        try {
            new StaticSentence(m_s, "update places set customer=? where id=? ", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(custName, tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param custName
     * @param ticketID
     */
    public void setCustomerNameInTableByTicketId(String custName, String ticketID) {
        try {
            new StaticSentence(m_s, "update places set customer=? where ticketid=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(custName, ticketID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public String getCustomerNameInTable(String tableName) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select customer from places where name = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableName);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {

        }
        return "";
    }

    /**
     *
     * @param tableId
     * @return
     */
    public String getCustomerNameInTableById(String tableId) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select c.name from places AS p "
                    + "join customers as c on p.customer=c.id "
                    + "where p.id = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableId);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {

        }
        return "";
    }

    /**
     *
     * @param tableName
     */
    public void clearCustomerNameInTable(String tableName) {
        try {
            new StaticSentence(m_s, "update places set customer=null where name=?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableID
     */
    public void clearCustomerNameInTableById(String tableID) {
        try {
            new StaticSentence(m_s, "update places set customer=null where id=?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param waiterName
     * @param tableName
     */
    public void setWaiterNameInTable(String waiterName, String tableName) {
        try {
            new StaticSentence(m_s, "update places set waiter=? where name=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(waiterName, tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param waiterName
     * @param tableID
     */
    public void setWaiterNameInTableById(String waiterName, String tableID) {
        try {
            new StaticSentence(m_s, "update places set waiter=? where id=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(waiterName, tableID);
        } catch (BasicException e) {

        }
    }

    public void setWaiterNameInTableByTicketId(String waiter, String ticketID) {
        try {
            new StaticSentence(m_s, "update places set waiter = ? where ticketid=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(waiter, ticketID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public String getWaiterNameInTable(String tableName) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select waiter from places where name = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableName);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {

        }
        return "";
    }

    /**
     *
     * @param tableID
     * @return
     */
    public String getWaiterNameInTableById(String tableID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select waiter from places where id= ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableID);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {
        }
        return "";
    }

    /**
     *
     * @param tableName
     */
    public void clearWaiterNameInTable(String tableName) {
        try {
            new StaticSentence(m_s, "update places set waiter=null where name=?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableID
     */
    public void clearWaiterNameInTableById(String tableID) {
        try {
            new StaticSentence(m_s, "update places set waiter=null where id=?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableID
     * @return
     */
    public String getTicketIdInTable(String tableID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select ticketid from places where id = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableID);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {
        }
        return "";
    }

    /**
     *
     * @param ticketID
     * @param tableName
     */
    public void setTicketIdInTable(String ticketID, String tableName) {
        try {
            new StaticSentence(m_s, "update places set ticketid=? where name=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(ticketID, tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableName
     */
    public void clearTicketIdInTable(String tableName) {
        try {
            new StaticSentence(m_s, "update places set ticketid=null where name=?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableID
     */
    public void clearTicketIdInTableById(String tableID) {
        try {
            new StaticSentence(m_s, "update places set ticketid=null where id=?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public Integer countTicketIdInTable(String ticketID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select count(*) as recordcount from places where ticketid = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadInteger.INSTANCE).find(ticketID);
            return (Integer) m_result;
        } catch (BasicException e) {

        }

        return 0;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableDetails(String ticketID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select name from places where ticketid = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(ticketID);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {

        }
        return "";
    }

    /**
     *
     * @param tableID
     */
    public void setTableMovedFlag(String tableID) {
        try {
            new StaticSentence(m_s, "update places set tablemoved=" + TRUE + " where id=?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableMovedName(String ticketID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select name from places where ticketid = ? and tablemoved = " + TRUE,
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(ticketID);
            return (String) m_result;
        } catch (BasicException e) {
            Logger.getLogger(RestaurantDBUtils.class.getName()).log(Level.WARNING, null, e);
        }
        return null;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public Boolean getTableMovedFlag(String ticketID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select tablemoved from places where ticketid = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadBoolean.INSTANCE).find(ticketID);
            return (m_result == null) ? false : (Boolean) m_result;
        } catch (BasicException e) {

        }
        return (false);
    }

    /**
     *
     * @param tableName
     */
    public void clearTableMovedFlag(String tableName) {
        try {
            new StaticSentence(m_s, "update places set tablemoved=" + FALSE + " where name=?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {

        }
    }

    public void clearTableLockByTicket(String ticketID) {
        try {
            new StaticSentence(m_s, "update places set locked = false, openedby = null where ticketid = ?", SerializerWriteString.INSTANCE).exec(ticketID);
        } catch (BasicException e) {
            System.out.println("clear lock error");
        }
    }

    public void clearTableLockByName(String tableName) {
        try {
            new StaticSentence(m_s, "update places set locked = false, openedby = null where name = ?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {
            System.out.println("clear lock error");
        }
    }

    public void setTableLock(String tableID, String user) {
        try {
            new StaticSentence(m_s, "update places set locked = true, openedby = ? where ticketid = ?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(user, tableID);
        } catch (BasicException e) {
            System.out.println("clear lock error");
        }
    }

    public void clearTableLock(String tableID) {
        try {
            new StaticSentence(m_s, "update places set locked = false, openedby = null where id = ?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {
            System.out.println("clear lock error");
        }
    }

    public Boolean getTableLock(String tableID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select locked from places where id= ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadInteger.INSTANCE).find(tableID);
            return ((Integer) m_result == 1);
        } catch (BasicException e) {

        }
        return (false);
    }

    public String getTableOpenedBy(String tableID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "select openedby from places where id = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableID);
            return (String) m_result;
        } catch (BasicException e) {

        }
        return null;
    }

}
