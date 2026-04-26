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


package uk.kalc.pos.loyalty;

import uk.kalc.pos.datalogic.DataLogicLoyalty;
import java.awt.Dimension;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.kalc.basic.BasicException;
import uk.kalc.commons.dialogs.JAlertPane;
import uk.kalc.globals.SystemProperty;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.AppUser;
import uk.kalc.pos.printer.TicketParser;
import uk.kalc.pos.sales.JPanelTicket;
import uk.kalc.pos.ticket.TicketInfo;

/**
 *
 * @author John
 */
public class LoyaltyCard {

    protected String id;
    protected String customerId = null;
    protected String cardNumber = "";
    protected Boolean isActive = false;
    protected Integer cardBalance = 0;
    protected Integer ticketBalance = 0;
    protected Integer pointsRedeemed = 0;
    protected static DataLogicLoyalty dlLoyalty = null;
    protected LoyaltyInfoPanel infoPanel;
    protected Integer openingPoints;
    protected Boolean isLocked = false;
    protected Double workingTotal = 0.00;

    protected TicketParser m_TTP;

    public LoyaltyCard(String cardNumber) {
        if (LoyaltyCard.dlLoyalty == null) {
            dlLoyalty = new DataLogicLoyalty();
        }
        this.cardNumber = cardNumber;
        isActive = (isCardActive() && isCardPresent()) ? true : activateCard(false);
        isLocked = (isCardLocked() && isCardPresent());
        id = (isCardActive()) ? dlLoyalty.getCardID(cardNumber) : null;
        cardBalance = (id != null) ? dlLoyalty.getCardBalance(id) : 0;
        openingPoints = cardBalance;
    }

    public LoyaltyCard() {
        if (LoyaltyCard.dlLoyalty == null) {
            dlLoyalty = new DataLogicLoyalty();
        }
    }

    //Get the card locked status
    public final Boolean isCardLocked() {
        return isLocked;
    }

    //get the card active status
    public Boolean isCardActive() {
        return dlLoyalty.isCardActive(cardNumber);
    }

    //get the card number of the current card
    public String getCardNumber() {
        return cardNumber;
    }

    //check if card is present in the database
    public Boolean isCardPresent() {
        return dlLoyalty.isCardPresent(cardNumber);
    }

    public Integer getOpeningPoints() {
        return openingPoints;
    }

    //get the card points balance
    public Integer getCardBalance() {
        return cardBalance;
    }

