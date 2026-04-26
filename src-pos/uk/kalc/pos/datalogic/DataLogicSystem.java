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
package uk.kalc.pos.datalogic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import uk.kalc.globals.IconFactory;
import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.Datas;
import uk.kalc.data.loader.ImageUtils;
import uk.kalc.data.loader.PreparedSentence;
import uk.kalc.data.loader.QBFBuilder;
import uk.kalc.data.loader.SentenceExec;
import uk.kalc.data.loader.SentenceFind;
import uk.kalc.data.loader.SentenceList;
import uk.kalc.data.loader.SerializerRead;
import uk.kalc.data.loader.SerializerReadBasic;
import uk.kalc.data.loader.SerializerReadBytes;
import uk.kalc.data.loader.SerializerReadInteger;
import uk.kalc.data.loader.SerializerReadString;
import uk.kalc.data.loader.SerializerWriteBasic;
import uk.kalc.data.loader.SerializerWriteString;
import uk.kalc.data.loader.Session;
import uk.kalc.data.loader.StaticSentence;
import uk.kalc.format.Formats;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.AppUser;
import uk.kalc.pos.forms.BeanFactoryDataSingle;
import uk.kalc.pos.panels.HourlySalesInfo;
import uk.kalc.pos.panels.UserSales;
import uk.kalc.pos.ticket.UserInfo;
import uk.kalc.pos.util.ThumbNailBuilder;

public class DataLogicSystem extends BeanFactoryDataSingle {

    public static String dbVersion = "";
    protected Map<String, byte[]> resourcesCache;
    protected Session s;

    protected SentenceFind activeCash;
    protected SentenceExec addOrder;
    protected SentenceFind appVersion;
    protected SentenceExec changePassword;
    protected SentenceExec drawerOpened;
    protected SentenceFind fobExists;
    protected SentenceFind getTicketId;
    protected SentenceFind getTicketType;
    protected SentenceExec insertCash;
    protected SentenceFind locationName;
    protected SentenceFind peopleByCard;
    protected SerializerRead peopleRead;
    protected SentenceList peopleVisible;
    protected SentenceList waiterList;
    protected SentenceFind rolePermissions;
    protected SentenceFind resourceBytes;
    protected SentenceFind sequenceCash;
    protected SentenceFind siteGuid;
    protected SentenceExec updatePlaces;
    protected SentenceFind xSite;

    public DataLogicSystem() {
    }

