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


package ke.kalc.pos.datalogic;

import java.awt.Dimension;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.PreparedSentence;
import ke.kalc.data.loader.SentenceFind;
import ke.kalc.data.loader.SerializerReadBasic;
import ke.kalc.data.loader.SerializerReadBoolean;
import ke.kalc.data.loader.SerializerReadInteger;
import ke.kalc.data.loader.SerializerReadString;
import ke.kalc.data.loader.SerializerWriteBasicExt;
import ke.kalc.data.loader.SerializerWriteString;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.data.loader.StaticSentence;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.ticket.TicketInfo;

/**
 *
 * @author John
 */
public class DataLogicLoyalty {

    protected static final Session session;
    protected static SentenceFind sLoyaltyPresent;
    protected static SentenceFind sLoyaltyActive;
    protected static SentenceFind sLoyaltyLocked;
    protected static SentenceFind sLoyaltyAvailable;
    protected static SentenceFind sLoyaltyCardRetired;
    protected static SentenceFind sLoyaltyCardBalance;
    protected static SentenceFind sCustomerActive;
    protected static SentenceFind sLoyaltyCustomerID;
    protected static SentenceFind sLoyaltyReplaced;
    protected static SentenceFind sLoyaltyCount;
    protected static SentenceFind sIsCardEnabled;
    protected static SentenceFind sLoyaltyBalanceByCard;
    protected static SentenceFind sGetCardByCustomerID;
    protected static SentenceFind sGetCardIdByCustomerID;
    protected static SentenceFind sIsCardAssignedToAnotherCustomer;
    protected static SentenceFind sCardHasCustomerAssigned;
    protected static SentenceFind sLoyaltyID;
    protected static SentenceFind sCustomerHasCard;
    protected static SentenceFind sCustomerHasLoyaltyCard;
    protected static SentenceFind sCardID;
    protected static SentenceFind sLoyaltyCardLastActivity;
    //protected static SentenceFind loyaltyCardTransBalance;

