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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ke.kalc.pos.datalogic.DataLogicSystem;

/**
 *
 * @author John
 */
public class IncludeFile {

    private static final Logger logger = Logger.getLogger(IncludeFile.class.getName());
    private static SAXParser m_saxParser = null;
    private DataLogicSystem dlSystem;
    private StringBuilder inputBuilder;
    private HashMap<String, String> replacements = new HashMap<>();

    public IncludeFile(String inputFile, DataLogicSystem dlSystem) {
        this.dlSystem = dlSystem;
        inputBuilder = new StringBuilder(inputFile.replaceAll("[&]", "~%~"));
    }

    public String processInclude() {
        SAXParserFactory factory = null;
        try {
            factory = SAXParserFactory.newInstance();
            // XXE Protection
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            factory.setXIncludeAware(false);
            
            if (m_saxParser == null) {
                m_saxParser = factory.newSAXParser();
            }
            
            IncludeHandler handler = new IncludeHandler();
            m_saxParser.parse(new InputSource(new StringReader(inputBuilder.toString())), handler);
            
            // Apply replacements
            Iterator<Map.Entry<String, String>> it = replacements.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                int startPos = inputBuilder.toString().indexOf(pair.getKey());
                if (startPos != -1) {
                    inputBuilder.insert(startPos + pair.getKey().length(), pair.getValue());
                    inputBuilder.delete(startPos, startPos + pair.getKey().length());
                }
                it.remove();
            }
            
            logger.info("XML includes processed successfully.");
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "Parser configuration error: [Message: {0}] Stack Trace: {1}", new Object[]{e.getMessage(), e});
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "SAX parsing error: [Message: {0}] Stack Trace: {1}", new Object[]{e.getMessage(), e});
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error during XML parsing: [Message: {0}] Stack Trace: {1}", new Object[]{e.getMessage(), e});
        } catch (Exception e) {
            logger.log(Level.SEVERE, "General error occurred during XML parsing: [Message: {0}] Stack Trace: {1}", new Object[]{e.getMessage(), e});
        }
        return inputBuilder.toString().replace("~%~", "&");
    }

    private class IncludeHandler extends DefaultHandler {
        private boolean inInclude = false;
        private StringBuilder includeContent = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("include".equals(qName)) {
                inInclude = true;
                includeContent.setLength(0);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inInclude) {
                includeContent.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("include".equals(qName)) {
                String key = "<include>" + includeContent.toString() + "</include>";
                String newText = dlSystem.getResourceAsXML(includeContent.toString().trim());
                if (newText != null && !newText.isEmpty()) {
                    replacements.put(key, newText.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
                }
                inInclude = false;
            }
        }
    }
}
