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

import ke.kalc.format.Formats;
import ke.kalc.pos.forms.AppLocal;

/**
 *
 *
 */
public class TableDefinition {

    private Session m_s;
    private String tablename;

    private String[] fieldname;
    private String[] fieldtran;
    private Datas[] fielddata;
    private Formats[] fieldformat;
    private String m_orderBy;
    private String m_where;

    private int[] idinx;

    /**
     * Creates a new instance of TableDefinition
     *
     * @param s
     * @param fieldformat
     * @param tablename
     * @param fieldname
     * @param fieldtran
     * @param idinx
     * @param fielddata
     */
    public TableDefinition(
            Session s,
            String tablename,
            String[] fieldname, String[] fieldtran, Datas[] fielddata, Formats[] fieldformat,
            int[] idinx) {
        this(s, tablename, fieldname, fieldtran, fielddata, fieldformat, idinx, null);
    }

    /**
     * Creates a new instance of TableDefinition
     *
     * @param s
     * @param fieldformat
     * @param tablename
     * @param fieldname
     * @param fieldtran
     * @param idinx
     * @param fielddata
     */
    public TableDefinition(
            Session s,
            String tablename,
            String[] fieldname, String[] fieldtran, Datas[] fielddata, Formats[] fieldformat,
            int[] idinx, String orderby) {

        m_s = s;
        this.tablename = tablename;

        this.fieldname = fieldname;
        this.fieldtran = fieldtran;
        this.fielddata = fielddata;
        this.fieldformat = fieldformat;

        this.idinx = idinx;

        m_orderBy = orderby;

    }

    public TableDefinition(
            Session s,
            String tablename,
            String[] fieldname, String[] fieldtran, Datas[] fielddata, Formats[] fieldformat,
            int[] idinx, String orderby, String where) {

        m_s = s;
        this.tablename = tablename;

        this.fieldname = fieldname;
        this.fieldtran = fieldtran;
        this.fielddata = fielddata;
        this.fieldformat = fieldformat;

        this.idinx = idinx;

        m_orderBy = orderby;
        m_where = where;
    }

    /**
     *
     * @param s
     * @param tablename
     * @param fieldname
     * @param fielddata
     * @param fieldformat
     * @param idinx
     */
    public TableDefinition(
            Session s,
            String tablename,
            String[] fieldname, Datas[] fielddata, Formats[] fieldformat,
            int[] idinx) {
        this(s, tablename, fieldname, fieldname, fielddata, fieldformat, idinx);
    }

    /**
     *
     * @return
     */
    public String getTableName() {
        return tablename;
    }

    /**
     *
     * @return
     */
    public String[] getFields() {
        return fieldname;
    }

    /**
     *
     * @param aiFields
     * @return
     */
    public Vectorer getVectorerBasic(int[] aiFields) {
        return new VectorerBasic(fieldtran, fieldformat, aiFields);
    }

    /**
     *
     * @param aiFields
     * @return
     */
    public IRenderString getRenderStringBasic(int[] aiFields) {
        return new RenderStringBasic(fieldformat, aiFields);
    }

    /**
     *
     * @param aiOrders
     * @return
     */
    public ComparatorCreator getComparatorCreator(int[] aiOrders) {
        return new ComparatorCreatorBasic(fieldtran, fielddata, aiOrders);
    }

    /**
     *
     * @return
     */
    public IKeyGetter getKeyGetterBasic() {
        if (idinx.length == 1) {
            return new KeyGetterFirst(idinx);
        } else {
            return new KeyGetterBasic(idinx);
        }
    }

    /**
     *
     * @return
     */
    public SerializerRead getSerializerReadBasic() {
        return new SerializerReadBasic(fielddata);
    }

    /**
     *
     * @param fieldindx
     * @return
     */
    public SerializerWrite getSerializerInsertBasic(int[] fieldindx) {
        return new SerializerWriteBasicExt(fielddata, fieldindx);
    }

    /**
     *
     * @return
     */
    public SerializerWrite getSerializerDeleteBasic() {
        return new SerializerWriteBasicExt(fielddata, idinx);
    }

    /**
     *
     * @param fieldindx
     * @return
     */
    public SerializerWrite getSerializerUpdateBasic(int[] fieldindx) {

        int[] aindex = new int[fieldindx.length + idinx.length];

        for (int i = 0; i < fieldindx.length; i++) {
            aindex[i] = fieldindx[i];
        }
        for (int i = 0; i < idinx.length; i++) {
            aindex[i + fieldindx.length] = idinx[i];
        }

        return new SerializerWriteBasicExt(fielddata, aindex);
    }

