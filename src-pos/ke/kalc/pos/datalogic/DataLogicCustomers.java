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
package ke.kalc.pos.datalogic;

import java.util.List;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataParams;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.PreparedSentence;
import ke.kalc.data.loader.QBFBuilder;
import ke.kalc.data.loader.SentenceExec;
import ke.kalc.data.loader.SentenceExecTransaction;
import ke.kalc.data.loader.SentenceFind;
import ke.kalc.data.loader.SentenceList;
import ke.kalc.data.loader.SerializerRead;
import ke.kalc.data.loader.SerializerReadBasic;
import ke.kalc.data.loader.SerializerReadInteger;
import ke.kalc.data.loader.SerializerWriteBasic;
import ke.kalc.data.loader.SerializerWriteBasicExt;
import ke.kalc.data.loader.SerializerWriteParams;
import ke.kalc.data.loader.SerializerWriteString;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.StaticSentence;
import ke.kalc.data.loader.TableDefinition;
import ke.kalc.pos.customers.CustomerInfo;
import ke.kalc.pos.customers.CustomerInfoExt;
import ke.kalc.pos.forms.BeanFactoryDataSingle;
import ke.kalc.pos.sales.CustomerDeliveryInfo;

public class DataLogicCustomers extends BeanFactoryDataSingle {

    private SentenceExec receivedCustomer;
    private SentenceFind m_CustomerPresent;
    protected Session s;
    private TableDefinition tcustomers;
    private static final Datas[] customerdatas = new Datas[]{
        Datas.STRING,
        Datas.TIMESTAMP,
        Datas.TIMESTAMP,
        Datas.STRING,
        Datas.STRING,
        Datas.STRING,
        Datas.INT,
        Datas.BOOLEAN,
        Datas.STRING};

