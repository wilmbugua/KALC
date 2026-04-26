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
package uk.kalc.pos.customers;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.ImageUtils;
import uk.kalc.data.loader.SerializerRead;
import uk.kalc.pos.util.StringUtils;

public class CustomerInfo implements Serializable {

    private static final long serialVersionUID = 9083257536541L;
    protected String id;
    protected String customerType;
    protected String name;
    protected String taxid;
    protected String taxCategory;
    protected String customerCard;
    protected Double maxDebt;
    protected String address;
    protected String address2;
    protected String postal;
    protected String city;
    protected String region;
    protected String country;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phone;
    protected String phone2;
    protected String fax;
    protected String notes;
    protected Boolean ActiveCustomer;
    protected Date curDate;
    protected Double currentDebt;
    protected BufferedImage image = null;
    protected Double customerDiscount;
    protected Date dob;
    protected String loyaltyCardId;
    protected String loyaltyCardNumber;
    protected Boolean loyaltyenabled;
    protected Boolean marketable;
    protected String siteguid;
    protected Boolean taxExempt;
    protected Date reviewDate;

    /**
     * Creates a new instance of UserInfoBasic
     *
     * @param id
     */
    public CustomerInfo(String id) {
        this.id = id;
        this.taxid = null;
        this.name = null;
        this.postal = null;
        this.phone = null;
        this.email = null;
    }

    public CustomerInfo() {
        id = UUID.randomUUID().toString();
        customerType = null;
        name = null;
        taxid = null;
        taxCategory = null;
        customerCard = null;
        maxDebt = 0.00;
        address = null;
        address2 = null;
        postal = null;
        city = null;
        region = null;
        country = null;
        firstName = null;
        lastName = null;
        email = null;
        phone = null;
        phone2 = null;
        fax = null;
        notes = null;
        ActiveCustomer = null;
        curDate = null;
        currentDebt = null;
        image = null;
        customerDiscount = 0.00;
        dob = null;
        loyaltyCardId = null;
        loyaltyCardNumber = null;
        loyaltyenabled = false;
        marketable = false;
        taxExempt = false;
        reviewDate = null;

    }

    public CustomerInfo(String id, String customerType, String taxid, String name, String taxCategory, String customerCard, Double maxDebt, String address,
            String address2, String postal, String city, String region, String country, String firstName, String lastName, String email,
            String phone, String phone2, String fax, String notes, Boolean ActiveCustomer, Date curDate, Double currentDebt,
            BufferedImage image, Double customerDiscount, Date dob, String loyaltyCardId, String loyaltyCardNumber, Boolean loyaltyenabled, Boolean marketable,
            Boolean taxExempt, Date reviewDate) {
        this.id = id;
        this.customerType = customerType;
        this.name = name;
        this.taxid = taxid;
        this.taxCategory = taxCategory;
        this.customerCard = customerCard;
        this.maxDebt = maxDebt;
        this.address = address;
        this.address2 = address2;
        this.postal = postal;
        this.city = city;
        this.region = region;
        this.country = country;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.phone2 = phone2;
        this.fax = fax;
        this.notes = notes;
        this.ActiveCustomer = ActiveCustomer;
        this.curDate = curDate;
        this.currentDebt = currentDebt;
        this.image = image;
        this.customerDiscount = customerDiscount;
        this.dob = dob;
        this.loyaltyCardId = loyaltyCardId;
        this.loyaltyCardNumber = loyaltyCardNumber;
        this.loyaltyenabled = loyaltyenabled;
        this.marketable = marketable;
        this.taxExempt = taxExempt;
        this.reviewDate = reviewDate;

    }

    public String getId() {
        return id;
    }

    public int getCustomerStatus() {
        return ((exemptionExpired()) ? 4 : 0) + ((taxExempt) ? 2 : 0) + ((customerDiscount > 0) ? 1 : 0);
    }

