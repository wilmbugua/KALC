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

import uk.kalc.data.loader.IKeyed;

public class CodeType implements IKeyed {

    /**
     *
     */
    public static final CodeType EAN13 = new CodeType("EAN13", "EAN13");
    public static final CodeType EAN8 = new CodeType("EAN-8", "EAN-8");
    public static final CodeType UPCA = new CodeType("UPC-A", "UPC-A");
    public static final CodeType UPCE = new CodeType("UPC-E", "UPC-E");
    public static final CodeType CODE128 = new CodeType("CODE128", "CODE128");

    /**
     *
     */
    protected String m_sKey;

    /**
     *
     */
    protected String m_sValue;

    private CodeType(String key, String value) {
        m_sKey = key;
        m_sValue = value;
    }

    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
        return m_sKey;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return m_sValue;
    }

    @Override
    public String toString() {
        return m_sValue;
    }
}
