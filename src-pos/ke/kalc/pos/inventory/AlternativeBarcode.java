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


package ke.kalc.pos.inventory;

import java.io.Serializable;
import static java.lang.Integer.parseInt;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.loader.SerializerRead;
import ke.kalc.pos.datalogic.DataLogicSales;

public class AlternativeBarcode implements IKeyed, Serializable {
        
    private static final long serialVersionUID = 9083257536541L;

    private String m_sProductID;
    private String m_sBarcode;
    private String m_description;
    private String m_sbaseBarcode;
    private Double m_sUomFactor = 1.00;
    private Double m_sSellingQty = 1.00;
    private int count;
    private static DataLogicSales dlSales;
    private Object[] barcodeObjects;

    /**
     * Creates a new instance of PromotionInfo
     *
     * @param productID
     * @param uomFactor
     */
    public AlternativeBarcode(String productID, String barCode, Double uomFactor, String description) {
        m_sProductID = productID;
        m_sUomFactor = uomFactor;
        m_sBarcode = barCode;
        m_description = description;
    }

    public AlternativeBarcode(DataLogicSales dlSales) {
        AlternativeBarcode.dlSales = dlSales;
        m_sProductID = null;
        m_sUomFactor = 1.00;
        m_sBarcode = null;
        m_description = null;
    }

    public AlternativeBarcode(String barCode, String siteGuid, DataLogicSales dlSales) {
        AlternativeBarcode.dlSales = dlSales;
        count = 1;
        if (barCode.contains("*")) {
            count = (barCode.indexOf("*") == 0) ? 1 : parseInt(barCode.substring(0, barCode.indexOf("*")));
            m_sBarcode = barCode.substring(barCode.indexOf("*") + 1, barCode.length());
        } else {
            m_sBarcode = barCode;
        }

        try {
            barcodeObjects = dlSales.getAlternativeBarcode(m_sBarcode, siteGuid);
            if (barcodeObjects == null) {
                m_sbaseBarcode = m_sBarcode;
            } else {
                m_sProductID = barcodeObjects[0].toString();
                m_sSellingQty = (Double) barcodeObjects[1];
                m_sUomFactor = (Double) barcodeObjects[2];
                if (barcodeObjects[3] != null) {
                    m_description = barcodeObjects[3].toString();
                } else {
                    m_description = dlSales.getParentName(m_sProductID);
                }
                m_sbaseBarcode = dlSales.getParentProduct(m_sProductID);
            }
            //get any new price
        } catch (BasicException ex) {
            Logger.getLogger(AlternativeBarcode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getDescription() {
        return m_description;
    }

    public String getM_sbaseBarcode() {
        return m_sbaseBarcode;
    }

    public Double getSalesCount() {
        return count * m_sUomFactor * m_sSellingQty;
    }

    public String getM_sProductID() {
        return m_sProductID;
    }

    public void setM_sProductID(String m_sProductID) {
        this.m_sProductID = m_sProductID;
    }

    public Double getM_sUomFactor() {
        return m_sUomFactor;
    }

    public void setM_sUomFactor(Double m_sUomFactor) {
        this.m_sUomFactor = m_sUomFactor;
    }

    //write code to get price from netrx stage of dev
    public Double getPrice() {

        return 3.00;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new AlternativeBarcode(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getDouble(3),
                        dr.getString(4)
                );
            }
        };
    }

    @Override
    public Object getKey() {
        return m_sProductID;
    }

}
