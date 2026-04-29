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


package ke.kalc.pos.barcodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.datalogic.DataLogicCustomers;
import ke.kalc.pos.datalogic.DataLogicLoyalty;

/**
 *
 * @author John
 */
public class Barcode {

    public static final int INVALID = -1;
    public static final int NORMAL = 0;
    public static final int CUSTOMER = 1;
    public static final int CUSTOMERWITHLOYALTY = 2;
    public static final int LOYALTYCARD = 3;
    public static final int GIFTCARD = 4;
    public static final int GIFTVOUCHER = 5;
    public static final int PICKUPBARCODE = 6;
    public static final int VOUCHER = 7;
    public static final int ISSN = 8;

    private final StringBuilder barcodeSB;
    private int returnCode = NORMAL;
    private Boolean validCode = true;

    private final DataLogicCustomers dlCustomers;
    private final StringBuilder msrRegex = new StringBuilder("^[");
    private final StringBuilder cardSchemeRegex = new StringBuilder("^");
    private final StringBuilder prefixRegex = new StringBuilder("^[");
    private final String customerRegex = "^[Cc][0-9]*$";
    private final String giftVoucherRegex = "^[0-9][0-9][vV]*$";
    private final String pickupRegex = "^[Pp][a-zA-Z0-9]*$";
    private HashMap<String, Integer> cardTypes = new HashMap();
    private Boolean eof = false;
    private Iterator it;
    private final String barcode;

    public Barcode(String barcode) {
        this.barcode = barcode;
        dlCustomers = new DataLogicCustomers();
        dlCustomers.init(SessionFactory.getSession());

        barcodeSB = new StringBuilder(barcode);

        if (barcode.length() == 0) {
            returnCode = INVALID;
            validCode = false;
            return;
        }

        if ((int) barcodeSB.charAt(barcodeSB.length() - 1) == 255) {
            barcodeSB.deleteCharAt(barcodeSB.length() - 1);
            eof = true;
        }

        msrRegex.append(SystemProperty.MAGNETICLEADING);
        msrRegex.append("][0-9]*[");
        msrRegex.append(SystemProperty.MAGNETICTRAILING);
        msrRegex.append("]$");

        cardSchemeRegex.append("[0-9]*$");

        prefixRegex.append("]*$");

        //Build up card type list
        if (SystemProperty.LOYALTYENABLED) {
            cardTypes.put(SystemProperty.LOYALTYSTART, LOYALTYCARD);
        }
        if (SystemProperty.GIFTCARDSENABLED) {
            cardTypes.put(SystemProperty.GIFTCARDSTART, GIFTCARD);
        }
        it = cardTypes.entrySet().iterator();

        if (Pattern.matches(customerRegex.toString(), barcodeSB)) {
            if (DataLogicLoyalty.customerHasLoyaltyCard(barcodeSB.toString()) & DataLogicLoyalty.isCardAvailable(barcodeSB.toString())) {
                returnCode = CUSTOMERWITHLOYALTY;
            } else {
                returnCode = CUSTOMER;
            }
            validCode = true;
            return;
        }

        if (Pattern.matches(pickupRegex.toString(), barcodeSB)) {
            returnCode = PICKUPBARCODE;
            validCode = true;
            return;
        }

        //Check if this comes from MSR read
        if (Pattern.matches(msrRegex.toString(), barcodeSB)) {
            barcodeSB.deleteCharAt(0);
            barcodeSB.deleteCharAt(barcodeSB.length() - 1);
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (checkGiftCard(barcodeSB, (String) pair.getKey())) {
                    returnCode = (Integer) pair.getValue();
                    validCode = true;
                    return;
                }
                it.remove();
            }
            return;
        }

        //Check for card scheme regex match
        if (Pattern.matches(cardSchemeRegex.toString(), barcodeSB)) {
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (checkGiftCard(barcodeSB, (String) pair.getKey())) {
                    returnCode = (Integer) pair.getValue();
                    validCode = true;
                    return;
                }
                it.remove();
            }
        }

        //Check if number is card scheme
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (checkGiftCard(barcodeSB, (String) pair.getKey())) {
                returnCode = (Integer) pair.getValue();
                validCode = true;
                return;
            }
            it.remove();
        }

        //check if this is alternative customer ID
        if (dlCustomers.customerExists(barcode)) {
            if (DataLogicLoyalty.customerHasLoyaltyCard(barcodeSB.toString()) & DataLogicLoyalty.isCardAvailable(barcodeSB.toString())) {
                returnCode = CUSTOMERWITHLOYALTY;
            } else {
                returnCode = CUSTOMER;
            }
            validCode = true;
            return;
        }

        //check if the barcode is in the loyalty card table, this allows reads from mifare 1k cards etc
        if (DataLogicLoyalty.isCardPresent(barcode)) {
            returnCode = LOYALTYCARD;
            validCode = true;
            return;
        }

        //check if the barcode is newspaper or magazine
        if (barcode.startsWith("977")) {
            returnCode = ISSN;
            validCode = true;
            return;
        }
        validCode = false;
    }

    private Boolean checkGiftCard(StringBuilder stringSB, String startCode) {
        return stringSB.toString().startsWith(startCode);
    }

    public int getBarcodeType() {
        return returnCode;
    }

    public String getBarCode() {
        return barcodeSB.toString();
    }

    public Boolean isValid() {
        return validCode;
    }

    public Boolean isEOF() {
        return eof;
    }

    public Boolean isCustomer() {
        return dlCustomers.customerExists(barcode);
    }

}
