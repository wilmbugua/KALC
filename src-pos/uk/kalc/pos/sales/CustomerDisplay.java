/*
**    KALC Administration  - Open Source Point of Sale
**
**    This file is part of KALC Administration Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC  
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
package uk.kalc.pos.sales;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.kalc.basic.BasicException;
import uk.kalc.commons.dialogs.JAlertPane;
import uk.kalc.commons.utils.TerminalInfo;
import uk.kalc.data.loader.SessionFactory;
import uk.kalc.globals.SystemProperty;
import uk.kalc.pos.datalogic.DataLogicSales;
import uk.kalc.pos.datalogic.DataLogicSystem;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.AppView;
import uk.kalc.pos.forms.JRootApp;
import uk.kalc.pos.printer.DeviceDisplayAdvance;
import uk.kalc.pos.printer.IncludeFile;
import uk.kalc.pos.printer.TicketParser;
import uk.kalc.pos.printer.TicketPrinterException;
import uk.kalc.pos.scripting.ScriptEngine;
import uk.kalc.pos.scripting.ScriptException;
import uk.kalc.pos.scripting.ScriptFactory;
import uk.kalc.pos.ticket.TicketLineInfo;

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
