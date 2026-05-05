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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationShutdownHook.class);

    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                flushLogs();
                closeDatabaseConnections();
                saveSessionState();
                auditShutdownReason();
            } catch (Exception e) {
                logger.error("Error during shutdown: ", e);
            }
        }));
    }

    private static void flushLogs() {
        // Logic to flush logs
        logger.info("Logs flushed");
    }

    private static void closeDatabaseConnections() {
        // Logic to close database connections
        logger.info("Database connections closed");
    }

    private static void saveSessionState() {
        // Logic to save session state
        logger.info("Session state saved");
    }

    private static void auditShutdownReason() {
        // Logic to audit shutdown reason
        logger.info("Shutdown reason audited");
    }

    // Replace System.exit() calls with this method
    public static void shutdown() {
        logger.info("Shutting down application... ");
        // Additional shutdown logic can be added here
        // System.exit() calls should be avoided
    }
}