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


package ke.kalc.data.model;

import ke.kalc.data.loader.Datas;
import ke.kalc.format.Formats;


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
