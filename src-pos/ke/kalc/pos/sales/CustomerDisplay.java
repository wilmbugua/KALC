/*
**    KALC Administration  - Professional Point of Sale
**
**    This file is part of KALC Administration Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC  
**
**    https://www.kalc.co.ke
**   
**
 */
package ke.kalc.pos.sales;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.commons.utils.TerminalInfo;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.forms.JRootApp;
import ke.kalc.pos.printer.DeviceDisplayAdvance;
import ke.kalc.pos.printer.IncludeFile;
import ke.kalc.pos.printer.TicketParser;
import ke.kalc.pos.printer.TicketPrinterException;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;
import ke.kalc.pos.ticket.TicketLineInfo;

/**
 *
 * @author John
 */
public class CustomerDisplay {

    protected static AppView m_App = JRootApp.getRootInstance();
    protected static final TicketParser m_TTP;
    protected static final DataLogicSystem dlSystem = new DataLogicSystem();
    protected static final DataLogicSales dlSales = new DataLogicSales();
    protected static ScriptEngine script;
    protected static String sresource;
    protected static IncludeFile incFile;
    protected static DeviceDisplayAdvance advDisplay = null;

    static {
        dlSystem.init(SessionFactory.getSession());
        dlSales.init(SessionFactory.getSession());
        m_TTP = new TicketParser(JRootApp.getRootInstance().getDeviceTicket(), dlSystem);
        if (isEnabled()) {
            advDisplay = (DeviceDisplayAdvance) m_App.getDeviceTicket().getDeviceDisplay();
        }
        try {
            script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
        } catch (ScriptException ex) {
            script = null;
            Logger.getLogger(CustomerDisplay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateDisplay(TicketLineInfo oLine) {
        if (advDisplay != null) {
            advDisplay.setProductImage(null);
            if (oLine == null) {
                clearDisplay();
            } else {
                try {
                    script.put("ticketline", oLine);
                    m_TTP.printTicket(script.eval(dlSystem.getResourceAsXML(
                            (SystemProperty.TAXINCLUDED) ? "Display.TicketLineInc"
                                    : "Display.TicketLineExcl")).toString());
                    updateProductImage(oLine.getProductID());
                } catch (ScriptException | TicketPrinterException e) {
                    StringBuilder sb = new StringBuilder();
                    if (e.getCause() instanceof Throwable) {
                        Throwable t = (Throwable) e.getCause();
                        while (t != null) {
                            sb.append(t.getClass().getName());
                            sb.append(": \n");
                            sb.append(t.getMessage());
                            sb.append("\n\n");
                            t = t.getCause();
                        }
                    }

                    JAlertPane.showExceptionStackDialog("", AppLocal.getIntString("message.scripterror"),
                            AppLocal.getIntString("message.cannotprocessline"), sb.toString());
                }
            }
        }
    }

    public static void updateDisplay(String resource) {
        if (advDisplay != null) {
            advDisplay.setProductImage(null);
            String source = dlSystem.getResourceAsXML(resource);
            if (source.isEmpty()) {
                return;
            }

            incFile = new IncludeFile(source, dlSystem);
            sresource = incFile.processInclude();
            try {
                m_TTP.printTicket(sresource);
            } catch (TicketPrinterException e) {
                StringBuilder sb = new StringBuilder();
                if (e.getCause() instanceof Throwable) {
                    Throwable t = (Throwable) e.getCause();
                    while (t != null) {
                        sb.append(t.getClass().getName());
                        sb.append(": \n");
                        sb.append(t.getMessage());
                        sb.append("\n\n");
                        t = t.getCause();
                    }
                }
                JAlertPane.showExceptionStackDialog("", AppLocal.getIntString("message.scripterror"),
                        AppLocal.getIntString("message.cannotprocessline"), sb.toString());
            }
        }
    }

    public static void updateDisplay(String resource, HashMap parameters) {
        if (advDisplay != null) {
            advDisplay.setProductImage(null);
            String source = dlSystem.getResourceAsXML(resource);
            if (source.isEmpty()) {
                return;
            }

            incFile = new IncludeFile(source, dlSystem);
            sresource = incFile.processInclude();
            try {
                parameters.forEach((key, value) -> script.put((String) key, value));
                m_TTP.printTicket(script.eval(sresource).toString());
                updateProductImage((String) parameters.get("id"));
            } catch (ScriptException | TicketPrinterException e) {
                StringBuilder sb = new StringBuilder();
                if (e.getCause() instanceof Throwable) {
                    Throwable t = (Throwable) e.getCause();
                    while (t != null) {
                        sb.append(t.getClass().getName());
                        sb.append(": \n");
                        sb.append(t.getMessage());
                        sb.append("\n\n");
                        t = t.getCause();
                    }
                }
                JAlertPane.showExceptionStackDialog("", AppLocal.getIntString("message.scripterror"),
                        AppLocal.getIntString("message.cannotprocessline"), sb.toString());
            }
        }
    }

    public static void clearDisplay() {
        if (advDisplay != null) {
            advDisplay.setProductImage(null);
        }
    }

    private static Boolean isEnabled() {
        return !TerminalInfo.getDisplay().equals("Not defined");
    }

    public static void updateProductImage(String id) {
        if (advDisplay != null) {
            try {
                advDisplay.setProductImage(dlSales.getProductImage(id));
            } catch (BasicException ex) {
                advDisplay.setProductImage(null);
            }
        }
    }
}
