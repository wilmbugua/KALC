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


package ke.kalc.pos.vouchers;

/**
 *
 * @author John
 */
public class GiftVoucher extends Voucher{
   
    private Double voucherValue; 
    
    public GiftVoucher(String voucherID, Double voucherValue){
            super(voucherID);
            this.voucherValue = voucherValue;
        
        
    }
}