    @Override
    public void init(Session s) {
        this.s = s;

        final ThumbNailBuilder tnb = new ThumbNailBuilder(32, 32, IconFactory.getIcon("sysadmin.png"));

        activeCash = new StaticSentence(s, "select host, hostsequence, datestart, dateend, nosales from closedcash where money = ? ", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP,
            Datas.TIMESTAMP,
            Datas.INT}));

        addOrder = new StaticSentence(s, "insert into orders (id, orderid, qty, details, attributes, notes, ticketid, displayid, auxiliary, sequence) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.INT,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.INT,
            Datas.INT,
            Datas.INT
        }));

        appVersion = new PreparedSentence(s, "select version from applications where id = ?", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        changePassword = new StaticSentence(s, "update people set apppassword = ? where id = ?", new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}));

        dbVersion = s.DB.getName();

        drawerOpened = new StaticSentence(s, "insert into draweropened (id, name, ticketid) values (?, ?, ?)", new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING}));

        fobExists = new PreparedSentence(s, "select count(*) from foblist where fobnumber = ? ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);

        getTicketId = new PreparedSentence(s, "select ticketid from tickets where id like ? ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);

        getTicketType = new PreparedSentence(s, "select tickettype from tickets where id like ? ", SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE);

        insertCash = new StaticSentence(s, "insert into closedcash(money, host, hostsequence, datestart, dateend) "
                + "values (?, ?, ?, ?, ?)", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP,
            Datas.TIMESTAMP}));

        locationName = new StaticSentence(s, "select name from locations where id = ?", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        peopleByCard = new PreparedSentence(s, "select id, name, apppassword, card, role, image from people where card = ? and visible = " + s.DB.TRUE(), SerializerWriteString.INSTANCE, peopleRead);

        peopleRead = new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new AppUser(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getString(5),
                        new ImageIcon(tnb.getThumbNail(ImageUtils.readImage(dr.getBytes(6)))));
            }
        };

        peopleVisible = new StaticSentence(s, "select id, name, apppassword, card, role, image from people p join siteguid s on p.siteguid = s.guid where s.guid = p.siteguid and visible = true and iswaiter = false  order by name", null, peopleRead);

        waiterList = new StaticSentence(s, "select id, name, apppassword, card, role, image from people p join siteguid s on p.siteguid = s.guid where s.guid = p.siteguid and visible = true and iswaiter = true  order by name", null, peopleRead);

        resourceBytes = new PreparedSentence(s, "select content from resources r join siteguid s on r.siteguid = s.guid  where name = ? and s.guid = r.siteguid ",
                SerializerWriteString.INSTANCE, SerializerReadBytes.INSTANCE);

        rolePermissions = new PreparedSentence(s, "select permissions from roles where id = ? and siteguid= ? ", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}), SerializerReadBytes.INSTANCE);

        sequenceCash = new StaticSentence(s,
                "select max(hostsequence) from closedcash where host = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadInteger.INSTANCE);

        siteGuid = new StaticSentence(s, "select guid from  siteguid ", null, SerializerReadString.INSTANCE);

        updatePlaces = new StaticSentence(s, "update places set x = ?, y = ? where id = ?   ", new SerializerWriteBasic(new Datas[]{
            Datas.INT,
            Datas.INT,
            Datas.STRING
        }));

        xSite = new PreparedSentence(s, "SELECT COUNT(*) FROM XSITELIST ", null, SerializerReadInteger.INSTANCE);

        resetResourcesCache();

    }

    public final SentenceList getActiveWaiters() {
        return new StaticSentence(s, new QBFBuilder("select id, name, apppassword, card, role, image  "
                + "from people where visible = true and iswaiter = true  order by name",
                new String[]{"name"}),
                new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING}), new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                UserInfo u = new UserInfo(dr.getString(1),
                        dr.getString(2));
                return u;
            }
        });
    }

    public final void addOrder(String id, String orderId, Integer qty, String details, String attributes, String notes, String ticketId, Integer displayId, Integer auxiliaryId, Integer sequence) throws BasicException {
        addOrder.exec(id, orderId, qty, details, attributes, notes, ticketId, displayId, auxiliaryId, sequence);
    }

    public final void execChangePassword(Object[] userdata) throws BasicException {
        changePassword.exec(userdata);
    }

    public final void execDrawerOpened(Object[] drawer) throws BasicException {
        drawerOpened.exec(drawer);
    }

    public final void execInsertCash(Object[] cash) throws BasicException {
        insertCash.exec(cash);
    }

    public final Object[] findActiveCash(String sActiveCashIndex) throws BasicException {
        return (Object[]) activeCash.find(sActiveCashIndex);
    }

    public final String findLocationName(String iLocation) throws BasicException {
        return (String) locationName.find(iLocation);
    }

    public String getUserName(String id) throws BasicException {
        return (String) new PreparedSentence(s, "Select name from people where id = ? ",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE).find(id);
    }

    public final AppUser findPeopleByCard(String card) throws BasicException {
        return (AppUser) new PreparedSentence(s, "select "
                + " id,"
                + " name,"
                + " apppassword,"
                + " card,"
                + " role,"
                + " image"
                + " from people"
                + " where card = ? and visible = true  and iswaiter = false",
                SerializerWriteString.INSTANCE,
                AppUser.getSerializerRead()).find(card);
    }

    public final AppUser findPeopleById(String id) throws BasicException {
        return (AppUser) new PreparedSentence(s, "select "
                + " id,"
                + " name,"
                + " apppassword,"
                + " card,"
                + " role,"
                + " image"
                + " from people"
                + " where id = ? and visible = true  and iswaiter = false",
                SerializerWriteString.INSTANCE,
                AppUser.getSerializerRead()).find(id);
    }

    public final AppUser findPeopleByName(String userName) throws BasicException {
        return (AppUser) new PreparedSentence(s, "select "
                + " id,"
                + " name,"
                + " apppassword,"
                + " card,"
                + " role,"
                + " image"
                + " from people"
                + " where name = ? and visible = true and iswaiter = false ",
                SerializerWriteString.INSTANCE,
                AppUser.getSerializerRead()).find(userName);
    }

    public final String findRolePermissions(String sRole, String guid) {
        try {
            return Formats.BYTEA.formatValue(rolePermissions.find(sRole, guid));
        } catch (BasicException e) {
            return null;
        }
    }

    public final String findVersion() throws BasicException {
        return (String) appVersion.find(AppLocal.APP_ID);
    }

    public final String getDBVersion() {
        return dbVersion;
    }

    public final List<HourlySalesInfo> getHourlySales(String salesDay) throws BasicException {
        return new PreparedSentence(s, "select date(r.datenew), hour(r.datenew), sum(t.units * t.soldprice), count(distinct r.id), sum(t.units * t.soldpriceexc) "
                + "from ticketlines AS t "
                + "join receipts AS r ON t.ticket = r.id "
                + "where hour(r.datenew) between 0 and 23 and "
                + "r.money = ? "
                + "group by date(r.datenew), hour(r.datenew) "
                + "order by date(r.datenew), hour(r.datenew)",
                SerializerWriteString.INSTANCE,
                HourlySalesInfo.getSerializerRead()).list(salesDay);
    }

    public final HashSet<String> getNewPermissions(String id) throws BasicException, IOException, ClassNotFoundException, SQLException {
        HashSet set = new HashSet();
        PreparedStatement pstmt = s.getConnection().prepareStatement("select * from roles where id = ? ");
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(rs.getBytes("permissions")));
            ArrayList<Object> list = (ArrayList<Object>) ois.readObject();
            list.forEach(o -> {
                set.add((String) o);
            });
        }
        return set;
    }

    public final Integer getNoSales(String dateString) {
        Integer count;
        try {
            count = (Integer) new StaticSentence(s, "select count(*) from draweropened where ticketid = 'No Sale' and opendate > {fn TIMESTAMP('" + dateString + "')}",
                    null, SerializerReadInteger.INSTANCE).find();
        } catch (BasicException ex) {
            return 0;
        }
        return count;
    }

    public final List<String> getParentCategories(String salesDay) throws BasicException {
        return new StaticSentence(s, "select distinct p.name "
                + " from categories as a "
                + " left join products as b on a.id = b.category "
                + " left join ticketlines as c on b.id = c.product \n"
                + " left join taxes as d on c.taxid = d.id \n"
                + " left join receipts as e on c.ticket = e.id "
                + " join categories as p on p.id = a.parentid "
                + " where e.money = ? "
                + " group by p.name "
                + " order by p.name",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE).list(salesDay);
    }

    public final Integer getReceiptCount(String money) {
        Integer record;
        try {
            record = (Integer) new StaticSentence(s, "Select count(*) from receipts where money = ? ",
                    SerializerWriteString.INSTANCE, SerializerReadInteger.INSTANCE).find(money);
        } catch (BasicException ex) {
            return 0;
        }
        return record;
    }

    private byte[] getResource(String name) {

        byte[] resource;
        resource = resourcesCache.get(name);

        if (resource == null) {
            try {
                resource = (byte[]) resourceBytes.find(name);
                resourcesCache.put(name, resource);
            } catch (BasicException e) {
                resource = null;
            }
        }

        return resource;
    }

    public final byte[] getResourceAsBinary(String sName) {
        return getResource(sName);
    }

    public final BufferedImage getResourceAsImage(String sName) {
        try {
            byte[] img = getResource(sName);
            return img == null ? null : ImageIO.read(new ByteArrayInputStream(img));
        } catch (IOException e) {
            return null;
        }
    }

    public final Properties getResourceAsProperties(String sName) {
        Properties p = new Properties();
        try {
            byte[] img = getResourceAsBinary(sName);
            if (img != null) {
                p.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }
        return p;
    }

    public final String getResourceAsText(String sName) {
        return Formats.BYTEA.formatValue(getResource(sName));
    }

    public final String getResourceAsXML(String sName) {
        return Formats.BYTEA.formatValue(getResource(sName));
    }

    public String getSiteGUID() {
        try {
            return (String) siteGuid.find();
        } catch (BasicException ex) {

        }
        return null;
    }

    public final int getSequenceCash(String host) throws BasicException {
        Integer i = (Integer) sequenceCash.find(host);
        return (i == null) ? 1 : i;
    }

    public final Integer getTicketId(String id) {
        try {
            Integer i = (Integer) getTicketId.find("%" + id + "%");
            return i;
        } catch (BasicException ex) {
            return 0;
        }
    }

    public final Integer getTicketType(String id) {
        try {
            Integer i = (Integer) getTicketType.find("%" + id + "%");
            return i;
        } catch (BasicException ex) {
            return 0;
        }
    }

    public final List<UserSales> getUserSales(String salesDay) throws BasicException {
        return new PreparedSentence(s, "select  "
                + " p.name, "
                + " sum(py.total)"
                + " from "
                + " receipts as r"
                + " join people AS p on p.id=r.person"
                + " join payments AS py on py.receipt=r.id"
                + " where r.money = ? "
                + " group by p.name"
                + " order by p.name",
                SerializerWriteString.INSTANCE,
                UserSales.getSerializerRead()).list(salesDay);
    }

    public final Boolean isFobPresent(String fobNumber) {
        try {
            Integer i = (Integer) fobExists.find(fobNumber);
            return (i > 0) ? true : false;
        } catch (BasicException ex) {
            return false;
        }
    }

    public final Boolean isXSiteAvailable() {
        try {
            Integer i = (Integer) xSite.find();
            return (i > 0);
        } catch (BasicException ex) {
            return false;
        }
    }

    public final List listPeopleVisible() throws BasicException {
        return peopleVisible.list();
    }

    public final List activeWaiters() throws BasicException {
        return waiterList.list();
    }

    public final void resetResourcesCache() {
        resourcesCache = new HashMap<>();
    }

    public final void updatePlaces(int x, int y, String id) throws BasicException {
        updatePlaces.exec(x, y, id);
    }

}
