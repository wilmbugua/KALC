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
package ke.kalc.pos.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.DataWrite;
import ke.kalc.data.loader.SerializerRead;

public class HourlySalesInfo {

    private final String hourRange;
    private final Double hourTotal;
    private final Double hourNetTotal;
    private final Integer sales;
    private final Date salesDate;
    private final String hourStr;

    public HourlySalesInfo(Date salesDate, String hourRate, Double hourTotal, Integer sales, Double hourNetTotal, String hourStr) {
        this.salesDate = salesDate;
        this.hourRange = hourRate;
        this.hourTotal = hourTotal;
        this.sales = sales;
        this.hourStr = hourStr;
        this.hourNetTotal = hourNetTotal;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                Date salesDate = dr.getTimestamp(1);
                String hourRange = dr.getString(2);
                Double hourTotal = dr.getDouble(3);
                Integer sales = dr.getInt(4);
                Double hourNetTotal = dr.getDouble(5);

                StringBuilder sb = new StringBuilder();
                sb.append((hourRange.length() == 1) ? "0" + hourRange : hourRange);
                sb.append(":00-");
                sb.append((hourRange.length() == 1) ? "0" + hourRange : hourRange);
                sb.append(":59");

                return new HourlySalesInfo(salesDate, hourRange, hourTotal, sales, hourNetTotal, sb.toString());
            }
        };
    }

    public void writeValues(DataWrite dp) throws BasicException {
        dp.setTimestamp(1, salesDate);
        dp.setString(2, hourRange);
        dp.setDouble(3, hourTotal);
        dp.setInt(4, sales);
        dp.setDouble(4, hourNetTotal);
    }

    public String getHourRange() {
        return hourRange;
    }

    public String getHourTotal() {
        BigDecimal bd = new BigDecimal(hourTotal);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public String getHourNetTotal() {
        BigDecimal bd = new BigDecimal(hourNetTotal);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public Integer getSales() {
        return sales;
    }

    public String getSalesDate() {
        SimpleDateFormat sdfr = new SimpleDateFormat("dd-mm-yyyy");
        return sdfr.format(salesDate);
    }

    public String getHourStr() {
        return hourStr;
    }

    public String getDate(){
       DateFormat format = new SimpleDateFormat("dd"); 
       return  format.format(salesDate);
    }    
}
