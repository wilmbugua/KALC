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


package ke.kalc.pos.forms;

import static java.util.Arrays.asList;
import java.util.LinkedHashSet;

public class ProductData {

    public static int FIELD_COUNT = 0;
    public static int INDEX_ID = FIELD_COUNT++;                         // 0
    public static int INDEX_REFERENCE = FIELD_COUNT++;                  // 1
    public static int INDEX_CODE = FIELD_COUNT++;                       // 2
    public static int INDEX_CODETYPE = FIELD_COUNT++;                   // 3    
    public static int INDEX_NAME = FIELD_COUNT++;                       // 4
    public static int INDEX_PRICEBUY = FIELD_COUNT++;                   // 5
    public static int INDEX_PRICESELL = FIELD_COUNT++;                  // 6
    public static int INDEX_PRICESELLINC = FIELD_COUNT++;               // 7
    public static int INDEX_CATEGORY = FIELD_COUNT++;                   // 8
    public static int INDEX_TAXCAT = FIELD_COUNT++;                     // 9
    public static int INDEX_ATTRIBUTESET_ID = FIELD_COUNT++;            // 10
    public static int INDEX_ISCOM = FIELD_COUNT++;                      // 11
    public static int INDEX_ISSCALE = FIELD_COUNT++;                    // 12
    public static int INDEX_ISKITCHEN = FIELD_COUNT++;                  // 13
    public static int INDEX_KITCHENDESCRIPTION = FIELD_COUNT++;         // 13
    public static int INDEX_ISSERVICE = FIELD_COUNT++;                  // 14
    public static int INDEX_DISPLAY = FIELD_COUNT++;                    // 15
    public static int INDEX_ATTRIBUTES = FIELD_COUNT++;                 // 16
    public static int INDEX_ISVPRICE = FIELD_COUNT++;                   // 17
    public static int INDEX_ISVERPATRIB = FIELD_COUNT++;                // 18
    public static int INDEX_WARRANTY = FIELD_COUNT++;                   // 19
    public static int INDEX_IMAGE = FIELD_COUNT++;                      // 20
    public static int INDEX_ALIAS = FIELD_COUNT++;                      // 21
    public static int INDEX_ALWAYSAVAILABLE = FIELD_COUNT++;            // 22
    public static int INDEX_CANDISCOUNT = FIELD_COUNT++;                // 23
    public static int INDEX_ISCATALOG = FIELD_COUNT++;                  // 24
    public static int INDEX_CATORDER = FIELD_COUNT++;                   // 25    
    public static int INDEX_MANAGESTOCK = FIELD_COUNT++;                // 26
    public static int INDEX_COMMISSION = FIELD_COUNT++;                 // 27
    public static int INDEX_REMOTEDISPLAY = FIELD_COUNT++;              // 28
    public static int INDEX_AVERAGECOST = FIELD_COUNT++;                // 29
    public static int INDEX_BURNVALUE = FIELD_COUNT++;                  // 30
    public static int INDEX_EARNVALUE = FIELD_COUNT++;                  // 31
    public static int INDEX_LOYALTYMULTIPLIER = FIELD_COUNT++;          // 32
    public static int INDEX_SELLUNIT = FIELD_COUNT++;                   // 33
    public static int INDEX_STOCKUNIT = FIELD_COUNT++;                  // 34
    public static int INDEX_BUYUNIT = FIELD_COUNT++;                    // 35
    public static int INDEX_AGERESTRICTED = FIELD_COUNT++;              // 36 
    public static int INDEX_ISRECIPE = FIELD_COUNT++;                   // 37 
    public static int INDEX_ISINGREDIENT = FIELD_COUNT++;               // 38 
    public static int INDEX_SYSTEMOBJECT = FIELD_COUNT++;               // 39
    public static int INDEX_SITEGUID = FIELD_COUNT++;                   // 40

    private static LinkedHashSet<String> productTableInsert;
    private LinkedHashSet<String> productTableUpdate;

    public ProductData() {
    }

    public static LinkedHashSet getProductTableInsert() {
        return new LinkedHashSet<>(asList(
                "id, ", "reference, ", "code, ", "codetype, ", "name, ", "pricebuy, ", "pricesell, ", "pricesellinc, ", "category, ", "taxcat, ",
                "attributeset_id, ", "iscom, ", "isscale, ", "iskitchen, ",  "kitchendescription, ", "isservice, ", "display, ", "attributes, ", "isvprice, ", "isverpatrib, ",
                "warranty, ", "image, ", "alias, ", "alwaysavailable, ", "candiscount, ", "iscatalog, ", "catorder, ",
                "managestock, ", "commission, ", "remotedisplay, ", "averagecost, ", "burnvalue, ", "earnvalue, ", "loyaltymultiplier, ",
                "sellunit, ", "stockunit, ", "buyunit, ", "agerestricted, ", "isrecipe, ", "isingredient, ", "systemobject, ", "siteguid "
        ));
    }

    public static LinkedHashSet getProductTableUpdate() {
        return new LinkedHashSet<>(asList("reference = ?, ", "code = ?, ", "codetype = ?, ", "name = ?, ", "pricebuy = ?, ", "pricesell = ?, ", "pricesellinc = ?, ",
                "category = ?, ", "taxcat = ?, ", "attributeset_id = ?, ", "iscom = ?, ", "isscale = ?, ", "iskitchen = ?, ",  "kitchendescription = ? , ","isservice = ?, ", "display = ?, ",
                "attributes = ?, ", "isvprice = ?, ", "isverpatrib = ?, ", "warranty = ?, ", "image = ?, ", "alias = ?, ", "alwaysavailable = ?, ",
                "candiscount = ?, ", "iscatalog = ?, ", "catorder = ?, ", "managestock = ?, ", "commission = ?, ", "remotedisplay = ?, ", "burnvalue = ?, ",
                "earnvalue = ?, ", "loyaltymultiplier = ?, ", "sellunit = ?, ", "stockunit = ?, ", "buyunit = ?, ", "agerestricted = ?, ", "isrecipe = ?, ",
                "isingredient = ? "
        ));
    }

    public static String getFieldList() {
        StringBuilder sb = new StringBuilder();
        getProductTableInsert().forEach((s) -> {
            sb.append((String) s);
        });
        return sb.toString();
    }

    public static String getAuxiliaryFieldList() {
        StringBuilder sb = new StringBuilder();
        getProductTableInsert().forEach((s) -> {
            if (((String) s).equals("category, ")) {
                sb.append("(select name from categories where id = category), ");
            } else {
                sb.append((String) s);
            }
        });
        return sb.toString();
    }

}
