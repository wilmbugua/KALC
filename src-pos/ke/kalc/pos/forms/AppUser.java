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
package ke.kalc.pos.forms;

import ke.kalc.pos.datalogic.DataLogicSystem;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.ImageUtils;
import ke.kalc.data.loader.SerializerRead;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.ticket.UserInfo;
import ke.kalc.pos.util.Hashcypher;
import ke.kalc.pos.util.ThumbNailBuilder;

public class AppUser {

    private static final Logger logger = Logger.getLogger("ke.kalc.pos.forms.AppUser");

    private static SAXParser m_sp = null;
    private static HashMap<String, String> m_oldclasses; // This is for backwards compatibility purposes

    private final String m_sId;
    private final String m_sName;
    private final String m_sCard;
    private String m_sPassword;
    private final String m_sRole;
    private final Icon m_Icon;

    private static Set<String> m_apermissions;

    public AppUser(String id, String name, String password, String card, String role, Icon icon) {
        m_sId = id;
        m_sName = name;
        m_sPassword = password;
        m_sCard = card;
        m_sRole = role;
        m_Icon = icon;
        m_apermissions = null;
    }

    /**
     *
     * @return
     */
    public Icon getIcon() {
        return m_Icon;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return m_sId;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return m_sName;
    }

    /**
     *
     * @param sValue
     */
    public void setPassword(String sValue) {
        m_sPassword = sValue;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return m_sPassword;
    }

    /**
     *
     * @return
     */
    public String getRole() {
        return m_sRole;
    }

    /**
     *
     * @return
     */
    public String getCard() {
        return m_sCard;
    }

    /**
     *
     * @return
     */
    public boolean authenticate() {
        return m_sPassword == null || m_sPassword.equals("") || m_sPassword.startsWith("empty:");
    }

    /**
     *
     * @param sPwd
     * @return
     */
    public boolean authenticate(String sPwd) {
        return Hashcypher.authenticate(sPwd, m_sPassword);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static SerializerRead getSerializerRead() {
        final ThumbNailBuilder tnb = new ThumbNailBuilder(32, 32, IconFactory.getIcon("sysadmin.png"));
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                AppUser user = new AppUser(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getString(5),
                        new ImageIcon(tnb.getThumbNail(ImageUtils.readImage(dr.getBytes(6)))));
                return user;
            }
        };
    }

    /**
     *
     * @param dlSystem
     */
    public void fillPermissions(DataLogicSystem dlSystem) {
        m_apermissions = new HashSet<>();
        try {
            m_apermissions = dlSystem.getNewPermissions(m_sRole);
        } catch (BasicException | IOException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AppUser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @param classname
     * @return
     */
    public static boolean hasPermission(String classname) {
        return (m_apermissions == null) ? false : m_apermissions.contains(classname);
    }

    /**
     *
     * @return
     */
    public UserInfo getUserInfo() {
        return new UserInfo(m_sId, m_sName);
    }

    private static String mapNewClass(String classname) {
        String newclass = m_oldclasses.get(classname);
        return newclass == null
                ? classname
                : newclass;
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
            if ("class".equals(qName)) {
                m_apermissions.add(mapNewClass(attributes.getValue("name")));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }

}
