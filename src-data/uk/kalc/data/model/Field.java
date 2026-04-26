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


package uk.kalc.data.model;

import uk.kalc.data.loader.Datas;
import uk.kalc.format.Formats;


public class Field {
    
    private String label;
    private Datas data;
    private Formats format;
    
    private boolean searchable;
    private boolean comparable;
    private boolean title;
    
    /**
     *
     * @param label
     * @param data
     * @param format
     * @param title
     * @param searchable
     * @param comparable
     */
    public Field(String label, Datas data, Formats format, boolean title, boolean searchable, boolean comparable) {
        this.label = label;
        this.data = data;
        this.format = format;
        this.title = title;
        this.searchable = searchable;
        this.comparable = comparable;             
    }
    
    /**
     *
     * @param label
     * @param data
     * @param format
     */
    public Field(String label, Datas data, Formats format) {
        this(label, data, format, false, false, false);
    }
    
    /**
     *
     * @return
     */
    public String getLabel() {
        return label;
    }
    
    /**
     *
     * @return
     */
    public Formats getFormat() {
        return format;
    }
    
    /**
     *
     * @return
     */
    public Datas getData() {
        return data;
    }
    
    /**
     *
     * @return
     */
    public boolean isSearchable() {
        return searchable;
    }
    
    /**
     *
     * @return
     */
    public boolean isComparable() {
        return comparable;
    }
    
    /**
     *
     * @return
     */
    public boolean isTitle() {
        return title;
    }    
}