    public Boolean isTaxExempt() {
        return taxExempt;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setTaxExempt(Boolean taxExempt) {
        this.taxExempt = taxExempt;

    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Boolean exemptionExpired() {
        if (reviewDate == null) {
            return false;
        }
        return !reviewDate.after(new Date());
    }

    public Boolean getLoyaltyEnabled() {
        return loyaltyenabled;
    }

    public void setLoyaltyEnabled(Boolean loyaltyenabled) {
        this.loyaltyenabled = loyaltyenabled;
    }

    public Boolean getMarketable() {
        return marketable;
    }

    public void setMarketable(Boolean marketable) {
        this.marketable = marketable;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid(String taxid) {
        this.taxid = taxid;
    }

    public String getCustomerCard() {
        return customerCard;
    }

    public void setCustomerCard(String customerCard) {
        this.customerCard = customerCard;
    }

    public Double getMaxDebt() {
        return maxDebt;
    }

    public void setMaxDebt(Double maxDebt) {
        this.maxDebt = maxDebt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getActiveCustomer() {
        return ActiveCustomer;
    }

    public void setActiveCustomer(Boolean ActiveCustomer) {
        this.ActiveCustomer = ActiveCustomer;
    }

    public Date getCurDate() {
        return curDate;
    }

    public void setCurDate(Date curDate) {
        this.curDate = curDate;
    }

    public Double getCurrentDebt() {
        return currentDebt;
    }

    public void setCurrentDebt(Double currentDebt) {
        this.currentDebt = currentDebt;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Double getCustomerDiscount() {
        return customerDiscount;
    }

    public void setCustomerDiscount(Double customerDiscount) {
        this.customerDiscount = customerDiscount;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getLoyaltyCardId() {
        return loyaltyCardId;
    }

    public void setLoyaltyCardId(String loyaltyCardId) {
        this.loyaltyCardId = loyaltyCardId;
    }

    public String getLoyaltyCardNumber() {
        return loyaltyCardNumber;
    }

    public void setLoyaltyCardNumber(String loyaltyCardNumber) {
        this.loyaltyCardNumber = loyaltyCardNumber;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getTaxCategory() {
        return taxCategory;
    }

    public void setTaxCategory(String taxCategory) {
        this.taxCategory = taxCategory;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String printTaxid() {
        return StringUtils.encodeXML(taxid);
    }

    public String printName() {
        return StringUtils.encodeXML(name);
    }

    public Object createParams() throws BasicException {

        Object[] customer = new Object[30];
        customer[0] = getId();
        customer[1] = getCustomerType();
        customer[2] = getTaxid();
        customer[3] = getName();
        customer[4] = getTaxCategory();
        customer[5] = getCustomerCard();
        customer[6] = getMaxDebt();
        customer[7] = getAddress();
        customer[8] = getAddress2();
        customer[9] = getPostal();
        customer[10] = getCity();
        customer[11] = getRegion();
        customer[12] = getCountry();
        customer[13] = getFirstName();
        customer[14] = getLastName();
        customer[15] = getEmail();
        customer[16] = getPhone();
        customer[17] = getPhone2();
        customer[18] = getFax();
        customer[19] = getNotes();
        customer[20] = getActiveCustomer();
        customer[21] = getCurDate();
        customer[22] = getCurrentDebt();
        customer[23] = getImage();
        customer[24] = getCustomerDiscount();
        customer[25] = getDob();
        customer[26] = getLoyaltyCardId();
        customer[27] = getLoyaltyCardNumber();
        customer[28] = getLoyaltyEnabled();
        customer[29] = getMarketable();
        return customer;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new CustomerInfo(
                        dr.getString(1), //id
                        dr.getString(2), //customertype
                        dr.getString(3), //taxid
                        dr.getString(4), //name
                        dr.getString(5), //taxcategory
                        dr.getString(6), //custmercard 
                        dr.getDouble(7), //maxdebt
                        dr.getString(8), //address
                        dr.getString(9), //address2
                        dr.getString(10), //postal
                        dr.getString(11), //city  
                        dr.getString(12), //region
                        dr.getString(13), //country
                        dr.getString(14), //firstname
                        dr.getString(15), //lastname
                        dr.getString(16), //email
                        dr.getString(17), //phone
                        dr.getString(18), //phone2
                        dr.getString(19), //fax
                        dr.getString(20), //notes
                        dr.getBoolean(21), //activecustomer
                        dr.getTimestamp(22), //currdate
                        dr.getDouble(23), //currdebt
                        ImageUtils.readImage(dr.getBytes(24)), //image
                        dr.getDouble(25), //discount
                        dr.getTimestamp(26), //dob                                        
                        dr.getString(27), //loyaltycardId    
                        dr.getString(28), //loyaltycardNumber 
                        dr.getBoolean(29), //marketable
                        dr.getBoolean(30), //marketable
                        dr.getBoolean(31), //marketable
                        dr.getTimestamp(32) //marketable
                //  dr.getString(30) //siteguid - STRING

                );
            }
        };
    }
}
