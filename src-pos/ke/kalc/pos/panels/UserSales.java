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