    static {
        session = SessionFactory.getSession();

        //card status's
        sLoyaltyPresent = new PreparedSentence(session, "select count(*) from loyaltycards where cardnumber = ? ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sLoyaltyActive = new PreparedSentence(session, "select count(*) from loyaltycards where cardnumber = ? and active = true ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sLoyaltyLocked = new PreparedSentence(session, "select count(*) from loyaltycards where cardnumber = ? and cardlocked = true and replaced = false and removed = false ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sLoyaltyReplaced = new PreparedSentence(session, "select count(*) from loyaltycards where cardnumber = ? and replaced = true ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);

        sLoyaltyAvailable = new PreparedSentence(session, "select count(*) from loyaltycards where cardnumber = ? and active = true and replaced = false and removed = false ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sLoyaltyCardRetired = new PreparedSentence(session, "select count(*) from retiredcards where cardnumber = ? ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);

        sCustomerActive = new PreparedSentence(session, "select active from customers where id = ? ", SerializerWriteString.INSTANCE, SerializerReadBoolean.INSTANCE);
        sLoyaltyCustomerID = new PreparedSentence(session, "select customerid from loyaltycards where cardnumber = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        sIsCardEnabled = new PreparedSentence(session, "select loyaltyenabled from customers where id = ? ", SerializerWriteString.INSTANCE, SerializerReadBoolean.INSTANCE);

        //card balances        
        sLoyaltyCardBalance = new PreparedSentence(session, "select currentpoints from loyaltycards where id = ? ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sLoyaltyBalanceByCard = new PreparedSentence(session, "select currentpoints from loyaltycards where cardnumber = ?", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
// sLoyaltyBalanceByCard = new PreparedSentence(session, "select currentpoints from loyaltycards where cardnumber = (select id from loyaltycards where cardnumber = ?) order by id desc limit 1", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        
        sLoyaltyCount = new PreparedSentence(session, "select count(*) from loyaltycards ", null, SerializerReadInteger.INSTANCE);

        sGetCardByCustomerID = new StaticSentence(session, "select loyaltycardnumber from customers where id = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);
        sGetCardIdByCustomerID = new StaticSentence(session, "select loyaltycardid from customers where id = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);
        sIsCardAssignedToAnotherCustomer = new PreparedSentence(session, "select count(*) from loyaltycards where cardnumber = ? and customerid is not null", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sCardHasCustomerAssigned = new PreparedSentence(session, "select count(*) from loyaltycards where cardnumber = ? and active = true and customerid is null", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sLoyaltyID = new PreparedSentence(session, "select id from loyaltycards where cardnumber = ?", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);
        sCustomerHasCard = new PreparedSentence(session, "select count(*) from loyaltycards where customerid = ? ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sCustomerHasLoyaltyCard = new PreparedSentence(session, "select count(*) from customers where card = ? and loyaltyenabled = true ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);
        sCardID = new PreparedSentence(session, "select cardnumber from loyaltycards where id = ?", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        sLoyaltyCardLastActivity = new StaticSentence(session, "select id, cardbalance, activity, activitypoints, activitydate from loyaltytrans where cardnumber = ? order by id desc limit 1 ",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{
            Datas.STRING,
            Datas.INT,
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP}));

//      m_loyaltyCardTransBalance = new PreparedSentence(session, "select cardbalance from loyaltytrans where cardnumber = ? order by id desc limit 1", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);        
    }

    public DataLogicLoyalty() {
    }

    public static Boolean isCardPresent(String cardNumber) {
        try {
            Integer i = (Integer) sLoyaltyPresent.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static Boolean isCardActive(String cardNumber) {
        try {
            Integer i = (Integer) sLoyaltyActive.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static Boolean isCustomerActive(String customerID) {
        try {
            return (Boolean) sCustomerActive.find(customerID);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static Boolean isCardAvailable(String cardNumber) {
        try {
            Integer i = (Integer) sLoyaltyAvailable.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static Boolean isCardRetired(String cardNumber) {
        try {
            Integer i = (Integer) sLoyaltyCardRetired.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static Boolean isCardLocked(String cardNumber) {
        try {
            Integer i = (Integer) sLoyaltyLocked.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static Integer getCardPoints(String cardNumber) {
        Object[] values = new Object[]{cardNumber};
        try {
            return (Integer) sLoyaltyCardBalance.find(cardNumber);
        } catch (BasicException ex) {
            return 0;
        }
    }

    public static Integer getBalanceByCardNumber(String cardNumber) {
        try {
            Integer i = (Integer) sLoyaltyBalanceByCard.find(cardNumber);
            return (i > 0) ? i : 0;
        } catch (BasicException ex) {

        }
        return 0;
    }

    public static Boolean cardReplaced(String cardNumber) {
        try {
            Integer i = (Integer) sLoyaltyReplaced.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static int getLoyaltyCount() {
        try {
            Integer i = (Integer) sLoyaltyCount.find();
            return i;
        } catch (BasicException ex) {
            return 0;
        }

    }

    public static Boolean isCardEnabled(String customerID) {
        try {
            return (Boolean) sIsCardEnabled.find(customerID);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static String getCardByCustomerId(String customerID) {
        if (customerID.length() != 0) {
            try {
                return (String) sGetCardByCustomerID.find(customerID);
            } catch (BasicException ex) {
                return "";
            }
        }
        return "";
    }

    public static String getCardIdByCustomerId(String customerID) {
        if (customerID.length() != 0) {
            try {
                return (String) sGetCardIdByCustomerID.find(customerID);
            } catch (BasicException ex) {
                return "";
            }
        }
        return "";
    }

    public static final Boolean isCardAssignedToAnotherCustomer(String cardNumber) {
        try {
            Integer i = (Integer) sIsCardAssignedToAnotherCustomer.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static final Boolean cardHasCustomerAssigned(String cardNumber) {
        try {
            Integer i = (Integer) sCardHasCustomerAssigned.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static final Boolean customerHasCardAssigned(String customerId) {
        try {
            Integer i = (Integer) sCustomerHasCard.find(customerId);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static final Boolean customerHasLoyaltyCard(String cardNumber) {
        try {
            Integer i = (Integer) sCustomerHasLoyaltyCard.find(cardNumber);
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public static String getCardID(String cardNumber) {
        if (cardNumber.length() != 0) {
            try {
                return (String) sLoyaltyID.find(cardNumber);
            } catch (BasicException ex) {
                return null;
            }
        }
        return null;
    }

    public static String getLoyaltyCustomerID(String cardNumber) {
        try {
            return (String) sLoyaltyCustomerID.find(cardNumber);
        } catch (BasicException ex) {
            return null;
        }
    }

    public static String getCardNumber(String cardID) {
        if (cardID.length() != 0) {
            try {
                return (String) sCardID.find(cardID);
            } catch (BasicException ex) {
                return null;
            }
        }
        return null;
    }

    //add a new loyalty card to the database - tested 01-03
    public static void addAccountLoyaltyCard(String cardNumber, String customerID, String customerName, Boolean active, String siteGuid) throws BasicException {
        Object values[] = new Object[]{cardNumber, customerID, active, siteGuid};
        if (!isCardPresent(cardNumber)) {
            try {
                createNewCard(UUID.randomUUID().toString(), cardNumber, customerID, customerName, active);
                JAlertPane.showAlertDialog(JAlertPane.CONFIRMATION,
                        AppLocal.getIntString("dialog.customersLoyaltyWarning"),
                        AppLocal.getIntString("dialog.customersLoyaltyHeaderActivated"),
                        AppLocal.getIntString("dialog.customersLoyaltyContextactivated", cardNumber),
                        JAlertPane.OK_OPTION, true);
                new PreparedSentence(session, "update customers set loyaltycardid = (select id from loyaltycards where cardnumber = ?), loyaltycardnumber = ? where id = ? and siteguid = ? ",
                        new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.BOOLEAN, Datas.STRING}, new int[]{0, 0, 1, 3})).exec(values);
            } catch (BasicException e) {
                JAlertPane.showAlertDialog(JAlertPane.WARNING,
                        AppLocal.getIntString("dialog.customersLoyaltyWarning"),
                        AppLocal.getIntString("dialog.customersLoyaltyHeaderActivatedFailed"),
                        AppLocal.getIntString("dialog.customersLoyaltyContextActivateFailed", cardNumber),
                        JAlertPane.OK_OPTION);
                throw new BasicException();
            }
        }
    }

    //create a new loyalty card  record transaction - tested 01-03
    public static void createNewCard(String id, String cardNumber, String customerId, String customerName, Boolean active) throws BasicException {
        int dialogResult;
        String message;
        if (active) {
            dialogResult = JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.loyaltyCardNotAvailable", cardNumber), 16,
                    new Dimension(125, 50), JAlertPane.YES_NO_OPTION);
            message = cardNumber + " : New card created, activated and assigned to customer : '" + customerName + "'.";
        } else {
            dialogResult = showAlert(AppLocal.getIntString("message.loyaltyCardNotAvailable", cardNumber), AppLocal.getIntString("message.loyaltyAddNewCardNoActivation"));
            message = cardNumber + " : New card created and assigned to customer : '" + customerName + "'.";
        }
        if (dialogResult == 5) {
            Object[] values = new Object[]{id, cardNumber, customerId};
            new PreparedSentence(session, "insert into loyaltycards (id, cardnumber, customerid, active, cardlocked) values(?, ?, ?, " + active + "," + !active + ") ",
                    new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING}, new int[]{0, 1, 2})).exec(values);
            new PreparedSentence(session, "update customers set loyaltycardid = ?, loyaltycardnumber = ?  where id = ? ",
                    new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING}, new int[]{0, 1, 2})).exec(values);
            addTransaction(cardNumber, customerId, message);
        }
    }

    //called by customer insert 
    public static void addCustomerIDToLoyaltyCard(String cardNumber, String customerId, String customerName, Boolean active) throws BasicException {
        String header;
        String context;
        if (isCardPresent(cardNumber)) {
            addCustomerIDtoLoyaltyCard(cardNumber, customerId);
            addTransaction(cardNumber, customerId, cardNumber + " : Card has been assigned to a customer  '" + customerName + "'", 0, getBalanceByCardNumber(cardNumber));
        } else {
            createNewCard(UUID.randomUUID().toString(), cardNumber, customerId, customerName, active);
            addCustomerIDtoLoyaltyCard(cardNumber, customerId);
            if (active) {
                header = AppLocal.getIntString("dialog.customersLoyaltyHeaderActivatedFailed");
                context = AppLocal.getIntString("dialog.customersLoyaltyContextActivateFailed", cardNumber);
            } else {
                header = AppLocal.getIntString("dialog.customersLoyaltyHeaderCreateFailed");
                context = AppLocal.getIntString("dialog.customersLoyaltyContextCreateFailed", cardNumber);
            }
            JAlertPane.showAlertDialog(JAlertPane.WARNING,
                    AppLocal.getIntString("dialog.customersLoyaltyWarning"),
                    header, context, JAlertPane.OK_OPTION, true);
        }
        // addCustomerIDtoLoyaltyCard(cardNumber, customerId);
    }

    private static final void addCustomerIDtoLoyaltyCard(String cardNumber, String customerId) throws BasicException {
        Object[] values = new Object[]{customerId, cardNumber};
        new PreparedSentence(session, "update loyaltycards "
                + "set customerid = ? "
                + "where cardnumber = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 1})).exec(values);
        new PreparedSentence(session, "update customers set loyaltycardid = (select id from loyaltycards where cardnumber = ?) where id = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{1, 0})).exec(values);
    }

    //deactivate and lock the card
    public static void deactivateAndLockCard(String cardNumber, String customerId, String reason) throws BasicException {
        Object[] values = new Object[]{cardNumber};
        new PreparedSentence(session, "update loyaltycards "
                + "set cardlocked = true, active = false "
                + "where cardnumber = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.BOOLEAN}, new int[]{0})).exec(values);
        addTransaction(cardNumber, customerId, cardNumber + " : Card deactivated and Locked. " + reason, 0, getBalanceByCardNumber(cardNumber));
    }

    public static void deactivateAndRemoveCard(String cardNumber, String customerId, String reason) throws BasicException {
        Object[] values = new Object[]{cardNumber};
        new PreparedSentence(session, "update loyaltycards "
                + "set cardlocked = true, active = false "
                + "where cardnumber = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.BOOLEAN}, new int[]{0})).exec(values);
        addTransaction(cardNumber, customerId, cardNumber + " : Card deactivated and Removed. " + reason);
    }

    public static void reactivateAndUnlockCard(String cardNumber, String customerId, String reason) throws BasicException {
        Object[] values = new Object[]{cardNumber};
        new PreparedSentence(session, "update loyaltycards "
                + "set cardlocked = false, active = true "
                + "where cardnumber = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.BOOLEAN}, new int[]{0})).exec(values);

        addTransaction(cardNumber, customerId, cardNumber + " : Card reactivated and unlocked. " + reason, 0, getBalanceByCardNumber(cardNumber));
    }

    public static void replaceCard(String oldCardNumber, String newCardNumber, String customerId, String customerName, Boolean active, Boolean loyaltyEnabled) throws BasicException {
        if (oldCardNumber.equals(newCardNumber)) {
            return;
        }
        if (oldCardNumber.isEmpty() & loyaltyEnabled) {
            createNewCard(UUID.randomUUID().toString(), newCardNumber, customerId, customerName, active);
        } else if (oldCardNumber.isEmpty() & !loyaltyEnabled) {
            return;
        }

        Integer cardBalance = getBalanceByCardNumber(oldCardNumber);
        if (newCardNumber.isEmpty()) {
            removeCard(oldCardNumber, newCardNumber, customerId, customerName);
        } else {
            Object values[] = new Object[]{oldCardNumber, newCardNumber, customerId};
            createNewCard(UUID.randomUUID().toString(), newCardNumber, customerId, customerName, active);

            //retire the old card
            int i = new PreparedSentence(session, "update customers set taxid = ?  where customertype = 'loyalty' and id = ? ",
                    new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING}, new int[]{1, 2})).exec(values);
            new PreparedSentence(session, "update loyaltycards set replaced = true, cardlocked = true, active = false where cardnumber = ? ",
                    new SerializerWriteBasicExt(new Datas[]{Datas.STRING}, new int[]{0})).exec(values);
            new PreparedSentence(session, "update loyaltycards set oldcardnumber = ? where cardnumber = ? ",
                    new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 1})).exec(values);

            addTransaction(oldCardNumber, customerId, oldCardNumber + " : Card replaced, with new card : " + newCardNumber);
            addTransaction(newCardNumber, customerId, newCardNumber + " : " + cardBalance + " Points transfered to new card  ", cardBalance);
            addTransaction(oldCardNumber, customerId, oldCardNumber + " : " + cardBalance + " Points removed from old card  ", -cardBalance, 0);
        }
    }

    public static void removeCard(String oldCardNumber, String newCardNumber, String customerId, String customerName) throws BasicException {
        Integer cardBalance = getBalanceByCardNumber(oldCardNumber);
        Object values[] = new Object[]{oldCardNumber, newCardNumber, customerId};
        new PreparedSentence(session, "update customers set loyaltycardnumber = null, loyaltycardid = null, loyaltyenabled = false where id = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING}, new int[]{2})).exec(values);
        new PreparedSentence(session, "update loyaltycards set cardlocked = true, removed = true, active = false where cardnumber = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING}, new int[]{0})).exec(values);
        addTransaction(oldCardNumber, customerId, oldCardNumber + " : Card removed from customer account : '" + customerName + "'", 0);
        addTransaction(oldCardNumber, customerId, oldCardNumber + " : " + cardBalance + " Points removed from removed card", 0);
        JAlertPane.showAlertDialog(JAlertPane.WARNING,
                AppLocal.getIntString("dialog.customersLoyaltyWarning"),
                AppLocal.getIntString("dialog.customersLoyaltyRemoved"),
                AppLocal.getIntString("dialog.customersLoyaltyContextRemoved", oldCardNumber),
                JAlertPane.OK_OPTION,
                true);
    }

    private static void showMessage(String cardNumber, Boolean active) {
        if (active) {
            JAlertPane.showAlertDialog(JAlertPane.WARNING,
                    AppLocal.getIntString("dialog.customersLoyaltyWarning"),
                    AppLocal.getIntString("dialog.customersLoyaltyReActivated"),
                    AppLocal.getIntString("dialog.customersLoyaltyContextReActivate", cardNumber),
                    JAlertPane.OK_OPTION, true);
        } else {
            JAlertPane.showAlertDialog(JAlertPane.WARNING,
                    AppLocal.getIntString("dialog.customersLoyaltyWarning"),
                    AppLocal.getIntString("dialog.customersLoyaltyDeActivated"),
                    AppLocal.getIntString("dialog.customersLoyaltyContextDeActivate", cardNumber),
                    JAlertPane.OK_OPTION,
                    true);
        }
    }

    private static int showAlert(String headerText, String contextText) {
        return JAlertPane.showAlertDialog(JAlertPane.CONFIRMATION,
                AppLocal.getIntString("message.loyaltyCardTitle"),
                headerText,
                contextText,
                JAlertPane.YES_NO_OPTION,
                true);
    }

    public static void addTransaction(String cardNumber, String customerId, String activity) throws BasicException {
        addLoyaltyTransaction(new Object[]{
            getCardID(cardNumber),
            customerId,
            activity,
            0,
            0,
            null,
            null
        });
    }

    public static void addTransaction(String card, String customerId, String activity, Integer activityPoints) throws BasicException {
        addLoyaltyTransaction(new Object[]{
            getCardID(card),
            customerId,
            activity,
            activityPoints,
            activityPoints,
            null,
            null
        });
    }

    public static void addTransaction(String card, String customerId, String activity, Integer activityPoints, Integer cardBalance) throws BasicException {
        addLoyaltyTransaction(new Object[]{
            getCardID(card),
            customerId,
            activity,
            activityPoints,
            cardBalance,
            null,
            null
        });
    }

    public static void addLoyaltyTransaction(Object[] values) throws BasicException {
        new PreparedSentence(session, "insert into loyaltytrans (cardnumber, customerid, activity, activitypoints, cardbalance, ticketid, ticketdetails) values(?, ?, ?, ?, ?, ?, ?) ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.INT, Datas.INT, Datas.STRING, Datas.SERIALIZABLE},
                new int[]{0, 1, 2, 3, 4, 5, 6})).exec(values);

        new PreparedSentence(session, "update loyaltycards set currentpoints = ? where id = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.INT, Datas.INT, Datas.INT, Datas.INT},
                new int[]{4, 0})).exec(values);
    }

    public static void updateLoyaltyOrig(Object params, Boolean prevLoyaltyEnabledState, Boolean prevActiveState) throws BasicException {
        Object[] values = (Object[]) params;

        String oldCardNumber;
        String newCardNumber;
        String loyaltyCard;
        String customerId = (String) values[0];
        String customerName = (String) values[3];
        Boolean active = (Boolean) values[20];
        Boolean loyaltyEnabled = (Boolean) values[28];
        Boolean cardChangedTest = (values[1].toString().equals("account")) ? !values[27].toString().equals(values[5].toString())
                : !values[27].toString().equals(values[2].toString());

        int activeChanged = (Boolean.compare(prevActiveState, active) == 0) ? 0 : 1;
        int enabledChanged = (Boolean.compare(loyaltyEnabled, prevLoyaltyEnabledState) == 0) ? 0 : 2;
        int cardChanged;
        switch (values[1].toString()) {
            case "loyalty":
                loyaltyCard = (String) values[2];
                newCardNumber = (String) values[27];

                cardChanged = (loyaltyCard.isEmpty()) ? ((cardChangedTest & loyaltyCard.isEmpty()) ? 0 : 4) : ((cardChangedTest) ? 4 : 0);
                switch (activeChanged + cardChanged) {
                    case 0:
                        return;
                    case 1:
                        showMessage(loyaltyCard, active);
                        if (active) {
                            if (loyaltyEnabled) {
                                reactivateAndUnlockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                            }
                        } else {
                            deactivateAndLockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                        }

                        break;
                    case 4:
                        replaceCard(loyaltyCard, newCardNumber, (String) values[0], (String) values[3], active, loyaltyEnabled);
                        break;
                    case 5:
                        replaceCard(loyaltyCard, newCardNumber, (String) values[0], (String) values[3], active, loyaltyEnabled);
                        break;
                }
                break;
            case "account":
                loyaltyCard = (String) values[27];
                newCardNumber = (String) values[5];
                cardChanged = (loyaltyCard.isEmpty()) ? ((cardChangedTest & loyaltyCard.isEmpty()) ? 0 : 4) : ((cardChangedTest) ? 4 : 0);
                switch (activeChanged + enabledChanged + cardChanged) {
                    case 0:
                        return;
                    case 1:
                    case 3:
                        if (!loyaltyCard.isEmpty()) {
                            if (active) {
                                if (loyaltyEnabled) {
                                    reactivateAndUnlockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                                    showMessage(loyaltyCard, active);
                                }
                            } else {
                                if (!loyaltyEnabled) {
                                    deactivateAndLockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                                    showMessage(loyaltyCard, active);
                                }
                            }
                        }
                        break;
                    case 2:
                        if (loyaltyCard.isEmpty()) {
                            createNewCard(UUID.randomUUID().toString(), newCardNumber, (String) values[0], (String) values[3], active);
                        } else if (loyaltyEnabled) {
                            reactivateAndUnlockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                            showMessage(loyaltyCard, loyaltyEnabled);
                        } else {
                            deactivateAndLockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                            showMessage(loyaltyCard, loyaltyEnabled);
                        }
                        break;
                    case 4:
                    case 5:
                        replaceCard(loyaltyCard, newCardNumber, (String) values[0], (String) values[3], active, loyaltyEnabled);
                        break;
                    case 6:
                    case 7:
                        replaceCard(loyaltyCard, newCardNumber, (String) values[0], (String) values[3], active, loyaltyEnabled);
                        Object update[] = new Object[]{loyaltyEnabled, !loyaltyEnabled, newCardNumber};
                        new PreparedSentence(session, "update loyaltycards set active = ?, cardlocked = ? where cardnumber = ? ",
                                new SerializerWriteBasicExt(new Datas[]{Datas.BOOLEAN, Datas.BOOLEAN, Datas.STRING},
                                new int[]{0, 1, 2})).exec(update);
                        break;
                }
        }
    }

    public static void updateLoyalty(Object params, Boolean prevLoyaltyEnabledState, Boolean prevActiveState) throws BasicException {
        Object[] values = (Object[]) params;

        String oldCardNumber;
        String newCardNumber;
        String loyaltyCard;
        String customerId = (String) values[0];
        String customerName = (String) values[2];
        Boolean active = (Boolean) values[17];
        Boolean loyaltyEnabled = (Boolean) values[25];
        Boolean cardChangedTest = (values[1].toString().equals("account")) ? !values[24].toString().equals(values[4].toString())
                : !values[24].toString().equals(values[3].toString());

        int activeChanged = (Boolean.compare(prevActiveState, active) == 0) ? 0 : 1;
        int enabledChanged = (Boolean.compare(loyaltyEnabled, prevLoyaltyEnabledState) == 0) ? 0 : 2;
        int cardChanged;
        switch (values[1].toString()) {
            case "loyalty":
                loyaltyCard = (String) values[3];
                newCardNumber = (String) values[24];

                cardChanged = (loyaltyCard.isEmpty()) ? ((cardChangedTest & loyaltyCard.isEmpty()) ? 0 : 4) : ((cardChangedTest) ? 4 : 0);
                switch (activeChanged + cardChanged) {
                    case 0:
                        return;
                    case 1:
                        showMessage(loyaltyCard, active);
                        if (active) {
                            if (loyaltyEnabled) {
                                reactivateAndUnlockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                            }
                        } else {
                            deactivateAndLockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                        }

                        break;
                    case 4:
                        replaceCard(loyaltyCard, newCardNumber, (String) values[0], (String) values[2], active, loyaltyEnabled);
                        break;
                    case 5:
                        replaceCard(loyaltyCard, newCardNumber, (String) values[0], (String) values[2], active, loyaltyEnabled);
                        break;
                }
                break;
            case "account":
                loyaltyCard = (String) values[24];
                newCardNumber = (String) values[4];
                cardChanged = (loyaltyCard.isEmpty()) ? ((cardChangedTest & loyaltyCard.isEmpty()) ? 0 : 4) : ((cardChangedTest) ? 4 : 0);
                switch (activeChanged + enabledChanged + cardChanged) {
                    case 0:
                        return;
                    case 1:
                    case 3:
                        if (!loyaltyCard.isEmpty()) {
                            if (active) {
                                if (loyaltyEnabled) {
                                    reactivateAndUnlockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                                    showMessage(loyaltyCard, active);
                                }
                            } else {
                                if (!loyaltyEnabled) {
                                    deactivateAndLockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                                    showMessage(loyaltyCard, active);
                                }
                            }
                        }
                        break;
                    case 2:
                        if (loyaltyCard.isEmpty()) {
                            createNewCard(UUID.randomUUID().toString(), newCardNumber, (String) values[0], (String) values[2], active);
                        } else if (loyaltyEnabled) {
                            reactivateAndUnlockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                            showMessage(loyaltyCard, loyaltyEnabled);
                        } else {
                            deactivateAndLockCard(loyaltyCard, customerId, "Status change in Customer panel for '" + customerName + "'");
                            showMessage(loyaltyCard, loyaltyEnabled);
                        }
                        break;
                    case 4:
                    case 5:
                        replaceCard(loyaltyCard, newCardNumber, (String) values[0], (String) values[2], active, loyaltyEnabled);
                        break;
                    case 6:
                    case 7:
                        replaceCard(loyaltyCard, newCardNumber, (String) values[0], (String) values[2], active, loyaltyEnabled);
                        Object update[] = new Object[]{loyaltyEnabled, !loyaltyEnabled, newCardNumber};
                        new PreparedSentence(session, "update loyaltycards set active = ?, cardlocked = ? where cardnumber = ? ",
                                new SerializerWriteBasicExt(new Datas[]{Datas.BOOLEAN, Datas.BOOLEAN, Datas.STRING},
                                new int[]{0, 1, 2})).exec(update);
                        break;
                }
        }
    }

    public final void activateCard(String cardNumber) throws BasicException {
        Object[] values = new Object[]{cardNumber};
        new PreparedSentence(session, "update loyaltycards set active = true where cardnumber = ? ", new SerializerWriteBasicExt(new Datas[]{Datas.STRING}, new int[]{0})).exec(values);
    }

    public final void createCard(String id, String cardNumber, String customerId, Boolean activate) throws BasicException {
        try {
            Object[] values = new Object[]{id, cardNumber, customerId};
            new PreparedSentence(session, "insert into loyaltycards (id, cardnumber, customerid, active, cardlocked) values(?, ?, ?, " + activate + "," + !activate + ") ", new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING}, new int[]{0, 1, 2})).exec(values);
        } catch (BasicException ex) {
            System.out.println("Error creating the card");
        }
    }

    public final void createCardCustomer(String cardId, String customerId, String cardNumber, String customerName, String customerEmail, Boolean marketable) throws BasicException {
        try {
            Object[] values = new Object[]{customerId, cardNumber, customerName, cardNumber, customerEmail, marketable, cardId};
            new PreparedSentence(session, "insert into customers (id, customertype, taxid, name, active, loyaltycardnumber, loyaltyenabled, email, marketable, loyaltycardid) "
                    + " values (?, 'loyalty', ?, ?, true, ?, true, ?, ?, ?)",
                    new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING,
                Datas.STRING, Datas.BOOLEAN, Datas.STRING}, new int[]{0, 1, 2, 3, 4, 5, 6})).exec(values);
        } catch (BasicException ex) {
            System.out.println("Error creating the card customer");
        }
    }

    public final Object[] getLastCardActivity(String id) throws BasicException {
        return (Object[]) sLoyaltyCardLastActivity.find(id);
    }

    public Integer getCardBalance(String cardID) {
        try {
            Integer i = (Integer) sLoyaltyCardBalance.find(cardID);
            return (i > 0) ? i : 0;
        } catch (BasicException ex) {
            return 0;
        }
    }

    public TicketInfo getDetails(String ticketID) {
        try {
            Object[] record = (Object[]) new StaticSentence(session, "select ticketdetails from loyaltytrans where ticketid = ? ", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.SERIALIZABLE})).find(ticketID);
            return record == null ? null : (TicketInfo) record[0];

        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSystem.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


}

//    public void expirePoints(LoyaltyCard loyaltyCard) {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MONTH, -SystemProperty.LOYALTYEXPIRYPERIOD);
//        cal.add(Calendar.DATE, -1);
//
//        if (loyaltyCard.cardBalance > dlLoyalty.getPeriodPoints(loyaltyCard.id, dateFormat.format(cal.getTime()))) {
//            int expiredPoints = loyaltyCard.cardBalance - dlLoyalty.getPeriodPoints(loyaltyCard.id, dateFormat.format(cal.getTime()));
//            addTransaction("loyalty Points expired due to age : " + expiredPoints, -expiredPoints);
//        }
//    }

