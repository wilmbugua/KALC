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


package uk.kalc.pos.sales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import uk.kalc.pos.customers.CustomerInfoExt;
import uk.kalc.pos.inventory.TaxCategoryInfo;
import uk.kalc.pos.ticket.TaxInfo;
import uk.kalc.pos.ticket.TicketInfo;
import uk.kalc.pos.ticket.TicketLineInfo;
import uk.kalc.pos.ticket.TicketTaxInfo;

public class TaxesLogic {

    private List<TaxInfo> taxlist;

    private Map<String, TaxesLogicElement> taxtrees;

    /**
     *
     * @param taxlist
     */
    public TaxesLogic(List<TaxInfo> taxlist) {
        this.taxlist = taxlist;

        taxtrees = new HashMap<>();

        List<TaxInfo> taxlistordered = new ArrayList<>();
        taxlistordered.addAll(taxlist);
        Collections.sort(taxlistordered, new Comparator<TaxInfo>() {
            @Override
            public int compare(TaxInfo o1, TaxInfo o2) {
                if (o1.getApplicationOrder() < o2.getApplicationOrder()) {
                    return -1;
                } else if (o1.getApplicationOrder() == o2.getApplicationOrder()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        // Generate the taxtrees
        HashMap<String, TaxesLogicElement> taxorphans = new HashMap<>();

        for (TaxInfo t : taxlistordered) {

            TaxesLogicElement te = new TaxesLogicElement(t);

            // get the parent
            TaxesLogicElement teparent = taxtrees.get(t.getParentID());
            if (teparent == null) {
                // orphan node
                teparent = taxorphans.get(t.getParentID());
                if (teparent == null) {
                    teparent = new TaxesLogicElement(null);
                    taxorphans.put(t.getParentID(), teparent);
                }
            }

            teparent.getSons().add(te);

            // Does it have orphans ?
            teparent = taxorphans.get(t.getId());
            if (teparent != null) {
                // get all the sons
                te.getSons().addAll(teparent.getSons());
                // remove the orphans
                taxorphans.remove(t.getId());
            }

            // Add it to the tree...
            taxtrees.put(t.getId(), te);
        }
    }

    /**
     *
     * @param ticket
     * @throws TaxesException
     */
//    public void calculateTaxes(TicketInfo ticket) throws TaxesException {
//        List<TicketTaxInfo> tickettaxes = new ArrayList<>();
//
//        for (TicketTaxInfo t : ticket.getTaxLines()) {
//            tickettaxes.add(t);
//        }
//        ticket.setTaxes(tickettaxes);
//    }
    /**
     *
     * @param ticket
     * @throws TaxesException
     */
    public void calculateTaxes(TicketInfo ticket) throws TaxesException {

        List<TicketTaxInfo> tickettaxes = new ArrayList<>();
        TicketTaxInfo[] taxes = ticket.getTaxLines();

        HashMap<String, Double> taxlines = new HashMap();

        for (TicketLineInfo tl : ticket.getLines()) {
            TaxesLogicElement taxesapplied = getTaxesApplied(tl.getTaxInfo());

            Double amount = tl.getLinePrice();
            if (taxesapplied.getSons().isEmpty()) {
                TicketTaxInfo ti = new TicketTaxInfo(taxesapplied.getTax());
                taxlines.putIfAbsent(ti.getTaxInfo().getId(), 0.00);
                taxlines.put(ti.getTaxInfo().getId(), taxlines.get(ti.getTaxInfo().getId()) + amount);
            } else {
                for (TaxesLogicElement te : taxesapplied.getSons()) {
                    TicketTaxInfo ti = new TicketTaxInfo(te.getTax());
                    taxlines.putIfAbsent(ti.getTaxInfo().getId(), 0.00);
                    taxlines.put(ti.getTaxInfo().getId(), (double) taxlines.get(ti.getTaxInfo().getId()) + amount);
                }
            }
        }

        Iterator it = taxlines.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            for (TaxInfo taxInfo : taxlist) {
                if (taxInfo.getId().equals(pair.getKey())) {
                    TicketTaxInfo ttInfo = new TicketTaxInfo(taxInfo);
                    tickettaxes.add(ttInfo);
                    ttInfo.add((double) pair.getValue());
                    if (taxInfo.getParentID() != null) {
                        for (TaxInfo parent : taxlist) {
                            if (parent.getId().equals(taxInfo.getParentID())) {
                                ttInfo.setParentTaxRate(parent.getRate());
                            }
                        }
                    }
                }
            }
            it.remove();
        }
        ticket.setTaxes(tickettaxes);
    }

    private TicketTaxInfo searchTicketTax(List<TicketTaxInfo> l, String id) {
        for (TicketTaxInfo tickettax : l) {
            if (id.equals(tickettax.getTaxInfo().getId())) {
                return tickettax;
            }
        }
        return null;
    }

//
//    private void addTaxInfo() {
//
//    }
    /**
     *
     * @param line
     * @return
     * @throws TaxesException
     */
//    public List<TicketTaxInfo> calculateTaxes(TicketLineInfo line) throws TaxesException {
//
//        TaxesLogicElement taxesapplied = getTaxesApplied(line.getTaxInfo());
//        return calculateLineTaxes(line.getSubValue(), taxesapplied);
//    }
//    private List<TicketTaxInfo> calculateLineTaxes(double base, TaxesLogicElement taxesapplied) {
//
//        List<TicketTaxInfo> linetaxes = new ArrayList<>();
//
//        if (taxesapplied.getSons().isEmpty()) {
//            TicketTaxInfo tickettax = new TicketTaxInfo(taxesapplied.getTax());
//            tickettax.add(base);
//            linetaxes.add(tickettax);
//        } else {
//            double acum = base;
//
//            for (TaxesLogicElement te : taxesapplied.getSons()) {
//
//                List<TicketTaxInfo> sublinetaxes = calculateLineTaxes(
//                        te.getTax().isCascade() ? acum : base,
//                        te);
//                linetaxes.addAll(sublinetaxes);
//                acum += sumTaxes(sublinetaxes);
//            }
//        }
//
//        return linetaxes;
//    }
    private TaxesLogicElement getTaxesApplied(TaxInfo t) throws TaxesException {

        if (t == null) {
            throw new TaxesException(new java.lang.NullPointerException());
        }

        return taxtrees.get(t.getId());
    }

    private double sumTaxes(List<TicketTaxInfo> linetaxes) {

        double taxtotal = 0.0;

        for (TicketTaxInfo tickettax : linetaxes) {
            taxtotal += tickettax.getTax();

        }
        return taxtotal;
    }

    private List<TicketTaxInfo> sumLineTaxes(List<TicketTaxInfo> list1, List<TicketTaxInfo> list2) {

        for (TicketTaxInfo tickettax : list2) {
            TicketTaxInfo i = searchTicketTax(list1, tickettax.getTaxInfo().getId());
            if (i == null) {
                list1.add(tickettax);
            } else {
                i.add(tickettax.getSubTotal());
            }
        }
        return list1;
    }

    /**
     *
     * @param tcid
     * @return
     */
    public double getTaxRate(String tcid) {
        return getTaxRate(tcid, null);
    }

    /**
     *
     * @param tc
     * @return
     */
    public double getTaxRate(TaxCategoryInfo tc) {
        return getTaxRate(tc, null);
    }

    /**
     *
     * @param tc
     * @param customer
     * @return
     */
    public double getTaxRate(TaxCategoryInfo tc, CustomerInfoExt customer) {

        if (tc == null) {
            return 0.0;
        } else {
            return getTaxRate(tc.getID(), customer);
        }
    }

    /**
     *
     * @param tcid
     * @param customer
     * @return
     */
    public double getTaxRate(String tcid, CustomerInfoExt customer) {

        if (tcid == null) {
            return 0.0;
        } else {
            TaxInfo tax = getTaxInfo(tcid, customer);
            if (tax == null) {
                return 0.0;
            } else {
                return tax.getRate();
            }
        }
    }

    /**
     *
     * @param tcid
     * @return
     */
    public TaxInfo getTaxInfo(String tcid) {
        return getTaxInfo(tcid, null);
    }

    /**
     *
     * @param tc
     * @return
     */
    public TaxInfo getTaxInfo(TaxCategoryInfo tc) {
        return getTaxInfo(tc.getID(), null);
    }

    /**
     *
     * @param tc
     * @param customer
     * @return
     */
    public TaxInfo getTaxInfo(TaxCategoryInfo tc, CustomerInfoExt customer) {
        return getTaxInfo(tc.getID(), customer);
    }

    /**
     *
     * @param tcid
     * @param customer
     * @return
     */
    public TaxInfo getTaxInfo(String tcid, CustomerInfoExt customer) {
        TaxInfo defaulttax = null;
        for (TaxInfo tax : taxlist) {
          //  if (tax.getTaxCategoryID() != null) {
                if (tax.getTaxCategoryID() != null && tax.getTaxCategoryID().equals(tcid)) {
                    return tax;
                }

//                if (tax.getTaxCategoryID() != null) {
//                    if (tax.getTaxCategoryID().equals(tcid)) {
//                        return tax;
//                    }
//                }
         //   }

        }
        return defaulttax;
    }
}