    /**
     *
     * @return
     */
    public SentenceList getListSentence() {
        return getListSentence(getSerializerReadBasic());
    }

    /**
     *
     * @param sr
     * @return
     */
    public SentenceList getListSentence(SerializerRead sr) {
        return new PreparedSentence(m_s, getListSQL(), null, sr);
    }

    /**
     *
     * @return
     */
    public String getListSQL() {

        StringBuilder sent = new StringBuilder();
        sent.append("select ");

        // Add all the fields passed to the string
        for (int i = 0; i < fieldname.length; i++) {
            if (i > 0) {
                sent.append(", ");
            }
            sent.append(fieldname[i]);
        }

        sent.append(" from ");
        sent.append(tablename);
        if (m_where != null && m_where.length() > 0) {
            sent.append(" where siteguid = '");
            sent.append(m_where);
            sent.append("' ");
        }
        
        
        if (m_orderBy != null && m_orderBy.length() > 0) {
            sent.append(" order by ");
            sent.append(m_orderBy);
        }

        if (!AppLocal.LIST_BY_RIGHTS.equals("")) {

            sent.delete(0, sent.length());
            sent.append(AppLocal.LIST_BY_RIGHTS);
        }
        return sent.toString();
    }

    /**
     *
     * @return
     */
    public SentenceExec getDeleteSentence() {
        return getDeleteSentence(getSerializerDeleteBasic());
    }

    /**
     *
     * @param sw
     * @return
     */
    public SentenceExec getDeleteSentence(SerializerWrite sw) {
        return new PreparedSentence(m_s, getDeleteSQL(), sw, null);
    }

    /**
     *
     * @return
     */
    public String getDeleteSQL() {

        StringBuilder sent = new StringBuilder();
        sent.append("delete from ");
        sent.append(tablename);

        for (int i = 0; i < idinx.length; i++) {
            sent.append((i == 0) ? " where " : " and ");
            sent.append(fieldname[idinx[i]]);
            sent.append(" = ?");
        }

        return sent.toString();
    }

    /**
     *
     * @return
     */
    public SentenceExec getInsertSentence() {
        return getInsertSentence(getAllFields());
    }

    /**
     *
     * @param fieldindx
     * @return
     */
    public SentenceExec getInsertSentence(int[] fieldindx) {
        return new PreparedSentence(m_s, getInsertSQL(fieldindx), getSerializerInsertBasic(fieldindx), null);
    }

    private String getInsertSQL(int[] fieldindx) {

        StringBuilder sent = new StringBuilder();
        StringBuilder values = new StringBuilder();

        sent.append("insert into ");
        sent.append(tablename);
        sent.append(" (");

        for (int i = 0; i < fieldindx.length; i++) {
            if (i > 0) {
                sent.append(", ");
                values.append(", ");
            }
            sent.append(fieldname[fieldindx[i]]);
            values.append("?");
        }

        sent.append(") values (");
        sent.append(values.toString());
        sent.append(")");

        return sent.toString();
    }

    private int[] getAllFields() {

        int[] fieldindx = new int[fieldname.length];
        for (int i = 0; i < fieldname.length; i++) {
            fieldindx[i] = i;
        }
        return fieldindx;
    }

    /**
     *
     * @return
     */
    public SentenceExec getUpdateSentence() {
        return getUpdateSentence(getAllFields());
    }

    /**
     *
     * @param fieldindx
     * @return
     */
    public SentenceExec getUpdateSentence(int[] fieldindx) {
        return new PreparedSentence(m_s, getUpdateSQL(fieldindx), getSerializerUpdateBasic(fieldindx), null);
    }

    private String getUpdateSQL(int[] fieldindx) {

        StringBuilder sent = new StringBuilder();

        sent.append("update ");
        sent.append(tablename);
        sent.append(" set ");

        for (int i = 0; i < fieldindx.length; i++) {
            if (i > 0) {
                sent.append(", ");
            }
            sent.append(fieldname[fieldindx[i]]);
            sent.append(" = ?");
        }

        for (int i = 0; i < idinx.length; i++) {
            sent.append((i == 0) ? " where " : " and ");
            sent.append(fieldname[idinx[i]]);
            sent.append(" = ?");
        }

        return sent.toString();
    }
}
