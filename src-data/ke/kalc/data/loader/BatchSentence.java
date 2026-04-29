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


package ke.kalc.data.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ke.kalc.basic.BasicException;
import ke.kalc.pos.forms.AppLocal;

/**
 *
 *   
 */
public abstract class BatchSentence extends BaseSentence {
    
    /**
     *
     */
    protected Session m_s;    

    /**
     *
     */
    protected HashMap<String, String> m_parameters;
    
    /** Creates a new instance of BatchSentence
     * @param s */
    public BatchSentence(Session s) {
        m_s = s;
        m_parameters = new HashMap<>();
    }
    
    /**
     *
     * @param name
     * @param replacement
     */
    public void putParameter(String name, String replacement) {
        m_parameters.put(name, replacement);
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    protected abstract Reader getReader() throws BasicException;
    
    /**
     *
     */
    public class ExceptionsResultSet implements DataResultSet {
        
        List l;
        int m_iIndex;
        
        /**
         *
         * @param l
         */
        public ExceptionsResultSet(List l) {
            this.l = l;
            m_iIndex = -1;
        }
        
        /**
         *
         * @param columnIndex
         * @return
         * @throws BasicException
         */
        @Override
        public Integer getInt(int columnIndex) throws BasicException {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }

        /**
         *
         * @param columnIndex
         * @return
         * @throws BasicException
         */
        @Override
        public String getString(int columnIndex) throws BasicException {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }

        /**
         *
         * @param columnIndex
         * @return
         * @throws BasicException
         */
        @Override
        public Double getDouble(int columnIndex) throws BasicException {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }

        /**
         *
         * @param columnIndex
         * @return
         * @throws BasicException
         */
        @Override
        public Boolean getBoolean(int columnIndex) throws BasicException {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }

        /**
         *
         * @param columnIndex
         * @return
         * @throws BasicException
         */
        @Override
        public java.util.Date getTimestamp(int columnIndex) throws BasicException {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }

        //public java.io.InputStream getBinaryStream(int columnIndex) throws DataException;

        /**
         *
         * @param columnIndex
         * @return
         * @throws BasicException
         */
                @Override
        public byte[] getBytes(int columnIndex) throws BasicException {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }

        /**
         *
         * @param columnIndex
         * @return
         * @throws BasicException
         */
        @Override
        public Object getObject(int columnIndex) throws BasicException  {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }

    //    public int getColumnCount() throws DataException;

        /**
         *
         * @return
         * @throws BasicException
         */
                @Override
        public DataField[] getDataField() throws BasicException {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }

        /**
         *
         * @return
         * @throws BasicException
         */
        @Override
        public Object getCurrent() throws BasicException {
            if (m_iIndex < 0 || m_iIndex >= l.size()) {
                throw new BasicException(AppLocal.getIntString("exception.outofbounds"));
            } else {
                return l.get(m_iIndex);
            }
        }
        
        /**
         *
         * @return
         * @throws BasicException
         */
        @Override
        public boolean next() throws BasicException {
            return ++m_iIndex < l.size();
        }

        /**
         *
         * @throws BasicException
         */
        @Override
        public void close() throws BasicException {
        }

        /**
         *
         * @return
         */
        @Override
        public int updateCount() {
            return 0;
        }

        @Override
        public java.math.BigDecimal getBigDecimal(int columnIndex) throws BasicException {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        }
    }
    
    /**
     *
     * @throws BasicException
     */
    @Override
    public final void closeExec() throws BasicException {
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public final DataResultSet moreResults() throws BasicException {
        return null;
    }
    
    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    @Override
    public DataResultSet openExec(Object params) throws BasicException {

        BufferedReader br = new BufferedReader(getReader());

        String sLine;
        StringBuffer sSentence = new StringBuffer();
        List aExceptions = new ArrayList();

        try {
            while ((sLine = br.readLine()) != null) {
                sLine = sLine.trim();
                if (!sLine.equals("") && !sLine.startsWith("--")) {
                    // No es un comentario ni linea vacia
                    if (sLine.endsWith(";")) {
                        // ha terminado la sentencia
                        sSentence.append(sLine.substring(0, sLine.length() - 1));                             

                        // File parameters
                        Pattern pattern = Pattern.compile("\\$(\\w+)\\{([^}]*)\\}");
                        Matcher matcher = pattern.matcher(sSentence.toString());
                        List paramlist = new ArrayList();

                        // Replace all occurrences of pattern in input
                        StringBuffer buf = new StringBuffer();
                        while (matcher.find()) {
                            if ("FILE".equals(matcher.group(1))) {
                                paramlist.add(ImageUtils.getBytesFromResource(matcher.group(2)));
                                matcher.appendReplacement(buf, "?");
                            } else {
                                String replacement = m_parameters.get(matcher.group(1));
                                if (replacement == null) {
                                    matcher.appendReplacement(buf, Matcher.quoteReplacement(matcher.group(0)));
                                } else {
                                    paramlist.add(replacement);
                                    matcher.appendReplacement(buf, "?");
                                }
                            }
                        }
                        matcher.appendTail(buf); 
                        
                        // La disparo
                        try {
                            BaseSentence sent;
                            if (paramlist.isEmpty()) {
                                sent = new StaticSentence(m_s, buf.toString());
                                sent.exec();
                            } else {
                                sent = new PreparedSentence(m_s, buf.toString(), SerializerWriteBuilder.INSTANCE);
                                sent.exec(new VarParams(paramlist));
                            }
                        } catch (BasicException eD) {
                            aExceptions.add(eD);
                        }
                        sSentence = new StringBuffer();

                    } else {
                        // la sentencia continua en la linea siguiente
                        sSentence.append(sLine);
                    }
                }
            }

            br.close();

        } catch (IOException eIO) {
            throw new BasicException(AppLocal.getIntString("exception.noreadfile"), eIO);
        }

        if (sSentence.length() > 0) {
            // ha quedado una sentencia inacabada
            aExceptions.add(new BasicException(AppLocal.getIntString("exception.nofinishedfile")));
        }   

        return new ExceptionsResultSet(aExceptions);
    }    
       
    private static class VarParams implements SerializableWrite {
        
        private List l;
        
        public VarParams(List l) {
            this.l = l;
        }
        
        @Override
        public void writeValues(DataWrite dp) throws BasicException {
            for (int i = 0; i < l.size(); i++) {
                Object v = l.get(i);
                if (v instanceof String) {
                    dp.setString(i + 1, (String) v);
                } else if (v instanceof byte[]) {
                    dp.setBytes(i + 1, (byte[]) l.get(i));
                } else {
                    dp.setObject(i + 1, v);
                }                
            }
        }
    }
}
