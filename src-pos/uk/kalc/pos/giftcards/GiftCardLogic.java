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


package uk.kalc.pos.giftcards;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.Datas;
import uk.kalc.data.loader.PreparedSentence;
import uk.kalc.data.loader.SerializerReadDouble;
import uk.kalc.data.loader.SerializerReadInteger;
import uk.kalc.data.loader.SerializerReadString;
import uk.kalc.data.loader.SerializerWriteBasicExt;
import uk.kalc.data.loader.SerializerWriteString;
import uk.kalc.data.loader.Session;
import uk.kalc.format.Formats;
import uk.kalc.pos.loyalty.LoyaltyCard;
import uk.kalc.pos.ticket.TicketInfo;
import uk.kalc.data.loader.SessionFactory;

/**
 *
 * @author John Lewis
 */
public class GiftCardLogic {

    private PreparedSentence m_GiftCardActived;
    private PreparedSentence m_GiftCardBalance;
    private PreparedSentence m_CreateGiftCard;
    private PreparedSentence m_InsertTransaction;
    private PreparedSentence m_UpdateGiftCardBalance;
    private PreparedSentence m_CardKey;
    private PreparedSentence m_GiftCount;
    private Double value;

    public GiftCardLogic() {
        init(SessionFactory.getSession());
    }

    private void init(Session session) {

        m_GiftCardActived = new PreparedSentence(session, "select count(*) from giftcards where cardnumber = ? and active = true ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        m_GiftCardBalance = new PreparedSentence(session, "select currentvalue from giftcards where cardnumber = ? ", SerializerWriteString.INSTANCE, SerializerReadDouble.INSTANCE);
        m_CreateGiftCard = new PreparedSentence(session, "insert into giftcards (id, cardnumber, active, currentvalue ) values(?, ?, true, ?) ", new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.DOUBLE}, new int[]{0, 1, 2}));
        m_InsertTransaction = new PreparedSentence(session, "insert into giftcardtrans (cardnumber, activity, spendvalue, cardbalancevalue, ticketid, ticketdetails) values(?, ?, ?, ?, ?, ?) ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.STRING, Datas.SERIALIZABLE},
                new int[]{0, 1, 2, 3, 4, 5}));

        m_UpdateGiftCardBalance = new PreparedSentence(session, "update giftcards set currentvalue = ? where cardnumber = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.DOUBLE, Datas.STRING}, new int[]{0, 1}));

        m_CardKey = new PreparedSentence(session, "select id from giftcards where cardnumber = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);
        m_GiftCount = new PreparedSentence(session, "select count(*) from giftscards ", null, SerializerReadInteger.INSTANCE);
    }

    public Boolean isCardActivated(String cardNumber) {
        try {
            Integer i = (Integer) m_GiftCardActived.find(cardNumber);
            return (i > 0) ? true : false;
        } catch (BasicException ex) {
            return false;
        }
    }

    public Boolean activateCard(String cardNumber, Double value, TicketInfo ticket) {
        try {
            String id = UUID.randomUUID().toString();
            createCard(id, cardNumber, value);
            addTransaction(id, "Card activated", value, ticket);
        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public final void createCard(String id, String cardNumber, Double value) throws BasicException {
        try {
            Object[] values = new Object[]{id, cardNumber, value};
            m_CreateGiftCard.exec(values);
        } catch (BasicException ex) {
            System.out.println("Error creating the card");
        }
    }

    public Double getCardBalance(String cardID) {
        try {
            Object i = m_GiftCardBalance.find(cardID);
            return (i == null ? 0.00 : (Double) i);
        } catch (BasicException ex) {
            return 0.00;
        }
    }

    public String printCardBalance(String cardID) {
        return Formats.CURRENCY.formatValue(getCardBalance(cardID));
    }

    public void updateRedeemedValue(String cardID, TicketInfo ticket, GiftCardInfo giftCardInfo) {
        value = Double.valueOf(giftCardInfo.getRedeemedValue());
        try {
            Object[] values = new Object[]{m_CardKey.find(cardID), "Giftcard spend " + Formats.CURRENCY.formatValue(value), value, getCardBalance(cardID) - value, ticket.getId(), ticket};
            Object[] params = new Object[]{getCardBalance(cardID) - value, cardID};
            m_InsertTransaction.exec(values);
            m_UpdateGiftCardBalance.exec(params);

        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateRedeemedValue(String cardID, TicketInfo ticket, Double value) {
        //  value = Double.valueOf(giftCardInfo.getRedeemedValue());
        try {
            Object[] values = new Object[]{m_CardKey.find(cardID), "Giftcard spend " + Formats.CURRENCY.formatValue(value), value, getCardBalance(cardID) - value, ticket.getId(), ticket};
            Object[] params = new Object[]{getCardBalance(cardID) - value, cardID};
            m_InsertTransaction.exec(values);
            m_UpdateGiftCardBalance.exec(params);

        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateCard(String cardID, Double value, TicketInfo ticket) {
        try {
            Object[] values = new Object[]{m_CardKey.find(cardID), "Giftcard Top Up " + Formats.CURRENCY.formatValue(value), 0.0, getCardBalance(cardID) + value, ticket.getId(), ticket};
            Object[] params = new Object[]{getCardBalance(cardID) + value, cardID};
            m_InsertTransaction.exec(values);
            m_UpdateGiftCardBalance.exec(params);
        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addTransaction(String cardID, String activity, Double value, TicketInfo ticket) {
        try {
            Object[] values = new Object[]{cardID, activity, 0.00, value, ticket.getId(), ticket};
            m_InsertTransaction.exec(values);
        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getGiftCount() {
        int i;
        try {
            i = (Integer) m_GiftCount.find();;
        } catch (BasicException ex) {
            return 0;
        }
        return i;
    }
}
