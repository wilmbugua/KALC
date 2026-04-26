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
