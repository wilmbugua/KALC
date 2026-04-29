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


package ke.kalc.pos.util;

import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.data.loader.SerializerRead;


public class ReturnInfo implements SerializableRead, IKeyed {

    private static final long serialVersionUID = 8906929819402L;
    private Integer idret;

    /**
     *
     */
    public ReturnInfo() {
        idret = null;
    }

    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
        return idret;
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        idret = dr.getInt(1);
    }

    /**
     *
     * @param id
     */
    public void setId(Integer id) {
        idret = id;
    }

    /**
     *
     * @return
     */
    public Integer getId() {
        return idret;
    }

    /**
     *
     * @return
     */
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new ReturnInfo(
                        dr.getInt(1));
            }
        };
    }

    /**
     *
     * @param id
     */
    public ReturnInfo(Integer id) {
        this.idret = id;
    }

}
