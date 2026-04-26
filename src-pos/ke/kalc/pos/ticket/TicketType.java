/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
*/


package ke.kalc.pos.ticket;

public enum TicketType {
    
    NORMAL(0),
    REFUND(1),
    PAYMENT(2),
    NOSALE(3),
    INVOICE(4);

    int id;
    
    TicketType(int _id)
    {
        this.id = _id;
    }
    
    public int getId()
    {
        return this.id;
    }
    
    public static TicketType get(int _id)
    {
        TicketType ret = NORMAL;
        for (TicketType type : values()) {
            if (type.getId() == _id) {
                ret = type;
                break;
            }
        }
        return ret;
    }
}