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
package uk.kalc.pos.ticket;

import java.awt.image.BufferedImage;
import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.IKeyed;
import uk.kalc.data.loader.ImageUtils;
import uk.kalc.data.loader.SerializerRead;

public class CategoryInfo implements IKeyed {

    private static final long serialVersionUID = 8612449444103L;
    private String m_sID;
    private String m_sName;
    private String m_sPath;
    private BufferedImage m_Image;
    private Boolean m_bCatShowName;
    private Boolean m_isAvailable;
    private Integer m_iCatOrder;
    private String buttontext;
    private Integer agerestricted;

    /**
     * Creates new CategoryInfo
     *
     * @param id
     * @param name
     * @param image
     * @param catshowname
     */
    public CategoryInfo(String id, String name, BufferedImage image, Boolean catshowname, Boolean isavailable, String buttontext, Integer catorder, Integer agerestricted) {
        m_sID = id;
        m_sName = name;
        m_Image = image;
        m_bCatShowName = catshowname;
        m_isAvailable = isavailable;
        m_iCatOrder = catorder;
        this.buttontext = buttontext;
        this.agerestricted = agerestricted;
    }

    @Override
    public Object getKey() {
        return m_sID;
    }

    public void setID(String sID) {
        m_sID = sID;
    }

    public String getID() {
        return m_sID;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String sName) {
        m_sName = sName;
    }

    public String getButtonText() {
        if (m_bCatShowName) {
            return buttontext;
        } else {
            return "";
        }
    }

    public void setButtonText(String buttontext) {
        this.buttontext = buttontext;
    }

    public Boolean getCatShowName() {
        return m_bCatShowName;
    }

    public Boolean isAvailable() {
        return m_isAvailable;
    }

    public void setCatShowName(Boolean bcatshowname) {
        m_bCatShowName = bcatshowname;
    }

    public Integer getCatOrder() {
        return m_iCatOrder;
    }

    public void setColour(Integer catorder) {
        m_iCatOrder = catorder;
    }

    public BufferedImage getImage() {
        return m_Image;
    }

    public void setImage(BufferedImage img) {
        m_Image = img;
    }

    @Override
    public String toString() {
        return m_sName;
    }

    public String getPath() {
        return m_sPath;
    }

    public void setPath(String sPath) {
        m_sPath = sPath;
    }

    public void setAgeRestriction(Integer age) {
        agerestricted = age;
    }

    public Integer getAgeRestriction() {
        return agerestricted;
    }

    public static SerializerRead getSerializerRead() {
        return (DataRead dr) -> new CategoryInfo(
                dr.getString(1),
                dr.getString(2),
                ImageUtils.readImage(dr.getBytes(3)),
                dr.getBoolean(4),
                dr.getBoolean(5),
                dr.getString(6),
                dr.getInt(7),
                dr.getInt(8));
    }
}
