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


package ke.kalc.pos.payment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class PaymentInfoList {

    private final LinkedList<PaymentInfo> m_apayment;
    private LinkedList<PaymentInfo> tmp_apayment;

    public PaymentInfoList() {
        m_apayment = new LinkedList<>();
    }

    public double getTotal() {

        double dTotal = 0.0;
        Iterator i = m_apayment.iterator();
        while (i.hasNext()) {
            PaymentInfo p = (PaymentInfo) i.next();
            dTotal += p.getTotal();
        }

        return dTotal;
    }

    public boolean isEmpty() {
        return m_apayment.isEmpty();
    }

    public void add(PaymentInfo p) {
        m_apayment.addLast(p);
    }

    public void removeLast() {
        m_apayment.removeLast();
    }

    public void sortPayments(Double m_dTotal) {
        Set giftcards = new HashSet();
        tmp_apayment = new LinkedList<PaymentInfo>();// m_apayment.clone();

        m_apayment.forEach((p) -> {
            if (p.getName().equals("giftcard")) {
                giftcards.add(p.getECardNumber());
            }
        });

        m_apayment.forEach((p) -> {
            if (p.getName().equals("giftcard")) {
                if (giftcards.contains(p.getECardNumber())) {
                    tmp_apayment.add(p);
                    giftcards.remove(p.getECardNumber());
                } else {
                    tmp_apayment.forEach((a) -> {
                        if (p.getName().equals("giftcard")) {
                            if (p.getECardNumber().equals(a.getECardNumber())){
                                a.addToTotal(p.getTotal());
                                a.addToPaid(p.getPaid());
                            }
                        }
                    });
                }

            } else {
                tmp_apayment.add(p);
            }
        }
        );        
        
        
        
//        tmp_apayment.forEach((p) -> {
//            if (p.getName().equals("giftcard")) {
//                giftcards.add(p.getECardNumber());
//            }
//        });
//
//        tmp_apayment.forEach((p) -> {
//            if (p.getName().equals("giftcard")) {
//                if (giftcards.contains(p.getECardNumber())) {
//                    m_apayment.add(p);
//                    giftcards.remove(p.getECardNumber());
//                } else {
//                    m_apayment.forEach((a) -> {
//                        if (p.getName().equals("giftcard")) {
//                            if (p.getECardNumber().equals(a.getECardNumber())){
//                                a.addToTotal(p.getTotal());
//                                a.addToPaid(p.getPaid());
//                            }
//                        }
//                    });
//                }
//
//            } else {
//                m_apayment.add(p);
//            }
//        }
//        );

//        tmp_apayment = (LinkedList<PaymentInfo>) m_apayment.clone();
        m_apayment.clear();
        
        
        double dPaidOther = 0.0;
        double dPaidLoyalty = 0.0;
        double dPaidCash = 0.0;
        Boolean cash = false;
        Boolean loyalty = false;
        Boolean giftcard = false;
        for (PaymentInfo p : tmp_apayment) {
            if (p.getName().equals("cash")) {
                cash = true;
                dPaidCash = dPaidCash + p.getPaid();
            } else if (p.getName().equals("loyalty")) {
                loyalty = true;
                dPaidLoyalty = dPaidLoyalty + p.getPaid();
            } else {
                m_apayment.add(p);
                dPaidOther = dPaidOther + p.getTotal();
            }
        }

        if (cash) {
            if (dPaidCash < m_dTotal - dPaidOther - dPaidLoyalty) {
                m_apayment.add(new PaymentInfoCash_original(dPaidCash, dPaidCash));
            } else {
                m_apayment.add(new PaymentInfoCash_original(m_dTotal - dPaidOther - dPaidLoyalty, dPaidCash));
            }
        }

        if (loyalty) {
            m_apayment.add(new PaymentInfoLoyalty(dPaidLoyalty, dPaidLoyalty));            //}
        }

    }

    public LinkedList<PaymentInfo> getPayments() {
        return m_apayment;
    }
}
