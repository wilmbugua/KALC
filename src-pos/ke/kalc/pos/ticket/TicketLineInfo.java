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
package ke.kalc.pos.ticket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.DataWrite;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.data.loader.SerializableWrite;
import ke.kalc.format.Formats;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.sales.TaxesLogic;
import ke.kalc.pos.util.StringUtils;

public class TicketLineInfo implements SerializableWrite, SerializableRead, Serializable {

    private static final long serialVersionUID = 6608012948284450199L;

    protected Boolean taxinclusive;
    protected Boolean updated = false;
    protected Double averagecost;
    protected Double orderQty;
    protected ProductInfoExt product;
    protected Properties attributes;
    protected String attsetinstid;
    protected String cardnumber;
    protected String productid;
    protected TaxInfo tax;
    protected double multiply;
    protected double pricebuy;
    protected double priceexc;
    protected double priceinc;
    protected double soldprice;
    protected double soldpriceexc;
    protected int m_iLine = -1;
    protected int itemEarnValue = 0;
    protected Double commission = 0.0;
    protected Double refundQty = 0.0;
    protected String m_sTicket = null;
    protected Boolean discounted = false;

    //Called by its own copy ticketline line 231
    public TicketLineInfo() {
        //System.out.println("Parameters  : 0");
        this.taxinclusive = SystemProperty.TAXINCLUDED;
        this.productid = null;
        this.attsetinstid = null;
        this.multiply = 0.0;
        this.soldprice = 0.0;
        this.tax = null;
        this.attributes = new Properties();
        this.refundQty = 0.00;
        this.commission = 0.00;
    }

    //2 parameters
    //Called by JTicketsBagTicket line 521
    //Called by JRefundLines 204, 227, 237, 266, 274
    //JProductLineEdit 91
    public TicketLineInfo(TicketLineInfo line, Boolean refund) {
        //System.out.println("Parameters  : 2");
        this.taxinclusive = SystemProperty.TAXINCLUDED;

        this.productid = line.productid;
        this.attsetinstid = line.attsetinstid;
        this.multiply = line.multiply;
        this.soldprice = line.soldprice;
        this.tax = line.tax;
        this.attributes = (Properties) line.attributes.clone();
        this.refundQty = 0.00;
        this.commission = line.commission;

        priceinc = line.priceinc;
        soldpriceexc = line.priceexc;
        priceexc = line.priceexc;
        pricebuy = line.pricebuy;
        averagecost = line.averagecost;

        product = getProductInfoExt(line.productid);
        this.itemEarnValue = product.getEarnValue();

        if (refund) {
            // System.out.println("Refund Qty : " + line.refundQty);
            //    product = getProductInfoExt(line.productid);
            product.setPriceSellInc(product.getSellingPrice() * -1.0);
            product.setPriceSell(product.getPriceSell() * -1.0);
            pricebuy = line.pricebuy;
            averagecost = line.pricebuy;
        }
    }

    //3 parameters
    //Called by JPanelTicket lines 1550, 1574, 1600, 1625, 1710
    public TicketLineInfo(TicketLineInfo line) {
        //System.out.println("Parameters  : 3");
        this.taxinclusive = SystemProperty.TAXINCLUDED;

        product = getProductInfoExt(line.productid);
        product.setImage(null);

        if (!line.productid.equals("DefaultProduct")) {
            priceinc = product.getPriceSellinc();
            priceexc = product.getPriceSell();
            pricebuy = product.getPriceBuy();
            soldpriceexc = product.getPriceSell();
        }

        this.averagecost = product.getAverageCost();
        this.productid = line.productid;
        this.attsetinstid = line.attsetinstid;
        this.multiply = line.multiply;
        this.soldprice = line.soldprice;
        this.tax = line.tax;
        this.attributes = (Properties) line.attributes.clone();
        this.refundQty = line.refundQty;
        this.commission = line.commission;
        this.itemEarnValue = product.getEarnValue();
    }

