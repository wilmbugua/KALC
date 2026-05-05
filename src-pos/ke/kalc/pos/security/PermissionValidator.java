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

package ke.kalc.pos.security;

public class PermissionValidator {
    
    public boolean hasPermissionForOperation(String userRole, String operation) {
        switch (operation) {
            case "TICKET_PRINTING":
                return "ADMIN".equals(userRole) || "USER".equals(userRole);
            case "PAYMENT_CONFIRMATION":
                return "ADMIN".equals(userRole) || "USER".equals(userRole);
            case "DATABASE_MODIFICATIONS":
                return "ADMIN".equals(userRole);
            case "CONFIGURATION_CHANGES":
                return "ADMIN".equals(userRole);
            default:
                return false;
        }
    }
}