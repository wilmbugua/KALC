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


package uk.kalc.pos.printer;

import uk.kalc.pos.sales.JTicketLines;
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
