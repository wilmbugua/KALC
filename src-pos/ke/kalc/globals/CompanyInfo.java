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
package ke.kalc.globals;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author John
 */
public class CompanyInfo {

    public CompanyInfo() {

    }

    public String getName() {
        try {
            return new String(Company.NAME.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return Company.NAME;
        }
    }

    public String getAddressLine1() {
        return Company.ADDRESSLINE1;
    }

    public String getAddressLine2() {
        return Company.ADDRESSLINE2;
    }

    public String getAddressLine3() {
        return Company.ADDRESSLINE3;
    }

    public String getPostcode() {
        return Company.POSTCODE;
    }

    public String getTaxNumber() {
        return Company.TAXNUMBER;
    }

    public String getPhoneNumber() {
        return Company.PHONENUMBER;
    }

    public String getEmailAddr() {
        return Company.EMAILADDR;
    }

    public String getWebSite() {
        return Company.WEBSITE;
    }

    public String getRegisationNumber() {
        return Company.REGISTRATIONNUMBER;
    }

}
