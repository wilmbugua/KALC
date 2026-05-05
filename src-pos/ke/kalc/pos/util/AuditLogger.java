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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {
    private static final String LOG_FORMAT = "%s | User: %s | Operation: %s | Affected Records: %s | IP Address: %s";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String userId, String operationType, String affectedRecords, String ipAddress) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logMessage = String.format(LOG_FORMAT, timestamp, userId, operationType, affectedRecords, ipAddress);

        // Here, we would write the logMessage to a logging system or file
        System.out.println(logMessage); // Placeholder for actual logging
    }

    public static void main(String[] args) {
        // Example usage
        log("myne19706", "Login", "N/A", "192.168.1.1");
        log("myne19706", "Payment Processing", "Order ID: 12345", "192.168.1.1");
        log("myne19706", "Logout", "N/A", "192.168.1.1");
        // Add more logging as needed
    }
}