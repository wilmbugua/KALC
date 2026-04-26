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


package uk.kalc.pos.inventory;

import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.SerializerRead;

public class ProductsRecipeInfo {
    private static final long serialVersionUID = 7587646873036L;
    
    protected String id;
    protected String productId;
    protected String productKitId;
    protected Double quantity;
    protected Boolean isManaged;

    /**
     * 
     * @param id
     * @param product
     * @param productKit
     * @param quantity 
     */
    public ProductsRecipeInfo(String id, String productId, String productKitId, Double quantity, Boolean isManaged) {
        this.id = id;
        this.productId = productId;
        this.productKitId = productKitId;
        this.quantity = quantity;
        this.isManaged = isManaged;
    }
    

    public void setM_ID(String id) {
        this.id = id;
    }

    public void setM_sProduct(String productId) {
        this.productId = productId;
    }

    public void setM_sProductKit(String productKitId) {
        this.productKitId = productKitId;
    }

    public void setM_dQuantity(Double m_dQuantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductKitId() {
        return productKitId;
    }

    public Double getQuantity() {
        return quantity;
    }
    
    public Boolean isManaged(){
        return isManaged;
    }
    
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new ProductsRecipeInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getDouble(4), dr.getBoolean(5));
        }};
    }
    
}
