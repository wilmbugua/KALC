/*
**    KALC POS  - KALC POS
**
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
package ke.kalc.pos.ticket;

import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import static java.lang.Math.abs;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import ke.kalc.globals.SystemProperty;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dbmanager.DbUtils;
import ke.kalc.commons.utils.TerminalInfo;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.format.Formats;
import ke.kalc.pos.customers.CustomerInfoExt;
import ke.kalc.pos.datalogic.DataLogicCustomers;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.loyalty.LoyaltyCard;
import ke.kalc.pos.payment.PaymentInfo;
import ke.kalc.pos.payment.PaymentInfoMagcard;
import ke.kalc.pos.sales.CustomerDeliveryInfo;
import ke.kalc.pos.util.StringUtils;

public final class TicketInfo implements SerializableRead, Externalizable, Serializable {

    private static final long serialVersionUID = 2765650092387265178L;
    private SimpleDateFormat m_timeformat = new SimpleDateFormat("hh:mm:ss");
    private SimpleDateFormat m_dateformat = new SimpleDateFormat("dd/MM/yyyy");
    private DataLogicCustomers customerSession = new DataLogicCustomers();

    private Boolean m_sTaxinclusive;
    private Boolean m_sharedticket;
    private Boolean oldTicket;
    private Boolean scAdded = false;
    private CouponSet m_CouponLines;
    private CustomerDeliveryInfo deliveryInfo;
    private CustomerInfoExt m_Customer;
    private Double currentdebt = 0.00;
    private Double multiply;
    private Double ticketdiscount = 0.0;
    private Double ticketDiscountRate = 0.00;
    private Integer ageChecked = -1;
    private Integer burnPoints = 0;
    private Integer eCardBalance = 0;
    private Integer earnPoints = 0;
    private List<PaymentInfo> payments;
    private List<TicketLineInfo> m_aLines;
    private List<TicketTaxInfo> taxes;
    private LoyaltyCard loyaltyCard;
    private Properties attributes;
    private String eCardNumber = null;
    private String layawayCustomerName = "";
    private String m_nosc;
    private String m_sActiveCash;
    private String m_sHost;
    private String m_sId;
    private String noDelivery;
    private String place = null;
    private String siteGuid;
    private String ticketowner;
    private String waiter;
    private String tlvCode = "";
    private TicketType tickettype;
    private UserInfo m_User;
    private UserInfo m_sharedticketUser;
    private final String m_sResponse;
    private int m_iPickupId;
    private int m_iTicketId;
    private java.util.Date m_dDate;
    private double cardfees = 0.00;
    private Boolean taxExempt = false;

    public TicketInfo() {
        customerSession.init(SessionFactory.getSession());
        m_sId = UUID.randomUUID().toString();
        tickettype = TicketType.NORMAL;
        m_iTicketId = 0;
        m_dDate = new Date();
        attributes = new Properties();
        m_User = null;
        m_Customer = null;
        m_sActiveCash = null;
        m_aLines = new ArrayList<>();
        m_CouponLines = new CouponSet();
        payments = new ArrayList<>();
        taxes = null;
        m_sResponse = null;
        oldTicket = false;
        multiply = 0.0;
        m_sharedticket = false;
        m_nosc = "0";
        noDelivery = "0";
        loyaltyCard = null;
        m_sHost = DbUtils.getTerminalName();
        m_sTaxinclusive = SystemProperty.TAXINCLUDED;
        ageChecked = -1;
        scAdded = false;
        ticketdiscount = 0.0;
        tlvCode = "";
        ticketowner = null;
        currentdebt = 0.00;
        deliveryInfo = null;
        ticketDiscountRate = 0.00;
        ticketdiscount = 0.00;
    }

    public String getWaiter() {
        return waiter;
    }

    public String getWaiterName() {
        DataLogicSystem m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(SessionFactory.getSession());

        try {
            return m_dlSystem.getUserName(waiter) == null ? "" : m_dlSystem.getUserName(waiter);
        } catch (BasicException ex) {

        }
        return "";
    }

    public void setWaiter(String waiter) {
        this.waiter = waiter;
    }

    public LoyaltyCard getLoyaltyCard() {
        return loyaltyCard;
    }

    public void setLoyaltyCard(LoyaltyCard loyaltyCard) {
        this.loyaltyCard = loyaltyCard;
    }

    public String getLoyaltyCardNumber() {
        if (loyaltyCard == null) {
            return null;
        }
        return loyaltyCard.getCardNumber();
    }

    public String getHostname() {
        return m_sHost;
    }

    public Integer getAgeChecked() {
        return ageChecked;
    }

    public void setAgeChecked(Integer age) {
        ageChecked = age;
    }

    public Boolean isScAdded() {
        return scAdded;
    }

    public void setScAdded(Boolean scAdded) {
        scAdded = scAdded;
    }

    //Write to shared ticket contents
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(m_sId);
        out.writeInt(tickettype.id);
        out.writeInt(m_iTicketId);
        out.writeObject(m_Customer);
        out.writeObject(m_dDate);
        out.writeObject(m_User);
        out.writeObject(attributes);
        out.writeObject(m_aLines);
        out.writeObject(m_CouponLines);
        out.writeObject(m_nosc);
        out.writeObject(ticketowner);
        out.writeObject(noDelivery);
        out.writeObject(ticketDiscountRate);
        out.writeObject(ticketdiscount);
        out.writeObject(waiter);
        //  out.writeObject(deliveryInfo);
    }

    //Read from shared ticket contents
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_sId = (String) in.readObject();
        tickettype = TicketType.get(in.readInt());
        m_iTicketId = in.readInt();
        m_Customer = (CustomerInfoExt) in.readObject();
        m_dDate = (Date) in.readObject();
        m_User = (UserInfo) in.readObject();
        attributes = (Properties) in.readObject();
        m_aLines = (List<TicketLineInfo>) in.readObject();
        m_CouponLines = (CouponSet) in.readObject();
        m_nosc = (String) in.readObject();
        ticketowner = (String) in.readObject();
        noDelivery = (String) in.readObject();
        ticketDiscountRate = (Double) in.readObject();
        ticketdiscount = (Double) in.readObject();
        waiter = (String) in.readObject();

        m_sActiveCash = null;
        payments = new ArrayList<>();
        taxes = null;
        m_sharedticketUser = m_User;
    }

    public double getCardFees() {
        return cardfees;
    }

    public void setCardFees(Double cfees) {
        cardfees = cfees;
    }

    public String printCardFees() {
        return Formats.CURRENCY.formatValue(cardfees);
    }

    public boolean handlingFeesEnabled() {
        return SystemProperty.HANDLINGFEES;
    }

    public String getTicketOwner() {
        return ticketowner;
    }

    public String getTicketOwnerName() {
        return m_sharedticketUser.getName();
    }

    public void setTicketOwner(String owner) {
        ticketowner = owner;
    }

    public String getLayawayCustomer() {
        return layawayCustomerName;
    }

    public void setLayawayCustomer(String customerName) {
        this.layawayCustomerName = customerName;
    }

    public TicketInfo copyTicket() {
        TicketInfo t = new TicketInfo();
        t.tickettype = tickettype;
        t.m_iTicketId = m_iTicketId;
        t.m_dDate = m_dDate;
        t.m_sActiveCash = m_sActiveCash;
        t.attributes = (Properties) attributes.clone();
        t.m_User = m_User;
        t.m_Customer = m_Customer;
        t.m_aLines = new ArrayList<>();
        m_aLines.forEach((l) -> {
            t.m_aLines.add(l.copyTicketLine());
        });

        t.m_CouponLines = new CouponSet();
        t.m_CouponLines.copyAll(m_CouponLines);

        t.refreshLines();

        t.payments = new LinkedList<>();
        payments.forEach((p) -> {
            t.payments.add(p.copyPayment());
        });
        t.oldTicket = oldTicket;
        t.m_nosc = m_nosc;
        t.noDelivery = noDelivery;
        t.loyaltyCard = loyaltyCard;
        t.m_sHost = m_sHost;
        t.ticketdiscount = ticketdiscount;
        t.ticketowner = ticketowner;

        // taxes are not copied, must be calculated again.
        return t;

    }

    public Double getDiscount() {
        Double discount = null;
        if (m_Customer != null) {
            discount = m_Customer.getCustomerDiscount();
        }
        if (discount == null) {
            discount = 0.0;
        }
        return discount;
    }

    public void clearDiscount() {
        // discount = 0.0;;
    }

    private Double applyDiscount(Double value) {
        if (value != null && value > 0.0) {
            value = value - (value * getDiscount());
        }
        return value;
    }

    public Boolean isTaxExempt() {
        return taxExempt;
    }

    public void setTaxExempt(Boolean value) {
        taxExempt = value;
    }

    public int getTicketState() {
        return ((taxExempt) ? 2 : 0) + ((getTicketDiscountRate() > 0.0) ? 1 : 0);
    }

    public Boolean isTaxInclusive() {
        return m_sTaxinclusive;
    }

    public String getId() {
        return m_sId;
    }

    public TicketType getTicketType() {
        return tickettype;
    }

    public Boolean isRefund() {
        return tickettype.getId() == 1;
    }

    public Boolean isNormal() {
        return tickettype.getId() == 0;
    }

    public Boolean isInvoice() {
        return tickettype.getId() == 4;
    }

    public void setTicketType(final TicketType _tickettype) {
        this.tickettype = _tickettype;
    }

    public int getTicketId() {
        return m_iTicketId;
    }

    public void setTicketId(int iTicketId) {
        m_iTicketId = iTicketId;
    }

    public void setPickupId(int iTicketId) {
        m_iPickupId = iTicketId;
    }

    public int getPickupId() {
        return m_iPickupId;
    }

    public String getName(Object info) {
        StringBuilder name = new StringBuilder();

        if (m_User != null) {
            name.append(m_User.getName());
            name.append(" - ");
        }

        if (m_iPickupId > 0) {
            name.append(" ");
            name.append(m_iPickupId);
        }

        if (info == null) {
            if (m_iTicketId == 0) {
                name.append("(").append(Formats.TIME.formatValue(new Date())).append(")");
                // name.append("(").append(m_timeformat.format(m_dDate.getTime())).append(")");
            } else {
                name.append(Integer.toString(m_iTicketId));
            }
        } else {
            name.append(info.toString());

        }
        if (getCustomerId() != null) {
            name.append(" - ");
            name.append(m_Customer.toString());
            Double discount = getDiscount();
            if (discount > 0.0) {
                name.append(" -");
                name.append(Formats.PERCENT.formatValue(discount));
            }
        }
        return name.toString();
    }

    public String getCustomerName(Object info) {
        StringBuilder name = new StringBuilder();

        if (m_iPickupId > 0) {
            name.append(" ");
            name.append(m_iPickupId);
            name.append("-");
        }

        if (getCustomerId() != null) {
            //  name.append(" - ");
            name.append(m_Customer.toString());
            Double discount = getDiscount();
            if (discount > 0.0) {
                name.append(" -");
                name.append(Formats.PERCENT.formatValue(discount));
            }
        }
        return name.toString();
    }

    public String getName(Object info, String pickupID) {
        StringBuilder name = new StringBuilder();

        name.append(pickupID);
        name.append(" - ");

        if (info == null) {
            if (m_iTicketId == 0) {
                name.append("(").append(Formats.TIME.formatValue(new Date())).append(")");
            } else {
                name.append(Integer.toString(m_iTicketId));
            }
        } else {
            name.append(info.toString());

        }
        if (getCustomerId() != null) {
            name.append(" - ");
            name.append(m_Customer.toString());
            Double discount = getDiscount();
            if (discount > 0.0) {
                name.append(" -");
                name.append(Formats.PERCENT.formatValue(discount));
            }
        }
        return name.toString();
    }

    public String getName() {
        if (layawayCustomerName.equals("")) {
            return getName(null);
        } else {
            return layawayCustomerName;
        }
    }

    public String getTableName(Object info) {
        StringBuilder name = new StringBuilder();
        name.append(info.toString());
        if (getWaiterName().isBlank()) {
            name.append(" - No Waiter assigned.");
        } else {
            name.append(" - Waiter : ").append(getWaiterName());
        }
        return name.toString();
    }

    public String getSharedName() {
        StringBuilder name = new StringBuilder();
        if (getCustomerId() != null) {
            name.append(m_Customer.toString());
            if ((m_Customer.getPhone() != null) && (!m_Customer.getPhone().equals(""))) {
                name.append(" - ").append(m_Customer.getPhone());
            }
            name.append(" - ").append(m_dateformat.format(m_dDate.getTime()));
            name.append("  ").append(m_timeformat.format(m_dDate.getTime()));
        } else {
            if (m_User != null) {
                name.append(m_User.getName());
                name.append(" - ").append(m_dateformat.format(m_dDate.getTime()));
                name.append("  ").append(m_timeformat.format(m_dDate.getTime()));
            }
        }

        if (m_iPickupId > 0) {
            name.append(" ");
            name.append(m_iPickupId);
        }
        return name.toString();
    }

    public String getECardNumber() {
        return (eCardNumber == null) ? "" : eCardNumber;
    }

    public Integer getECardBalance() {
        return eCardBalance;
    }

    public Integer getEarnPoints() {
        return earnPoints;
    }

    public Integer getBurnPoints() {
        return burnPoints;
    }

    public void setECardNumber(String eCardNumber) {
        this.eCardNumber = eCardNumber;
    }

    public void setECardBalance(Integer eCardBalance) {
        this.eCardBalance = eCardBalance;
    }

    public void setEarnPoints(Integer earnPoints) {
        this.earnPoints = earnPoints;
    }

    public void setBurnPoints(Integer burnPoints) {
        this.burnPoints = burnPoints;
    }

    public String getTicketBarcode() {
        return "P" + getId().substring(24);
    }

    public java.util.Date getDate() {
        return m_dDate;
    }

    public String getFormattedDate() {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(m_dDate);
    }

    public void setDate(java.util.Date dDate) {
        m_dDate = dDate;
    }

    public String getNoSC() {
        return m_nosc;
    }

    public void setNoSC(String value) {
        m_nosc = value;
    }

    public String getNoDelivery() {
        return noDelivery;
    }

    public void setNoDelivery(String value) {
        noDelivery = value;
    }

    public UserInfo getUser() {
        return m_User;
    }

    public String getUserName() {
        return m_User.getName();
    }

    public UserInfo getSharedTicketUser() {
        return m_sharedticketUser;
    }

    public void setUser(UserInfo value) {
        m_User = value;
    }

    public Double getCustomerDebt() {
        if (m_Customer == null) {
            return 0.00;
        } else {
            return (m_Customer.getCurrentDebt() == null) ? 0.00 : m_Customer.getCurrentDebt();
        }
    }

    public CustomerInfoExt getCustomer() {
        return m_Customer;
    }

    public void setCustomer(CustomerInfoExt value) {
        m_Customer = value;
    }

    public String getCustomerId() {
        if (m_Customer == null) {
            return null;
        } else {
            return m_Customer.getId();
        }
    }

    public Boolean hasCustomer() {
        if (m_Customer == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getCustomerType() {
        if (m_Customer == null) {
            return null;
        } else {
            return m_Customer.getCustomerType();
        }
    }

    public String getTransactionID() {
        return (getPayments().size() > 0)
                ? (getPayments().get(getPayments().size() - 1)).getTransactionID()
                : StringUtils.getCardNumber(); //random transaction ID
    }

    public String getReturnMessage() {
        return ((getPayments().get(getPayments().size() - 1)) instanceof PaymentInfoMagcard)
                ? ((PaymentInfoMagcard) (getPayments().get(getPayments().size() - 1))).getReturnMessage()
                : AppLocal.getIntString("button.ok");
    }

    public void setActiveCash(String value) {
        m_sActiveCash = value;
    }

    public String getActiveCash() {
        return m_sActiveCash;
    }

    public String getProperty(String key) {
        return attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }

    public void setProperty(String key, Double value) {
        attributes.setProperty(key, String.valueOf(value));
    }

    public Double getPropertyDouble(String value) {
        return (attributes.getProperty(value) == null) ? 0.00 : Double.parseDouble(attributes.getProperty(value));
    }

    public Properties getProperties() {
        return attributes;
    }

    public TicketLineInfo getLine(int index) {
        return m_aLines.get(index);
    }

    public void addLine(TicketLineInfo oLine) {
        oLine.setTicket(m_sId, m_aLines.size());
        m_aLines.add(oLine);
    }

    public void addCouponLine(String id, int line, String text) {
        m_CouponLines.add(id, line, text);
    }

    public void removeCouponLine(String id, int line) {
        m_CouponLines.remove(id, line);
    }

    public void removeCoupon(String id) {
        if (id == null) {
            // Remove all coupons
            m_CouponLines.clear();
        } else {
            m_CouponLines.remove(id);
        }
    }

    public int checkAndAddLine(TicketLineInfo oLine, boolean flag) {
        // returns index of product in the ticket list or -1 if new product
        if (m_aLines.size() == 0 || !flag) {
            oLine.setTicket(m_sId, m_aLines.size());
            m_aLines.add(oLine);
            return -1;
        } else {
            int size = m_aLines.size();
            for (int i = 0; i < size; i++) {
                TicketLineInfo temp = m_aLines.get(i);
                if ((temp.getProductID().equals(oLine.getProductID())) && oLine.getProductAttSetId() == null) {
                    m_aLines.get(i).setMultiply(m_aLines.get(i).getMultiply() + oLine.getMultiply());
                    return i;
                }
            }
            oLine.setTicket(m_sId, m_aLines.size());
            m_aLines.add(oLine);
            return -1;
        }
    }

    public void insertLine(int index, TicketLineInfo oLine) {
        m_aLines.add(index, oLine);
        refreshLines();
    }

    public void setLine(int index, TicketLineInfo oLine) {
        oLine.setTicket(m_sId, index);
        m_aLines.set(index, oLine);
    }

    public void removeLine(int index) {
        if (m_aLines.get(index).isDiscounted()) {
            if (SystemProperty.TAXINCLUDED) {
                addTicketDiscount(m_aLines.get(index).getPrice() - m_aLines.get(index).getSellingPrice());
            } else {
                addTicketDiscount(m_aLines.get(index).getPrice() - m_aLines.get(index).getPriceExc());
            }
        }
        m_aLines.remove(index);
        refreshLines();
    }

    private void refreshLines() {
        for (int i = 0; i < m_aLines.size(); i++) {
            getLine(i).setTicket(m_sId, i);
        }
    }

    public int getLinesCount() {
        return m_aLines.size();
    }

    public double getArticlesCount() {
        double dArticles = 0.0;
        TicketLineInfo oLine;

        for (Iterator<TicketLineInfo> i = m_aLines.iterator(); i.hasNext();) {
            oLine = i.next();
            dArticles += oLine.getMultiply();
        }
        return dArticles;
    }

    public double getSubTotal() {
        double sum = 0.0;
        for (TicketLineInfo line : m_aLines) {
            sum += line.getSubValue();
        }
        return sum;
    }

    public double getTax() {
        double sum = 0.0;
        if (hasTaxesCalculated()) {
            for (TicketTaxInfo tax : taxes) {
                sum += tax.getTax();
            }
        } else {
            for (TicketLineInfo line : m_aLines) {
                sum += line.getTax();
            }
        }
        return sum;
    }

    public String printTax() {
        double sum = 0.0;
        if (hasTaxesCalculated()) {
            for (TicketTaxInfo tax : taxes) {
                sum += tax.getTax();
            }
        } else {
            for (TicketLineInfo line : m_aLines) {
                sum += line.getTax();
            }
        }

        Formats.setDecimalPattern("#0.00");
        return Formats.DECIMAL.formatValue(sum);
    }

    public double getTotalPaid() {
        double sum = 0.0;
        for (PaymentInfo p : payments) {
            if (!"debtpaid".equals(p.getName())) {
                sum += p.getTotal();
            }
        }
        return sum;
    }

    public double getChange() {
        double sum = 0.0;
        for (PaymentInfo p : payments) {
            sum += p.getPaid();
        }
        return sum - cardfees - getTicketTotal();
    }

    public double getTotalTendered() {
        double sum = 0.0;
        for (PaymentInfo p : payments) {
            if (!"debtpaid".equals(p.getName())) {
                sum += p.getTendered();
            }
        }
        return sum;
    }

    public String printTotalTendered() {
        double sum = 0.0;
        for (PaymentInfo p : payments) {
            if (!"debtpaid".equals(p.getName())) {
                sum += p.getPaid();
            }
        }
        return Formats.CURRENCY.formatValue(sum);
    }

    public double getTendered() {
        return getTotalTendered();
    }

    public List<String> getCouponLines() {
        return m_CouponLines.getCouponLines();
    }

    public List<TicketLineInfo> getLines() {
        return m_aLines;
    }

    public void setLines(List<TicketLineInfo> l) {
        m_aLines = l;
    }

    public List<PaymentInfo> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentInfo> l) {
        payments = l;
    }

    public void resetPayments() {
        payments = new ArrayList<>();
    }

    public List<TicketTaxInfo> getTaxes() {
        return taxes;
    }

    public boolean hasTaxesCalculated() {
        return taxes != null;
    }

    public void setTaxes(List<TicketTaxInfo> l) {
        taxes = l;
    }

    public void resetTaxes() {
        taxes = null;
    }

    public TicketTaxInfo getTaxLine(TaxInfo tax) {
        for (TicketTaxInfo taxline : taxes) {
            if (tax.getId().equals(taxline.getTaxInfo().getId())) {
                return taxline;
            }
        }
        return new TicketTaxInfo(tax);
    }

    public TicketTaxInfo[] getTaxLines() {
        Map<String, TicketTaxInfo> m = new HashMap<>();

        TicketLineInfo oLine;
        for (Iterator<TicketLineInfo> i = m_aLines.iterator(); i.hasNext();) {
            oLine = i.next();

            TicketTaxInfo t = m.get(oLine.getTaxInfo().getId());
            if (t == null) {
                t = new TicketTaxInfo(oLine.getTaxInfo());
                m.put(t.getTaxInfo().getId(), t);
            }
            if (oLine.isTaxInclusive()) {
                t.add(oLine.getLinePrice());
            } else {
                t.add(oLine.getLinePrice());
            }
        }
        Collection<TicketTaxInfo> avalues = m.values();
        return avalues.toArray(new TicketTaxInfo[avalues.size()]);
    }

    public String printId() {
        String receiptPrefix = (TerminalInfo.getReceiptPrefix());
        if (m_iTicketId > 0) {
            String tmpTicketId = Integer.toString(m_iTicketId);
            if (SystemProperty.RECEIPTSIZE <= tmpTicketId.length()) {
                if (receiptPrefix != null) {
                    tmpTicketId = receiptPrefix + tmpTicketId;
                }
                return tmpTicketId;
            }
            while (tmpTicketId.length() < SystemProperty.RECEIPTSIZE) {
                tmpTicketId = "0" + tmpTicketId;
            }
            if (receiptPrefix != null) {
                tmpTicketId = receiptPrefix + tmpTicketId;
            }
            return tmpTicketId;
        } else {
            return "";
        }
    }

    public String printDate() {
        return Formats.TIMESTAMP.formatValue(m_dDate);
    }

    public String printUser() {
        return m_User == null ? "" : m_User.getName();
    }

    public String getHost() {
        return m_sHost;
    }

    public String printHost() {
        return StringUtils.encodeXML(m_sHost);
    }

    public String printCustomer() {
        return m_Customer == null ? "" : m_Customer.getName();
    }

    public String printArticlesCount() {
        return Formats.DOUBLE.formatValue(getArticlesCount());
    }

    public String printTotalPaid() {
        return Formats.CURRENCY.formatValue(getTotalPaid());
    }

    public String printTendered() {
        return Formats.CURRENCY.formatValue(getTendered());
    }

    public String printOriginalUser() {
        if (getSharedTicketUser() == null) {
            return "";
        }
        return getSharedTicketUser().getName();
    }

    public String printChange() {
        return Formats.CURRENCY.formatValue((double) Math.round(getChange() * 100000) / 100000);
    }

    public String VoucherReturned() {
        return Formats.CURRENCY.formatValue(getTotalPaid() - getTicketTotal());
    }

    public boolean getOldTicket() {
        return (oldTicket);
    }

    public void setSharedTicket(Boolean shared) {
        m_sharedticket = shared;
    }

    public boolean isSharedTicket() {
        return (m_sharedticket);
    }

    public void setdDate(java.util.Date m_date) {
        m_dDate = m_date;
    }

    public java.util.Date getdDate() {
        return m_dDate;
    }

    public void setOldTicket(Boolean otState) {
        oldTicket = otState;
    }

    public Double getTotalsIncluding() {
        Double retailTotal = 0.00;
        for (TicketLineInfo t : m_aLines) {
            retailTotal = retailTotal + t.getLinePrice();
        }
        return retailTotal;
    }

    public Double getTaxAmountIncluding() {
        HashMap<Double, Double> taxAmounts = new HashMap();
        m_aLines.stream().map((t) -> {
            taxAmounts.putIfAbsent(t.getTaxRate(), 0.00);
            return t;
        }).forEachOrdered((t) -> {
            taxAmounts.put(t.getTaxRate(), taxAmounts.get(t.getTaxRate()) + t.getLinePrice());
        });
        Double tax = 0.00;
        Iterator it = taxAmounts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            tax += ((Double) pair.getValue() - ((Double) pair.getValue() / (1 + (Double) pair.getKey())));
            it.remove();
        }
        return tax;
    }

    public Double getTaxAmountExcluding() {
        HashMap<Double, Double> taxAmounts = new HashMap();
        m_aLines.stream().map((t) -> {
            taxAmounts.putIfAbsent(t.getTaxRate(), 0.00);
            return t;
        }).forEachOrdered((t) -> {
            taxAmounts.put(t.getTaxRate(), taxAmounts.get(t.getTaxRate()) + t.getSubValue());
        });
        Double tax = 0.00;

        Iterator it = taxAmounts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            tax += roundHalfEven((Double) pair.getValue() * (Double) pair.getKey());
            it.remove();
        }
        return tax;
    }

    public Double getSubTotalIncluding() {
        return getTotalsIncluding() - getTaxAmountIncluding();
    }

    public Double getSubTotalExcluding() {
        Double subTotal = 0.00;
        subTotal = m_aLines.stream().map((t) -> t.getSubValue()).reduce(subTotal, (accumulator, _item) -> accumulator + _item);
        return subTotal;
    }

    public Double getTicketTotal() {
        return (isTaxInclusive()) ? getTotalsIncluding() : getTotalsExcluding();
    }

    public Double getTotalsExcluding() {
        return getSubTotalExcluding() + getTaxAmountExcluding();
    }

    public String printDecimalTotal() {
        return Formats.CURRENCYNS.formatValue((isTaxInclusive()) ? getTotalsIncluding() : getTotalsExcluding());
    }

    public String printTotal() {
        return Formats.CURRENCY.formatValue((isTaxInclusive()) ? getTotalsIncluding() : getTotalsExcluding());
    }

    public String printRefundDecimalTotal() {
        return Formats.CURRENCYNS.formatValue((isTaxInclusive()) ? abs(getTotalsIncluding()) : abs(getTotalsExcluding()));
    }

    public String printRefundTotal() {
        return Formats.CURRENCY.formatValue((isTaxInclusive()) ? abs(getTotalsIncluding()) : abs(getTotalsExcluding()));
    }

    public String printDecimalSubTotal() {
        return Formats.CURRENCYNS.formatValue((isTaxInclusive()) ? getSubTotalIncluding() : getSubTotalExcluding());
    }

    public String printSubTotal() {
        return Formats.CURRENCY.formatValue((isTaxInclusive()) ? getSubTotalIncluding() : getSubTotalExcluding());
    }

    public String printDecimalTaxAmount() {
        return Formats.CURRENCYNS.formatValue((isTaxInclusive()) ? getTaxAmountIncluding() : getTaxAmountExcluding());
    }

    public String printTaxAmount() {
        return Formats.CURRENCY.formatValue((isTaxInclusive()) ? getTaxAmountIncluding() : getTaxAmountExcluding());
    }

    private Double roundHalfEven(Double value) {
        BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    /*
        Any discount details for the ticket
     */
    public void setTicketDiscountRate(Double rate) {
        this.ticketDiscountRate = rate;
    }

    public Double getTicketDiscountRate() {
        return ticketDiscountRate;
    }

    public void addTicketDiscount(Double discount) {
        ticketdiscount = ticketdiscount + discount;
    }

    public void minusTicketDiscount(Double discount) {
        ticketdiscount = ticketdiscount - discount;
    }

    public void setTicketDiscount(Double discount) {
        this.ticketdiscount = discount;
    }

    public Double getTicketDiscount() {
        return this.ticketdiscount;
    }

    public String printTicketDiscount() {
        return Formats.CURRENCY.formatValue(ticketdiscount);
    }

    /*
        Any delivery details for the ticket
     */
    public void setDeliveryInfo(CustomerDeliveryInfo deliveryinfo) {
        this.deliveryInfo = deliveryinfo;
    }

    public CustomerDeliveryInfo fetchDeliveryInfo() {
        return deliveryInfo;
    }

    /*
        Tlv qrcode produce base64 encrypted data based on 5 elemenst of the ticket
        Company name
        Company tax number
        Invoice date
        Invoice total
        Invoice tax amount
     */
    public String getTlvCode() {
        return this.tlvCode;
    }

    public void setTlvCode(String code) {
        this.tlvCode = code;
    }

    //****************************************************************************************************
    //****************************************************************************************************
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sId = dr.getString(1);
        tickettype = TicketType.get(dr.getInt(2));
        m_iTicketId = dr.getInt(3);
        m_dDate = dr.getTimestamp(4);
        m_sActiveCash = dr.getString(5);
        try {
            byte[] img = dr.getBytes(6);
            if (img != null) {
                attributes.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }

        m_User = new UserInfo(dr.getString(7), dr.getString(8));
        m_Customer = new CustomerInfoExt(dr.getString(9));
        m_aLines = new ArrayList<>();
        m_sHost = dr.getString(10);
        m_sTaxinclusive = dr.getBoolean(11);
        eCardNumber = dr.getString(12);
        eCardBalance = dr.getInt(13);
        earnPoints = dr.getInt(14);
        burnPoints = dr.getInt(15);
        currentdebt = dr.getDouble(16);
        ticketdiscount = dr.getDouble(17);
        cardfees = dr.getDouble(18);
        tlvCode = dr.getString(19);
        ticketowner = dr.getString(20);
        m_iPickupId = dr.getInt(21);
        place = dr.getString(22);
        waiter = dr.getString(23);

        deliveryInfo = customerSession.fetchCustomerDelivery(m_sId);

        try {
            m_CouponLines = (CouponSet) dr.getObject(24);
        } catch (BasicException e) {
            m_CouponLines = new CouponSet();
        }

        payments = new ArrayList<>();
        taxes = null;
        m_sharedticketUser = m_User;
    }

    public void setPlace(String value) {
        place = value;
    }

    public String getPlace() {
        return place;
    }
}
