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
package ke.kalc.pos.datalogic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.*;
import ke.kalc.data.model.Field;
import ke.kalc.data.model.Row;
import ke.kalc.format.Formats;
import ke.kalc.pos.customers.CustomerInfoExt;
import ke.kalc.pos.inventory.*;
import ke.kalc.pos.mant.FloorsInfo;
import ke.kalc.pos.payment.PaymentInfo;
import ke.kalc.pos.payment.PaymentInfoTicket;
import ke.kalc.pos.ticket.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
 import ke.kalc.commons.utils.KeyedData;
 import ke.kalc.commons.utils.TerminalInfo;
 import ke.kalc.globals.Company;
 import ke.kalc.globals.SystemProperty;
 import ke.kalc.pos.forms.AppConfig;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.BeanFactoryDataSingle;
import ke.kalc.pos.loyalty.LoyaltyCard;
import ke.kalc.pos.sales.CustomerDeliveryInfo;
import ke.kalc.tlv.Encoder;

public class DataLogicSales extends BeanFactoryDataSingle {

    protected Map<String, byte[]> productImageCache;
    protected Session s;
    protected Datas[] auxiliarDatas;
    protected Datas[] stockdiaryDatas;
    protected Datas[] stockAdjustDatas;
    protected SentenceExec m_sellvoucher;
    protected Datas[] paymenttabledatas;
    private SentenceFind m_ticketid;
    protected SentenceFind imageBytes;

    protected Datas[] stockdatas;
    protected Row productsRow;
    protected Row customersRow;
    public static final String DEBT = "debt";
    public static final String DEBT_PAID = "debtpaid";
    protected static final String PREPAY = "prepay";
    private static final Logger logger = Logger.getLogger("ke.kalc.pos.datalogic.DataLogicSales");
    private String getCardName;
    private DataLogicSystem m_dlSystem;
    private SentenceExec m_updateRefund;
    private SentenceExec m_refundStock;
    private SentenceExec m_addOrder;
    private SentenceFind m_productname;

    //Added 21-07-2019
    private LinkedHashSet<String> productTableInsert;
    private LinkedHashSet<String> productTableUpdate;

    // Use this INDEX_xxx instead of numbers to access arrays of product information
    public static int FIELD_COUNT = 0;
    public static int INDEX_ID = FIELD_COUNT++;
    public static int INDEX_REFERENCE = FIELD_COUNT++;
    public static int INDEX_CODE = FIELD_COUNT++;
    public static int INDEX_CODETYPE = FIELD_COUNT++;
    public static int INDEX_NAME = FIELD_COUNT++;
    public static int INDEX_ISCOM = FIELD_COUNT++;
    public static int INDEX_ISSCALE = FIELD_COUNT++;
    public static int INDEX_PRICEBUY = FIELD_COUNT++;
    public static int INDEX_PRICESELL = FIELD_COUNT++;
    public static int INDEX_PRICESELLINC = FIELD_COUNT++;
    public static int INDEX_COMMISSION = FIELD_COUNT++;
    public static int INDEX_CATEGORY = FIELD_COUNT++;
    public static int INDEX_TAXCAT = FIELD_COUNT++;
    public static int INDEX_ATTRIBUTESET_ID = FIELD_COUNT++;
    public static int INDEX_IMAGE = FIELD_COUNT++;
    public static int INDEX_ATTRIBUTES = FIELD_COUNT++;
    public static int INDEX_ISCATALOG = FIELD_COUNT++;
    public static int INDEX_CATORDER = FIELD_COUNT++;
    public static int INDEX_ISKITCHEN = FIELD_COUNT++;
    public static int INDEX_KITCHENDESCRIPTION = FIELD_COUNT++;
    public static int INDEX_ISSERVICE = FIELD_COUNT++;
    public static int INDEX_DISPLAY = FIELD_COUNT++;
    public static int INDEX_ISVPRICE = FIELD_COUNT++;
    public static int INDEX_ISVERPATRIB = FIELD_COUNT++;
    public static int INDEX_WARRANTY = FIELD_COUNT++;
    public static int INDEX_ALIAS = FIELD_COUNT++;
    public static int INDEX_ALWAYSAVAILABLE = FIELD_COUNT++;
    public static int INDEX_CANDISCOUNT = FIELD_COUNT++;
    public static int INDEX_MANAGESTOCK = FIELD_COUNT++;
    public static int INDEX_REMOTEDISPLAY = FIELD_COUNT++;
    public static int INDEX_BURNVALUE = FIELD_COUNT++;
    public static int INDEX_EARNVALUE = FIELD_COUNT++;
    public static int INDEX_LOYALTYMULTIPLIER = FIELD_COUNT++;
    public static int INDEX_SELLUNIT = FIELD_COUNT++;
    public static int INDEX_STOCKUNIT = FIELD_COUNT++;
    public static int INDEX_BUYUNIT = FIELD_COUNT++;
    public static int INDEX_AGERESTRICTED = FIELD_COUNT++;
    public static int INDEX_ISRECIPE = FIELD_COUNT++;
    public static int INDEX_ISINGREDIENT = FIELD_COUNT++;
    public static int INDEX_SYSTEMOBJECT = FIELD_COUNT++;
    public static int INDEX_REMOTEPRINTER = FIELD_COUNT++;
    public static int INDEX_DISPLAYID = FIELD_COUNT++;
    public static int INDEX_AVERAGECOST = FIELD_COUNT++;
    public static int INDEX_SITEGUID = FIELD_COUNT++;
    public static int INDEX_ISSALESOBJECT = FIELD_COUNT++;
    public static int INDEX_ISDEPOSITOBJECT = FIELD_COUNT++;
    public static int INDEX_STOCKLEVEL = FIELD_COUNT++;

    private static DataLogicSales salesSession;

    public static DataLogicSales getSession() {
        if (salesSession != null) {
            return salesSession;
        }
        salesSession = new DataLogicSales(SessionFactory.getSession());
        return salesSession;
    }

    /**
     * Creates a new instance of SentenceContainerGeneric
     */
    public DataLogicSales(Session session) {
        super();
        this.s = session;
        init(s);
    }

    public DataLogicSales() {

        stockAdjustDatas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.STRING
        };

        stockdiaryDatas = new Datas[]{
            Datas.TIMESTAMP, // 0 - Time  
            Datas.INT, // 1 - Reason  
            Datas.STRING, // 2 - Location  
            Datas.STRING, // 3 - Product  
            Datas.DOUBLE, // 4 - Units  
            Datas.DOUBLE, // 5 - cost  
            Datas.DOUBLE, // 6 - Price 
            Datas.DOUBLE, // 7 - Pricein 
            Datas.STRING, // 8 - User  
            Datas.STRING // 9 - siteguid  
        };

