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


package ke.kalc.data.gui;

import java.util.ArrayList;
import java.util.List;
import ke.kalc.data.loader.IKeyed;


public class ListKeyed<K extends IKeyed> extends ArrayList<K> {
    
    /**
     *
     * @param list
     */
    public ListKeyed(List<K> list) {
        this.addAll(list);
    }

    /**
     *
     * @param key
     * @return
     */
    public K get(Object key) {

        for (K elem : this) {
            if ((key == null && elem.getKey() == null) || (key != null && key.equals(elem.getKey()))) {
                return elem;
            }
        }
        return null;
    }
}
