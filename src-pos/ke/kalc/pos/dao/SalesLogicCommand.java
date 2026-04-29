/*
**    KALC Administration  - Professional Point of Sale
**
**    This file is part of KALC Administration Version KALC V1.5.0
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
 */
package ke.kalc.pos.dao;

import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.SentenceExec;
import ke.kalc.data.loader.SerializerWriteBasic;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.data.loader.StaticSentence;

/**
 *
 * @author John
 */
public class SalesLogicCommand {

    protected static Session session  = SessionFactory.getSession();
    protected SentenceExec lineRemoved;

    public void init() {
        
        lineRemoved = new StaticSentence(session,
                "insert into lineremoved (id, name, terminalid, ticketid, description, productid, productname, units, value) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE}));

    }

    
    public final void execLineRemoved(Object[] line) {
        try {
            lineRemoved.exec(line);
        } catch (BasicException e) {
        }
    }
}
