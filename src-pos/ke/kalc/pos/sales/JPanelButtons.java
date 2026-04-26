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


package ke.kalc.pos.sales;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppUser;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.util.ThumbNailBuilder;

/**
 *
 *
 */
public class JPanelButtons extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger("ke.kalc.pos.sales.JPanelButtons");

    private static SAXParser m_sp = null;

    private Properties props;
    private Map<String, String> events;

    private JPanelTicket panelticket;
    private ThumbNailBuilder tnbmacro;

    /**
     * Creates new form JPanelButtons
     *
     * @param sConfigKey
     * @param panelticket
     */
    public JPanelButtons(String sConfigKey, JPanelTicket panelticket) {
        initComponents();

        // Load categories default thumbnail
       tnbmacro = new ThumbNailBuilder(30, 30, IconFactory.getIcon("run_script.png"));
       

        this.panelticket = panelticket;

        props = new Properties();
        events = new HashMap<>();

        // get the ticket.buttons resource from the resource table
        String sConfigRes = panelticket.getResourceAsXML(sConfigKey);

        if (sConfigRes != null) {
            try {
                if (m_sp == null) {
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    m_sp = spf.newSAXParser();
                }
                m_sp.parse(new InputSource(new StringReader(sConfigRes)), new ConfigurationHandler());

            } catch (ParserConfigurationException ePC) {
                logger.log(Level.WARNING, AppLocal.getIntString("exception.parserconfig"), ePC);
            } catch (SAXException eSAX) {
                logger.log(Level.WARNING, AppLocal.getIntString("exception.xmlfile"), eSAX);
            } catch (IOException eIO) {
                logger.log(Level.WARNING, AppLocal.getIntString("exception.iofile"), eIO);
            }
        }
    }

    /**
     *
     * @param user
     */
    public void setPermissions(AppUser user) {
        for (Component c : this.getComponents()) {
            String sKey = c.getName();
            if (sKey == null || sKey.equals("")) {
                c.setEnabled(true);
            } else {
                c.setEnabled(user.hasPermission(c.getName()));
            }
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     *
     * @param key
     * @param defaultvalue
     * @return
     */
    public String getProperty(String key, String defaultvalue) {
        return props.getProperty(key, defaultvalue);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getEvent(String key) {
        return events.get(key);
    }

    private class ConfigurationHandler extends DefaultHandler {

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (qName) {
                case "button":
                    // The button title text
                    String titlekey = attributes.getValue("titlekey");
                    if (titlekey == null) {
                        titlekey = attributes.getValue("name");
                    }
                    String title = titlekey == null
                            ? attributes.getValue("title")
                            : AppLocal.getIntString(titlekey);
                    // adding the button to the panel                  
                    JButton btn = new JButtonFunc(attributes.getValue("key"),
                            attributes.getValue("image"),
                            title);
                    // The template resource or the code resource
                    final String template = attributes.getValue("template");
                    if (template == null) {
                        final String code = attributes.getValue("code");
                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                panelticket.evalScriptAndRefresh(code);
                            }
                        });
                    } else {
                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                panelticket.printTicket(template);
                            }
                        });
                    }
                    add(btn);
                    break;
                case "event":
                    events.put(attributes.getValue("key"), attributes.getValue("code"));
                    break;
                default:
                    String value = attributes.getValue("value");
                    if (value != null) {
                        props.setProperty(qName, attributes.getValue("value"));
                    }
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }

    private class JButtonFunc extends JButton {

        public JButtonFunc(String sKey, String sImage, String title) {
            setName(sKey);
            setText(title);
            URL imgURL = null;
            if (sImage != null) {
                imgURL = getClass().getResource(sImage);
            }

            if (imgURL == null) { 
                //not found image resource url
                BufferedImage bImage = panelticket.getResourceAsImage(sImage);
                if (bImage != null) {
                    setIcon(new ImageIcon(tnbmacro.getThumbNail(bImage)));
                } else {
                    Image icon = IconFactory.getIcon(sImage, "run_script.png").getImage();
                    setIcon(new ImageIcon(icon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH)));
                }
            } else {
                Image image = new ImageIcon(imgURL).getImage();
                setIcon(new ImageIcon(image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH)));
            }

            setFocusPainted(false);
            setFocusable(false);
            setRequestFocusEnabled(false);
            setPreferredSize(new Dimension(50, 40));

            setMargin(new Insets(8, 14, 8, 14));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setFont(KALCFonts.DEFAULTFONT.deriveFont(14f));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
