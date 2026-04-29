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

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.globals.SystemProperty;

/**
 *
 * @author John
 */
public class LocalResource {

    private static final List<ResourceBundle> localResources = new LinkedList<>();
    public static ResourceBundle resources;
    private static String localePath = System.getProperty("user.dir") + "/locales/";
    public static String locale = StartPOS.defaultLocale.toString();
    private static String cLocale = "";
    private static String rLocale = "";

    static {

        cLocale = "";
        try {
            cLocale = ((SystemProperty.USERLANGUAGE == null || SystemProperty.USERLANGUAGE.isEmpty()) ? "" : SystemProperty.USERLANGUAGE)
                    + (((SystemProperty.USERLANGUAGE != null && !SystemProperty.USERLANGUAGE.isEmpty())
                    && (SystemProperty.USERCOUNTRY != null && !SystemProperty.USERCOUNTRY.isEmpty())) ? "_" : "")
                    + ((SystemProperty.USERCOUNTRY == null) ? "" : SystemProperty.USERCOUNTRY);
            cLocale = (cLocale == null || cLocale.isEmpty()) ? "_" + Locale.getDefault().toString() : "_" + cLocale;
        } catch (ExceptionInInitializerError ex) {

        }

        rLocale = cLocale;
        File f = new File(localePath + "kalc" + rLocale + ".properties");
        if (!f.exists() || f.isDirectory()) {
            rLocale = "";
        }

        try {
            resources = buildResources(localePath + "kalc" + rLocale + ".properties");
        } catch (ExceptionInInitializerError | MissingResourceException ex) {
        }
        localResources.add(resources);
    }

    private static ResourceBundle buildResources(String propertiesFileLocation) {
        try {
            InputStream propStrStream = new FileInputStream(new File(propertiesFileLocation));
            return (ResourceBundle) new PropertyResourceBundle(propStrStream);
        } catch (FileNotFoundException ex) {
            missingAlert();
        } catch (IOException ex) {
            missingAlert();
        }
        return null;
    }

    private static void missingAlert() {
        JAlertPane.messageBox(JAlertPane.WARNING, "\nUnable to find default locale file \n\n'kalc.properties' cannot be found, please try reinstalling the application.", 16,
                new Dimension(125, 50), JAlertPane.OK_OPTION);
        System.exit(0);
    }

    public static String getString(String sKey) {
        if (sKey == null) {
            return null;
        } else {
            for (ResourceBundle r : localResources) {
                try {
                    return r.getString(sKey);
                } catch (MissingResourceException e) {
                }
            }
        }
        return "!! " + sKey + " !!";
    }

    public static String getString(String sKey, Integer length) {
        if (sKey == null) {
            return null;
        } else {
            for (ResourceBundle r : localResources) {
                try {
                    return String.format("%1$-" + length + "s", r.getString(sKey));
                } catch (MissingResourceException e) {
                }
            }
        }
        return "!! " + sKey + " !!";
    }

    public static String getString(Integer length, String sKey) {
        if (sKey == null) {
            return null;
        } else {
            for (ResourceBundle r : localResources) {
                try {
                    return String.format("%1$" + length + "s", r.getString(sKey));
                } catch (MissingResourceException e) {
                }
            }
        }
        return "!! " + sKey + " !!";
    }

    public static String getString(String sKey, Object... sValues) {
        if (sKey == null) {
            return null;
        } else {
            for (ResourceBundle r : localResources) {
                try {
                    return MessageFormat.format(r.getString(sKey), sValues);
                } catch (MissingResourceException e) {
                }
            }
            return "!! " + sKey + " !!";
        }
    }

}
