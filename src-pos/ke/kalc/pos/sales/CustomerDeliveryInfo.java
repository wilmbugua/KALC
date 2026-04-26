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


package ke.kalc.pos.sales;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.SerializerRead;
import ke.kalc.globals.SystemProperty;

public class CustomerDeliveryInfo implements Serializable {

    private static final long serialVersionUID = 9083257536541L;
    protected String id;
    protected String name;
    protected String addressLine1;
    protected String addressLine2;
    protected String addressLine3;
    protected String postCode;
    protected String phone;
    protected Date deliveryDate;
    protected Boolean delivered;
    protected String comments;

    public CustomerDeliveryInfo(String id, String name, String addressLine1, String addressLine2, String addressLine3, String postcode, String phone,
            Date deliveryDate, Boolean delivered, String comments) {
        this.id = id;
        this.name = name;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.postCode = postcode;
        this.phone = phone;
        this.deliveryDate = deliveryDate;
        this.delivered = delivered;
        this.comments = comments;
    }

    public String fetchId() {
        return id;
    }

    public String fetchName() {
        return name;
    }

    public String fetchAddressLine1() {
        return addressLine1;
    }

    public String fetchAddressLine2() {
        return addressLine2;
    }

    public String fetchAddressLine3() {
        return addressLine3;
    }

    public String fetchPostCode() {
        return postCode;
    }

    public String fetchPhone() {
        return phone;
    }

    public Date fetchDeliveryDate() {
        return deliveryDate;
    }

    public String printDeliveryDate() {
        String pattern = SystemProperty.DATE;
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(deliveryDate);
    }

    public Boolean isDelivered() {
        return delivered;
    }

    public String fetchComments() {
        return comments;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new CustomerDeliveryInfo(
                        dr.getString(1),    //id
                        dr.getString(2),    //name
                        dr.getString(3),    //addrssline1
                        dr.getString(4),    //addrssline2
                        dr.getString(5),    //addrssline3
                        dr.getString(6),    //postcode 
                        dr.getString(7),    //phone
                        dr.getTimestamp(8), //deliverdate
                        dr.getBoolean(9),   //deliveres
                        dr.getString(10)    //comments
                );
            }
        };
    }

}
