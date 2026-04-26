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
import java.util.ArrayList;
import java.util.List;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.PreparedSentence;
import ke.kalc.data.loader.SerializerRead;
import ke.kalc.data.loader.SerializerWriteBasicExt;
import ke.kalc.data.loader.SessionFactory;

public class TierPricing implements Serializable {

    private static final long serialVersionUID = 9083257536541L;

    private Integer tierQty = 0;
    private Double sellPrice = 0.00;
    private Double sellPriceInc = 0.00;

    public TierPricing() {
    }

    public TierPricing(Integer tierQty, Double sellPriceInc, Double sellPrice) {
        this.tierQty = tierQty;
        this.sellPrice = sellPrice;
        this.sellPriceInc = sellPriceInc;
    }

    public List<TierPricing> getPricing(String id, String siteguid) {
        List<TierPricing> priceTiers = getTierPricing(id, siteguid);
        return (priceTiers.isEmpty()) ? null : (List<TierPricing>) priceTiers.get(0);
    }

    public Integer getTierQty() {
        return tierQty;
    }

    public void setTierQty(Integer tierQTY) {
        this.tierQty = tierQTY;
    }

    public Double getTierPrice() {
        return this.sellPrice;
    }

    public Double getTierPriceInc() {
        return this.sellPriceInc;
    }

    private List<TierPricing> getTierPricing(String product, String siteguid) {
        List<TierPricing> prices = new ArrayList<>();
        try {
            return new PreparedSentence(SessionFactory.getSession(), "select "
                    + " tier1_qty, tier1_sellpriceinc, tier1_sellprice,"
                    + " tier2_qty, tier2_sellpriceinc, tier2_sellprice,"
                    + " tier3_qty, tier3_sellpriceinc, tier3_sellprice,"
                    + " tier4_qty, tier4_sellpriceinc, tier4_sellprice,"
                    + " tier5_qty, tier5_sellpriceinc, tier5_sellprice  "
                    + " from tierpricing "
                    + " where productid = ? and siteguid = ? ",
                    new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 1}),
                    new SerializerRead() {
                @Override
                public Object readValues(DataRead dr) throws BasicException {
                    prices.add(new TierPricing(dr.getInt(1), dr.getDouble(2), dr.getDouble(3)));
                    prices.add(new TierPricing(dr.getInt(4), dr.getDouble(5), dr.getDouble(6)));
                    prices.add(new TierPricing(dr.getInt(7), dr.getDouble(8), dr.getDouble(9)));
                    prices.add(new TierPricing(dr.getInt(10), dr.getDouble(11), dr.getDouble(12)));
                    prices.add(new TierPricing(dr.getInt(13), dr.getDouble(14), dr.getDouble(15)));
                    return prices;
                }
            }).list(product, siteguid);
        } catch (BasicException e) {
            System.out.println(e);
        }
        return null;
    }

}
