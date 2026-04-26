/*
**    KALC POS  - Open Source Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
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


package ke.kalc.pos.loyalty;

import ke.kalc.pos.ticket.TicketInfo;

/**
 *
 * @author John
 */
public class CollectItemLoyaltyPoints extends CollectLoyaltyPoints {

    private Integer pointsRedeemed = 0;

    public CollectItemLoyaltyPoints(String cardNumber) {
        super(cardNumber);
    }

    @Override
    public void processTicketPoints(String cardNumber, TicketInfo ticket) {
        ticketBalance = 0;
        pointsRedeemed = 0;       
        ticket.getLines().forEach((line) -> {
            if (line.getProperty("redeemed_points") != null) {
                pointsRedeemed += Integer.valueOf(line.getProperty("redeemed_points"));
            } else if (line.getPrice() != 0.00) {
                Double qty = line.getMultiply();
                ticketBalance += (line.getItemEarnValue() * qty.intValue());
            }

        });

        if (ticketBalance != 0) {
            addTransaction(cardNumber + " : " + ticketBalance + " Points added to the loyalty card. Receipt - " + ticket.printId(), ticketBalance, ticket);
        }

        if (pointsRedeemed != 0) {
            addTransaction(cardNumber + " : " + pointsRedeemed + " Points redeemed from loyalty card. Receipt - " + ticket.printId(), -pointsRedeemed, ticket);
        }
    }

    @Override
    public Integer getRedeemedPoints(String cardNumber, TicketInfo ticket) {
        ticketBalance = 0;
        pointsRedeemed = 0;
        ticket.getLines().forEach((line) -> {
            if (line.getProperty("redeemed_points") != null) {
                pointsRedeemed += Integer.valueOf(line.getProperty("redeemed_points"));
            } else if (line.getPrice() != 0.00) {
                Double qty = line.getMultiply();
                ticketBalance += (line.getItemEarnValue() * qty.intValue());
            }
        });

        return pointsRedeemed;
    }

    @Override
    public Integer getEarnedPoints(String cardNumber, TicketInfo ticket) {
        ticketBalance = 0;
        pointsRedeemed = 0;
        ticket.getLines().forEach((line) -> {
            if (line.getProperty("redeemed_points") != null) {
                pointsRedeemed += Integer.valueOf(line.getProperty("redeemed_points"));
            } else if (line.getPrice() != 0.00) {
                Double qty = line.getMultiply();
                ticketBalance += (line.getItemEarnValue() * qty.intValue());
            }
        });
        return ticketBalance;       
    }

}
