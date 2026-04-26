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


package ke.kalc.pos.scale;

import gnu.io.*;
import java.awt.Component;
import java.awt.Dimension;
import java.io.*;
import java.util.TooManyListenersException;
import ke.kalc.pos.forms.AppLocal;
import java.util.logging.Logger;

import java.awt.Font; 
import java.util.logging.Level;
import javax.swing.plaf.FontUIResource; 
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.UIManager;
import ke.kalc.pos.forms.KALCFonts;

/**
 *
 *   
 */
public class ScaleAdam implements Scale, SerialPortEventListener {
    
    private static final Logger logger = Logger.getLogger("ke.kalc.pos.scale.ScaleAdam");

    private CommPortIdentifier m_PortId;
    private NRSerialPort m_CommPort;  
    
    private String m_sPortScale;
    private OutputStream m_out;
    private InputStream m_in;

    private static final int SCALE_READY = 0;
    private static final int SCALE_READING = 1;
    private static final int SCALE_USERPRESSEDOK = 2;
    private static final int SCALE_USERPRESSEDCANCEL = 3;
    private static int SCALE_ERROR = -1;
    
    private String m_WeightBuffer;
    private int m_iStatusScale;
    private Component mParent;
    private Font m_OriginalFont;
    private JDialog m_Dialog;
    
    /** Creates a new instance of ScaleComm
     * @param sPortPrinter */
    public ScaleAdam(String sPortPrinter, Component parent ) {
        m_sPortScale = sPortPrinter;
        m_out = null;
        m_in = null;
        
        m_iStatusScale = SCALE_READY; 
        m_WeightBuffer = "";
        
         mParent = parent;
    }
    
    private void showDialog() {

        // Get details of the original font before we change it otherwise all dialogboxes will use new settings
        JOptionPane pane = new JOptionPane();
        Font originalFont=pane.getFont();

        UIManager.put("OptionPane.buttonFont", new FontUIResource(KALCFonts.DEFAULTFONT.deriveFont(22f)));
        String message =  AppLocal.getIntString("scale.weighitem");
        JLabel FontText = new JLabel(message);
        FontText.setFont (KALCFonts.DEFAULTFONTBOLD.deriveFont(38f) );

        JOptionPane newpane = new JOptionPane( FontText, JOptionPane.PLAIN_MESSAGE, JOptionPane.CANCEL_OPTION, null, new Object[]{"Cancel"} );
        newpane.setPreferredSize( new Dimension(450,150));
        m_Dialog = newpane.createDialog("Use Scales");
        
        m_Dialog.setVisible( true );
        
        // Return to default settings
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font(originalFont.getName(),originalFont.getStyle(),originalFont.getSize())));

        if( m_iStatusScale ==  SCALE_READING ) {
            // User must have pressed cancel
            changeStatus( SCALE_USERPRESSEDCANCEL );
        }

    }
    
    private void backgroundReadInput( final int nTimeout ) {
        Thread readThread = new Thread() {
            public void run() {
                readInput(nTimeout);
            }
        };
        readThread.start();
    } 
        
    private void readInput( int nTimeout ) {

        while( --nTimeout > 0 && m_iStatusScale ==  SCALE_READING ) {
            try {
                synchronized (this) {
                    wait(1000);
                }
            } catch (InterruptedException ex) {
               changeStatus( SCALE_ERROR );
            }
        }

        if( m_iStatusScale ==  SCALE_READING ) {
            // must have timed out
            changeStatus( SCALE_ERROR );
        }
            
    } 

    private void changeStatus( int status ) {
    
        m_iStatusScale = status;
        
        if( status != SCALE_READING ) {
            if( m_Dialog != null )
                m_Dialog.setVisible(false);
        }
    }
    
    /**
     *
     * @return
     */
    @Override
    public Double readWeight() {
        
        synchronized(this) {
            m_iStatusScale =  SCALE_READING;
            m_WeightBuffer = "";
            
            try {
                if (m_out == null) {
                    m_CommPort = new NRSerialPort(m_sPortScale, 4800); 
                    m_CommPort.connect();
                    m_CommPort.addEventListener(this);
                    m_CommPort.notifyOnDataAvailable(true);

                    m_out = m_CommPort.getOutputStream();  
                    m_in = m_CommPort.getInputStream();
                }
            } catch ( TooManyListenersException e ) {
                logger.log(Level.SEVERE, "Port exception", e );
                changeStatus( SCALE_ERROR );
            } 
            
            backgroundReadInput( 60 );
        }
        
        showDialog();
             
        synchronized(this) {
            try {
                if (m_out != null)
                    m_out.close();
                if (m_in != null)
                    m_in.close();
                if (m_CommPort != null) {
                    m_CommPort.removeEventListener();
                    m_CommPort.disconnect();
                }
            } catch ( IOException e ) {
            }
            m_out = null;
            m_in = null;
            m_CommPort = null;
            m_PortId = null;

            if( m_Dialog != null )
                m_Dialog.setVisible(false);
            m_Dialog = null;
            
            if (m_iStatusScale == SCALE_READY && m_WeightBuffer != null && m_WeightBuffer.isEmpty() == false ) {
                
                logger.log(Level.INFO, "Scale ready", m_WeightBuffer );
                
                double dWeight = Double.parseDouble( m_WeightBuffer );
                
                return dWeight;
            } else {

                logger.log(Level.WARNING, "Scale no data", m_WeightBuffer );
                
                // Timed out looking for weight or error
                return null;
            }
        }
    }

    private void write(byte[] data) {
        synchronized (this) {
            try {  
                m_out.write(data);
            } catch ( IOException e) {
                assert( false );
            }        
        }
    }
    
    /**
     *
     * @param e
     */
    @Override
    public void serialEvent(SerialPortEvent e) {

	// Determine type of event.
	switch (e.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
                
            case SerialPortEvent.DATA_AVAILABLE:
                synchronized (this) {

                    try {
                        while (m_in.available() > 0) {
                            int b = m_in.read();
  
                            logger.log(Level.WARNING, "Scale sent", Character.toString ((char) b) );

                            if (b == 0x000D) { // CR ASCII
                                // End of Line
                                synchronized (this) {
                                    changeStatus( SCALE_READY );
                                    notifyAll();
                                }
                            } else {
                                if( b == 0x2e || (b >= 0x30  && b <= 0x39 ) ) {  // Ascii for period or 0-9 
                                    m_WeightBuffer = m_WeightBuffer + Character.toString ((char) b);
                                }
                            }
                        }
                    } catch (IOException eIO) {
                        logger.log(Level.SEVERE, "Scale io error", eIO );
                        changeStatus( SCALE_ERROR );
                    }
                }
                break;
        }
    }
}
