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
package ke.kalc.pos.panels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.data.loader.SerializerReadBasic;
import ke.kalc.data.loader.SerializerReadClass;
import ke.kalc.data.loader.SerializerReadString;
import ke.kalc.data.loader.SerializerWriteString;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.data.loader.StaticSentence;
import ke.kalc.format.Formats;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.util.StringUtils;

public class PaymentsModel {

    private String m_sHost;
    private int m_iSeq;
    private Date m_dDateStart;
    private Date m_dDateEnd;
    private Date rDate;

    private Integer m_iPayments;
    private static Double m_dPaymentsTotal;
    private Integer cardHandling;
    private static Double cardHandlingTotal;
    private List<PaymentsLine> m_lpayments;

    private Integer m_iCategorySalesRows;
    private Double m_dCategorySalesTotalUnits;
    private Double m_dCategoryNetTotal;
    private Double m_dCategorySalesTotal;
    private List<CategorySalesLine> m_lcategorysales;

    private Integer m_iProductSalesRows;
    private Double m_dProductSalesTotalUnits;
    private Double m_dProductSalesTotal;

    private Integer m_iGiftCardSalesRows;
    private Double m_dGiftCardSalesTotalUnits;
    private Double m_dGiftCardSalesTotal;

    private List<ProductSalesLine> m_lproductsales;
    private List<ProductSalesLine> m_lgiftcardsales;
    private List<RemovedProductLines> m_lremovedlines;
    private List<String> m_lparentCategories;

    private final static String[] PAYMENTHEADERS = {"label.payment", "label.Money"};

    private Integer m_iSales;
    private Double m_dSalesBase;
    private Double m_dGiftCardSalesBase;
    private Double m_dSalesTaxes;
    private Double m_dSalesTaxNet;
    private List<SalesLine> m_lsales;
    private static Double saleTaxes = 0.00;
    private Double cashInDrawer;

    private static String activeCashIndex;
    private static String startDateFormatted;

    private final static String[] SALEHEADERS = {"label.taxname", "label.taxrate", "label.totaltax", "label.totalnet"};

    private PaymentsModel() {

    }

    /**
     *
     * @return
     */
    public static PaymentsModel emptyInstance() {

        PaymentsModel p = new PaymentsModel();

        p.m_iPayments = 0;
        p.m_dPaymentsTotal = 0.0;
        p.cardHandling = 0;
        p.cardHandlingTotal =0.00;
        p.m_lpayments = new ArrayList<>();
        p.m_iCategorySalesRows = 0;
        p.m_dCategorySalesTotalUnits = 0.0;
        p.m_dCategoryNetTotal = 0.0;
        p.m_dCategorySalesTotal = 0.0;
        p.m_lcategorysales = new ArrayList<>();
        p.m_iSales = null;
        p.m_dSalesBase = null;
        p.m_dGiftCardSalesBase = null;
        p.m_dSalesTaxes = null;
        p.m_dSalesTaxNet = null;

        p.m_iProductSalesRows = 0;
        p.m_dProductSalesTotalUnits = 0.0;
        p.m_dProductSalesTotal = 0.0;

        p.m_iGiftCardSalesRows = 0;
        p.m_dGiftCardSalesTotalUnits = 0.0;
        p.m_dGiftCardSalesTotal = 0.0;

        p.m_lproductsales = new ArrayList<>();
        p.m_lgiftcardsales = new ArrayList<>();
        // end

        p.m_lremovedlines = new ArrayList<>();

        p.m_lsales = new ArrayList<>();

        p.m_lparentCategories = new ArrayList<>();

        p.cashInDrawer = 0.0;

        return p;
    }

    /**
     *
     * @param app
     * @return
     * @throws BasicException
     */
    public static PaymentsModel loadInstance(AppView app) throws BasicException {

        SimpleDateFormat ndf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startDateFormatted = ndf.format(app.getActiveCashDateStart());

        activeCashIndex = app.getActiveCashIndex();
        // activeCashIndex = "a65ffe74-9404-4475-a1dd-19b0b2796ede";

        PaymentsModel p = new PaymentsModel();
        p.m_sHost = app.getProperties().getHost();
        p.m_iSeq = app.getActiveCashSequence();
        p.m_dDateStart = app.getActiveCashDateStart();
        p.m_dDateEnd = null;
        return getPaymentsData(p);
    }

