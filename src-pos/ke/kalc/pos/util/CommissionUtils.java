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

/**
 * Utility class for calculating waiter commissions based on sales.
 * <p>
 * Commission rates (in Kenyan Shillings - KSH):
 * <ul>
 *   <li>Kitchen commission: fixed 2% of food sales</li>
 *   <li>Bar commission: tiered structure based on bar sales amount</li>
 * </ul>
 */
public class CommissionUtils {

    private CommissionUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Calculate kitchen commission (fixed 2% of food sales)
     * @param foodSales Amount of food sales
     * @return Kitchen commission amount
     */
    public static double calculateKitchenCommission(double foodSales) {
        return foodSales * 0.02;
    }

    /**
     * Calculate bar commission based on tiered structure:
     *  - 0 - 100,000 KSH: 1%
     *  - 100,001 - 200,000 KSH: 2%
     *  - 200,001 - 300,000 KSH: 3%
     *  - 300,001 - 400,000 KSH: 4%
     *  - 400,001 - 500,000 KSH: 5%
     *  - 500,001 - 600,000 KSH: 6%
     *  - 600,001 - 700,000 KSH: 7%
     *  - 700,001 - 800,000 KSH: 8%
     *  - 800,001 - 900,000 KSH: 9%
     *  - 900,001 - 1,000,000 KSH: 10%
     *  - above 1,000,000 KSH: 11%
     * @param barSales Amount of bar sales
     * @return Bar commission amount
     */
    public static double calculateBarCommission(double barSales) {
        if (barSales <= 100000) {
            return barSales * 0.01;
        } else if (barSales <= 200000) {
            return barSales * 0.02;
        } else if (barSales <= 300000) {
            return barSales * 0.03;
        } else if (barSales <= 400000) {
            return barSales * 0.04;
        } else if (barSales <= 500000) {
            return barSales * 0.05;
        } else if (barSales <= 600000) {
            return barSales * 0.06;
        } else if (barSales <= 700000) {
            return barSales * 0.07;
        } else if (barSales <= 800000) {
            return barSales * 0.08;
        } else if (barSales <= 900000) {
            return barSales * 0.09;
        } else if (barSales <= 1000000) {
            return barSales * 0.10;
        } else {
            return barSales * 0.11;
        }
    }

    /**
     * Calculate total waiter commission (kitchen + bar)
     * @param foodSales Amount of food sales
     * @param barSales Amount of bar sales
     * @return Total commission amount
     */
    public static double calculateTotalCommission(double foodSales, double barSales) {
        return calculateKitchenCommission(foodSales) + calculateBarCommission(barSales);
    }

    /**
     * Format commission amount as currency string (Ksh)
     * @param amount Commission amount
     * @return Formatted currency string
     */
    public static String formatCommission(double amount) {
        return String.format("Ksh %.2f", amount);
    }
}