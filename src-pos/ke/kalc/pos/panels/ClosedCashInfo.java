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
package ke.kalc.pos.panels;

import java.util.Date;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.SerializableRead;

public class ClosedCashInfo implements SerializableRead {

    private static final long serialVersionUID = 9083257536541L;

    protected Date startDate;
    protected Date endDate;
    protected String host;
    protected Integer hostSequence;
    protected String moneyGuid;

    public ClosedCashInfo(Date startDate, Date endDate, String host, Integer hostSequence, String moneyGuid) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.host = host;
        this.hostSequence = hostSequence;
        this.moneyGuid = moneyGuid;
    }

    public ClosedCashInfo() {

    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getHost() {
        return host;
    }

    public Integer getHostSequence() {
        return hostSequence;
    }

    public String getMoneyGuid() {
        return moneyGuid;
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        startDate = dr.getTimestamp(1);
        endDate = dr.getTimestamp(2);
        host = dr.getString(3);
        hostSequence = dr.getInt(4);
        moneyGuid = dr.getString(5);
    }

}
