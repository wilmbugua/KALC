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

import java.util.Date;
import java.util.List;
import java.util.UUID;
import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.Datas;
import uk.kalc.data.loader.PreparedSentence;
import uk.kalc.data.loader.QBFBuilder;
import uk.kalc.data.loader.SentenceExec;
import uk.kalc.data.loader.SentenceFind;
import uk.kalc.data.loader.SentenceList;
import uk.kalc.data.loader.SerializerRead;
import uk.kalc.data.loader.SerializerReadDate;
import uk.kalc.data.loader.SerializerReadString;
import uk.kalc.data.loader.SerializerWriteBasic;
import uk.kalc.data.loader.SerializerWriteString;
import uk.kalc.data.loader.Session;
import uk.kalc.data.loader.StaticSentence;
import uk.kalc.data.loader.TableDefinition;
import uk.kalc.pos.epm.Break;
import uk.kalc.pos.epm.BreaksInfo;
import uk.kalc.pos.epm.EmployeeInfo;
import uk.kalc.pos.epm.EmployeeInfoExt;
import uk.kalc.pos.epm.LeavesInfo;
import uk.kalc.pos.forms.BeanFactoryDataSingle;

public class DataLogicPresenceManagement extends BeanFactoryDataSingle {

    /**
     *
     */
    protected Session s;

    private SentenceExec m_checkin;
    private SentenceExec m_checkout;
    private SentenceFind m_checkdate;

    private SentenceList m_breaksvisible;
    private SentenceExec m_startbreak;
    private SentenceExec m_endbreak;

    private SentenceFind m_isonbreak;
    private SentenceFind m_isonleave;
    private SentenceFind m_shiftid;

    private SentenceFind m_lastcheckin;
    private SentenceFind m_lastcheckout;
    private SentenceFind m_startbreaktime;

    private SentenceFind m_lastbreakid;
    private SentenceFind m_breakname;

    private SerializerRead breakread;
    private TableDefinition tbreaks;
    private TableDefinition tleaves;

    /**
     *
     */
    public DataLogicPresenceManagement() {
    }

