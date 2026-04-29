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


package ke.kalc.pos.sales;

import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.DataWrite;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.data.loader.SerializableWrite;

public class SharedTicketInfo implements SerializableRead, SerializableWrite {

    private static final long serialVersionUID = 7640633837719L;
    private String id;
    private String name;
    private String userName;
    private String customerName;
    private String loyalty;
    private String pickupBarcode;
    private CustomerDeliveryInfo deliveryInfo;

    /**
     * Creates a new instance of SharedTicketInfo
     */
    public SharedTicketInfo() {
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        name = dr.getString(2);
        userName = dr.getString(3);
        customerName = dr.getString(4);
        loyalty = dr.getString(5);
        pickupBarcode = dr.getString(6);
     //   deliveryInfo = (CustomerDeliveryInfo) dr.getObject(7);
    }

    /**
     *
     * @param dp
     * @throws BasicException
     */
    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, id);
        dp.setString(2, name);
        dp.setString(3, userName);
        dp.setString(4, customerName);
        dp.setString(5, loyalty);
        dp.setString(6, pickupBarcode);
        dp.setObject(7, deliveryInfo);
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getAppUser() {
        return userName;
    }

    /**
     *
     * @return
     */
    public String getCustomerName() {
        return customerName;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public String getPickupBarcode() {
        return pickupBarcode;
    }

    public CustomerDeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }
}
