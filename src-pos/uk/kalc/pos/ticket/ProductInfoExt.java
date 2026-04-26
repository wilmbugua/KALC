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
package uk.kalc.pos.ticket;

import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.ImageUtils;
import uk.kalc.data.loader.SerializerRead;
import uk.kalc.format.Formats;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import uk.kalc.globals.SystemProperty;
import uk.kalc.pos.datalogic.DataLogicSales;
import uk.kalc.pos.inventory.TierPricing;

public class ProductInfoExt implements Serializable {

    private static final long serialVersionUID = 7587696873037L;

    protected String m_ID = null;
    protected String m_sRef = "0000";
    protected String m_sCode = "0000";
    protected String m_sCodetype = null;
    protected String m_sName = null;
    protected Boolean m_bCom = false;
    protected Boolean m_bScale = false;
    protected Double m_dPriceBuy = 0.00;
    protected Double m_dPriceSell = 0.00;
    protected Double m_dPriceSellinc = 0.00;
    protected Double m_dCommission = 0.00;
    protected String categoryid = null;
    protected String taxcategoryid = null;
    protected String attributesetid = null;
    protected BufferedImage m_Image = null;
    protected Boolean m_bKitchen = false;
    protected String kitchendescription = "";
    protected Boolean m_bService = false;
    protected Properties m_attributes = new Properties();
    protected String m_sDisplay = null;
    protected Boolean m_bVprice = false;
    protected Boolean m_bVerpatrib = false;
    protected Boolean m_bWarranty = false;
    protected Double m_dStockUnits = 0.00;
    protected String m_sAlias = null;
    protected Boolean m_bAlwaysAvailable = false;
    protected Double m_canDiscount = -1.00;
    protected Boolean m_bCatalog = true;
    protected Double m_catorder = 0.00;
    protected Boolean m_manageStock = true;
    protected String m_supplier = null;
    protected Boolean m_jRemoteDisplay = false;
    protected Integer m_jBurnValue = 0;
    protected Integer m_jEarnValue = 0;
    protected Double m_jLoyaltyMultiplier = 1.0;
    protected String m_sellunit = "000-each";
    protected String m_stockunit = "000-each";
    protected String m_buyunit = "000-each";
    protected String m_siteguid = null;
    protected List<TierPricing> m_pricing;
    protected Boolean m_systemObject = false;
    protected Integer agerestricted = -1;
    protected Boolean isRecipe = false;
    protected Boolean hasTiering;
    protected String remoteprinter = null;
    protected Integer displayid = 0;
    protected Double averagecost = 0.00;
    protected Boolean isSalesObject = true;
    protected Boolean isDepositObject = false;
    protected Double stockLevel = 0.0;

    public ProductInfoExt() {

    }

    public List<TierPricing> getPricing() {
        return m_pricing;
    }

    public Boolean hasTierPringing() {
        if (m_pricing == null) {
            return false;
        }
        for (TierPricing t : m_pricing) {

            if (t.getTierPrice() > 0.0) {
                return true;
            }
        }
        return false;

    }

    public Double getPriceSellinc() {
        return m_dPriceSellinc;
    }

    public void setPriceSellInc(Double m_dPriceSellinc) {
        this.m_dPriceSellinc = m_dPriceSellinc;
    }

    public String getSellUnit() {
        return m_sellunit;
    }

    public String getStockUnit() {
        return m_stockunit;
    }

    public String getbuyUnit() {
        return m_buyunit;
    }

    public void setSellUnit(String unit) {
        m_sellunit = unit;
    }

    public void setStockUnit(String unit) {
        m_stockunit = unit;
    }

    public void setbuyUnit(String unit) {
        m_buyunit = unit;
    }

    public Integer getBurnValue() {
        return m_jBurnValue;
    }

    public void setBurnValue(Integer m_jBurnValue) {
        this.m_jBurnValue = m_jBurnValue;
    }

    public Integer getEarnValue() {
        return m_jEarnValue;
    }

    public void setEarnValue(Integer m_jEarnValue) {
        this.m_jEarnValue = m_jEarnValue;
    }

    public Double getLoyaltyMultiplier() {
        return m_jLoyaltyMultiplier;
    }

    public void setLoyaltyMultiplier(Double m_jLoyaltyMultiplier) {
        this.m_jLoyaltyMultiplier = m_jLoyaltyMultiplier;
    }

    public Boolean isRemoteDisplay() {
        return m_jRemoteDisplay;
    }

    public void setRemoteDisplay(Boolean remoteDisplay) {
        this.m_jRemoteDisplay = remoteDisplay;
    }