    @Override
    public void init(Session s) {
        this.s = s;

        m_CustomerPresent = new PreparedSentence(s, "select count(*) from customers where customertype = 'account' and card = ? ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        receivedCustomer = new StaticSentence(s, "update reservations set isdone = true where id = ? ", SerializerWriteString.INSTANCE);
    }

    public SentenceList getCustomerList(String siteGuid) {
        if (siteGuid == null) {
            return new StaticSentence(s, new QBFBuilder("select id, taxid, name, postal, email, "
                    + "phone from customers where active = " + s.DB.TRUE()
                    + " and ?(QBF_FILTER) order by lower (name)",
                    new String[]{"taxid", "name", "postal", "phone", "email"}), new SerializerWriteBasic(new Datas[]{
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING}), new SerializerRead() {
                @Override
                public Object readValues(DataRead dr) throws BasicException {
                    CustomerInfo c = new CustomerInfo(dr.getString(1));
                    c.setTaxid(dr.getString(2));
                    c.setName(dr.getString(3));
                    c.setPostal(dr.getString(4));
                    c.setPhone(dr.getString(5));
                    c.setEmail(dr.getString(6));

                    return c;
                }
            });
        } else {
            return new StaticSentence(s, new QBFBuilder("select id, taxid, name, postal, email, "
                    + "phone from customers where active = " + s.DB.TRUE() + " and siteguid ='" + siteGuid + "'"
                    + " and ?(QBF_FILTER) order by lower (name)",
                    new String[]{"taxid", "name", "postal", "phone", "email"}), new SerializerWriteBasic(new Datas[]{
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING}), new SerializerRead() {
                @Override
                public Object readValues(DataRead dr) throws BasicException {
                    CustomerInfo c = new CustomerInfo(dr.getString(1));
                    c.setTaxid(dr.getString(2));
                    c.setName(dr.getString(3));
                    c.setPostal(dr.getString(4));
                    c.setPhone(dr.getString(5));
                    c.setEmail(dr.getString(6));

                    return c;
                }
            });

        }

    }

    /**
     *
     * @param customer
     * @return
     * @throws BasicException
     */
    public int updateCustomerExt(final CustomerInfoExt customer) throws BasicException {

        return new PreparedSentence(s, "update customers set notes = ? where id = ?", SerializerWriteParams.INSTANCE
        ).exec(new DataParams() {
            @Override
            public void writeValues() throws BasicException {
                setString(1, customer.getNotes());
                setString(2, customer.getId());
            }
        });
    }

    public final void receiveCustomer(String id) throws BasicException {
        receivedCustomer.exec(id);
    }

    /**
     *
     * @return customer's existing reservation (restaurant mode)
     */
    public final SentenceList getReservationsList() {
        return new PreparedSentence(s, "select r.id, r.created, r.datenew, c.customer, customers.taxid, coalesce(customers.name, r.title),  r.chairs, r.isdone, r.description "
                + "from reservations r left outer join reservation_customers c on r.id = c.id left outer join customers on c.customer = customers.id "
                + "where r.datenew >= ? and r.datenew < ?", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.TIMESTAMP}), new SerializerReadBasic(customerdatas));
    }

    /**
     *
     * @return create/update customer reservation (restaurant mode)
     */
    public final SentenceExec getReservationsUpdate() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {

                new PreparedSentence(s, "delete from reservation_customers where id = ?", new SerializerWriteBasicExt(customerdatas, new int[]{0})).exec(params);
                if (((Object[]) params)[3] != null) {
                    new PreparedSentence(s, "insert into reservation_customers (id, customer) values (?, ?)", new SerializerWriteBasicExt(customerdatas, new int[]{0, 3})).exec(params);
                }
                return new PreparedSentence(s, "update reservations set id = ?, created = ?, datenew = ?, title = ?, chairs = ?, isdone = ?, description = ? where id = ?", new SerializerWriteBasicExt(customerdatas, new int[]{0, 1, 2, 5, 6, 7, 8, 0})).exec(params);
            }
        };
    }

    /**
     *
     * @return delete customer reservation (restaurant mode)
     */
    public final SentenceExec getReservationsDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s, "delete from reservation_customers where id = ?", new SerializerWriteBasicExt(customerdatas, new int[]{0})).exec(params);
                return new PreparedSentence(s, "delete from reservations where id = ?", new SerializerWriteBasicExt(customerdatas, new int[]{0})).exec(params);
            }
        };
    }

    /**
     *
     * @return insert a new customer reservation (restaurant mode)
     */
    public final SentenceExec getReservationsInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {

                int i = new PreparedSentence(s, 
                        "insert into reservations (id, created, datenew, title, chairs, isdone, description) values (?, ?, ?, ?, ?, ?, ?)", 
                        new SerializerWriteBasicExt(customerdatas, new int[]{0, 1, 2, 5, 6, 7, 8})).exec(params);

                if (((Object[]) params)[3] != null) {
                    new PreparedSentence(s, 
                            "insert into reservation_customers (id, customer) values (?, ?)", 
                            new SerializerWriteBasicExt(customerdatas, new int[]{0, 3})).exec(params);
                }
                return i;
            }
        };
    }

    /**
     *
     * @return assign a table to a customer reservation (restaurant mode)
     */
    public final TableDefinition getTableCustomers() {
        return tcustomers;
    }

    public final Boolean customerExists(String customerCard) {
        try {
            Integer i = (Integer) m_CustomerPresent.find(customerCard);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public final Boolean customerExists(String customerCard, Boolean activeStatus) {
        try {
            Integer i = (Integer) m_CustomerPresent.find(customerCard);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public final CustomerDeliveryInfo fetchCustomerDelivery(String id) throws BasicException {
        return (CustomerDeliveryInfo) new PreparedSentence(s, "select "
                + "id,"
                + "name, "
                + "addressline1, "
                + "addressline2, "
                + "addressline3, "
                + "postcode, "
                + "phone, "
                + " deliverydate, "
                + "delivered, "
                + "comments "
                + "from customer_delivery "
                + "where id = ? ",
                SerializerWriteString.INSTANCE,
                CustomerDeliveryInfo.getSerializerRead()).find(id);
    }

    public final List<CustomerDeliveryInfo> fetchWaitingCustomerDeliveries() throws BasicException {
        return new PreparedSentence(s, "select "
                + "id,"
                + "name, "
                + "addressline1, "
                + "addressline2, "
                + "addressline3, "
                + "postcode, "
                + "phone, "
                + " deliverydate, "
                + "delivered, "
                + "comments "
                + "from customer_delivery "
                + "where delivered = false ", SerializerWriteString.INSTANCE, CustomerDeliveryInfo.getSerializerRead()).list();
    }

    public final List<CustomerDeliveryInfo> fetchAllCustomerDeliveries() throws BasicException {
        return new PreparedSentence(s, "select "
                + "id,"
                + "name, "
                + "addressline1, "
                + "addressline2, "
                + "addressline3, "
                + "postcode, "
                + "phone, "
                + " deliverydate, "
                + "delivered, "
                + "comments "
                + "from customer_delivery "
                + "where delivered = false ", null, CustomerDeliveryInfo.getSerializerRead()).list();
    }

}