        paymenttabledatas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.TIMESTAMP,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.STRING,
            Datas.STRING};

        stockdatas = new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE};
        auxiliarDatas = new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING};

        productsRow = new Row(
                new Field("id", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodbarcodetype"), Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING),
                new Field("iscom", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("isscale", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field(AppLocal.getIntString("label.prodpricebuy"), Datas.DOUBLE, Formats.CURRENCY),
                new Field(AppLocal.getIntString("label.prodpricesell"), Datas.DOUBLE, Formats.CURRENCY),
                new Field(AppLocal.getIntString("label.prodpriceselltax"), Datas.DOUBLE, Formats.CURRENCY),
                new Field(AppLocal.getIntString("label.commission"), Datas.DOUBLE, Formats.CURRENCY),
                new Field(AppLocal.getIntString("label.prodcategory"), Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.taxcategory"), Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.attributeset"), Datas.STRING, Formats.STRING),
                new Field("image", Datas.IMAGE, Formats.NULL),
                new Field("attributes", Datas.BYTES, Formats.NULL),
                new Field("iscatalog", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("catorder", Datas.INT, Formats.INT),
                new Field("iskitchen", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("kitchendescription", Datas.STRING, Formats.STRING),
                new Field("isservice", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field(AppLocal.getIntString("label.display"), Datas.STRING, Formats.STRING, false, true, true),
                new Field("isvprice", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("isverpatrib", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("warranty", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("alias", Datas.STRING, Formats.STRING), //26
                new Field("alwaysavailable", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("candiscount", Datas.DOUBLE, Formats.DOUBLE),
                new Field("managestock", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("remotedisplay", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("burnvalue", Datas.INT, Formats.INT),
                new Field("earnvalue", Datas.INT, Formats.INT),
                new Field("loyaltymultiplier", Datas.DOUBLE, Formats.DOUBLE, false, true, true),
                new Field("sellunit", Datas.STRING, Formats.STRING),
                new Field("stockunit", Datas.STRING, Formats.STRING),
                new Field("buyunit", Datas.STRING, Formats.STRING),
                new Field("agerestricted", Datas.INT, Formats.INT),
                new Field("isrecipe", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("isingredient", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("systemobject", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("remoteprinter", Datas.STRING, Formats.STRING),
                new Field("remotedisplayid", Datas.INT, Formats.INT),
                new Field("averagecost", Datas.DOUBLE, Formats.CURRENCY),
                new Field("salesobject", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("depositobject", Datas.STRING, Formats.STRING),
                new Field("siteguid", Datas.STRING, Formats.STRING),
                new Field("stocklevel", Datas.DOUBLE, Formats.DOUBLE)
        );

        // If this fails there is a coding error - have you added a column
        // to the PRODUCTS table and not added an INDEX_xxx for it?
        assert (FIELD_COUNT == productsRow.getFields().length);

        customersRow = new Row(
                new Field("id", Datas.STRING, Formats.STRING),
                new Field("customertype", Datas.STRING, Formats.STRING),
                new Field("taxid", Datas.STRING, Formats.STRING),
                new Field("name", Datas.STRING, Formats.STRING, true, true, true),
                new Field("taxcategory", Datas.STRING, Formats.STRING),
                new Field("card", Datas.STRING, Formats.STRING),
                new Field("maxdebt", Datas.DOUBLE, Formats.DOUBLE),
                new Field("address", Datas.STRING, Formats.STRING),
                new Field("address2", Datas.STRING, Formats.STRING),
                new Field("postal", Datas.STRING, Formats.STRING),
                new Field("city", Datas.STRING, Formats.STRING),
                new Field("region", Datas.STRING, Formats.STRING),
                new Field("country", Datas.STRING, Formats.STRING),
                new Field("firstname", Datas.STRING, Formats.STRING),
                new Field("lastname", Datas.STRING, Formats.STRING),
                new Field("email", Datas.STRING, Formats.STRING),
                new Field("phone", Datas.STRING, Formats.STRING),
                new Field("phone2", Datas.STRING, Formats.STRING),
                new Field("fax", Datas.STRING, Formats.STRING),
                new Field("notes", Datas.STRING, Formats.STRING),
                new Field("active", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("curdate", Datas.TIMESTAMP, Formats.TIMESTAMP),
                new Field("curdebt", Datas.DOUBLE, Formats.DOUBLE),
                new Field("image", Datas.IMAGE, Formats.NULL),
                new Field("discount", Datas.DOUBLE, Formats.DOUBLE),
                new Field("dob", Datas.TIMESTAMP, Formats.TIMESTAMP),
                new Field("loyaltycardid", Datas.STRING, Formats.STRING),
                new Field("loyaltycardnumber", Datas.STRING, Formats.STRING),
                new Field("loyaltyenabled", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("marketable", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("siteguid", Datas.STRING, Formats.STRING)
        );
    }

    private String getProductFieldList(String prefix) {
        Iterator<String> i = productTableInsert.iterator();
        StringBuilder fieldList = new StringBuilder();

        while (i.hasNext()) {
            if (prefix != null) {
                fieldList.append(prefix);
                fieldList.append(i.next());
            } else {
                fieldList.append(i.next());
            }
        }
        return fieldList.toString();
    }

    private String getSelectFieldList() {
        String sel = "p.id, "
                + "p.reference, "
                + "p.code, "
                + "p.codetype, "
                + "p.name, "
                + "p.iscom, "
                + "p.isscale, "
                + "p.pricebuy, "
                + "p.pricesell, "
                + "p.pricesellinc, "
                + "p.commission, "
                + "p.category, "
                + "p.taxcat, "
                + "p.attributeset_id, "
                + "p.image, "
                + "p.attributes, "
                + "p.iscatalog, "
                + "p.catorder, "
                + "p.iskitchen, "
                + "p.kitchendescription, "
                + "p.isservice, "
                + "p.display, "
                + "p.isvprice, "
                + "p.isverpatrib, "
                + "p.warranty, "
                + "p.alias, "
                + "p.alwaysavailable, "
                + "p.candiscount, "
                + "p.managestock, "
                + "p.remotedisplay, "
                + "p.burnvalue, "
                + "p.earnvalue, "
                + "p.loyaltymultiplier, "
                + "p.sellunit, "
                + "p.stockunit, "
                + "p.buyunit, "
                + "p.agerestricted, "
                + "p.isrecipe, "
                + "p.isingredient, "
                + "p.systemobject, "
                + "p.remoteprinter, "
                + "p.remotedisplayid, "
                + "p.averagecost, "
                + "p.siteguid, "
                + "p.salesobject, "
                + "p.depositobject ";
        return sel;
    }

    @Override
    public void init(Session s) {
        this.s = s;

        m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(s);

        m_updateRefund = new StaticSentence(s, "update ticketlines set refundqty = refundqty + ? where ticket = ? and line = ?  ", new SerializerWriteBasic(new Datas[]{
            Datas.DOUBLE,
            Datas.STRING,
            Datas.INT
        }));

        m_productname = new StaticSentence(s, "SELECT NAME FROM PRODUCTS WHERE ID = ? ",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        m_sellvoucher = new StaticSentence(s, "insert into vouchers ( voucherid, issueticketid, vouchervalue) "
                + "values (?, ?, ?)", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE}));

        m_addOrder = new StaticSentence(s,
                "insert into orders (id, orderid, qty, details, attributes, notes, ticketid, displayid, auxiliary) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.INT,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.INT,
            Datas.INT
        }));

        imageBytes = new PreparedSentence(s, "select image from products where id = ?  ",
                SerializerWriteString.INSTANCE, SerializerReadBytes.INSTANCE);

        resetImageResourceCache();

    }

    public String getSiteGUID() {
        try {
            return new StaticSentence(s,
                    "select guid from siteguid ",
                    null,
                    SerializerReadString.INSTANCE).find().toString();
        } catch (BasicException e) {
        }
        return null;
    }

    public String getExemptTaxDetails() {
        try {
            return new StaticSentence(s,
                    "select id from taxes where systemobject = true ",
                    null,
                    SerializerReadString.INSTANCE).find().toString();
        } catch (BasicException e) {
        }
        return null;
    }

    public final Row getProductsRow() {
        return productsRow;
    }

    public final String getTicket(String ticketid) throws BasicException {
        String record = (String) new StaticSentence(s, "select ticketid from tickets where id = ? ",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE).find(ticketid);
        return record;
    }

    public final Integer getAgeRestriction(String id) {
        try {
            Integer record = (Integer) new StaticSentence(s, "select agerestricted from products where id = ? ",
                    SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE).find(id);
            return record;
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public final Integer getAgeRestrictionCategory(String id) {
        try {
            Integer record = (Integer) new StaticSentence(s, "select c.agerestricted from categories as c join products as p on p.category=c.id where p.id = ? ",
                    SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE).find(id);
            return record;
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public final ProductInfoExt getProductInfo(String id) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "select "
                + getSelectFieldList()
                + " from products as p "
                + " where p.id = ? ",
                SerializerWriteString.INSTANCE,
                ProductInfoExt.getSerializerRead()).find(id);
    }

    public final BufferedImage getProductImage(String id) throws BasicException {
        byte[] resource;

        resource = productImageCache.get(id);

        if (resource == null) {
            try {
                resource = (byte[]) imageBytes.find(id);
                productImageCache.put(id, resource);
            } catch (BasicException e) {
                resource = null;
            }
        }

        try {
            return resource == null ? null : ImageIO.read(new ByteArrayInputStream(resource));
        } catch (IOException ex) {
            return null;
        }
    }

    public final void resetImageResourceCache() {
        productImageCache = new HashMap<>();
    }

    public final ProductInfoExt getDefaultProductInfo(String id, String siteGuid) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "select "
                + getSelectFieldList()
                + " from products as p "
                + " where p.id = ? and p.siteguid = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 1}),
                ProductInfoExt.getSerializerRead()).find(id, siteGuid);
    }

    public final ProductInfoExt getProductInfoByCode(String sCode) throws BasicException {
        if (sCode.startsWith("977")) {
            // This is an ISSN barcode (news and magazines) 
            // the first 3 digits correspond to the 977 prefix assigned to serial publications, 
            // the next 7 digits correspond to the ISSN of the publication 
            // Anything after that is publisher dependant - we strip everything after  
            // the 10th character 
            return (ProductInfoExt) new PreparedSentence(s, "select "
                    + getSelectFieldList()
                    + " from products as p "
                    + " where left(p.code, 10) = ?  ",
                    SerializerWriteString.INSTANCE,
                    ProductInfoExt.getSerializerRead()).find(sCode.substring(0, 10));
        }

        return (ProductInfoExt) new PreparedSentence(s, "select "
                + getSelectFieldList()
                + " from products as p "
                + " where p.code = ?  ",
                SerializerWriteString.INSTANCE,
                ProductInfoExt.getSerializerRead()).find(sCode);
    }

    public final ProductInfoExt getProductInfoByReference(String sReference, String siteGuid) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "select "
                + getSelectFieldList()
                + " from stockcurrent c right join products p on (c.product = p.id) "
                + " where reference = ? and p.siteguid = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}, new int[]{0, 1}),
                ProductInfoExt.getSerializerRead()).find(sReference, siteGuid);
    }

    public final ProductInfoExt getProductInfoNoSC(String id) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p where p.id = ? "
                + "order by p.id, p.reference, p.name ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).find(id);
    }

    public final boolean categoryUsed(String category) throws BasicException {
        try {
            Object m_result = new StaticSentence(s,
                    "select count(*) from  "
                    + " products "
                    + " where category = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadInteger.INSTANCE).find(category);
            return ((Integer) m_result != 0);
        } catch (BasicException e) {

        }
        return false;
    }

    public final List<CategoryInfo> getRootCategories(String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + "c.id, "
                + "c.name, "
                + "c.image, "
                + "c.catshowname, "
                + "c.isavailable, "
                + "c.buttontext, "
                + "c.catorder, "
                + "c.agerestricted "
                + "from categories as c "
                + "where c.siteguid = ? and parentid is null and c.isavailable = " + s.DB.TRUE() + " "
                + "order by name", SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).list(siteGuid);
    }

    public final List<CategoryInfo> getRootCategoriesByCatOrder(String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "image, "
                + "catshowname, "
                + "isavailable, "
                + "buttontext, "
                + "catorder, "
                + "agerestricted "
                + "from categories "
                + "where siteguid = ? and parentid is null and isavailable = " + s.DB.TRUE() + " and catorder is not null "
                + "order by catorder", SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).list(siteGuid);
    }

    public final List<CategoryInfo> getRootCategoriesByName(String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "image, "
                + "catshowname, "
                + "isavailable, "
                + "buttontext, "
                + "catorder, "
                + "agerestricted "
                + "from categories "
                + "where siteguid = ? and parentid is null and isavailable = " + s.DB.TRUE() + " and catorder is null "
                + "order by name", SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).list(siteGuid);
    }

    public final List<CategoryInfo> getSubcategories(String category, String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "image, "
                + "catshowname, "
                + "isavailable, "
                + "buttontext, "
                + "catorder, "
                + "agerestricted "
                + "from categories where siteguid = ? and parentid = ? order by name",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}, new int[]{1, 0}),
                CategoryInfo.getSerializerRead()).list(category, siteGuid);
    }

    public final List<CategoryInfo> getSubcategoriesByCatOrder(String category, String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "image, "
                + "catshowname, "
                + "isavailable, "
                + "buttontext, "
                + "catorder, "
                + "agerestricted "
                + "from categories where siteguid = ? and parentid = ? and catorder is not null order by catorder",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}, new int[]{1, 0}),
                CategoryInfo.getSerializerRead()).list(category, siteGuid);
    }

    public final List<CategoryInfo> getSubcategoriesByName(String category, String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "image, "
                + "catshowname, "
                + "isavailable, "
                + "buttontext, "
                + "catorder, "
                + "agerestricted "
                + "from categories where siteguid = ? and parentid = ? and catorder is null order by name",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}, new int[]{1, 0}),
                CategoryInfo.getSerializerRead()).list(category, siteGuid);
    }

    public List<ProductInfoExt> getProductCatalog(String category, String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p "
                + "where p.siteguid = ? and (p.iscatalog = " + s.DB.TRUE() + " and p.category = ?) or (p.alwaysavailable = " + s.DB.TRUE() + ") "
                + "order by p.catorder, p.name ",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}, new int[]{1, 0}),
                ProductInfoExt.getSerializerRead()).list(category, siteGuid);
    }

    public List<ProductInfoExt> getProductCatalogNormal(String category, String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p "
                + "where p.siteguid = ? and (p.iscatalog = " + s.DB.TRUE() + " and p.iscom = " + s.DB.FALSE() + " And p.category = ?) or (p.alwaysavailable = " + s.DB.TRUE() + ") "
                + "order by p.catorder, p.name ",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}, new int[]{1, 0}),
                ProductInfoExt.getSerializerRead()).list(category, siteGuid);
    }

    public List<ProductInfoExt> getBurnItemsEnoughPoints(Integer points) throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p "
                + "where burnvalue > 0 and burnvalue <= "
                + points
                + " order by name",
                null,
                ProductInfoExt.getSerializerRead()).list();
    }

    public List<ProductInfoExt> getBurnItemsInsufficientPoints(Integer points) throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p "
                + "where burnvalue > "
                + points
                + " order by name",
                null,
                ProductInfoExt.getSerializerRead()).list();
    }

    public List<ProductInfoExt> getAllProductCatalogByCatOrder(String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p "
                + "where p.siteguid =? and p.iscatalog = " + s.DB.TRUE() + " "
                + "order by p.catorder, p.name ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).list(siteGuid);
    }

    public List<ProductInfoExt> getAllNonProductCatalog() throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p "
                + "where p.iscatalog = " + s.DB.FALSE() + " "
                + "order by p.category, p.name ", null, ProductInfoExt.getSerializerRead()).list();
    }

    public List<ProductInfoExt> getAllProductCatalog(String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p "
                + "where p.siteguid =? and p.iscatalog = " + s.DB.TRUE() + " "
                + "order by p.category, p.name ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).list(siteGuid);
    }

    public List<ProductInfoExt> getProductCatalogAlways() throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from categories c inner join products p on (p.category = c.id) "
                + "where p.alwaysavailable = " + s.DB.TRUE() + " "
                + "order by  c.name, p.name",
                null,
                ProductInfoExt.getSerializerRead()).list();

    }

    public List<ProductInfoExt> getProductNonCatalog(String category) throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + "from products p "
                + "where p.iscatalog = " + s.DB.FALSE() + " "
                + "and p.category = ? "
                + "order by p.name ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).list(category);
    }

    public List<ProductInfoExt> getProductComments(String id, String siteGuid) throws BasicException {
        return new PreparedSentence(s, "select "
                + getSelectFieldList()
                + " from products p, products_com m "
                + "where p.iscatalog = " + s.DB.TRUE() + " "
                + "and p.id = m.product2 and m.product = ? "
                + "and p.iscom = " + s.DB.TRUE() + " and p.siteguid = ? "
                + "order by p.catorder, p.name",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 1}),
                ProductInfoExt.getSerializerRead()).list(id, siteGuid);
    }

    public final CategoryInfo getCategoryInfo(String id) throws BasicException {
        return (CategoryInfo) new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "image, "
                + "catshowname, "
                + "isavailable, "
                + "buttontext, "
                + "catorder, "
                + "agerestricted "
                + "from categories "
                + "where id = ? "
                + "order by name", SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).find(id);
    }

    public final SentenceList getProductList(String siteGuid) {
        return new StaticSentence(s, new QBFBuilder(
                "select "
                + getSelectFieldList()
                + ", (select units from stockcurrent where location = '" + TerminalInfo.getTerminalLocation() + "' and product = p.id ) as stockcount "
                + "from stockcurrent c right outer join products p on (c.product = p.id) "
                + "where ?(QBF_FILTER) "
                + "order by p.reference, p.name",
                new String[]{"p.code", "stockcount", "p.name", "p.category"}
        ), new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING
        }),
                ProductInfoExt.getSerializerRead()
        );
    }

    public SentenceList getProductListNormal(String siteGuid) {
        m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(s);
        Properties m_propsdb = m_dlSystem.getResourceAsProperties(AppConfig.getString("terminalID") + "/properties");
        return new StaticSentence(s, new QBFBuilder(
                "select "
                + getSelectFieldList()
                + ", (select units from stockcurrent where location = '" + TerminalInfo.getTerminalLocation() + "' and product = p.id ) "
                + "from stockcurrent c right outer join products p on (c.product = p.id) "
                + "where p.siteguid = '" + siteGuid + "' and p.iscom = " + s.DB.FALSE() + " and ?(QBF_FILTER) "
                + "order by p.reference, p.name",
                new String[]{"p.code", "units", "p.name", "p.category"}), new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING
        }), ProductInfoExt.getSerializerRead());
    }

    public SentenceList getProductListAuxiliar(String siteGuid) {
        m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(s);
        Properties m_propsdb = m_dlSystem.getResourceAsProperties(AppConfig.getString("terminalID") + "/properties");
        return new StaticSentence(s, new QBFBuilder(
                "select "
                + getSelectFieldList()
                + ", (select units from stockcurrent where  location = '" + TerminalInfo.getTerminalLocation() + "' and product = p.id) "
                + "from stockcurrent c right outer join products p on (c.product = p.id) "
                + "where p.siteguid = '" + siteGuid + "' and p.iscom = " + s.DB.TRUE() + " and ?(QBF_FILTER) "
                + "order by p.reference",
                new String[]{"p.code", "units", "p.name", "p.category"}), new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING
        }), ProductInfoExt.getSerializerRead());
    }

    public SentenceList getTicketsList() {
        return new StaticSentence(s, new QBFBuilder(
                "select "
                + "t.ticketid, "
                + "t.tickettype, "
                + "r.datenew, "
                + "p.name, "
                + "c.name, "
                //+ "sum(tl.soldprice * tl.units) "
                + "sum(pm.total) "
                + "from receipts as r "
                + "join tickets t on r.id = t.id left outer join payments pm "
                + "on r.id = pm.receipt left outer join customers c "
                + "on c.id = t.customer left outer join people p on t.person = p.id "
                //+ "join ticketlines as tl on tl.ticket = t.id "
                + "where ?(QBF_FILTER) "
                + "group by "
                + "t.id, "
                + "t.ticketid, "
                + "t.tickettype, "
                + "r.datenew, "
                + "p.name, "
                + "c.name "
                + "order by r.datenew desc, t.ticketid",
                new String[]{"t.ticketid", "t.tickettype", "tl.soldprice", "r.datenew", "r.datenew", "p.name", "c.name"}), new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.INT,
            Datas.OBJECT, Datas.INT,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.TIMESTAMP,
            Datas.OBJECT, Datas.TIMESTAMP,
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING}), new SerializerReadClass(FindTicketsInfo.class));
    }

    public final SentenceList getUserList() {
        return new StaticSentence(s, "select "
                + "id, "
                + "name "
                + "from people "
                + "order by name", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(
                        dr.getString(1),
                        dr.getString(2));
            }
        });
    }

    public final SentenceList getTaxList(String siteGuid) {
        return new StaticSentence(s, "select "
                + "id, "
                + "name, "
                + "category, "
                + "custcategory, "
                + "parentid, "
                + "rate, "
                + "ratecascade, "
                + "rateorder, "
                + "if ((select count(a.id) from taxes as a "
                + "join taxes as b on a.id=b.parentid where a.id = t.id)>0, true ,false ) as haschildren, "
                + "siteguid "
                + "from taxes as t "
                + "where siteguid = '"
                + siteGuid
                + "'"
                + " order by name", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxInfo(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getString(5),
                        dr.getDouble(6),
                        dr.getBoolean(7),
                        dr.getInt(8),
                        dr.getBoolean(9));
            }
        });
    }

    public List<TaxInfo> getTaxList() throws BasicException {
        return new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "category, "
                + "custcategory, "
                + "parentid, "
                + "rate, "
                + "ratecascade, "
                + "rateorder, "
                + "if ((select count(a.id) from taxes as a "
                + "join taxes as b on a.id=b.parentid where a.id = t.id)>0, true ,false ) as haschildren, "
                + "siteguid "
                + "from taxes as t "
                + " order by name", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxInfo(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getString(5),
                        dr.getDouble(6),
                        dr.getBoolean(7),
                        dr.getInt(8),
                        dr.getBoolean(9));
            }
        }).list();
    }

     public final TaxInfo getTaxByID(String id) throws BasicException {
         // If tax collection is globally disabled, return a zero-rate dummy tax
         if (ke.kalc.globals.SystemProperty.DISABLETAXES) {
             return new TaxInfo("DISABLED", "Tax Disabled", null, null, null, 0.0, false, 0, false);
         }
         return (TaxInfo) new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "category, "
                + "custcategory, "
                + "parentid, "
                + "rate, "
                + "ratecascade, "
                + "rateorder, "
                + "if ((select count(a.id) from taxes as a "
                + "join taxes as b on a.id=b.parentid where a.id = t.id)>0, true ,false ) as haschildren, "
                + "siteguid "
                + "from taxes as t "
                + "where id = ? ", SerializerWriteString.INSTANCE,
                TaxInfo.getSerializerRead()).find(id);
    }

     public final TaxInfo getTaxByCategoryID(String id) throws BasicException {
         // If tax collection is globally disabled, return a zero-rate dummy tax
         if (ke.kalc.globals.SystemProperty.DISABLETAXES) {
             return new TaxInfo("DISABLED", "Tax Disabled", null, null, null, 0.0, false, 0, false);
         }
         return (TaxInfo) new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "category, "
                + "custcategory, "
                + "parentid, "
                + "rate, "
                + "ratecascade, "
                + "rateorder, "
                + "if ((select count(a.id) from taxes as a "
                + "join taxes as b on a.id=b.parentid where a.id = t.id)>0, true ,false ) as haschildren, "
                + "siteguid "
                + "from taxes as t "
                + "where t.category = ? ", SerializerWriteString.INSTANCE,
                TaxInfo.getSerializerRead()).find(id);
    }

    public final List<TaxInfo> getTaxChildrenList(String parent) throws BasicException {
        return new PreparedSentence(s, "select "
                + "id, "
                + "name, "
                + "category, "
                + "custcategory, "
                + "parentid, "
                + "rate, "
                + "ratecascade, "
                + "rateorder, "
                + "if ((select count(a.id) from taxes as a "
                + "join taxes as b on a.id=b.parentid where a.id = t.id)>0, true ,false ) as haschildren, "
                + "siteguid "
                + "from taxes as t "
                + "where parentid = ? ",
                SerializerWriteString.INSTANCE, TaxInfo.getSerializerRead()).list(parent);
    }

    public final List<LineTaxRates> getLineTaxRates(String ticket, int line) throws BasicException {
        return new PreparedSentence(s, "select "
                + " id, line, basetax, basename, linenett, haschildren, childtax1, childtax2, childtax3, "
                + " childtax4, childtax5, childtax6, childtaxrate1, childtaxrate2, childtaxrate3, childtaxrate4, childtaxrate5, childtaxrate6, "
                + " childtaxname1, childtaxname2, childtaxname3, childtaxname4, childtaxname5, childtaxname6 "
                + "from linetaxrates "
                + "where id = ?  and line = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.INT}, new int[]{0, 1}),
                LineTaxRates.getSerializerRead()).list(ticket, line);
    }

    public final List<LineTaxRates> getLineTaxRates(String ticket) throws BasicException {
        return new PreparedSentence(s, "select "
                + " id, line, basetax, basename, linenett, haschildren, childtax1,  childtax2, childtax3, "
                + " childtax4, childtax5, childtax6, childtaxrate1, childtaxrate2, childtaxrate3, childtaxrate4, childtaxrate5, childtaxrate6, "
                + " childtaxname1, childtaxname2, childtaxname3, childtaxname4, childtaxname5, childtaxname6 "
                + "from linetaxrates "
                + "where id = ? ",
                SerializerWriteString.INSTANCE,
                LineTaxRates.getSerializerRead()).list(ticket);
    }

    public final SentenceList getCategoriesList() {
        return new StaticSentence(s, "select "
                + "id, "
                + "name, "
                + "null, "
                + "catshowname, "
                + "isavailable, "
                + "buttontext, "
                + "catorder, "
                + "agerestricted "
                + "from categories "
                + " order by name", null, CategoryInfo.getSerializerRead());
    }

    public final SentenceList getTaxCategoriesList(String guid) {
        return new StaticSentence(s, "select "
                + "t.id, "
                + "t.name "
                + "from taxcategories as t "
                + "join taxes as c on c.category = t.id "
                + "where t.siteguid = '"
                + guid
                + "' "
                + "and c.parentid is null "
                + "order by name", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public final SentenceList getUnusedTaxCategoriesList(String guid) {
        return new StaticSentence(s, "select "
                + " t.id,"
                + " t.name"
                + " from taxcategories as t"
                + " left join taxes as b on t.id = b.category"
                + " where t.siteguid = '"
                + guid
                + "' "
                + " and b.category is null "
                + " order by t.name",
                null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public final SentenceList getTaxCategoriesListNoParent(String guid) {
        return new StaticSentence(s, "select "
                + " t.id,"
                + " t.name"
                + " from taxcategories as t"
                + " join taxes as c on c.category = t.id"
                + " where t.siteguid = '"
                + guid
                + "' "
                + " and c.parentid is null and c.custcategory is null "
                + " order by t.name",
                null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public final TaxCategoryInfo getTaxCategory(String category, String guid) throws BasicException {
        return (TaxCategoryInfo) new PreparedSentence(s, "select "
                + " id,"
                + " name"
                + " from taxcategories "
                + " where id = ? "
                + " and siteguid = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 1}),
                new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }
        }).find(category, guid);
    }

    public final SentenceList getLocationsList(String siteGuid) {
        return new StaticSentence(s, "select "
                + "id, "
                + "name, "
                + "address, "
                + "siteguid "
                + "from locations "
                + "where siteguid = '"
                + siteGuid
                + "' order by name", null, new SerializerReadClass(LocationInfo.class
                ));
    }

    public final SentenceList getProductListList() {
        return new StaticSentence(s, "select distinct "
                + "listname from productlists "
                + "order by listname", null, new SerializerReadClass(ProductListInfo.class
                ));
    }

    public final SentenceList getProductListItems(String listName) {
        return new StaticSentence(s, "select "
                + "l.product, p.reference, p.name from productlists l left join products p "
                + "on p.id = l.product "
                + "where l.listname = '" + listName + "' "
                + "order by p.reference ",
                null, new SerializerReadClass(ProductListItem.class));
    }

    public final SentenceList getFloorsList(String siteGuid) {
        return new StaticSentence(s, "select id, name from floors where siteguid ='"
                + siteGuid
                + "' order by name", null,
                new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new FloorsInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public CustomerInfoExt findCustomerExt(String card) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s, "select "
                + "id, "
                + "customertype, "
                + "taxid, "
                + "name ,"
                + "taxcategory, "
                + "card, "
                + "maxdebt, "
                + "address, "
                + "address2, "
                + "postal, "
                + "city, "
                + "region,"
                + "country, "
                + "firstname, "
                + "lastname, "
                + "email, "
                + "phone,"
                + "phone2,"
                + "fax, "
                + "notes, "
                + "active, "
                + "curdate, "
                + "curdebt, "
                + "image, "
                + "discount, "
                + "dob, "
                + "loyaltycardid, "
                + "loyaltycardnumber, "
                + "loyaltyenabled, "
                + "marketable, "
                + "taxexempt, "
                + "reviewdate "
                + "from customers "
                + "where card = ? and active = " + s.DB.TRUE() + " "
                + "order by name", SerializerWriteString.INSTANCE,
                new CustomerExtRead()
        ).find(card);
    }

    public CustomerInfoExt loadCustomerExt(String id) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s, "select "
                + "id, "
                + "customertype, "
                + "taxid, "
                + "name ,"
                + "taxcategory, "
                + "card, "
                + "maxdebt, "
                + "address, "
                + "address2, "
                + "postal, "
                + "city, "
                + "region,"
                + "country, "
                + "firstname, "
                + "lastname, "
                + "email, "
                + "phone,"
                + "phone2,"
                + "fax, "
                + "notes, "
                + "active, "
                + "curdate, "
                + "curdebt, "
                + "image, "
                + "discount, "
                + "dob, "
                + "loyaltycardid, "
                + "loyaltycardnumber, "
                + "loyaltyenabled, "
                + "marketable, "
                + "taxexempt, "
                + "reviewdate "
                + "from customers where id = ?", SerializerWriteString.INSTANCE, new CustomerExtRead()).find(id);
    }

    public final boolean isCashActive(String id) throws BasicException {

        return new PreparedSentence(s,
                "select money from closedcash where dateend is null and money = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE).find(id)
                != null;
    }

    public final TicketInfo loadTicket(final int tickettype, final int ticketid) throws BasicException {
        TicketInfo ticket = (TicketInfo) new PreparedSentence(s, "select "
                + "t.id, "
                + "t.tickettype, "
                + "t.ticketid, "
                + "r.datenew, "
                + "r.money, "
                + "r.attributes, "
                + "p.id, "
                + "p.name, "
                + "t.customer, "
                + "t.terminal, "
                + "t.taxinclusive, "
                + "t.ecardnumber, "
                + "t.ecardbalance, "
                + "t.earnpoints, "
                + "t.burnpoints, "
                + "t.currentdebt, "
                + "t.ticketdiscount, "
                + "t.cardfees, "
                + "t.tlvcode, "
                + "t.ticketowner, "
                + "t.pickupid, "
                + "t.tabledetails, "
                + "t.waiter "
                + "from receipts r "
                + "join tickets t on r.id = t.id "
                + "left outer join people p on t.person = p.id "
                + "where t.tickettype = ? and t.ticketid = ?  "
                + "order by r.datenew desc",
                SerializerWriteParams.INSTANCE,
                new SerializerReadClass(TicketInfo.class)).find(new DataParams() {
            @Override
            public void writeValues() throws BasicException {
                setInt(1, tickettype);
                setInt(2, ticketid);
            }
        });

        if (ticket != null) {
            String customerid = ticket.getCustomerId();
            ticket.setCustomer(customerid == null
                    ? null
                    : loadCustomerExt(customerid));

            ticket.setLines(new PreparedSentence(s, "Select l.ticket, l.line, l.product, l.attributesetinstance_id, l.units, l.soldprice,  "
                    + " t.id, t.name, t.category,  "
                    + "t.custcategory, t.parentid, t.rate, t.ratecascade, t.rateorder, "
                    + "if ((select count(a.id) from taxes as a "
                    + "join taxes as b on a.id=b.parentid where a.id = t.id)>0, true ,false ) as haschildren, "
                    + "l.attributes, l.refundqty, l.taxinclusive,  "
                    + "l.soldpriceexc, l.priceinc, l.priceexc, l.buyprice, l.discounted "
                    + "from ticketlines l, taxes t where l.taxid = t.id and l.ticket = ? order by l.line", SerializerWriteString.INSTANCE, new SerializerReadClass(TicketLineInfo.class
                    )).list(ticket.getId()));

            ticket.setPayments(new PreparedSentence(s,
                    "select payment, total, transid, tendered, cardname, ecardnumber, ecardbalance from payments where receipt = ? ", SerializerWriteString.INSTANCE, new SerializerReadClass(PaymentInfoTicket.class
                    )).list(ticket.getId()));

            // ticket.setPickupId();
        }
        return ticket;
    }

    public final void saveTicket(final TicketInfo ticket, final String location, final LoyaltyCard loyaltyCard, Object ticketText, CustomerDeliveryInfo deliveryInfo) throws BasicException {
        Transaction t;
        t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {
                // Set Receipt Id
//                switch (ticket.getTicketType()) {
//                    case NORMAL:
//                        if (ticket.getTicketId() == 0) {
//                            ticket.setTicketId(getNextTicketIndex());
//                        }
//                        break;
//                    case REFUND:
//                        ticket.setTicketId(getNextTicketRefundIndex());
//                        break;
//                    case PAYMENT:
//                        ticket.setTicketId(getNextTicketPaymentIndex());
//                        break;
//                    case NOSALE:
//                        ticket.setTicketId(getNextTicketPaymentIndex());
//                        break;
//                    case INVOICE:
//                        ticket.setTicketId(getNextTicketInvoiceIndex());
//                        break;
//                    default:
//                        throw new BasicException();
//                }

                switch (ticket.getTicketType()) {
                    case NORMAL:
                    case PAYMENT:
                        if (ticket.getTicketId() == 0) {
                            ticket.setTicketId((Integer) s.DB.getSequenceSentence(s, "ticketsnum").find());
                        }
                        break;
                    default:
                        ticket.setTicketId((Integer) s.DB.getSequenceSentence(s, "ticketsnum_" + ticket.getTicketType().toString().toLowerCase()).find());
                        break;
                }

                new PreparedSentence(s, "insert into receipts (id, money, datenew, attributes, person) values (?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE
                ).exec(new DataParams() {
                    @Override
                    public void writeValues() {
                        try {
                            setString(1, ticket.getId());
                            setString(2, ticket.getActiveCash());
                            setTimestamp(3, ticket.getDate());
                            try {
                                ByteArrayOutputStream o = new ByteArrayOutputStream();
                                ticket.getProperties().storeToXML(o, AppLocal.APP_NAME, "UTF-8");
                                setBytes(4, o.toByteArray());
                            } catch (IOException e) {
                                setBytes(4, null);
                            }
                            setString(5, ticket.getUser().getId());
                        } catch (BasicException ex) {
                            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
                );

                ticket.setECardNumber((loyaltyCard != null) ? loyaltyCard.getCardNumber() : null);
                ticket.setECardBalance((loyaltyCard != null) ? loyaltyCard.getCardBalance() : null);
                ticket.setEarnPoints((loyaltyCard != null) ? loyaltyCard.getEarnedPoints(loyaltyCard.getCardNumber(), ticket) : null);
                ticket.setBurnPoints((loyaltyCard != null) ? loyaltyCard.getRedeemedPoints(loyaltyCard.getCardNumber(), ticket) : null);

                new PreparedSentence(s, "insert into tickets (id, tickettype, ticketid, person, customer, waiter, terminal, taxinclusive, "
                        + "ecardnumber, ecardbalance, earnpoints, burnpoints, currentdebt, location, ticketdiscount, cardfees, tlvcode, ticketowner, tabledetails, pickupid)"
                        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE).exec(new DataParams() {
                    @Override
                    public void writeValues() throws BasicException {
                        Double debt = 0.00;
                        if (ticket.getCustomerId() != null) {
                            if (ticket.getCustomerType().equalsIgnoreCase("account")) {
                                for (final PaymentInfo p : ticket.getPayments()) {
                                    if (p.getName().equalsIgnoreCase("debt")) {
                                        debt = debt + p.getPaid();
                                    }
                                }
                            }
                        }

                        setString(1, ticket.getId());
                        setInt(2, ticket.getTicketType().getId());
                        setInt(3, ticket.getTicketId());
                        setString(4, ticket.getUser().getId());
                        setString(5, ticket.getCustomerId());
                        setString(6, ticket.getWaiter());
                        setString(7, ticket.getHost());
                        setBoolean(8, ticket.isTaxInclusive());
                        setString(9, (loyaltyCard != null) ? loyaltyCard.getCardNumber() : null);
                        setInt(10, (loyaltyCard != null) ? loyaltyCard.getCardBalance() : null);
                        setInt(11, (loyaltyCard != null) ? loyaltyCard.getEarnedPoints(loyaltyCard.getCardNumber(), ticket) : null);
                        setInt(12, (loyaltyCard != null) ? loyaltyCard.getRedeemedPoints(loyaltyCard.getCardNumber(), ticket) : null);
                        setDouble(13, (ticket.getCustomerId() != null) ? ticket.getCustomerDebt() + debt : 0.00);
                        setString(14, location);
                        setDouble(15, ticket.getTicketDiscount());
                        setDouble(16, ticket.getCardFees());
                        Object[] tmp = new Object[5];
                        tmp[0] = (Company.NAME.isEmpty()) ? "Not defined in system" : Company.NAME;
                        tmp[1] = (Company.TAXNUMBER.isEmpty()) ? "Not defined in system" : Company.TAXNUMBER;
                        tmp[2] = ticket.getFormattedDate();
                        tmp[3] = ticket.getTicketTotal();
                        tmp[4] = ticket.printTax();
                        ticket.setTlvCode(Encoder.getTLVString(tmp));
                        setString(17, Encoder.getTLVString(tmp));
                        setString(18, (ticket.getTicketOwner() == null) ? ticket.getUser().getId() : ticket.getTicketOwner());
                        setString(19, (ticketText == null) ? "" : (String) ticketText);
                        setInt(20, ticket.getPickupId());
                    }
                }
                );

                SentenceExec ticketlineinsert = new PreparedSentence(s, "insert into ticketlines (ticket, linetype, line, product, attributesetinstance_id, "
                        + "units, soldprice, soldpriceexc, priceinc, priceexc, buyprice, taxid, attributes, refundqty, taxinclusive, taxrate, taxamount, commission, cardid, discounted) "
                        + "values (?, '"
                        + ticket.getTicketType()
                        // + "', ?, ?, ?, ?, abs(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", SerializerWriteBuilder.INSTANCE);
                        + "', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", SerializerWriteBuilder.INSTANCE);

                SentenceExec lineTaxRatesInsert = new PreparedSentence(s, "insert into linetaxrates (id, line, basetax, basename, linenett, haschildren, childtax1,  childtax2, childtax3, "
                        + " childtax4, childtax5, childtax6, childtaxrate1, childtaxrate2, childtaxrate3, childtaxrate4, childtaxrate5, childtaxrate6, "
                        + " childtaxname1, childtaxname2, childtaxname3, childtaxname4, childtaxname5, childtaxname6 ) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", SerializerWriteBuilder.INSTANCE);

                if (deliveryInfo != null) {

                    SentenceExec customerDeliveryInfo = new PreparedSentence(s, "insert into customer_delivery (id, name, addressline1, addressline2, addressline3, "
                            + "postcode, phone,  deliverydate, delivered, comments )"
                            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE);

                    customerDeliveryInfo.exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            setString(1, ticket.getId());
                            setString(2, deliveryInfo.fetchName());
                            setString(3, deliveryInfo.fetchAddressLine1());
                            setString(4, deliveryInfo.fetchAddressLine2());
                            setString(5, deliveryInfo.fetchAddressLine3());
                            setString(6, deliveryInfo.fetchPostCode());
                            setString(7, deliveryInfo.fetchPhone());
                            setTimestamp(8, deliveryInfo.fetchDeliveryDate());
                            setBoolean(9, deliveryInfo.isDelivered());
                            setString(10, deliveryInfo.fetchComments());
                        }
                    });
                }

                //Update stock holding details
                for (TicketLineInfo l : ticket.getLines()) {

                    ticketlineinsert.exec(l);

                    // Only record tax details if tax collection is enabled
                    if (!SystemProperty.DISABLETAXES) {
                        lineTaxRatesInsert.exec(new LineTaxRates(l, getTaxByID(l.getTaxInfo().getId())));
                    }

                    if (l.getProductID() != null & l.isProductService() != true
                            & l.getManageStock() == true || l.isRecipe() || l.getProductID().equalsIgnoreCase("DefaultProduct")) {

                        // update the stock
                        int reason = 0;
                        if (l.getMultiply() > 0.0 && l.getRefundQty() == 0.00) {
                            reason = (int) MovementReason.OUT_SALE.getKey();
                        } else {
                            reason = (int) MovementReason.IN_REFUND.getKey();
                        }

                        double units; // = 0.00;
                        if (l.getRefundQty() < 1.00) {
                            units = -l.getMultiply();
                        } else {
                            units = l.getRefundQty();
                        }

                        //Code to resolve refundit issue
                        if (ticket.getTicketType().toString().equals("REFUND")) {
                            reason = 2;
                            units = l.getMultiply();
                        }

                        if (l.isRecipe()) {
                            reason = (ticket.getTicketType().toString().equals("REFUND")) ? 11 : -11;
                        }

                        processStockDiaryInsert().exec(new Object[]{
                            ticket.getDate(),
                            reason,
                            location,
                            l.getProductID(),
                            units,
                            l.getAverageCost(),
                            l.getSoldPriceExe(),
                            Math.abs(l.getPrice()),
                            ticket.getUser().getId(),
                            getSiteGUID(),
                            l.isRecipe()
                        });
                    }
                }

                SentenceExec paymentinsert = new PreparedSentence(s,
                        " insert into payments (id, receipt, payment, description, total, transid, returnmsg, tendered, cardname, ecardnumber, ecardbalance)"
                        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE);

                for (final PaymentInfo p : ticket.getPayments()) {
                    paymentinsert.exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            setString(1, UUID.randomUUID().toString());
                            setString(2, ticket.getId());
                            setString(3, p.getName());
                            setString(4, p.getDescription());
                            setDouble(5, p.getTotal());
                            setString(6, ticket.getTransactionID());
                            setBytes(7, (byte[]) Formats.BYTEA.parseValue(ticket.getReturnMessage()));
                            setDouble(8, p.getTendered());
                            setString(9, p.getCardName());
                            setString(10, p.getECardNumber());
                            setDouble(11, p.getECardBalance());
                        }
                    });

                    if ("debt".equals(p.getName()) || "debtpaid".equals(p.getName())) {
                        ticket.getCustomer().updateCurDebt(p.getTotal(), ticket.getDate());
                        getDebtUpdate().exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setDouble(1, ticket.getCustomer().getCurrentDebt());
                                setTimestamp(2, ticket.getCustomer().getCurDate());
                                setString(3, ticket.getCustomer().getId());
                            }
                        });
                    }
                }

                SentenceExec taxlinesinsert = new PreparedSentence(s, "insert into taxlines (id, receipt, taxid, base, amount, rate)  values (?, ?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE);
                if (ticket.getTaxes() != null) {

                    for (final TicketTaxInfo tickettax : ticket.getTaxes()) {
                        taxlinesinsert.exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setString(1, UUID.randomUUID().toString());             //id
                                setString(2, ticket.getId());                           //receipt
                                setString(3, tickettax.getTaxInfo().getId());           //taxid
                                if (ticket.isTaxInclusive()) {
                                    setDouble(4, tickettax.getSubTotalIncluding());
                                    setDouble(5, tickettax.getTaxIncluding());
                                } else {
                                    setDouble(4, tickettax.getSubTotalExcluding());
                                    setDouble(5, tickettax.getTaxExcluding());
                                }
                                setDouble(6, tickettax.getTaxInfo().getRate());         //tax rate
                            }
                        });
                    }
                }
                return null;
            }
        };
        t.execute();
    }

    public final void deleteTicket(final TicketInfo ticket, final String location) throws BasicException {

        Transaction t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {
                // update the inventory
                Date date = new Date();
                for (int i = 0; i < ticket.getLinesCount(); i++) {
                    if (ticket.getLine(i).getProductID() != null
                            && ticket.getLine(i).getManageStock() == true) {

                        processStockDiaryInsert().exec(new Object[]{
                            date,
                            ticket.getLine(i).getMultiply() >= 0.0
                            ? MovementReason.IN_REFUND.getKey()
                            : MovementReason.OUT_SALE.getKey(),
                            location,
                            ticket.getLine(i).getProductID(),
                            //  ticket.getLine(i).getProductAttSetInstId(),
                            ticket.getLine(i).getMultiply(),
                            ticket.getLine(i).getAverageCost(),
                            ticket.getLine(i).getSoldPriceExe(),
                            ticket.getLine(i).getPrice(),
                            ticket.getUser().getName(),
                            getSiteGUID(),
                            ticket.getLine(i).isRecipe()
                        });
                    }
                }

                // update customer debts
                for (PaymentInfo p : ticket.getPayments()) {
                    if ("debt".equals(p.getName()) || "debtpaid".equals(p.getName())) {

                        // udate customer fields...
                        ticket.getCustomer().updateCurDebt(-p.getTotal(), ticket.getDate());

                        // save customer fields...
                        getDebtUpdate().exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setDouble(1, ticket.getCustomer().getCurrentDebt());
                                setTimestamp(2, ticket.getCustomer().getCurDate());
                                setString(3, ticket.getCustomer().getId());
                            }
                        });
                    }
                }

                // and delete the receipt
                new StaticSentence(s, "delete from taxlines where receipt = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "delete from payments where receipt = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "delete from ticketlines where ticket = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "delete from tickets where id = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "delete from receipts where id = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                return null;
            }
        };
        t.execute();
    }

    public final Integer getNextPickupIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "pickup_number").find();
    }

    public final Integer getNextTicketIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "ticketsnum").find();
    }

    public final Integer getNextTicketInvoiceIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "ticketsnum_invoice").find();
    }

    public final Integer getNextTicketRefundIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "ticketsnum_refund").find();
    }

    public final Integer getNextTicketPaymentIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "ticketsnum_payment").find();
    }

    public final SentenceExec getDebtUpdate() {
        return new PreparedSentence(s, "update customers set curdebt = ?, curdate = ? where id = ?",
                SerializerWriteParams.INSTANCE);
    }

    public final SentenceExec processStockDiaryInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                /* Set up adjust parameters */
                Object[] adjustParams = new Object[6];
                Object[] paramsArray = (Object[]) params;
                adjustParams[0] = paramsArray[2]; //Location
                adjustParams[1] = paramsArray[3]; //Product
                adjustParams[2] = paramsArray[4]; //units
                try {
                    adjustParams[3] = paramsArray[9]; //siteguid
                } catch (Exception ex) {
                    adjustParams[3] = getSiteGUID();
                }
                adjustParams[4] = paramsArray[0]; //date
                adjustParams[5] = paramsArray[8]; //user

                adjustStock(adjustParams);

                return new PreparedSentence(s,
                        " insert into stockdiary (datenew, reason, location, product, units, cost, price, priceinc, appuser, siteguid) "
                        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9})).exec(paramsArray);
            }
        };
    }

    private int adjustStock(Object params[]) throws BasicException {
        List<ProductsRecipeInfo> recipe = getRecipe((String) ((Object[]) params)[1]);
        // if the product is a recipe we need to process it
        if (recipe.size() > 0) {
            int as = 0;

            for (ProductsRecipeInfo component : recipe) {
                Object[] adjustParams = new Object[7];
                adjustParams[0] = params[0];
                adjustParams[1] = component.getProductKitId();
                adjustParams[2] = ((Double) params[2]) * component.getQuantity();
                adjustParams[3] = params[3];
                adjustParams[4] = params[4];
                adjustParams[5] = params[5];
                adjustParams[6] = (((Double) params[2]) * component.getQuantity() > 0) ? 99 : -99;

                Double cost = (Double) new PreparedSentence(s,
                        " select averagecost from products where id = ?",
                        SerializerWriteString.INSTANCE,
                        SerializerReadDouble.INSTANCE).find(component.getProductKitId());

                if (component.isManaged()) {
                    new PreparedSentence(s,
                            " insert into stockdiary (datenew, reason, location, product, units, cost, price, priceinc, appuser, siteguid) "
                            + " values (?, ?, ?, ?, ?, ?, 0.00, 0.00, ?, ?)",
                            new SerializerWriteBasicExt(
                                    new Datas[]{Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.STRING, Datas.STRING},
                                    new int[]{0, 1, 2, 3, 4, 5, 6, 7})).exec(
                            new Object[]{params[4], adjustParams[6], params[0], component.getProductKitId(),
                                ((Double) params[2]) * component.getQuantity(), cost,
                                params[5], params[3]});
                }
                as += adjustStock(adjustParams);
            }
            return as;  //number of ingredients

        } else {
            int updateresult;
            updateresult = new PreparedSentence(s, "update stockcurrent set units = (units + ?) where location = ? and product = ? and siteguid = ? ",
                    new SerializerWriteBasicExt(stockAdjustDatas, new int[]{2, 0, 1, 3})).exec(params);
            if (updateresult == 0) {
                new PreparedSentence(s, "insert into stockcurrent (location, product, units, siteguid) values (?, ?, ?, ?)",
                        new SerializerWriteBasicExt(stockAdjustDatas, new int[]{0, 1, 2, 3})).exec(params);
            }
            return 1;
        }
    }

    public final List<ProductsRecipeInfo> getRecipe(String productId) throws BasicException {
        return new PreparedSentence(s, "select "
                + "r.id, "
                + "r.product, "
                + "r.ingredient, "
                + "r.quantity, "
                + "p.managestock "
                + "from recipe_ingredients as r "
                + "join products as p on p.id=r.ingredient "
                + "where r.product = ? ", SerializerWriteString.INSTANCE, ProductsRecipeInfo.getSerializerRead()).list(productId);
    }

    public Object[] getAlternativeBarcode(String barcode, String siteGuid) throws BasicException {
        return (Object[]) new PreparedSentence(s, "select productid, sellingqty, uomfactor, description from barcodes where barcode = ? and siteguid = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 1}),
                new SerializerReadBasic(new Datas[]{Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.STRING})).find(barcode, siteGuid);
    }

    public String getParentProduct(String id) throws BasicException {
        return (String) new PreparedSentence(s, "select code from products where id = ? ",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE).find(id);
    }

    public String getParentName(String id) throws BasicException {
        return (String) new PreparedSentence(s, "select name from products where id = ? ",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE).find(id);
    }

    public void addProductListItem(String listName, String ProductID) throws BasicException {
        new PreparedSentence(s, "insert into productlists (listname, product) values ('"
                + listName + "','" + ProductID + "')", null).exec();
    }

    public void removeProductListItem(String listName, String ProductID) throws BasicException {
        new PreparedSentence(s, "delete from productlists where listname ='"
                + listName + "' and product = '" + ProductID + "'", null).exec();
    }

    public void removeProductList(String listName) throws BasicException {
        new PreparedSentence(s, "delete from productlists where listname ='"
                + listName + "'", null).exec();
    }

    public final SentenceExec getPaymentMovementInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s, "insert into receipts (id, money, datenew, person) values (?, ?, ?, ?)", new SerializerWriteBasicExt(paymenttabledatas, new int[]{0, 1, 2, 8})).exec(params);
                return new PreparedSentence(s, "insert into payments (id, receipt, payment, description, total, notes) values (?, ?, ?, ?, ?, ?)", new SerializerWriteBasicExt(paymenttabledatas, new int[]{3, 0, 4, 5, 6, 7})).exec(params);
            }
        };
    }