    //4 parameters
    //Called by JGiftCardEdit line277 and discount scripts
    public TicketLineInfo(ProductInfoExt product, double qtySold, double soldPrice, String cardNumber) {
        try {
            TaxesLogic taxesLogic = new TaxesLogic(DataLogicSales.getSession().getTaxList());
            this.tax = taxesLogic.getTaxInfo(product.getTaxCategoryID());
        } catch (BasicException ex) {
            Logger.getLogger(TicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.product = product;
        this.cardnumber = cardNumber;
        this.taxinclusive = SystemProperty.TAXINCLUDED;
        this.productid = product.getID();
        this.itemEarnValue = product.getEarnValue();
        product.setImage(null);

        attributes = new Properties();
        this.attributes.setProperty("product.name", product.getName());
        if (product.getKitchenDescription() == null || product.getKitchenDescription().isBlank()) {
            this.attributes.setProperty("kitchen.name", product.getName());
        } else {
            this.attributes.setProperty("kitchen.name", product.getKitchenDescription());
        }
        this.attributes.setProperty("product.alias", product.getAlias());
        this.attributes.setProperty("product.iscom", String.valueOf(product.isCom()));
        this.attributes.setProperty("product.warranty", String.valueOf(product.getWarranty()));
        this.attributes.setProperty("product.systemobject", String.valueOf(product.isSystemObject()));
        this.attributes.setProperty("product.managestock", String.valueOf(product.isManagedStock()));
        this.attributes.setProperty("product.kitchen", String.valueOf(product.isKitchen()));
        this.attributes.setProperty("usedisplay", String.valueOf(product.isRemoteDisplay()));
        this.attributes.setProperty("remote.printer", product.getRemotePrinter());

        this.priceinc = product.getSellingPrice();
        this.priceexc = product.getPriceSell();
        this.pricebuy = product.getPriceBuy();
        this.averagecost = product.getAverageCost();

        this.multiply = qtySold;
        this.soldprice = soldPrice;
        if (taxinclusive) {
            this.soldpriceexc = soldPrice - (soldPrice - (soldPrice / (1 + tax.getRate())));
        } else {
            this.soldpriceexc = soldPrice;
        }

        this.attsetinstid = null;
    }

    //5 parameters
    //Called by JPanelTicket to add TicketLineInfo to the current ticket    
    public TicketLineInfo(ProductInfoExt product, double qtySold, double soldPrice, TaxInfo tax, Properties attributes) {
        this.taxinclusive = SystemProperty.TAXINCLUDED;
        this.product = product;
        this.productid = product.getID();
        this.tax = tax;
        this.itemEarnValue = product.getEarnValue();

        product.setImage(null);

        if (taxinclusive) {
            this.priceinc = (product.isVprice()) ? soldPrice : product.getPriceSellinc();
            this.priceexc = (product.getPriceSell() == 0.00) ? soldPrice - (soldPrice - (soldPrice / (1 + tax.getRate()))) : product.getPriceSell();
            this.soldpriceexc = priceexc;
        } else {
            this.priceinc = (priceinc == 0.00) ? soldPrice * (1 + tax.getRate()) : product.getPriceSellinc();
            this.priceexc = soldPrice;
            this.soldpriceexc = priceexc;
        }

        this.averagecost = product.getAverageCost();
        this.pricebuy = product.getAverageCost();
        this.multiply = qtySold;
        this.soldprice = soldPrice;

        attsetinstid = null;
        this.attributes = attributes;
        this.attributes.setProperty("product.name", product.getName());
        if (product.getKitchenDescription() == null || product.getKitchenDescription().isBlank()) {
            this.attributes.setProperty("kitchen.name", product.getName());
        } else {
            this.attributes.setProperty("kitchen.name", product.getKitchenDescription());
        }
        this.attributes.setProperty("product.alias", product.getAlias());
        this.attributes.setProperty("product.iscom", String.valueOf(product.isCom()));
        this.attributes.setProperty("product.warranty", String.valueOf(product.getWarranty()));
        this.attributes.setProperty("product.systemobject", String.valueOf(product.isSystemObject()));
        this.attributes.setProperty("product.compulsoryattributes", String.valueOf(product.isVerpatrib()));
        this.attributes.setProperty("product.service", String.valueOf(product.isService()));
        this.attributes.setProperty("product.kitchen", String.valueOf(product.isKitchen()));
        this.attributes.setProperty("product.variableprice", String.valueOf(product.isVprice()));
        this.attributes.setProperty("usedisplay", String.valueOf(product.isRemoteDisplay()));
        this.attributes.setProperty("product.managestock", String.valueOf(product.isManagedStock()));
        this.attributes.setProperty("product.isrecipe", String.valueOf(product.isRecipe()));
        this.attributes.setProperty("remote.printer", product.getRemotePrinter());
        if (product.getAttributeSetID() != null) {
            this.attributes.setProperty("product.attsetid", product.getAttributeSetID());
        }
        this.commission = product.getCommission();
    }

    public TicketLineInfo copyTicketLine() {
        TicketLineInfo l = new TicketLineInfo();
        l.product = product;
        l.productid = productid;
        l.attsetinstid = attsetinstid;
        l.multiply = multiply;
        l.soldprice = soldprice;
        l.taxinclusive = taxinclusive;
        l.tax = tax;
        l.averagecost = averagecost;
        l.pricebuy = pricebuy;
        l.priceexc = priceexc;
        l.priceinc = priceinc;
        l.soldpriceexc = soldpriceexc;

        l.attributes = (Properties) attributes.clone();
        return l;
    }

    public ProductInfoExt getProductInfoExt(String id) {
        try {
            return DataLogicSales.getSession().getProductInfo(id);
        } catch (BasicException ex) {

        }
        return new ProductInfoExt();
    }

    public String getTaxCategory() {
        return product.getTaxCategoryID();
    }

    //Set up the ticketline to save to database
    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, m_sTicket);
        dp.setInt(2, m_iLine);
        dp.setString(3, productid);
        dp.setString(4, attsetinstid);
        dp.setDouble(5, multiply);
        dp.setDouble(6, soldprice);
        dp.setDouble(7, soldpriceexc);
        dp.setDouble(8, priceinc);
        dp.setDouble(9, priceexc);
        // dp.setDouble(10, pricebuy);
        dp.setDouble(10, averagecost);

        dp.setString(11, tax.getId());
        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            attributes.storeToXML(o, AppLocal.APP_NAME, "UTF-8");
            dp.setBytes(12, o.toByteArray());
        } catch (IOException e) {
            dp.setBytes(12, null);
        }
        dp.setDouble(13, refundQty);
        dp.setBoolean(14, taxinclusive);
        dp.setDouble(15, tax.getRate());
        if (taxinclusive) {
            dp.setDouble(16, (multiply * soldprice) - ((multiply * soldprice) / (1 + tax.getRate())));
        } else {
            dp.setDouble(16, tax.getRate() * soldprice * multiply);
        }
        dp.setDouble(17, commission);
        dp.setString(18, cardnumber);
        dp.setBoolean(19, discounted);

