/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke   
**
*/

package ke.kalc.commons.dbmanager;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.commons.utils.TerminalInfo;
import ke.kalc.data.loader.ConnectionFactory;
import ke.kalc.pos.forms.AppConfig;
import ke.kalc.pos.forms.AppLocal;

/**
 * Database utility class for common operations.
 */
public class DbUtils {

    private static final Connection connection = ConnectionFactory.getInstance().getConnection();
    private static final Logger logger = Logger.getLogger(DbUtils.class.getName());
    private static int rowCount = 0;

    public static String getTerminalName() {
        String terminal = TerminalInfo.getTerminalName();
        if (terminal.equalsIgnoreCase("Unknown")) {
            JAlertPane.messageBox(new Dimension(450, 250), JAlertPane.INFORMATION, AppLocal.getIntString("alert.noTerminalName"), 16,
                    new Dimension(125, 50), JAlertPane.OK_OPTION);
            // Removed System.exit - throw exception instead
            throw new IllegalStateException("Terminal name is unknown. Configuration required.");
        }

        String terminalID = TerminalInfo.getTerminalID();
        try (PreparedStatement pstmt = connection.prepareStatement("select count(*) from terminals where terminal_key = ? ")) {
            pstmt.setString(1, terminalID);
            try (ResultSet rsTables = pstmt.executeQuery()) {
                if (rsTables.next()) {
                    if (rsTables.getInt(1) == 0) {
                        try (PreparedStatement insertStmt = connection.prepareStatement(
                                "insert into terminals (id, terminal_name, terminal_key, terminal_location) values (?, ?, ?, ?)")) {
                            insertStmt.setString(1, TerminalInfo.getTerminalName());
                            insertStmt.setString(2, TerminalInfo.getTerminalName());
                            insertStmt.setString(3, terminalID);
                            insertStmt.setString(4, TerminalInfo.getLocation());
                            insertStmt.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement updateStmt = connection.prepareStatement(
                                "update terminals set id = ?, terminal_name = ?, terminal_location = ? where terminal_key = ?")) {
                            updateStmt.setString(1, TerminalInfo.getTerminalName());
                            updateStmt.setString(2, TerminalInfo.getTerminalName());
                            updateStmt.setString(3, TerminalInfo.getLocation());
                            updateStmt.setString(4, terminalID);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Database error while accessing terminal information: {0}", ex.getMessage());
        }
        AppConfig.put("terminalID", terminal);
        return AppConfig.getString("terminalID");
    }

    public static Integer getTriggerCount() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("select count(*) from INFORMATION_SCHEMA.TRIGGERS where trigger_schema = DATABASE() AND trigger_name NOT IN ('giftcard_insert','gift_trans_insert','loyalty_insert','loyalty_trans_insert' )")) {
            if (rs.next()) {
                rowCount = rs.getInt(1);
            }
            return rowCount;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving trigger count: {0}", ex.getMessage());
        }
        return 0;
    }

    public static Integer getViewCount() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("select count(*) from INFORMATION_SCHEMA.VIEWS where TABLE_SCHEMA = DATABASE()  and TABLE_NAME = 'recipes'")) {
            if (rs.next()) {
                rowCount = rs.getInt(1);
            }
            return rowCount;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving view count: {0}", ex.getMessage());
        }
        return 0;
    }
}
                }
            } catch (SQLException ex) {

            }
            AppConfig.put("terminalID", TerminalInfo.getTerminalName());
        }
        return AppConfig.getString("terminalID");
    }

    public static Integer getTriggerCount() {
        try {
            String sql = "select count(*) from INFORMATION_SCHEMA.TRIGGERS where trigger_schema = DATABASE() AND trigger_name NOT IN ('giftcard_insert','gift_trans_insert','loyalty_insert','loyalty_trans_insert' )";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                rowCount = rs.getInt(1);
            }
            return rowCount;
        } catch (SQLException ex) {
        }
        return 0;
    }

    public static Integer getViewCount() {
        try {
            String sql = "select count(*) from INFORMATION_SCHEMA.VIEWS where TABLE_SCHEMA = DATABASE()  and TABLE_NAME = 'recipes'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                rowCount = rs.getInt(1);
            }
            return rowCount;
        } catch (SQLException ex) {
        }
        return 0;
    }
}
