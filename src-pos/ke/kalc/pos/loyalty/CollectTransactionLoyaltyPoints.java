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


package ke.kalc.pos.loyalty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.payment.PaymentInfo;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.pos.ticket.TicketLineInfo;

/**
 *
 * @author John
 */
public class CollectTransactionLoyaltyPoints extends CollectLoyaltyPoints {

    public CollectTransactionLoyaltyPoints(String cardNumber) {
        super(cardNumber);
    }

    @Override
    public void processTicketPoints(String cardNumber, TicketInfo ticket) {
        Double values = 0.00;
        int lines = 0;
        while (lines < ticket.getLinesCount()) {
            if (ticket.getLine(lines).getProductID().equals("giftcard-001") || ticket.getLine(lines).getProductID().equals("giftcard-topup")) {
                values += ticket.getLine(lines).getPrice();
            }
            lines++;
        }

        pointsRedeemed = 0;
        Double pointsValue = 0.00;
        List<PaymentInfo> payments = new ArrayList(ticket.getPayments());
        for (PaymentInfo p : payments) {
            if (p.getName().equals("loyalty")) {
                pointsRedeemed += p.getBurnPoints();
                pointsValue += p.getTotal();
            }
        }

        ticket.getLines().forEach((line) -> {
            //check if the product is allowed in the loyalty scheme and get multiplier
            if (line.getPrice() != 0.00 & !line.getProductID().equals("giftcard-sale") & !line.getProductID().equals("giftcard-topup")) {
                workingTotal += (line.getLinePrice() * line.getProductInfoExt().getLoyaltyMultiplier());
            }
        });

        ticketBalance = (BigDecimal.valueOf(workingTotal - pointsValue - values)).setScale(2, RoundingMode.HALF_UP).intValue() * SystemProperty.EARNXPOINTS;

        if (ticketBalance != 0) {
            addTransaction(cardNumber + " : " + ticketBalance + " Points added to the loyalty card. Receipt - " + ticket.printId(), ticketBalance, ticket);
        }

        if (pointsRedeemed != 0) {
            addTransaction(cardNumber + " : " + pointsRedeemed + " Points redeemed from loyalty card. Receipt - " + ticket.printId(), -pointsRedeemed, ticket);
        }
    }

    @Override
    public Integer getRedeemedPoints(String cardNumber, TicketInfo ticket) {
        Double values = 0.00;
        Double workingTotal = 0.00;
        Integer pointsRedeemed = 0;
        Double pointsValue = 0.00;

        int lines = 0;
        while (lines < ticket.getLinesCount()) {
            if (ticket.getLine(lines).getProductID().equals("giftcard-001") || ticket.getLine(lines).getProductID().equals("giftcard-topup")) {
                values += ticket.getLine(lines).getPrice();
            }
            lines++;
        }

        List<PaymentInfo> payments = new ArrayList(ticket.getPayments());
        for (PaymentInfo p : payments) {
            if (p.getName().equals("loyalty")) {
                pointsRedeemed += p.getBurnPoints();
                pointsValue += p.getTotal();
            }
        }

        return pointsRedeemed;
    }

    @Override
    public Integer getEarnedPoints(String cardNumber, TicketInfo ticket) {
        Double values = 0.00;
        Double workingTotal = 0.00;
        Integer pointsRedeemed = 0;
        Double pointsValue = 0.00;

        int lines = 0;
        while (lines < ticket.getLinesCount()) {
            if (ticket.getLine(lines).getProductID().equals("giftcard-001") || ticket.getLine(lines).getProductID().equals("giftcard-topup")) {
                values += ticket.getLine(lines).getPrice();
            }
            lines++;
        }

        List<PaymentInfo> payments = new ArrayList(ticket.getPayments());
        for (PaymentInfo p : payments) {
            if (p.getName().equals("loyalty")) {
                pointsRedeemed += p.getBurnPoints();
                pointsValue += p.getTotal();
            }
        }

        for (TicketLineInfo line : ticket.getLines()) {
            if (line.getPrice() != 0.00 & !line.getProductID().equals("giftcard-sale") & !line.getProductID().equals("giftcard-topup")) {
                workingTotal += (line.getLinePrice() * line.getProductInfoExt().getLoyaltyMultiplier());
            }
        };
        return (BigDecimal.valueOf(workingTotal - pointsValue - values)).setScale(2, RoundingMode.HALF_UP).intValue() * SystemProperty.EARNXPOINTS;
    }

}