    //get the last activity record from the trans logs for current card
    public String getLastActivity(String action) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Object[] result;
        try {
            result = dlLoyalty.getLastCardActivity(id);
            switch (action.toLowerCase()) {
                case "date":
                    return dateFormat.format(result[4]);
                case "activity":
                    StringBuilder holdingStr = new StringBuilder((String) result[2]);
                    holdingStr.replace(0, DataLogicLoyalty.getCardNumber(id).length() + 3, "");
                    return holdingStr.toString();
                default:
                    return null;
            }
        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //get the last activity record from the trans logs for specific card
    public String getLastActivity(String cardNumber, String action) {
        String id = dlLoyalty.getCardID(cardNumber);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Object[] result;
        try {
            result = dlLoyalty.getLastCardActivity(id);
            switch (action.toLowerCase()) {
                case "date":
                    return dateFormat.format(result[4]);
                case "activity":
                    return (String) result[2];
                default:
                    return null;
            }
        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void processVoucherCheck(TicketInfo ticket, TicketParser m_TTP) {
    }

    public void processTicketPoints(String cardNumber, TicketInfo ticket) {
    }

    public Integer getRedeemedPoints(String cardNumber, TicketInfo ticket) {
        return 0;
    }

    public Integer getEarnedPoints(String cardNumber, TicketInfo ticket) {
        return 0;
    }

    public void showCardStatus() {
        infoPanel = new LoyaltyInfoPanel();
        infoPanel.showLoyaltyInformation(this);
    }

    public void showCardStatus(JPanelTicket ticket) {
        LoyaltyInfoPanel infoPanel = new LoyaltyInfoPanel();
        infoPanel.showLoyaltyInformation(this, ticket);
    }

    public Integer getTicketBalance() {
        return ticketBalance;
    }

    private Boolean activateCard(Boolean details) {
        if (isCardPresent()) {
            if (JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.loyaltyCardNotActive", cardNumber), 16,
                    new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 5) {

                try {
                    dlLoyalty.activateCard(cardNumber);
                    addTransaction(cardNumber + " : Card activated from sales panel");
                } catch (BasicException ex) {
                    Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
                return true;
            }
        } else {

            if (SystemProperty.REGISTERCUSTOMER && AppUser.hasPermission("access.registercustomer")) {
                Object[] result = JAlertPane.registerLoyaltyCustomer(AppLocal.getIntString("message.loyaltyCardNotAvailable", cardNumber));
                if ((Integer) result[0] == 0) {
                    String cardId = UUID.randomUUID().toString();
                    String customerId = UUID.randomUUID().toString();
                    try {
                        dlLoyalty.createCard(cardId, cardNumber, customerId, true);
                        addTransaction(cardNumber + " : New card and customer added and activated from sales panel");

                        if (!((String) result[1]).isBlank()) {
                            dlLoyalty.createCardCustomer(cardId, customerId, cardNumber, (String) result[1], (String) result[2], (Boolean) result[3]);
                        }
                    } catch (BasicException ex) {
                        Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
                        showAlert(AppLocal.getIntString("dialog.loyaltyRegistrationHeaderFailed", cardNumber), AppLocal.getIntString("dialog.loyaltyRegistrationFailed"));
                        return false;
                    }
                    return true;
                }
                return false;
            }
            if (JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.loyaltyCardNotAvailable", cardNumber), 16,
                    new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 5) {
                try {
                    dlLoyalty.createCard(UUID.randomUUID().toString(), cardNumber, customerId, true);
                    addTransaction(cardNumber + " : New card added and activated from sales panel");
                } catch (BasicException ex) {
                    Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
                    showAlert(AppLocal.getIntString("dialog.loyaltyRegistrationHeaderFailed", cardNumber), AppLocal.getIntString("dialog.loyaltyRegistrationFailed"));
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private void addTransaction(String activity) {
        try {
            dlLoyalty.addLoyaltyTransaction(new Object[]{
                dlLoyalty.getCardID(cardNumber),
                null,
                activity,
                0,
                0,
                null,
                null
            });
        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTransaction(String activity, Integer activityPoints, TicketInfo ticket) {
        id = dlLoyalty.getCardID(cardNumber);
        cardBalance = dlLoyalty.getCardBalance(id);
        try {
            dlLoyalty.addLoyaltyTransaction(new Object[]{
                id,
                dlLoyalty.getLoyaltyCustomerID(cardNumber),
                activity,
                activityPoints,
                getCardBalance() + activityPoints,
                ticket.getId(),
                ticket
            });
        } catch (BasicException ex) {
            Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int showAlert(String headerText, String contextText) {
        return JAlertPane.messageBox(JAlertPane.CONFIRMATION, headerText + "\n\n" + contextText, 16,
                new Dimension(125, 50), JAlertPane.YES_NO_OPTION);

//        return JAlertPane.showAlertDialog(JAlertPane.CONFIRMATION,
//                AppLocal.getIntString("message.loyaltyCardTitle"),
//                headerText,
//                contextText,
//                JAlertPane.YES_NO_OPTION,
//                true);
    }

    public boolean createCard(String id, String cardNumber, String customerId, Boolean activate) throws BasicException {
        int dialogResult = 0;
        this.cardNumber = cardNumber;
        String message = "";
        if (activate) {
            dialogResult = JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.loyaltyCardNotAvailable", cardNumber), 16,
                    new Dimension(125, 50), JAlertPane.YES_NO_OPTION);
            message = "New card created and activated from sales panel";
        } else {
            dialogResult = JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.loyaltyCardNotAvailable", cardNumber), 16,
                    new Dimension(125, 50), JAlertPane.YES_NO_OPTION);
            message = "New card created from sales panel";
        }
        if (dialogResult == 5) {
            try {
                dlLoyalty.createCard(UUID.randomUUID().toString(), cardNumber, customerId, activate);
                addTransaction(cardNumber + " : " + message);
            } catch (BasicException ex) {
                Logger.getLogger(LoyaltyCard.class.getName()).log(Level.SEVERE, null, ex);
                showAlert(AppLocal.getIntString("dialog.loyaltyRegistrationHeaderFailed", cardNumber), AppLocal.getIntString("dialog.loyaltyRegistrationFailed"));
                return false;
            }
            return true;
        }
        return false;
    }

    public TicketInfo getDetails(String ticketID) {
        return dlLoyalty.getDetails(ticketID);
    }

}
