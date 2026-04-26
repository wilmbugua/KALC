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
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.SerializerRead;

/**
 *
 * @author John
 */
public class UserSales {
    
    private String userName;
    private Double userSales;
    
    public UserSales(String userName, Double userSales){
        this.userName = userName;
        this.userSales = userSales;
    }
    
    public String getUserName(){
        return userName;
    }
       
    public String getUserSales() {
        BigDecimal bd = new BigDecimal(userSales);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }
    
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                String userName = dr.getString(1);
                Double userSales = dr.getDouble(2);
                return new UserSales(userName, userSales);
            }
        };
    }
    
}
