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
package ke.kalc.pos.ticket;

import java.awt.image.BufferedImage;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.loader.ImageUtils;
import ke.kalc.data.loader.SerializerRead;

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