    public static PaymentsModel loadInstance(ClosedCashInfo closedData) throws BasicException {
        SimpleDateFormat ndf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startDateFormatted = ndf.format(closedData.getStartDate());

        activeCashIndex = closedData.getMoneyGuid();
        PaymentsModel p = new PaymentsModel();
        p.m_sHost = closedData.getHost();
        p.m_iSeq = closedData.getHostSequence();
        p.m_dDateStart = closedData.getStartDate();

        p.m_dDateEnd = closedData.getEndDate();
        return getPaymentsData(p);

    }

    private static PaymentsModel getPaymentsData(PaymentsModel p) throws BasicException {
        Session session = SessionFactory.getSession();

        p.cashInDrawer = 0.0;
        saleTaxes = 0.0;

        Object[] valcategorysales = (Object[]) new StaticSentence(session, "select "
                + "sum(tl.units) as qty, "
                + "sum(tl.soldpriceexc * tl.units) as net_total, "
                + "sum(tl.soldprice * tl.units) as soldprice "
                + "from ticketlines AS tl, tickets as t, receipts AS r, taxes AS tx  "
                + "where tl.ticket = t.id and t.id = r.id and tl.taxid = tx.id and tl.product is not null and r.money = ? "
                + "group by r.money", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE}))
                .find(activeCashIndex);

        if (valcategorysales == null) {
            p.m_dCategorySalesTotalUnits = 0.0;
            p.m_dCategoryNetTotal = 0.00;
            p.m_dCategorySalesTotal = 0.0;
        } else {
            p.m_dCategorySalesTotalUnits = (Double) valcategorysales[0];
            p.m_dCategoryNetTotal = (Double) valcategorysales[1];
            p.m_dCategorySalesTotal = (Double) valcategorysales[2];
        }

        List categorys = new StaticSentence(session, "select null, a.name, sum(c.units) as qty, "
                + "sum(c.soldprice * c.units) as soldtotal, "
                + "sum(c.soldpriceexc * c.units) as nettotal "
                + "from categories as a "
                + "left join products as b on a.id = b.category "
                + "left join ticketlines as c on b.id = c.product "
                + "left join taxes as d on c.taxid = d.id "
                + "left join receipts as e on c.ticket = e.id "
                + "where a.parentid is null and e.money = ? "
                + "group by a.name", SerializerWriteString.INSTANCE, new SerializerReadClass(PaymentsModel.CategorySalesLine.class))
                .list(activeCashIndex);

        if (categorys == null) {
            p.m_lcategorysales = new ArrayList();
        } else {
            p.m_lcategorysales = categorys;
        }

        List parents = new StaticSentence(session, "select p.name, a.name, sum(c.units) as qty, "
                + "sum(c.soldprice * c.units) as soldtotal, "
                + "sum(c.soldpriceexc * c.units) as nettotal "
                + "from categories as a "
                + "left join products as b on a.id = b.category "
                + "left join ticketlines as c on b.id = c.product "
                + "left join taxes as d on c.taxid = d.id "
                + "left join receipts as e on c.ticket = e.id "
                + "join categories as p on p.id = a.parentid "
                + "where e.money = ? "
                + "group by p.name, a.name", SerializerWriteString.INSTANCE, new SerializerReadClass(PaymentsModel.CategorySalesLine.class))
                .list(activeCashIndex);

        if (categorys == null) {
            p.m_lcategorysales = new ArrayList();
        } else {
            p.m_lcategorysales.addAll(parents);
        }

        List parentCategories = new StaticSentence(session, "select p.name "
                + "from categories as a "
                + "left join products as b on a.id = b.category "
                + "left join ticketlines as c on b.id = c.product "
                + "left join taxes as d on c.taxid = d.id "
                + "left join receipts as e on c.ticket = e.id "
                + "join categories as p on p.id = a.parentid "
                + "where e.money = ? "
                + "group by p.name", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE) //new SerializerReadBasic(new Datas[]{Datas.STRING}))
                .list(activeCashIndex);

        if (parentCategories == null) {
            p.m_lparentCategories = new ArrayList<>();
        } else {
            p.m_lparentCategories = parentCategories;
        }

        // Payments
        Object[] valtickets = (Object[]) new StaticSentence(session, "select count(*), sum(payments.total) "
                + "from payments, receipts "
                + "where payments.receipt = receipts.id and receipts.money = ?", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE}))
                .find(activeCashIndex);

        if (valtickets == null) {
            p.m_iPayments = 0;
            p.m_dPaymentsTotal = 0.0;
        } else {
            p.m_iPayments = (Integer) valtickets[0];
            p.m_dPaymentsTotal = (Double) valtickets[1];
        }

        // Card handling fees
        Object[] cardFees = (Object[]) new StaticSentence(session, "select count(*), sum(t.cardfees) "
                + "from  tickets AS t "
                + "join  receipts as r on t.id = r.id "
                + "where r.money = ?", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE}))
                .find(activeCashIndex);

        if (cardFees == null) {
            p.cardHandling = 0;
            p.cardHandlingTotal = 0.0;
        } else {
            p.cardHandling = (Integer) cardFees[0];
            p.cardHandlingTotal = (Double) cardFees[1];
        }

        //get list of payments
        List l = new StaticSentence(session, "select payments.payment, sum(payments.total), payments.notes "
                + "from payments, receipts "
                + "where payments.receipt = receipts.id and receipts.money = ? "
                + "group by payments.payment, payments.notes", SerializerWriteString.INSTANCE, new SerializerReadClass(PaymentsModel.PaymentsLine.class)) //new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.DOUBLE}))
                .list(activeCashIndex);

        if (l == null) {
            p.m_lpayments = new ArrayList();
        } else {
            p.m_lpayments = l;
        }

        //Taxes elements
        List<SalesLine> asales = new StaticSentence(session,
                "select tc.name, sum(t.amount), sum(t.base), sum(t.base + t.amount),t.rate, "
                + "CONCAT(tc.name,' - ',t.rate * 100,'%') AS label "
                + "from taxlines as t "
                + "join receipts as r on r.id = t.receipt "
                + "join taxes as tc on tc.id = t.taxid "
                + "where r.money =  ? "
                + "group by label",
                SerializerWriteString.INSTANCE,
                new SerializerReadClass(PaymentsModel.SalesLine.class)
        )
                .list(activeCashIndex);

        if (asales == null) {
            p.m_lsales = new ArrayList<>();
        } else {
            p.m_lsales = asales;
        }

        for (SalesLine s : asales) {
            saleTaxes += s.getTaxes();
        }

        // Sales
        Object[] recsales = (Object[]) new StaticSentence(session,
                "select count(distinct receipts.id), COALESCE(sum(ticketlines.units * ticketlines.soldprice), 0.00) as sales "
                + "from receipts, ticketlines where receipts.id = ticketlines.ticket "
                + "and ticketlines.product not in ('giftcard-sale','giftcard-topup') "
                + "and receipts.money = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE}))
                .find(activeCashIndex);
        if (recsales == null) {
            p.m_iSales = null;
            p.m_dSalesBase = null;
        } else {
            p.m_iSales = (Integer) recsales[0];
            if (p.m_dPaymentsTotal != null) {
                p.m_dSalesBase = (SystemProperty.TAXINCLUDED) ? (Double) recsales[1] - saleTaxes : (Double) recsales[1];
            }
        }

        // Taxes
        Object[] rectaxes = (Object[]) new StaticSentence(session,
                "select sum(taxlines.amount), sum(taxlines.base), taxlines.rate "
                + "from receipts, taxlines where receipts.id = taxlines.receipt and receipts.money = ? ",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE}))
                .find(activeCashIndex);
        if (rectaxes == null) {
            p.m_dSalesTaxes = null;
            p.m_dSalesTaxNet = null;

        } else {
            p.m_dSalesTaxes = (Double) rectaxes[0];
            p.m_dSalesTaxNet = (Double) rectaxes[1];
        }

        List removedLines = new StaticSentence(session, "select lineremoved.name, lineremoved.ticketid, lineremoved.productname, sum(lineremoved.units) as total_units  "
                + "from lineremoved "
                + "where lineremoved.removeddate > ? "
                + "group by lineremoved.name, lineremoved.ticketid, lineremoved.productname", SerializerWriteString.INSTANCE,
                new SerializerReadClass(PaymentsModel.RemovedProductLines.class))
                .list(startDateFormatted);

        if (removedLines == null) {
            p.m_lremovedlines = new ArrayList();
        } else {
            p.m_lremovedlines = removedLines;
        }

        // Product Sales
        Object[] valproductsales = (Object[]) new StaticSentence(session, "select count(*), sum(ticketlines.units), "
                + "sum(ticketlines.soldprice * ticketlines.units) "
                + "from ticketlines, tickets, receipts, taxes, products "
                + "where ticketlines.ticket = tickets.id "
                + "and ticketlines.product = products.id "
                + "and products.id not in ('giftcard-sale','giftcard-topup') "
                + "and tickets.id = receipts.id "
                + "and ticketlines.taxid = taxes.id "
                + "and ticketlines.product is not null and receipts.money = ? "
                + "group by receipts.money", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE, Datas.DOUBLE}))
                .find(activeCashIndex);

        if (valproductsales == null) {
            p.m_iProductSalesRows = 0;
            p.m_dProductSalesTotalUnits = 0.0;
            p.m_dProductSalesTotal = 0.0;
        } else {
            p.m_iProductSalesRows = (Integer) valproductsales[0];
            p.m_dProductSalesTotalUnits = (Double) valproductsales[1];
            p.m_dProductSalesTotal = (Double) valproductsales[2];
        }

        List products = new StaticSentence(session, "select products.name, sum(ticketlines.units) as units, "
                + "if (taxincluded.uservalue, round(ticketlines.soldprice/(1 + taxes.rate),2),  ticketlines.soldprice) as net, "
                + "ticketlines.soldprice as soldprice, "
                + "taxes.rate as rate "
                + "from ticketlines, tickets, receipts, products, taxes, "
                + "(select uservalue from systemproperties where constant = 'taxincluded') as taxincluded "
                + "where ticketlines.product = products.id "
                + "and ticketlines.ticket = tickets.id "
                + "and products.id not in ('giftcard-sale','giftcard-topup') "
                + "and tickets.id = receipts.id "
                + "and ticketlines.taxid = taxes.id and receipts.money = ? "
                + "group by products.name, ticketlines.soldprice, taxes.rate", SerializerWriteString.INSTANCE, new SerializerReadClass(PaymentsModel.ProductSalesLine.class)) //new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.DOUBLE}))
                .list(activeCashIndex);

        if (products == null) {
            p.m_lproductsales = new ArrayList();
        } else {
            p.m_lproductsales = products;
        }

        // Gift Cards sales values
        Object[] valgiftcardsales = (Object[]) new StaticSentence(session, "select count(*), sum(ticketlines.units), "
                + "if (taxincluded.uservalue, sum(ticketlines.soldprice), sum((ticketlines.soldprice + (ticketlines.soldprice * taxes.rate )) * ticketlines.units)) as net "
                + "from ticketlines, tickets, receipts, taxes, products, "
                + "(select uservalue from systemproperties where constant = 'taxincluded') as taxincluded "
                + "where ticketlines.ticket = tickets.id "
                + "and ticketlines.product = products.id "
                + "and products.id in ('giftcard-sale','giftcard-topup') "
                + "and tickets.id = receipts.id "
                + "and ticketlines.taxid = taxes.id "
                + "and ticketlines.product is not null and receipts.money = ? "
                + "group by receipts.money", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.INT, Datas.DOUBLE, Datas.DOUBLE}))
                .find(activeCashIndex);

        if (valgiftcardsales == null) {
            p.m_iGiftCardSalesRows = 0;
            p.m_dGiftCardSalesTotalUnits = 0.0;
            p.m_dGiftCardSalesTotal = 0.0;
        } else {
            p.m_iGiftCardSalesRows = (Integer) valgiftcardsales[0];
            p.m_dGiftCardSalesTotalUnits = (Double) valgiftcardsales[1];
            p.m_dGiftCardSalesTotal = (Double) valgiftcardsales[2];
        }

        // Gift cards
        List giftcards = new StaticSentence(session, "select products.name, sum(ticketlines.units) as units, "
                + "if (taxincluded.uservalue, round(ticketlines.soldprice/(1 + taxes.rate),2),  ticketlines.soldprice) as net, "
                + "ticketlines.soldprice as soldprice, "
                + "taxes.rate "
                + "from ticketlines, tickets, receipts, products, taxes, "
                + "(select uservalue from systemproperties where constant = 'taxincluded') as taxincluded "
                + "where ticketlines.product = products.id "
                + "and ticketlines.ticket = tickets.id "
                + "and products.id in ('giftcard-sale','giftcard-topup') "
                + "and tickets.id = receipts.id "
                + "and ticketlines.taxid = taxes.id and receipts.money = ? "
                + "group by products.name, ticketlines.soldprice, taxes.rate", SerializerWriteString.INSTANCE, new SerializerReadClass(PaymentsModel.ProductSalesLine.class)) //new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.DOUBLE}))
                .list(activeCashIndex);

        if (giftcards == null) {
            p.m_lgiftcardsales = new ArrayList();
        } else {
            p.m_lgiftcardsales = giftcards;
        }

        //Get cash only
        p.m_lpayments.stream()
                .filter(pm -> (pm.getType().equalsIgnoreCase("cashin")) || pm.getType().equalsIgnoreCase("cash")
                || pm.getType().equalsIgnoreCase("cashout")
                ).forEachOrdered(pm -> p.cashInDrawer += pm.getValue());
        return p;
    }

    /**
     *
     * @return
     */
    public String getCashInDrawer() {
        return Formats.CURRENCY.formatValue(cashInDrawer);
    }

    /**
     *
     * @return
     */
    public int getPayments() {
        return m_iPayments;
    }

    /**
     *
     * @return
     */
    public double getTotal() {
        return m_dPaymentsTotal;
    }

    /**
     *
     * @return
     */
    public String getHost() {
        return m_sHost;
    }

    /**
     *
     * @return
     */
    public int getSequence() {
        return m_iSeq;
    }

    /**
     *
     * @return
     */
    public Date getDateStart() {
        return m_dDateStart;
    }

    /**
     *
     * @param dValue
     */
    public void setDateEnd(Date dValue) {
        m_dDateEnd = dValue;
    }

    /**
     *
     * @return
     */
    public Date getDateEnd() {
        return m_dDateEnd;
    }

    /**
     *
     * @return
     */
    public String getStartDate() {
        SimpleDateFormat ndf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ndf.format(m_dDateStart);
    }

    /**
     *
     * @return
     */
    public String printHost() {
//        return m_sHost;
        return StringUtils.encodeXML(m_sHost);
    }

    /**
     *
     * @return
     */
    public String printSequence() {
        return Formats.INT.formatValue(m_iSeq);
    }

    /**
     *
     * @return
     */
    public String printDateStart() {
        return Formats.TIMESTAMP.formatValue(m_dDateStart);
    }

    /**
     *
     * @return
     */
    public String printDateEnd() {
        return Formats.TIMESTAMP.formatValue(m_dDateEnd);
    }

    /**
     *
     * @return
     */
    public String printPayments() {
        return Formats.INT.formatValue(m_iPayments);
    }

        /**
     *
     * @return
     */
    public String printCardFees() {
        return Formats.CURRENCY.formatValue(cardHandlingTotal);
    }
    
    
    /**
     *
     * @return
     */
    public String printPaymentsTotal() {
        return Formats.CURRENCY.formatValue(m_dPaymentsTotal);
    }

    /**
     *
     * @return
     */
    public List<PaymentsLine> getPaymentLines() {
        return m_lpayments;
    }

    /**
     *
     * @return
     */
    public int getSales() {
        return m_iSales == null ? 0 : m_iSales;
    }

    /**
     *
     * @return
     */
    public String printSales() {
        return Formats.INT.formatValue(m_iSales);
    }

    /**
     *
     * @return
     */
    public String printSalesBase() {
        return Formats.CURRENCY.formatValue(m_dSalesBase);
    }

    public String printGiftCardSalesBase() {
        return Formats.CURRENCY.formatValue(m_dGiftCardSalesBase);
    }

    /**
     *
     * @return
     */
    public String printSalesTaxes() {
        return Formats.CURRENCY.formatValue(m_dSalesTaxes);
    }

    /**
     *
     * @return
     */
    public String printSalesTotal() {
        return Formats.CURRENCY.formatValue((m_dSalesBase == null || m_dSalesTaxes == null)
                ? null
                : m_dSalesBase + m_dSalesTaxes);
    }

    /**
     *
     * @return
     */
    public List<SalesLine> getSaleLines() {
        return m_lsales;
    }

    /**
     *
     * @return
     */
    public double getCategorySalesTotalUnits() {
        return m_dCategorySalesTotalUnits;
    }

    /**
     *
     * @return
     */
    public String printCategorySalesTotalUnits() {
        return Formats.DOUBLE.formatValue(m_dCategorySalesTotalUnits);
    }

    /**
     *
     * @return
     */
    public double getCategorySalesTotal() {
        return m_dCategorySalesTotal;
    }

    /**
     *
     * @return
     */
    public String printCategoryNetTotal() {
        return Formats.CURRENCY.formatValue(m_dCategoryNetTotal);
    }

    /**
     *
     * @return
     */
    public String printCategorySalesTotal() {
        return Formats.CURRENCY.formatValue(m_dCategorySalesTotal);
    }

    /**
     *
     * @return
     */
    public List<CategorySalesLine> getCategorySalesLines() {
        return m_lcategorysales;
    }
