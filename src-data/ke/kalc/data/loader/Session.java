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


package ke.kalc.data.loader;

import java.sql.Connection;
import java.sql.SQLException;
import ke.kalc.connectionpool.ConnectionPoolFactory;

public final class Session {

    private Connection m_c;
    private boolean m_bInTransaction;

    /**
     *
     */
    public final SessionDB DB =new SessionDBMySQL();;

    /**
     * Creates a new instance of Session
     *
     * @throws java.sql.SQLException
     */
    public Session(Connection connection) throws SQLException {
        m_bInTransaction = false;
        m_c = connection;
        m_c.setAutoCommit(true);
    }

    /**
     *
     * @throws SQLException
     */
    public void connect() throws SQLException {
        close();
        m_c = ConnectionPoolFactory.getConnection();
        m_c.setAutoCommit(true);
        m_bInTransaction = false;
    }

    /**
     *
     */
    public void close() {
        if (m_c != null) {
            try {
                if (m_bInTransaction) {
                    m_bInTransaction = false; 
                    m_c.rollback();
                    m_c.setAutoCommit(true);
                }
                m_c.close();
            } catch (SQLException e) {
            } finally {
                m_c = null;
            }
        }
    }

    /**
     *
     * @return @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        if (!m_bInTransaction) {
            ensureConnection();
        }
        return m_c;
    }

    /**
     *
     * @throws SQLException
     */
    public void begin() throws SQLException {
        if (m_bInTransaction) {
            throw new SQLException("Already in transaction");
        } else {
            ensureConnection();
            m_c.setAutoCommit(false);
            m_bInTransaction = true;
        }
    }

    /**
     *
     * @throws SQLException
     */
    public void commit() throws SQLException {
        if (m_bInTransaction) {
            m_bInTransaction = false; 
            m_c.commit();
            m_c.setAutoCommit(true);
        } else {
            throw new SQLException("Transaction not started");
        }
    }

    /**
     *
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        if (m_bInTransaction) {
            m_bInTransaction = false;
            m_c.rollback();
            m_c.setAutoCommit(true);
        } else {
            throw new SQLException("Transaction not started");
        }
    }

    /**
     *
     * @return
     */
    public boolean isTransaction() {
        return m_bInTransaction;
    }

    private void ensureConnection() throws SQLException {
        boolean bclosed;
        try {
            bclosed = m_c == null || m_c.isClosed();
        } catch (SQLException e) {
            bclosed = true;
        }
        if (bclosed) {
            connect();
        }
    }
}