    /**
     *
     * @param s
     */
    @Override
    public void init(Session s) {

        this.s = s;
        breakread = new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new Break(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getBoolean(4));
            }
        };

        m_breaksvisible = new StaticSentence(s,
                 "select id, name, notes, visible from breaks where visible = " + s.DB.TRUE() + " and siteguid = ? ",
                 SerializerWriteString.INSTANCE,
                 breakread);

        m_checkin = new PreparedSentence(s,
                 "insert into shifts(id, startshift, pplid) values (?, ?, ?)",
                 new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.TIMESTAMP, Datas.STRING}));

        m_checkout = new StaticSentence(s,
                 "update shifts set endshift = ?, startshift = startshift where endshift is null and pplid = ?",
                 new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.STRING}));

        m_checkdate = new StaticSentence(s,
                 "select count(*) from shifts where endshift is null and pplid = ?",
                 SerializerWriteString.INSTANCE,
                 SerializerReadString.INSTANCE);

        m_startbreak = new PreparedSentence(s,
                 "insert into shift_breaks(id, shiftid, breakid, starttime) values (?, ?, ?, ?)",
                 new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.TIMESTAMP}));

        m_endbreak = new StaticSentence(s,
                 "update shift_breaks set endtime = ?, starttime = starttime where endtime is null and shiftid = ?",
                 new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.STRING}));

        m_isonbreak = new StaticSentence(s,
                 "select count(*) from shift_breaks where endtime is null",
                 SerializerWriteString.INSTANCE,
                 SerializerReadString.INSTANCE);

        m_shiftid = new StaticSentence(s,
                 "select id from shifts where endshift is null and pplid = ?",
                 SerializerWriteString.INSTANCE,
                 SerializerReadString.INSTANCE);

        m_isonleave = new StaticSentence(s,
                 "select count(*) from leaves where startdate < ? and enddate > ? and pplid = ?",
                 new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.STRING}),
                 SerializerReadString.INSTANCE);

        m_lastcheckin = new StaticSentence(s,
                 "select startshift from shifts where endshift is null and pplid = ?",
                 SerializerWriteString.INSTANCE,
                 SerializerReadDate.INSTANCE);

        m_lastcheckout = new StaticSentence(s,
                 "select max(endshift) from shifts where pplid = ?",
                 SerializerWriteString.INSTANCE,
                 SerializerReadDate.INSTANCE);

        m_startbreaktime = new StaticSentence(s,
                 "select starttime from shift_breaks where endtime is null and shiftid = ?",
                 SerializerWriteString.INSTANCE,
                 SerializerReadDate.INSTANCE);

        m_lastbreakid = new StaticSentence(s,
                 "select breakid from shift_breaks where endtime is null and shiftid = ?",
                 SerializerWriteString.INSTANCE,
                 SerializerReadString.INSTANCE);

        m_breakname = new StaticSentence(s,
                 "select name from breaks where id = ?",
                 SerializerWriteString.INSTANCE,
                 SerializerReadString.INSTANCE);
    }

    public String getSiteGUID() {
        try {
            return new StaticSentence(s,
                    "select guid from  siteguid ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find().toString();
        } catch (BasicException e) {

        }
        return null;
    }

    /**
     *
     * @return
     */
    public final SentenceList getBreaksList() {
        return new StaticSentence(s,
                 "select id, name from breaks order by name",
                 null,
                 new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new BreaksInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    /**
     *
     * @return
     */
    public final SentenceList getLeavesList() {
        return new StaticSentence(s,
                 "select id, pplid, name, startdate, enddate, notes from leaves order by name",
                 null,
                 new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new LeavesInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4), dr.getString(5), dr.getString(6));
            }
        });
    }

    /**
     *
     * @return @throws BasicException
     */
    public final List listBreaksVisible(String siteGuid) throws BasicException {
        return m_breaksvisible.list(siteGuid);
    }

    /**
     *
     * @param user
     * @throws BasicException
     */
    public final void CheckIn(String user) throws BasicException {
        Object[] value = new Object[]{UUID.randomUUID().toString(), new Date(), user};
        m_checkin.exec(value);
    }

    /**
     *
     * @param user
     * @throws BasicException
     */
    public final void CheckOut(String user) throws BasicException {
        Object[] value = new Object[]{new Date(), user};
        m_checkout.exec(value);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsCheckedIn(String user) throws BasicException {
        String Data = (String) m_checkdate.find(user);
        // "0" rows shows user is not checked in
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param UserID
     * @param BreakID
     * @throws BasicException
     */
    public final void StartBreak(String UserID, String BreakID) throws BasicException {
        String ShiftID = GetShiftID(UserID);
        Object[] value = new Object[]{UUID.randomUUID().toString(), ShiftID, BreakID, new Date()};
        m_startbreak.exec(value);
    }

    /**
     *
     * @param UserID
     * @throws BasicException
     */
    public final void EndBreak(String UserID) throws BasicException {
        String ShiftID = GetShiftID(UserID);
        Object[] value = new Object[]{new Date(), ShiftID};
        m_endbreak.exec(value);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsOnBreak(String user) throws BasicException {
        String ShiftID = GetShiftID(user);
        String Data = (String) m_isonbreak.find(ShiftID);
        // "0" rows shows user is not on break
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final String GetShiftID(String user) throws BasicException {
        return (String) m_shiftid.find(user);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Date GetLastCheckIn(String user) throws BasicException {
        return (Date) m_lastcheckin.find(user);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Date GetLastCheckOut(String user) throws BasicException {
        return (Date) m_lastcheckout.find(user);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final Date GetStartBreakTime(String ShiftID) throws BasicException {
        return (Date) m_startbreaktime.find(ShiftID);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final String GetLastBreakID(String ShiftID) throws BasicException {
        return (String) m_lastbreakid.find(ShiftID);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final String GetLastBreakName(String ShiftID) throws BasicException {
        String BreakID = GetLastBreakID(ShiftID);
        return (String) m_breakname.find(BreakID);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Object[] GetLastBreak(String user) throws BasicException {
        String ShiftID = GetShiftID(user);
        Date StartBreakTime = GetStartBreakTime(ShiftID);
        String BreakName = GetLastBreakName(ShiftID);
        return new Object[]{BreakName, StartBreakTime};
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsOnLeave(String user) throws BasicException {
        Object[] value = new Object[]{new Date(), new Date(), user};
        String Data = (String) m_isonleave.find(value);
        // "0" rows shows user is not on leave
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    // EmployeeList list
    // Changed ='4' to !='0' --it lists all the users except admin who doesn´t clock in
    /**
     *
     * @return
     */
    public SentenceList getEmployeeList(String siteGuid) {
        return new StaticSentence(s,
                 new QBFBuilder("select id, name from people where siteguid = '" + siteGuid + "' And role != '0' and visible = " + s.DB.TRUE() + " and ?(QBF_FILTER) order by name", new String[]{"NAME"}),
                 new SerializerWriteBasic(new Datas[]{Datas.OBJECT, Datas.STRING}),
                 new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                EmployeeInfo c = new EmployeeInfo(dr.getString(1));
                c.setName(dr.getString(2));
                return c;
            }
        });
    }

    /**
     *
     * @param user
     * @throws BasicException
     */
    public void BlockEmployee(String user) throws BasicException {
        boolean isOnBreak = IsOnBreak(user);
        if (isOnBreak) {
            EndBreak(user);
        }
        CheckOut(user);
    }

    TableDefinition getTableBreaks() {
        return tbreaks;
    }

    TableDefinition getTableLeaves() {
        return tleaves;
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public EmployeeInfoExt loadEmployeeExt(String id) throws BasicException {
        return (EmployeeInfoExt) new PreparedSentence(s,
                 "select id, name from people where id = ?",
                 SerializerWriteString.INSTANCE,
                 new EmployeeExtRead()).find(id);
    }

    /**
     *
     */
    protected static class EmployeeExtRead implements SerializerRead {

        /**
         *
         * @param dr
         * @return
         * @throws BasicException
         */
        @Override
        public Object readValues(DataRead dr) throws BasicException {
            EmployeeInfoExt c = new EmployeeInfoExt(dr.getString(1));
            c.setName(dr.getString(2));
            return c;
        }
    }
}