    public String getSiteguid() {
        return m_siteguid;
    }

    public void setSiteguid(String siteguid) {
        this.m_siteguid = siteguid;
    }

    public final String getID() {
        return m_ID;
    }

    public final void setID(String id) {
        m_ID = id;
    }

    public final String getReference() {
        return m_sRef;
    }

    public final void setReference(String sRef) {
        m_sRef = sRef;
    }

    public final String getCode() {
        return m_sCode;
    }

    public final void setCode(String sCode) {
        m_sCode = sCode;
    }

    public final String getCodetype() {
        return m_sCodetype;
    }

    public String getSupplier() {
        return m_supplier;
    }

    public void setSupplier(String m_supplier) {
        this.m_supplier = m_supplier;
    }

    public final void setCodetype(String sCodetype) {
        m_sCodetype = sCodetype;
    }

    public final String getName() {
        return m_sName;
    }

    public final void setName(String sName) {
        m_sName = sName;
    }

    public final String getDisplay() {
        return m_sDisplay;
    }

    public final void setDisplay(String sDisplay) {
        m_sDisplay = sDisplay;
    }

    public final Boolean isCom() {
        return m_bCom;
    }

    public final void setCom(Boolean bValue) {
        m_bCom = bValue;
    }

    public final Boolean isScale() {
        return m_bScale;
    }

    public final void setScale(Boolean bValue) {
        m_bScale = bValue;
    }

    public final Boolean isKitchen() {
        return m_bKitchen;
    }

    public final void setKitchen(Boolean bValue) {
        m_bKitchen = bValue;
    }

    public final Boolean isService() {
        return m_bService;
    }

    public final void setService(Boolean bValue) {
        m_bService = bValue;
    }

    public final Boolean isVprice() {
        return m_bVprice;
    }

    public final Boolean isVerpatrib() {
        return m_bVerpatrib;
    }

    public final Boolean getWarranty() {
        return m_bWarranty;
    }

    public final void setWarranty(Boolean bValue) {
        m_bWarranty = bValue;
    }

    public final String getCategoryID() {
        return categoryid;
    }

    public final void setCategoryID(String sCategoryID) {
        categoryid = sCategoryID;
    }

    public final String getTaxCategoryID() {
        return taxcategoryid;
    }

    public final void setTaxCategoryID(String value) {
        taxcategoryid = value;
    }

    public final String getAttributeSetID() {
        return attributesetid;
    }

    public final void setAttributeSetID(String value) {
        attributesetid = value;
    }

    public final Double getPriceBuy() {
        return m_dPriceBuy;
    }

    public final void setPriceBuy(Double dPrice) {
        m_dPriceBuy = dPrice;
    }

    public final Double getCommission() {
        return m_dCommission;
    }

    public final void setCommission(Double dCommission) {
        m_dCommission = dCommission;
    }

    public final Double getPriceSell() {
        return m_dPriceSell;
    }

    public final void setPriceSell(Double dPrice) {
        m_dPriceSell = dPrice;
    }

    public final Double getStockUnits() {
        return m_dStockUnits;
    }

    public final void setStockUnits(Double dStockUnits) {
        m_dStockUnits = dStockUnits;
    }

    public final Double getPriceSellTax(TaxInfo tax) {
        return m_dPriceSell * (1.0 + tax.getRate());
    }

    public final Boolean getInCatalog() {
        return m_bCatalog;
    }

    public final Double getCatOrder() {
        return m_catorder;
    }

    public String printPriceSell() {
        return Formats.CURRENCY.formatValue(getPriceSell());
    }

    public String printPriceSellInc() {
        return Formats.CURRENCY.formatValue(m_dPriceSellinc);
    }

    public String getButtonPrice() {
        if (m_bVprice) {
            return "-.--";
        }
        if (SystemProperty.TAXINCLUDED) {
            return Formats.CURRENCY.formatValue(getPriceSell());
        } else {
            return Formats.CURRENCY.formatValue(getPriceSell());
        }
    }

    public String printPriceSellTax(TaxInfo tax) {
        return Formats.CURRENCY.formatValue(getPriceSellTax(tax));
    }

    public BufferedImage getImage() {
        return m_Image;
    }

    public void setImage(BufferedImage img) {
        m_Image = img;
    }

    public String getProperty(String key) {
        return m_attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return m_attributes.getProperty(key, defaultvalue);
    }

    public void setProperty(String key, String value) {
        m_attributes.setProperty(key, value);
    }

    public Properties getProperties() {
        return m_attributes;
    }

