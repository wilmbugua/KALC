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


package ke.kalc.pos.loyalty;

import ke.kalc.data.gui.MessageInf;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.printer.TicketParser;
import ke.kalc.pos.printer.TicketPrinterException;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.pos.printer.IncludeFile;

/**
 *
 * @author John
 */
public class CollectLoyaltyPoints extends LoyaltyCard {

    protected static DataLogicSystem dlSystem = null;

    public CollectLoyaltyPoints(String cardNumber) {
        super(cardNumber);
        dlSystem = new DataLogicSystem();
        dlSystem.init(SessionFactory.getSession());

    }

    private void printVoucher(String sresourcename) {
        String source = dlSystem.getResourceAsXML(sresourcename);

        String sresource;
        IncludeFile incFile = new IncludeFile(source, dlSystem);

        if (source == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
        } else {
            sresource = incFile.processInclude();
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                m_TTP.printTicket(script.eval(sresource).toString());
            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
            }
        }
    }

    @Override
    public void processVoucherCheck(TicketInfo ticket, TicketParser m_TTP) {
    }

    @Override
    public void processTicketPoints(String cardNumber, TicketInfo ticket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getRedeemedPoints(String cardNumber, TicketInfo ticket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getEarnedPoints(String cardNumber, TicketInfo ticket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
