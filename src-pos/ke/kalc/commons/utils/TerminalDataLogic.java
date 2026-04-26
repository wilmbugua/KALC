/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
 */
package ke.kalc.commons.utils;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.PreparedSentence;
import ke.kalc.data.loader.SentenceExec;
import ke.kalc.data.loader.SentenceFind;
import ke.kalc.data.loader.SerializerReadString;
import ke.kalc.data.loader.SerializerWriteBasic;
import ke.kalc.data.loader.SerializerWriteString;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.data.loader.StaticSentence;
import ke.kalc.pos.forms.AppConfig;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppUser;

public class TerminalDataLogic {

    private Session session;
    private final SentenceFind terminalActiveCash;
    private final SentenceFind terminalLocation;
    protected SentenceExec setLocation;
    protected SentenceExec setVersion;
    protected SentenceExec setActiveCash;
    protected SentenceExec logon;
    protected SentenceExec logoff;
    public static AppUser currentUser = null;

    public TerminalDataLogic() {
        session = SessionFactory.getSession();

        terminalLocation = new PreparedSentence(session,
                "select terminal_location from terminals where terminal_key = ? ",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE);

        terminalActiveCash = new PreparedSentence(session,
                "select active_cash from terminals where terminal_key = ? ",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE);

        setLocation = new StaticSentence(session, "update terminals set terminal_location = ? where terminal_key = ? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}));

        setVersion = new StaticSentence(session, "update terminals set appversion = ? where terminal_key = ? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}));

        setActiveCash = new StaticSentence(session, "update terminals set active_cash = ? where terminal_key = ? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}));

        logon = new StaticSentence(session, "update terminals set active_session = true, active_user = ? where terminal_key = ? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}));

        logoff = new StaticSentence(session, "update terminals set active_session = false, active_user = null where terminal_key = ? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING}));

    }

    public AppUser getCurrentUser() {
        return currentUser;
    }

    public String getTerminalLocation() {
        return TerminalInfo.getLocation();
    }

    public String getActiveCash() {
        try {
            return (String) terminalActiveCash.find(TerminalInfo.getTerminalID());
        } catch (BasicException ex) {
            Logger.getLogger(TerminalDataLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setTerminalLocation(String location) {
        try {
            setLocation.exec(new Object[]{location, TerminalInfo.getLocation()});
        } catch (BasicException ex) {
            Logger.getLogger(TerminalDataLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setTerminalVersion() {
        try {
            setVersion.exec(new Object[]{AppLocal.APP_VERSION, TerminalInfo.getTerminalID()});
        } catch (BasicException ex) {
            Logger.getLogger(TerminalDataLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setActiveCash(String activeCash) {
        try {
            setActiveCash.exec(new Object[]{activeCash, TerminalInfo.getTerminalID()});
        } catch (BasicException ex) {
            Logger.getLogger(TerminalDataLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loginUser(AppUser m_actionuser) {
        try {
            logon.exec(new Object[]{m_actionuser.getId(), TerminalInfo.getTerminalID()});
            currentUser = m_actionuser;
        } catch (BasicException ex) {
            currentUser = null;
            Logger.getLogger(TerminalDataLogic.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public void logoutUser() {
        try {
            logoff.exec(new Object[]{TerminalInfo.getTerminalID()});
            currentUser = null;
        } catch (BasicException ex) {
            currentUser = null;
            Logger.getLogger(TerminalDataLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getTerminalName() {
        String terminal = TerminalInfo.getTerminalName();
        if (terminal.equalsIgnoreCase("Unknown")) {
            JAlertPane.messageBox(new Dimension(450, 250), JAlertPane.INFORMATION, AppLocal.getIntString("alert.noTerminalName"), 16,
                    new Dimension(125, 50), JAlertPane.OK_OPTION);
            System.exit(0);
        } else {
            try {
                PreparedStatement pstmt = session.getConnection().prepareStatement("select count(*) from terminals where terminal_key = ? ");
                pstmt.setString(1, TerminalInfo.getTerminalID());
                ResultSet rsTables = pstmt.executeQuery();
                if (rsTables.next()) {
                    if (rsTables.getInt(1) == 0) {
                        pstmt = session.getConnection().prepareStatement("insert into terminals (id, terminal_name, terminal_key) values (?, ?, ?)");
                        pstmt.setString(1, TerminalInfo.getTerminalName());
                        pstmt.setString(2, TerminalInfo.getTerminalName());
                        pstmt.setString(3, TerminalInfo.getTerminalID());
                        pstmt.executeUpdate();
                    } else {
                        pstmt = session.getConnection().prepareStatement("update terminals set id = ?, terminal_name = ? where  terminal_key = ?");
                        pstmt.setString(1, TerminalInfo.getTerminalName());
                        pstmt.setString(2, TerminalInfo.getTerminalName());
                        pstmt.setString(3, TerminalInfo.getTerminalID());
                        pstmt.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(TerminalDataLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
            AppConfig.put("terminalID", terminal);
            return terminal;
        }

        return AppConfig.getString("terminalID");
    }

}
