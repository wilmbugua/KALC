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


package ke.kalc.pos.printer;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ke.kalc.pos.datalogic.DataLogicSystem;

/**
 *
 * @author John
 */
public class IncludeFile {

    private Document doc;
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private NodeList nodes;
    private Element element;
    private HashMap<String, String> replacements = new HashMap();
    private String newText;
    private DataLogicSystem dlSystem;
    private StringBuilder inputBuilder;
    private int startPos;

    public IncludeFile(String inputFile, DataLogicSystem dlSystem) {
        this.dlSystem = dlSystem;
        inputBuilder = new StringBuilder(inputFile.replaceAll("[&]", "~%~"));
    }

    public String processInclude() {
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(inputBuilder.toString())));
            nodes = doc.getElementsByTagName("include");
            for (int i = 0; i < nodes.getLength(); i++) {
                element = (Element) nodes.item(i);
                newText = dlSystem.getResourceAsXML(element.getTextContent().trim());
                if (!newText.equals("")) {
                    replacements.put("<include>" + element.getTextContent() + "</include>", newText.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
                }
            }
            Iterator it = replacements.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                startPos = inputBuilder.toString().indexOf(pair.getKey().toString());
                inputBuilder.insert(startPos + pair.getKey().toString().length(), pair.getValue().toString());
                inputBuilder.delete(startPos, startPos + pair.getKey().toString().length());
                it.remove();
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(IncludeFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inputBuilder.toString().replace("~%~", "&");
    }
}
