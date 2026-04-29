/*
**    KALC Administration  - Professional Point of Sale
**
**    This file is part of KALC Administration Version KALC V1.5.3
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
 */



package ke.kalc.pos.auditing;

import java.util.UUID;
import ke.kalc.commons.dbmanager.DbUtils;
import ke.kalc.commons.utils.TerminalDataLogic;
import ke.kalc.pos.dao.SalesLogicCommand;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.pos.ticket.TicketLineInfo;

/**
 *
 * @author John Lewis
 */
public class Audit {

    private static final SalesLogicCommand salesLogicCommand;
    private static final TerminalDataLogic terminal;

    static {
        salesLogicCommand = new SalesLogicCommand();
        salesLogicCommand.init();
        terminal = new TerminalDataLogic();
    }

    public static void itemRemoved(TicketInfo ticket, Integer i, String reason) {
        salesLogicCommand.execLineRemoved(
                new Object[]{
                    UUID.randomUUID().toString(),
                    terminal.getCurrentUser().getName(),
                    DbUtils.getTerminalName(),
                    ticket.getId(),
                    reason,
                    ticket.getLine(i).getProductID(),
                    ticket.getLine(i).getProductName(),
                    ticket.getLine(i).getMultiply(),
                    ticket.getLine(i).getPrice()
                });
    }

        public static void itemRemoved(TicketInfo ticket, Integer i, Double qty, String reason) {
        salesLogicCommand.execLineRemoved(
                new Object[]{
                    UUID.randomUUID().toString(),
                    terminal.getCurrentUser().getName(),
                    DbUtils.getTerminalName(),
                    ticket.getId(),
                    reason,
                    ticket.getLine(i).getProductID(),
                    ticket.getLine(i).getProductName(),
                    qty,
                    ticket.getLine(i).getPrice()
                });
    }
    
    
    
    public static void itemRemoved(TicketInfo ticket, TicketLineInfo line, String reason) {
        salesLogicCommand.execLineRemoved(
                new Object[]{
                    UUID.randomUUID().toString(),
                    terminal.getCurrentUser().getName(),
                    DbUtils.getTerminalName(),
                    ticket.getId(),
                    reason,
                    line.getProductID(),
                    line.getProductName(),
                    line.getMultiply(),
                    line.getPrice()
                });
    }
}