    public final String getAlias() {
        return (m_sAlias == null) ? "" : m_sAlias;
    }

    public final void setAlias(String alias) {
        m_sAlias = alias;
    }

    public final boolean getAlwaysAvailable() {
        return m_bAlwaysAvailable;
    }

    public final Boolean isManagedStock() {
        return m_manageStock;
    }

    public final void setManageStock(Boolean bValue) {
        m_manageStock = bValue;
    }

    public final void setAlwaysAvailable(Boolean bValue) {
        m_bAlwaysAvailable = bValue;
    }

    public Boolean canDiscount() {
        return (m_canDiscount != -1.00);
    }

    public Double getDiscount() {
        return m_canDiscount;
    }

    public Boolean isAgeRestricted() {
        return (agerestricted != -1.00);
    }

    public Integer getRestrictionAge() {
        return agerestricted;
    }

    public Boolean isSystemObject() {
        return m_systemObject;
    }

    public void setSystemObject(Boolean value) {
        m_systemObject = value;
    }

    public String getRemotePrinter() {
        return (remoteprinter == null) ? "" : remoteprinter;
    }

    public void setRemotePrinter(String remoteprinter) {
        this.remoteprinter = remoteprinter;
    }

    public Integer getDisplayid() {
        return displayid;
    }

    public void setDisplayID(Integer displayid) {
        this.displayid = displayid;
    }

    public Double getAverageCost() {
        return averagecost;
    }

    public Boolean isRecipe() {
        return isRecipe;
    }

    public Boolean isSalesObject() {
        return isSalesObject;
    }

    public Boolean isDepositObject() {
        return isDepositObject;
    }

    public Double getUnitPrice(Double count) {
        Double price = (SystemProperty.TAXINCLUDED) ? m_dPriceSellinc : m_dPriceSell;
        DecimalFormat df2 = new DecimalFormat("#.##");
        //  price = (SystemProperty.TAXINCLUDED) ? m_dPriceSellinc : m_dPriceSell;
        if (m_pricing != null) {
            for (TierPricing p : m_pricing) {
                if (p.getTierQty() > 0) {
                    if (count >= p.getTierQty()) {
                        price = (SystemProperty.TAXINCLUDED) ? p.getTierPriceInc() : p.getTierPrice();
                    }
                }
            }
        }
        return price;
    }

    public Double getUnitPriceExempt(Double count) {
        Double price = m_dPriceSell;
        DecimalFormat df2 = new DecimalFormat("#.##");
        if (m_pricing != null) {
            for (TierPricing p : m_pricing) {
                if (p.getTierQty() > 0) {
                    if (count >= p.getTierQty()) {
                        price = p.getTierPrice();
                    }
                }
            }
        }
        return price;
    }

