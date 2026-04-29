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


package ke.kalc.pos.giftcards;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.PreparedSentence;
import ke.kalc.data.loader.SerializerReadDouble;
import ke.kalc.data.loader.SerializerReadInteger;
import ke.kalc.data.loader.SerializerReadString;
import ke.kalc.data.loader.SerializerWriteBasicExt;
import ke.kalc.data.loader.SerializerWriteString;
import ke.kalc.data.loader.Session;
import ke.kalc.format.Formats;
import ke.kalc.pos.loyalty.LoyaltyCard;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.data.loader.SessionFactory;

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
