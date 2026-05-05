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

package ke.kalc.pos.util;

/**
 * Centralized constants for SQL queries, table names, and column names.
 * This class provides type-safe references to database schema elements
 * to avoid hard-coded strings throughout the codebase.
 */
public final class SQLConstants {

    private SQLConstants() {
        // Prevent instantiation
    }

    // Table Names
    public static final String TABLE_PAYMENTS = "payments";
    public static final String TABLE_TICKETS = "tickets";
    public static final String TABLE_TICKET_LINES = "ticketlines";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CUSTOMERS = "customers";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_TAX_CATEGORIES = "taxcategories";
    public static final String TABLE_TAXES = "taxes";
    public static final String TABLE_INVENTORY = "inventory";
    public static final String TABLE_CATEGORIES = "categories";

    // Column Names - Payments
    public static final String COL_PAYMENT_ID = "id";
    public static final String COL_PAYMENT_DESCRIPTION = "description";
    public static final String COL_PAYMENT_PAYMENT = "payment";
    public static final String COL_PAYMENT_TOTAL = "total";

    // Column Names - Tickets
    public static final String COL_TICKET_ID = "id";
    public static final String COL_TICKET_TICKETID = "ticketid";
    public static final String COL_TICKET_PICKUPID = "pickupid";
    public static final String COL_TICKET_USER_ID = "userid";

    // Column Names - Products
    public static final String COL_PRODUCT_ID = "id";
    public static final String COL_PRODUCT_REFERENCE = "reference";
    public static final String COL_PRODUCT_NAME = "name";
    public static final String COL_PRODUCT_PRICE = "pricebuy";

    // Common Queries
    public static final String QUERY_COUNT_PAYMENTS_WITH_NULL_DESCRIPTION =
        "SELECT COUNT(*) FROM " + TABLE_PAYMENTS + " WHERE " + COL_PAYMENT_DESCRIPTION + " IS NULL OR " + COL_PAYMENT_DESCRIPTION + " = ''";

    public static final String QUERY_UPDATE_PAYMENT_DESCRIPTION =
        "UPDATE " + TABLE_PAYMENTS + " SET " + COL_PAYMENT_DESCRIPTION + " = ? WHERE " + COL_PAYMENT_ID + " = ?";

    public static final String QUERY_CREATE_PAYMENTS_TRIGGER =
        "CREATE DEFINER = CURRENT_USER TRIGGER update_payments BEFORE UPDATE ON " + TABLE_PAYMENTS +
        " FOR EACHROW BEGIN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'UPDATE cancelled payments'; END";
}
