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


package ke.kalc.beans;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocaleResources {

    private List<ResourceBundle> m_resources;

    /**
     * Creates a new instance of LocaleResources
     */
    public LocaleResources() {
        m_resources = new LinkedList<>();

    }

    /**
     *
     * @param bundlename
     */

    public void addResource(ResourceBundle resource) {
        m_resources.add(resource);
    }

    public void addBundleName(String bundlename) {
        m_resources.add(ResourceBundle.getBundle(bundlename));
    }

    public void addBundleName(String bundlename, Locale locale) {
        m_resources.add(ResourceBundle.getBundle(bundlename, locale));
    }

    /**
     *
     * @param sKey
     * @return
     */
    public String getString(String sKey) {

        if (sKey == null) {
            return null;
        } else {
            for (ResourceBundle r : m_resources) {
                try {
                    return r.getString(sKey);
                } catch (MissingResourceException e) {
                    // Next
                }
            }
            return "** " + sKey + " **";
        }
    }

    /**
     *
     * @param sKey
     * @param sValues
     * @return
     */
    public String getString(String sKey, Object... sValues) {

        if (sKey == null) {
            return null;
        } else {
            for (ResourceBundle r : m_resources) {
                try {
                    return MessageFormat.format(r.getString(sKey), sValues);
                } catch (MissingResourceException e) {
                    // Next
                }
            }

            // MissingResourceException in all ResourceBundle
            StringBuilder sreturn = new StringBuilder();
            sreturn.append("** ");
            sreturn.append(sKey);
            for (Object value : sValues) {
                sreturn.append(" < ");
                sreturn.append(value.toString());
            }
            sreturn.append("** ");

            return sreturn.toString();
        }
    }
}