        //test if this is a refund line - temp patched fix
        if (soldprice < 0) {
            dp.setDouble(5, multiply * -1);
            dp.setDouble(6, Math.abs(soldprice));
        }

        if (refundQty != 0) {
            setPriceExcl(soldprice);
            dp.setDouble(7, getSoldPriceExe());
            dp.setDouble(8, -priceinc);
            dp.setDouble(9, -priceexc);
            dp.setDouble(10, -pricebuy);
        }
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sTicket = dr.getString(1);
        m_iLine = dr.getInt(2);
        productid = dr.getString(3);
        attsetinstid = dr.getString(4);
        multiply = dr.getDouble(5);
        soldprice = dr.getDouble(6);

        tax = new TaxInfo(
                dr.getString(7),
                dr.getString(8),
                dr.getString(9),
                dr.getString(10),
                dr.getString(11),
                dr.getDouble(12),
                dr.getBoolean(13),
                dr.getInt(14),
                dr.getBoolean(15));
        attributes = new Properties();
        try {
            byte[] img = dr.getBytes(16);
            if (img != null) {
                attributes.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }
        refundQty = dr.getDouble(17);
        taxinclusive = dr.getBoolean(18);
        soldpriceexc = dr.getDouble(19);
        priceinc = dr.getDouble(20);
        priceexc = dr.getDouble(21);
        pricebuy = dr.getDouble(22);
        discounted = dr.getBoolean(23);
    }

