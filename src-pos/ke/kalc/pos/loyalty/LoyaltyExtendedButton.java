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


package ke.kalc.pos.loyalty;

import javax.swing.JButton;
import ke.kalc.pos.ticket.ProductInfoExt;

/**
 *
 * @author john.lewis
 */
public class LoyaltyExtendedButton extends JButton {

        private int cardBalance;
        private ProductInfoExt product;

        public LoyaltyExtendedButton(String text) {
            super(text);
        }

         public LoyaltyExtendedButton(String text, int cardBalance) {
            super(text);
            this.cardBalance = cardBalance;
        }
        
        public LoyaltyExtendedButton(String text, int cardBalance, ProductInfoExt product) {
            super(text);
            this.cardBalance = cardBalance;
            this.product = product;
        }

        public int getCardBalance() {
            return cardBalance;
        }

        public void setCardBalance(int cardBalance) {
            this.cardBalance = cardBalance;
        }
    }
