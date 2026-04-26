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


package ke.kalc.pos.customers;

import ke.kalc.data.user.BrowsableEditableData;

public class CustomerInfoGlobal {

    private static CustomerInfoGlobal INSTANCE;
    private CustomerInfoExt customerInfoExt;
    private BrowsableEditableData editableData;

    private CustomerInfoGlobal() {
    }

    public static CustomerInfoGlobal getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomerInfoGlobal();
        }

        return INSTANCE;
    }

    public CustomerInfoExt getCustomerInfoExt() {
        return customerInfoExt;
    }


    public void setCustomerInfoExt(CustomerInfoExt customerInfoExt) {
        this.customerInfoExt = customerInfoExt;
    }


    public BrowsableEditableData getEditableData() {
        return editableData;
    }

    public void setEditableData(BrowsableEditableData editableData) {
        this.editableData = editableData;
    }

}