//    public final SentenceExec getPaymentMovementDelete() {
//        return new SentenceExecTransaction(s) {
//            @Override
//            public int execInTransaction(Object params) throws BasicException {
//                new PreparedSentence(s, "delete from payments where id = ?", new SerializerWriteBasicExt(paymenttabledatas, new int[]{3})).exec(params);
//                return new PreparedSentence(s, "delete from receipts where id = ?", new SerializerWriteBasicExt(paymenttabledatas, new int[]{0})).exec(params);
//            }
//        };
//    }
    public final Double getCustomerDebt(String id) throws BasicException {
        return (Double) new PreparedSentence(s, "select curdebt from customers where id = ? ",
                SerializerWriteString.INSTANCE, SerializerReadDouble.INSTANCE).find(id);

    }

    public final void updateRefundQty(Double qty, String ticket, Integer line) throws BasicException {
        m_updateRefund.exec(qty, ticket, line);
    }

    public final boolean getVoucher(String id) throws BasicException {
        return new PreparedSentence(s,
                "select issueticketid from vouchers where voucherid = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE).find(id)
                != null;
    }

    public final void sellVoucher(Object[] voucher) throws BasicException {
        m_sellvoucher.exec(voucher);
    }

    public int insertCustomer(Object params) throws BasicException {
        Object[] values = (Object[]) params;
        return new PreparedSentence(s, "insert into customers (id, customertype, taxid, name, taxcategory, card,  maxdebt, address, address2, postal, city, region, country, "
                + "firstname, lastname, email, phone, phone2, fax, notes, active, curdate, curdebt, image, discount, dob, loyaltycardid, loyaltycardnumber, loyaltyenabled, marketable )"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new SerializerWriteBasicExt(customersRow.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 30})).exec(params);

    }

    public final TableDefinition getTableLocations() {
        return new TableDefinition(s,
                "locations", new String[]{"id", "name", "address"},
                new String[]{"id", AppLocal.getIntString("label.locationname"), AppLocal.getIntString("label.location")},
                new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING},
                new Formats[]{Formats.STRING, Formats.STRING, Formats.STRING},
                new int[]{0},
                "name"
        );
    }

    public final void addOrder(String id, String orderId, Integer qty, String details, String attributes, String notes, String ticketId, Integer displayId, Integer auxiliary) throws BasicException {
        m_addOrder.exec(id, orderId, qty, details, attributes, notes, ticketId, displayId, auxiliary);

    }

    public final String getProductNameByCode(String sCode) throws BasicException {
        return (String) m_productname.find(sCode);
    }

    protected static class CustomerExtRead implements SerializerRead {

        /**
         *
         * @param dr
         * @return
         * @throws BasicException
         */
        @Override
        public Object readValues(DataRead dr) throws BasicException {
            CustomerInfoExt c = new CustomerInfoExt(
                    dr.getString(1));
            c.setCustomerType(dr.getString(2));
            c.setTaxid(dr.getString(3));
            c.setName(dr.getString(4));
            c.setTaxCategory(dr.getString(5));
            c.setCustomerCard(dr.getString(6));
            c.setMaxDebt(dr.getDouble(7));
            c.setAddress(dr.getString(8));
            c.setAddress2(dr.getString(9));
            c.setPostal(dr.getString(10));
            c.setCity(dr.getString(11));
            c.setRegion(dr.getString(12));
            c.setCountry(dr.getString(13));
            c.setFirstName(dr.getString(14));
            c.setLastName(dr.getString(15));
            c.setEmail(dr.getString(16));
            c.setPhone(dr.getString(17));
            c.setPhone2(dr.getString(18));
            c.setFax(dr.getString(19));
            c.setNotes(dr.getString(20));
            c.setActiveCustomer(dr.getBoolean(21));
            c.setCurDate(dr.getTimestamp(22));
            c.setCurrentDebt(dr.getDouble(23));
            c.setImage(ImageUtils.readImage(dr.getString(24)));
            c.setCustomerDiscount(dr.getDouble(25));
            c.setDob(dr.getTimestamp(26));
            c.setLoyaltyCardId(dr.getString(27));
            c.setLoyaltyCardNumber(dr.getString(28));
            c.setLoyaltyEnabled(dr.getBoolean(29));
            c.setMarketable(dr.getBoolean(30));
            c.setTaxExempt(dr.getBoolean(31));
            c.setReviewDate(dr.getTimestamp(32));
            return c;
        }
    }

    public final List<KeyedData> getRestrictedProducts() throws BasicException {
        return new PreparedSentence(s, "select "
                + "p.id, "
                + "greatest (p.agerestricted, c.agerestricted) AS agerestricted "
                + "from products as p "
                + "join categories as c on c.id = p.category "
                + "where p.agerestricted > 0 or c.agerestricted > 0 ", null, KeyedData.getSerializerRead()).list();
    }

}