    // ***************************************************************************************************************************
    // ***************************************************************************************************************************
    // *************************************************************************************************************************** 
    public int getTicketLine() {
        return m_iLine;
    }

    public Double getRefundQty() {
        return refundQty;
    }

    public void setRefundQty(double qty) {
        refundQty = qty;
    }

    public String getProductName() {
        return attributes.getProperty("product.name");
    }

    public String getKitchenName() {
        return (attributes.getProperty("kitchen.name").isEmpty())
                ? attributes.getProperty("product.name")
                : StringUtils.encodeXML(attributes.getProperty("kitchen.name"));
    }

    public String getProductAttSetId() {
        return attributes.getProperty("product.attsetid");
    }

    public String getProductAttSetInstDesc() {
        return attributes.getProperty("product.attsetdesc", "");
    }

    public void setProductAttSetInstDesc(String value) {
        if (value == null) {
            attributes.remove(value);
        } else {
            attributes.setProperty("product.attsetdesc", value);
        }
    }

    public String getProductTaxCategoryID() {
        return (product.getTaxCategoryID());
    }

    public double getMultiply() {
        if (multiply < 0) {
            return multiply * -1;
        } else {
            return multiply;
        }
    }

    public void setMultiply(double dValue) {
        multiply = dValue;
    }

    public double getPrice() {
        return soldprice;
    }

    public void setSoldPrice(Double price) {
        soldprice = price;
    }

    public double getPriceExc() {
        return priceexc;
    }

    public double getBuyPrice() {
        return pricebuy;
    }

    public String printBuyPrice() {
        return Formats.CURRENCY.formatValue(pricebuy);
    }

    public double getLinePrice() {
        return soldprice * getMultiply();
    }

    public void setPrice(double dValue) {
        soldprice = dValue;
    }

    public double getPriceTax() {
        if (soldprice < 0) {
            return soldprice * -1 * (1.0 + getTaxRate());
        } else {
            return soldprice * (1.0 + getTaxRate());
        }
    }

    public TaxInfo getTaxInfo() {
        return tax;
    }

    public void setTaxInfo(TaxInfo value) {
        tax = value;
    }

    public void setProperty(String key, String value) {
        if (key.equals("product.attsetid") && value == null) {
            return;
        }
        attributes.setProperty(key, value);
    }

    public void removeProperty(String key) {
        attributes.remove(key);
    }

    public void setProperty(String key, Double value) {
        attributes.setProperty(key, String.valueOf(value));
    }

    public Double getPropertyDouble(String value) {
        return (attributes.getProperty(value) == null) ? 0.00 : Double.parseDouble(attributes.getProperty(value));
    }

    public Properties getProperties() {
        return attributes;
    }

    public double getTaxRate() {
        return tax == null ? 0.0 : tax.getRate();
    }

    public double getSubValue() {
        return soldprice * multiply;
    }

    public double getTax() {
        return soldprice * multiply * getTaxRate();
    }

    public double getValue() {
        return this.soldprice * multiply * (1.0 + getTaxRate());
    }

    public double getValueExcl() {
        return this.soldprice * multiply;
    }

    public String getBarcode() {
        return product.getCode();
    }

    public String printMultiply() {
        return Formats.DOUBLE.formatValue(getMultiply());
    }

    public String printRefundQty() {
        return Formats.DOUBLE.formatValue(refundQty);
    }

    public String printPrice() {
        return Formats.CURRENCY.formatValue(getPrice());
    }

    public String printPriceTax() {
        return Formats.CURRENCY.formatValue(getPriceTax());
    }

    public String printTax() {
        return Formats.CURRENCY.formatValue(getTax());
    }