    public Boolean isTierPriced(Double count) {
        if (m_pricing != null) {
            for (TierPricing p : m_pricing) {
                if (p.getTierQty() > 0) {
                    if (count >= p.getTierQty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setSellingPrice(Double value) {
        m_dPriceSellinc = value;
    }

    public Double getSellingPrice() {
        return m_dPriceSellinc;
    }

    public String getKitchenDescription() {
        return kitchendescription;
    }

    public void setKitchenDescription(String kitchendescription) {
        this.kitchendescription = kitchendescription;
    }

    public Double getStockLevel() {
        return stockLevel;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {

                // If this assert fails it is likely a coding error
                // Look at the number of fields fetched by the SELECT statement
                // and cross check for a mismatch in the INDEX_xxx fields in
                // DataLogicSales
                assert (dr.getDataField().length == DataLogicSales.FIELD_COUNT);

                ProductInfoExt product = new ProductInfoExt();
                product.m_ID = dr.getString(DataLogicSales.INDEX_ID + 1);
                product.m_sRef = dr.getString(DataLogicSales.INDEX_REFERENCE + 1);
                product.m_sCode = dr.getString(DataLogicSales.INDEX_CODE + 1);
                product.m_sCodetype = dr.getString(DataLogicSales.INDEX_CODETYPE + 1);
                product.m_sName = dr.getString(DataLogicSales.INDEX_NAME + 1);
                product.m_bCom = dr.getBoolean(DataLogicSales.INDEX_ISCOM + 1);
                product.m_bScale = dr.getBoolean(DataLogicSales.INDEX_ISSCALE + 1);
                product.m_dPriceBuy = dr.getDouble(DataLogicSales.INDEX_PRICEBUY + 1);
                product.m_dPriceSell = dr.getDouble(DataLogicSales.INDEX_PRICESELL + 1);
                product.m_dPriceSellinc = dr.getDouble(DataLogicSales.INDEX_PRICESELLINC + 1);
                product.m_dCommission = dr.getDouble(DataLogicSales.INDEX_COMMISSION + 1);
                product.categoryid = dr.getString(DataLogicSales.INDEX_CATEGORY + 1);
                product.taxcategoryid = dr.getString(DataLogicSales.INDEX_TAXCAT + 1);
                product.attributesetid = dr.getString(DataLogicSales.INDEX_ATTRIBUTESET_ID + 1);
                product.m_Image = ImageUtils.readImage(dr.getBytes(DataLogicSales.INDEX_IMAGE + 1));
                product.m_attributes = ImageUtils.readProperties(dr.getBytes(DataLogicSales.INDEX_ATTRIBUTES + 1));
                product.m_bCatalog = dr.getBoolean(DataLogicSales.INDEX_ISCATALOG + 1);
                product.m_catorder = dr.getDouble(DataLogicSales.INDEX_CATORDER + 1);
                product.m_bKitchen = dr.getBoolean(DataLogicSales.INDEX_ISKITCHEN + 1);
                product.kitchendescription = dr.getString(DataLogicSales.INDEX_KITCHENDESCRIPTION + 1);
                product.m_bService = dr.getBoolean(DataLogicSales.INDEX_ISSERVICE + 1);
                product.m_sDisplay = dr.getString(DataLogicSales.INDEX_DISPLAY + 1);
                product.m_bVprice = dr.getBoolean(DataLogicSales.INDEX_ISVPRICE + 1);
                product.m_bVerpatrib = dr.getBoolean(DataLogicSales.INDEX_ISVERPATRIB + 1);
                product.m_bWarranty = dr.getBoolean(DataLogicSales.INDEX_WARRANTY + 1);
                product.m_sAlias = dr.getString(DataLogicSales.INDEX_ALIAS + 1);
                product.m_bAlwaysAvailable = dr.getBoolean(DataLogicSales.INDEX_ALWAYSAVAILABLE + 1);
                product.m_canDiscount = dr.getDouble(DataLogicSales.INDEX_CANDISCOUNT + 1);
                product.m_manageStock = dr.getBoolean(DataLogicSales.INDEX_MANAGESTOCK + 1);
                product.m_jRemoteDisplay = dr.getBoolean(DataLogicSales.INDEX_REMOTEDISPLAY + 1);
                product.m_jBurnValue = dr.getInt(DataLogicSales.INDEX_BURNVALUE + 1);
                product.m_jEarnValue = dr.getInt(DataLogicSales.INDEX_EARNVALUE + 1);
                product.m_jLoyaltyMultiplier = dr.getDouble(DataLogicSales.INDEX_LOYALTYMULTIPLIER + 1);
                product.m_sellunit = dr.getString(DataLogicSales.INDEX_SELLUNIT + 1);
                product.m_stockunit = dr.getString(DataLogicSales.INDEX_STOCKUNIT + 1);
                product.m_buyunit = dr.getString(DataLogicSales.INDEX_BUYUNIT + 1);
                product.agerestricted = dr.getInt(DataLogicSales.INDEX_AGERESTRICTED + 1);
                product.isRecipe = dr.getBoolean(DataLogicSales.INDEX_ISRECIPE + 1);
                product.m_systemObject = dr.getBoolean(DataLogicSales.INDEX_SYSTEMOBJECT + 1);
                product.remoteprinter = dr.getString(DataLogicSales.INDEX_REMOTEPRINTER + 1);
                product.displayid = dr.getInt(DataLogicSales.INDEX_DISPLAYID + 1);
                product.averagecost = dr.getDouble(DataLogicSales.INDEX_AVERAGECOST + 1);
                product.m_siteguid = dr.getString(DataLogicSales.INDEX_SITEGUID + 1);
                product.isSalesObject = dr.getBoolean(DataLogicSales.INDEX_ISSALESOBJECT + 1);
                product.isDepositObject = dr.getBoolean(DataLogicSales.INDEX_ISDEPOSITOBJECT + 1);

                TierPricing tiers = new TierPricing();
                product.m_pricing = (ArrayList) tiers.getPricing(product.m_ID, product.m_siteguid);

                try {
                    product.stockLevel = dr.getDouble(DataLogicSales.INDEX_STOCKLEVEL + 1);
                } catch (Exception ex) {
                    product.stockLevel = 0.00;
                }

                return product;
            }
        };
    }

    @Override
    public final String toString() {
        return m_sRef + " - " + m_sName;
    }
}
