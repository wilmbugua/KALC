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


package ke.kalc.pos.printer;

import ke.kalc.pos.sales.JTicketLines;
import java.awt.image.BufferedImage;


public interface DeviceDisplayAdvance {
    
    // has support for product image
    public static final int PRODUCT_IMAGE = 1;
    
    // has support for displaying ticket lines
    public static final int TICKETLINES = 2;
    
    // has support for displaying AD Image
    public static final int AD_IMAGE = 4;
    
    // Used to indicate what the advance display can support
    public boolean hasFeature(int feature);
    
    // Advance support for product image routines
    public boolean setProductImage(BufferedImage img);
    
    // Advance support for list of ticket lines 
    public boolean setTicketLines(JTicketLines ticketlinesPanel);
    
    
}
