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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import java.math.*;
import java.sql.CallableStatement;

public class PreparedProcedure extends JDBCSentence {

    private static final Logger logger = Logger.getLogger("ke.kalc.data.loader.PreparedProcedure");

    private String m_sentence;

    protected SerializerWrite m_SerWrite = null;
    protected SerializerRead m_SerRead = null;
    private CallableStatement m_Call;

    /**
     *
     * @param s
     * @param sentence
     * @param serwrite
     * @param serread
     */
    public PreparedProcedure(Session s, String sentence, SerializerWrite serwrite, SerializerRead serread) {
        super(s);
        m_sentence = sentence;
        m_SerWrite = serwrite;
        m_SerRead = serread;
        m_Call = null;
    }

    /**
     *
     * @param s
     * @param sentence
     * @param serwrite
     */
    public PreparedProcedure(Session s, String sentence, SerializerWrite serwrite) {
        this(s, sentence, serwrite, null);
    }

    /**
     *
     * @param s
     * @param sentence
     */
    public PreparedProcedure(Session s, String sentence) {
        this(s, sentence, null, null);
    }

    private static final class PreparedProcedurePars implements DataWrite {

        private PreparedStatement m_ps;

        PreparedProcedurePars(CallableStatement ps) {
            m_ps = ps;
        }

        @Override
        public void setInt(int paramIndex, Integer iValue) throws BasicException {
            try {
                m_ps.setObject(paramIndex, iValue, Types.INTEGER);
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setString(int paramIndex, String sValue) throws BasicException {
            try {
                m_ps.setString(paramIndex, sValue);
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setDouble(int paramIndex, Double dValue) throws BasicException {
            try {
                m_ps.setObject(paramIndex, dValue, Types.DOUBLE);
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
            try {
                if (bValue == null) {
                    m_ps.setObject(paramIndex, null);
                } else {
                    m_ps.setBoolean(paramIndex, bValue.booleanValue());
                }
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setTimestamp(int paramIndex, java.util.Date dValue) throws BasicException {
            try {
                m_ps.setObject(paramIndex, dValue == null ? null : new Timestamp(dValue.getTime()), Types.TIMESTAMP);
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setBytes(int paramIndex, byte[] value) throws BasicException {
            try {
                m_ps.setBytes(paramIndex, value);
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setObject(int paramIndex, Object value) throws BasicException {
            try {
                m_ps.setObject(paramIndex, value);
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setBigDecimal(int paramIndex, BigDecimal bdValue) throws BasicException {
            try {
                m_ps.setObject(paramIndex, bdValue);
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            }
        }
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    @Override
    public DataResultSet openExec(Object params) throws BasicException {
        closeExec();

        try {

            logger.log(Level.INFO, "Executing prepared SQL: {0}", m_sentence);

            m_Call = m_s.getConnection().prepareCall(m_sentence);

            if (m_SerWrite != null) {
                m_SerWrite.writeValues(new PreparedProcedurePars(m_Call), params);
            }

            if (m_Call.execute()) {
                return new JDBCDataResultSet(m_Call.getResultSet(), m_SerRead);
            } else {
                int iUC = m_Call.getUpdateCount();
                if (iUC < 0) {
                    return null;
                } else {
                    return new SentenceUpdateResultSet(iUC);
                }
            }
        } catch (SQLException eSQL) {
            throw new BasicException(eSQL);
        }
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public final DataResultSet moreResults() throws BasicException {
        try {
            if (m_Call.getMoreResults()) {
                // tenemos resultset
                return new JDBCDataResultSet(m_Call.getResultSet(), m_SerRead);
            } else {
                // tenemos updatecount o si devuelve -1 ya no hay mas
                int iUC = m_Call.getUpdateCount();
                if (iUC < 0) {
                    return null;
                } else {
                    return new SentenceUpdateResultSet(iUC);
                }
            }
        } catch (SQLException eSQL) {
            throw new BasicException(eSQL);
        }
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public final void closeExec() throws BasicException {

        if (m_Call != null) {
            try {
                m_Call.close();
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            } finally {
                m_Call = null;
            }
        }
    }
}