// end

    /**
     *
     * @return
     */
    public double getProductSalesRows() {
        return m_iProductSalesRows;
    }

    public double getGiftCardSalesRows() {
        return m_iGiftCardSalesRows;
    }

    /**
     *
     * @return
     */
    public String printProductSalesRows() {
        return Formats.INT.formatValue(m_iProductSalesRows);
    }

    public String printGiftCardSalesRows() {
        return Formats.INT.formatValue(m_iGiftCardSalesRows);
    }

    /**
     *
     * @return
     */
    public double getProductSalesTotalUnits() {
        return m_dProductSalesTotalUnits;
    }

    public double getGiftCardSalesTotalUnits() {
        return m_dGiftCardSalesTotalUnits;
    }

    /**
     *
     * @return
     */
    public String printProductSalesTotalUnits() {
        return Formats.DOUBLE.formatValue(m_dProductSalesTotalUnits);
    }

    public String printGiftCardSalesTotalUnits() {
        return Formats.DOUBLE.formatValue(m_dGiftCardSalesTotalUnits);
    }

    /**
     *
     * @return
     */
    public double getProductSalesTotal() {
        return m_dProductSalesTotal;
    }

    public double getGiftCardSalesTotal() {
        return m_dGiftCardSalesTotal;
    }

    /**
     *
     * @return
     */
    public String printProductSalesTotal() {
        return Formats.CURRENCY.formatValue(m_dProductSalesTotal);
    }

    public String printGiftCardSalesTotal() {
        return Formats.CURRENCY.formatValue(m_dGiftCardSalesTotal);
    }

    /**
     *
     * @return
     */
    public List<ProductSalesLine> getProductSalesLines() {
        return m_lproductsales;
    }

    public List<ProductSalesLine> getGiftCardSalesLines() {
        return m_lgiftcardsales;
    }

    public List<String> getParents() {
        return m_lparentCategories;
    }

    // end
    /**
     *
     * @return
     */
    public List<RemovedProductLines> getRemovedProductLines() {
        return m_lremovedlines;
    }

    /**
     *
     * @return
     */
    public AbstractTableModel getPaymentsModel() {
        return new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return AppLocal.getIntString(PAYMENTHEADERS[column]);
            }

            @Override
            public int getRowCount() {
                return m_lpayments.size();
            }

            @Override
            public int getColumnCount() {
                return PAYMENTHEADERS.length;
            }

            @Override
            public Object getValueAt(int row, int column) {
                PaymentsLine l = m_lpayments.get(row);
                switch (column) {
                    case 0:
                        return l.getType();
                    case 1:
                        return l.getValue();
                    default:
                        return null;
                }
            }
        };
    }

    /**
     *
     */
    public static class CategorySalesLine implements SerializableRead {

        private String m_CategoryParent;
        private String m_CategoryName;
        private Double m_CategoryUnits;
        private Double m_CategorySum;
        private Double m_CategoryNet;

        /**
         *
         * @param dr
         * @throws BasicException
         */
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_CategoryName = dr.getString(2);
            m_CategoryUnits = dr.getDouble(3);
            m_CategoryNet = dr.getDouble(4);
            m_CategorySum = dr.getDouble(5);
            m_CategoryParent = dr.getString(1);
        }

        public String printParentName() {
            return StringUtils.encodeXML(m_CategoryParent);
        }

        public Boolean isParent() {
            return m_CategoryParent != null;
        }

        public String getParentName() {
            return m_CategoryParent;
        }

        /**
         *
         * @return
         */
        public String printCategoryName() {
            //return m_CategoryName;
            return StringUtils.encodeXML(m_CategoryName);
        }

        /**
         *
         * @return
         */
        public String printCategoryUnits() {
            return Formats.DOUBLE.formatValue(m_CategoryUnits);
        }

        /**
         *
         * @return
         */
        public Double getCategoryUnits() {
            return m_CategoryUnits;
        }

        /**
         *
         * @return
         */
        public String printCategoryNet() {
            return Formats.CURRENCY.formatValue(m_CategoryNet);
        }

        /**
         *
         * @return
         */
        public Double getCategoryNet() {
            return m_CategoryNet;
        }

        /**
         *
         * @return
         */
        public String printCategorySum() {
            return Formats.CURRENCY.formatValue(m_CategorySum);
        }

        /**
         *
         * @return
         */
        public Double getCategorySum() {
            return m_CategorySum;
        }

    }

    /**
     *
     */
    public static class RemovedProductLines implements SerializableRead {

        private String m_Name;
        private String m_TicketId;
        private String m_ProductName;
        private Double m_TotalUnits;

        /**
         *
         * @param dr
         * @throws BasicException
         */
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_Name = dr.getString(1);
            m_TicketId = dr.getString(2);
            m_ProductName = dr.getString(3);
            m_TotalUnits = dr.getDouble(4);
        }

        /**
         *
         * @return
         */
        public String printWorkerName() {
            return StringUtils.encodeXML(m_Name);
        }

        /**
         *
         * @return
         */
        public String printTicketId() {
            return StringUtils.encodeXML(m_TicketId);
        }

        /**
         *
         * @return
         */
        public String printProductName() {
            return StringUtils.encodeXML(m_ProductName);
        }

        /**
         *
         * @return
         */
        public String printTotalUnits() {
            return Formats.DOUBLE.formatValue(m_TotalUnits);
        }

    }

    /**
     *
     */
    public static class ProductSalesLine implements SerializableRead {

        private String m_ProductName;
        private Double m_ProductUnits;
        private Double m_ProductPrice;
        private Double m_ProductPriceInc;
        private Double m_TaxRate;
        private Double m_ProductPriceTax;
        private Double m_ProductPriceNet;

        /**
         *
         * @param dr
         * @throws BasicException
         */
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_ProductName = dr.getString(1);
            m_ProductUnits = dr.getDouble(2);
            m_ProductPrice = dr.getDouble(3);
            m_ProductPriceTax = dr.getDouble(4);
            m_TaxRate = dr.getDouble(5);

            // m_ProductPriceTax = m_ProductPrice + m_ProductPrice * m_TaxRate;
            // m_ProductPriceTax = m_ProductPriceInc;
            m_ProductPriceNet = m_ProductPrice * m_TaxRate;
        }

        /**
         *
         * @return
         */
        public String printProductName() {
            return StringUtils.encodeXML(m_ProductName);
        }

        /**
         *
         * @return
         */
        public String printProductUnits() {
            return Formats.DOUBLE.formatValue(m_ProductUnits);
        }

        /**
         *
         * @return
         */
        public Double getProductUnits() {
            return m_ProductUnits;
        }

        /**
         *
         * @return
         */
        public String printProductPrice() {
            return Formats.CURRENCY.formatValue(m_ProductPrice);
        }

        /**
         *
         * @return
         */
        public Double getProductPrice() {
            return m_ProductPrice;
        }

        /**
         *
         * @return
         */
        public String printTaxRate() {
            return Formats.PERCENT.formatValue(m_TaxRate);
        }

        /**
         *
         * @return
         */
        public Double getTaxRate() {
            return m_TaxRate;
        }

        /**
         *
         * @return
         */
        public String printProductPriceTax() {
            return Formats.CURRENCY.formatValue(m_ProductPriceTax);
        }

        /**
         *
         * @return
         */
        public String printProductSubValue() {
            return Formats.CURRENCY.formatValue(m_ProductPriceTax * m_ProductUnits);
        }

        /**
         *
         * @return
         */
        public String printProductSubValueInc() {
            return Formats.CURRENCY.formatValue(m_ProductPriceTax * m_ProductUnits);
        }

        /**
         * @return
         */
        public String printProductPriceNet() {
            return Formats.CURRENCY.formatValue(m_ProductPrice * m_ProductUnits);
        }

    }
    // end

    /**
     *
     */
    public static class SalesLine implements SerializableRead {

        private String m_SalesTaxName;
        private Double m_SalesTaxes;
        private Double m_SalesTaxNet;
        private Double m_SalesTaxGross;
        private Double m_SalesTaxRate;

        /**
         *
         * @param dr
         * @throws BasicException
         */
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_SalesTaxName = dr.getString(1);
            m_SalesTaxes = dr.getDouble(2);
            m_SalesTaxNet = dr.getDouble(3);
            m_SalesTaxGross = dr.getDouble(4);
            m_SalesTaxRate = dr.getDouble(5);
        }

        /**
         *
         * @return
         */
        public String printTaxName() {
            // return m_SalesTaxName;
            return StringUtils.encodeXML(m_SalesTaxName);
        }

        /**
         *
         * @return
         */
        public String printTaxes() {
            return Formats.CURRENCY.formatValue(m_SalesTaxes);
        }

        /**
         * @return
         */
        public String printTaxNet() {
            return Formats.CURRENCY.formatValue(m_SalesTaxNet);
        }

        /**
         * @return
         */
        public String printTaxGross() {
            return Formats.CURRENCY.formatValue(m_SalesTaxes + m_SalesTaxNet);
        }

        public String printTaxRate() {
            return Formats.PERCENT.formatValue(m_SalesTaxRate);
        }

        /**
         *
         * @return
         */
        public String getTaxName() {
            return m_SalesTaxName;
        }

        /**
         *
         * @return
         */
        public Double getTaxes() {
            return m_SalesTaxes;
        }

        /**
         * @return
         */
        public Double getTaxNet() {
            return m_SalesTaxNet;
        }

        /**
         * @return
         */
        public Double getTaxGross() {
            return m_SalesTaxGross;
        }

        public Double getTaxRate() {
            return m_SalesTaxRate * 100;
        }

    }

    /**
     *
     * @return
     */
    public AbstractTableModel getSalesModel() {
        return new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }

            @Override
            public int getRowCount() {
                return m_lsales.size();
            }

            @Override
            public int getColumnCount() {
                return SALEHEADERS.length;
            }

            @Override
            public Object getValueAt(int row, int column) {
                SalesLine l = m_lsales.get(row);
                switch (column) {
                    case 0:
                        return l.getTaxName();
                    case 1:
                        return l.printTaxRate();

                    case 2:
                        return l.getTaxes();
                    case 3:
                        return l.getTaxNet();
                    default:
                        return null;
                }
            }
        };
    }

    /**
     *
     */
    public static class PaymentsLine implements SerializableRead {

        private String m_PaymentType;
        private Double m_PaymentValue;
        private String s_PaymentReason;

        /**
         *
         * @param dr
         * @throws BasicException
         */
        @Override
        public void readValues(DataRead dr) throws BasicException {
            m_PaymentType = dr.getString(1);
            m_PaymentValue = dr.getDouble(2);
            s_PaymentReason = dr.getString(3) == null ? "" : dr.getString(3);
        }

        /**
         *
         * @return
         */
        public String printType() {
            return AppLocal.getIntString("paymentdescription." + m_PaymentType);
        }

        /**
         *
         * @return
         */
        public String getType() {
            return m_PaymentType;
        }

        /**
         *
         * @return
         */
        public String printValue() {
            return Formats.CURRENCY.formatValue(m_PaymentValue);
        }

        /**
         *
         * @return
         */
        public Double getValue() {
            return m_PaymentValue;
        }

        /**
         *
         * @return
         */
        public String printReason() {
            return StringUtils.encodeXML(s_PaymentReason);
        }

        /**
         *
         * @return
         */
        public String getReason() {
            return s_PaymentReason;
        }
    }
}