    public String printTaxRate() {
        return Formats.PERCENT.formatValue(getTaxRate());
    }

    public String printSubValue() {
        return Formats.CURRENCY.formatValue(getSubValue());
    }

    public String printValue() {
        return Formats.CURRENCY.formatValue(getValue());
    }

    public String printDecimalValueExcl() {
        return Formats.CURRENCYNS.formatValue(getValueExcl());
    }

    public String printValueExcl() {
        return Formats.CURRENCY.formatValue(getValueExcl());
    }

    public Boolean isTaxInclusive() {
        return taxinclusive;
    }

    public String getDecimalSingleRetailPrice() {
        return Formats.DECIMAL.formatValue(soldprice);
    }

    public String getSingleRetailPrice() {
        return Formats.CURRENCY.formatValue(soldprice);
    }

//    public String getRetail() {
//        return Formats.CURRENCY.formatValue(product.getUnitPrice(multiply));
//    }
    public Double getSellingPrice() {
        return priceinc;
    }

    public void setSellingPrice(Double value) {
        product.setSellingPrice(value);
    }

    public String printPriceIncludingTax() {
        return Formats.CURRENCY.formatValue(getPriceTax());
    }

    public boolean warrantyRequired() {
        return (attributes.getProperty("product.warranty") == null) ? false : Boolean.valueOf(attributes.getProperty("product.warranty"));
    }

    public String printName() {
        return StringUtils.encodeXML(attributes.getProperty("product.name"));
    }

    public String printKitchenName() {
        return StringUtils.encodeXML(attributes.getProperty("kitchen.name"));
    }

    public String printAlias() {
        return StringUtils.encodeXML(attributes.getProperty("product.alias"));
    }

    public Boolean isSystemObject() {
        return (attributes.getProperty("product.systemobject") == null) ? false : Boolean.valueOf(attributes.getProperty("product.systemobject"));
    }

    public Boolean isProductCom() {
        return (attributes.getProperty("product.iscom") == null) ? false : Boolean.valueOf(attributes.getProperty("product.iscom"));
    }

    public Boolean compulsoryAttributes() {
        return (attributes.getProperty("product.compulsoryattributes") == null) ? false : Boolean.valueOf(attributes.getProperty("product.compulsoryattributes"));
    }

    public Boolean isKitchen() {
        return product.isKitchen();
    }

    public Boolean isProductKitchen() {
        return (attributes.getProperty("product.kitchen") == null) ? false : Boolean.valueOf(attributes.getProperty("product.kitchen"));
    }

    public Boolean isProductDisplay() {
        return (attributes.getProperty("usedisplay") == null) ? false : Boolean.valueOf(attributes.getProperty("usedisplay"));
    }

    public int getDisplayId() {
        return (product.isRemoteDisplay()) ? product.getDisplayid() : 0;
    }

    public Boolean isProductService() {
        return (attributes.getProperty("product.service") == null) ? false : Boolean.valueOf(attributes.getProperty("product.service"));
    }

    public Boolean isProductVprice() {
        return (attributes.getProperty("product.variableprice") == null) ? false : Boolean.valueOf(attributes.getProperty("product.variableprice"));
    }

    public Boolean isRecipe() {
        return (attributes.getProperty("product.isrecipe") == null) ? false : Boolean.valueOf(attributes.getProperty("product.isrecipe"));
    }

    public Boolean getManageStock() {
        return (attributes.getProperty("product.managestock") == null) ? false : Boolean.valueOf(attributes.getProperty("product.managestock"));
    }

    public Boolean isDiscounted() {
        return (attributes.getProperty("product.discounted") == null) ? false : (attributes.getProperty("product.discounted").equalsIgnoreCase("yes")) ? true : false;
    }

    public Boolean isTaxExempt() {
        return (attributes.getProperty("product.taxexempt") == null) ? false : (attributes.getProperty("product.taxexempt").equalsIgnoreCase("yes")) ? true : false;
    }

    //Retained to maintain compatabilty with scripts until full rewrite
    public String getDiscounted() {
        return (attributes.getProperty("product.discounted") == null) ? "no" : attributes.getProperty("product.discounted");
    }

    public String getProperty(String key) {
        return attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

    public Double getAverageCost() {
        return averagecost;
    }

    public String getProductID() {
        return productid;
    }

    public Boolean isServiceCharge() {
        return productid.equalsIgnoreCase("ServiceCharge");
    }

    public Boolean isDeliveryCharge() {
        return productid.equalsIgnoreCase("deliverycharge");
    }

    public Boolean isGiftCardSale() {
        return productid.equalsIgnoreCase("giftcard-sale");
    }

    public Boolean isGiftCardTopUp() {
        return productid.equalsIgnoreCase("giftcard-topup");
    }

    public void setUpdated(Boolean value) {
        updated = value;
    }

    public boolean getUpdated() {
        return updated;
    }

    public Double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(double qty) {
        orderQty = qty;
    }

    public String getProductAttSetInstId() {
        return attsetinstid;
    }

    public void setProductAttSetInstId(String value) {
        attsetinstid = value;
    }

    public void setProductInfoExt(ProductInfoExt prod) {
        product = prod;
    }

    public double getSoldPriceExe() {
        return soldpriceexc;
    }

    public void setPriceExcl(Double value) {
        soldpriceexc = value / (1.0 + getTaxRate());
    }

    public ProductInfoExt getProductInfoExt() {
        return product;
    }

    public String getRemotePrinter() {
        return attributes.getProperty("remote.printer");
    }

    public int getItemEarnValue() {
        return itemEarnValue;
    }

    void setTicket(String ticket, int line) {
        m_sTicket = ticket;
        m_iLine = line;
    }

    public String getTicket() {
        return m_sTicket;
    }

    public void setRefundTicket(String ticket, int line) {
        m_sTicket = ticket;
        m_iLine = line;
    }

    public Boolean hasAttributes() {
        return ((product == null) ? false : product.getAttributeSetID() != null);
    }

    public Boolean isTierPriced() {
        return (attributes.getProperty("tierpriced") == null) ? false : Boolean.valueOf(attributes.getProperty("tierpriced"));
    }

    public Double getProductPriceExc() {
        return product.getPriceSell();
    }

    public String getProductOrigName() {
        return product.getName();
    }

    public void setSoldPriceExc(Double price) {
        soldpriceexc = price;
    }

    public void setChangedPrice(Double priceexcl, Double priceinc) {
        this.soldpriceexc = priceexcl;
        this.soldprice = (SystemProperty.TAXINCLUDED) ? priceinc : priceexcl;
    }

    public void reflectChangedPrice(Double priceexcl, Double priceinc) {
        this.priceexc = priceexcl;
        this.soldpriceexc = priceexcl;
        this.priceinc = priceinc;
        this.soldprice = (SystemProperty.TAXINCLUDED) ? priceinc : priceexcl;
    }

    public void setKitchenDisplay(ProductInfoExt product, String printer) {
        System.out.println(product.isKitchen());
        product.setKitchen(product.isKitchen());
        product.setRemotePrinter(printer);
    }

// Tickeline discount information   
// Contains methods to be depricated and new method to improve code readability
    public boolean discountAllowed() {
        return (product == null) ? false : (product.getDiscount() != -1.00);
    }

//    public Double getDiscount() {
//        return (product == null) ? 0.00 : product.getDiscount();
//    }
    public void setDiscounted(String value) {
        attributes.setProperty("product.discounted", value);
        this.discounted = (value.equalsIgnoreCase("yes")) ? true : false;
    }

    //new methods    
    public boolean discountNotAllowed() {
        return !discountAllowed();
    }

    public Double getDiscountRate() {
        return (product == null) ? 0.00 : product.getDiscount();
    }

    public void setDiscountedRate(String value) {
        attributes.setProperty("product.discounted", value);
        this.discounted = (value.equalsIgnoreCase("yes")) ? true : false;
    }

    public Double getMaxDiscountRate() {
        return product.getDiscount();
    }

}
