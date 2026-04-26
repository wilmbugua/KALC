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


package ke.kalc.globals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author John
 */
public class IconFactory {

    private static final HashMap<String, ImageIcon> iconFactoryCache = new HashMap<>();
    private static final IconFactory iconFactory = null;

    private IconFactory() {
    }

    public static void setDefaultIcon(String iconName) {
        iconFactoryCache.get(iconName);
        try {
            ImageIcon icon = iconFactoryCache.get(iconName);
            iconFactoryCache.put("default_icon", icon);
        } catch (Exception x) {
        }
    }

    public static void setDefaultIcon(String path, String iconName) {
        try {
            ImageIcon icon = new ImageIcon(path + iconName);
            iconFactoryCache.put("default_icon", icon);
        } catch (Exception x) {
        }
    }

    public static void addGradientImage(int width, int height, Color gradient1, Color gradient2, String imageName) {
        try {
            iconFactoryCache.put(imageName, createGradientIcon(width, height, gradient1, gradient2));
        } catch (Exception x) {
        }
    }

    public static ImageIcon getDefaultIcon() {
        return iconFactoryCache.get("default_icon");

    }

    public static Image getImage(String iconName) {
        ImageIcon icon = getIcon(iconName);
        if (icon != null) {
            BufferedImage bi = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(icon.getImage(), 0, 0, null);
            g.dispose();
            return SwingFXUtils.toFXImage(bi, null);
        }
        return null;
    }

    public static BufferedImage getBufferedImage(String iconName){
        ImageIcon icon = getIcon(iconName);
        if (icon != null) {
            BufferedImage bi = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(icon.getImage(), 0, 0, null);
            g.dispose();
            return bi;
        }
        return null;
    }
    
    
    public static Image getImage(String iconName, String defaultImage) {
        ImageIcon icon = getIcon(iconName);
        if (icon == null) {
            try {
                icon = iconFactoryCache.get(defaultImage);
            } catch (Exception x) {
                return null;
            }
        }
        if (icon != null) {
            BufferedImage bi = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(icon.getImage(), 0, 0, null);
            g.dispose();
            return SwingFXUtils.toFXImage(bi, null);
        }
        return null;
    }

    public static ImageIcon createGradientIcon(int width, int height, Color gradient1, Color gradient2) {
        BufferedImage gradientImage = createCompatibleImage(width, height);
        //GradientPaint gradient = new GradientPaint(0, 0, gradient1, 0, height, gradient2, false);
        GradientPaint gradient = new GradientPaint(0, 0, gradient1, 0, height, gradient2, false);
        Graphics2D g2 = (Graphics2D) gradientImage.getGraphics();
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);
        g2.dispose();
        return new ImageIcon(gradientImage);
    }

    private static BufferedImage createCompatibleImage(int width, int height) {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration().createCompatibleImage(width, height);
    }

    public static ImageIcon getIcon(String iconName) {
        ImageIcon icon = iconFactoryCache.get(iconName);
        if (icon == null) {
            try {
                icon = iconFactoryCache.get("default_icon");
            } catch (Exception x) {
                return null;
            }
        }
        return icon;
    }

    public static ImageIcon getIcon(String iconName, String defaultImage) {
        ImageIcon icon = iconFactoryCache.get(iconName);
        if (icon == null) {
            try {
                icon = iconFactoryCache.get(defaultImage);
            } catch (Exception x) {
                return null;
            }
        }
        return icon;
    }

    public static ImageIcon getResizedIcon(String iconName, Dimension size) {
        ImageIcon icon = iconFactoryCache.get(iconName);
        if (icon == null) {
            icon = iconFactoryCache.get("default_icon");
        }

        if (icon != null) {
            icon = new ImageIcon(icon.getImage().getScaledInstance(size.width, size.height, java.awt.Image.SCALE_SMOOTH));
        }
        return icon;
    }

    public static ImageIcon resizeIcon(ImageIcon icon, Dimension size) {
        return new ImageIcon(icon.getImage().getScaledInstance(size.width, size.height, java.awt.Image.SCALE_SMOOTH));
    }

    public static void cacheIconFromFile(String path, String iconName) {
        try {
            ImageIcon icon = new ImageIcon(path + iconName);
            iconFactoryCache.put(iconName, icon);
        } catch (Exception x) {
        }
    }

    public static void cacheIconsFromFolder(String folder) {
        File dir = new File("./" + folder + "/");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif"));
        for (File imagefile : files) {
            ImageIcon icon = new ImageIcon(imagefile.getAbsolutePath());
            iconFactoryCache.put(imagefile.getName(), icon);
        }
    }

    public static void cacheIconsFromResource(String resource) throws IOException {
        try {
            JarFile jarFile = new JarFile(new File(System.getProperty("user.dir") + resource));
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = (JarEntry) e.nextElement();
                String name = entry.getName();
                if (name.endsWith(".png") || name.endsWith(".jpg")) {
                    InputStream is = IconFactory.class.getResourceAsStream("/" + name);
                    BufferedImage bf = ImageIO.read(is);
                    iconFactoryCache.put(name.substring(name.lastIndexOf('/') + 1), new ImageIcon(bf));
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void cacheIconsFromZip(String zipFileName) {
        String currentPath;
        currentPath = System.getProperty("user.dir");
        ByteArrayOutputStream out;
        FileInputStream fis;        
        
        try {
            fis = new FileInputStream(currentPath + "/" + zipFileName);
            try (
                     ZipInputStream zis = new ZipInputStream(fis)) {
                ZipEntry ze = zis.getNextEntry();

                while (ze != null) {
                    String fileName = ze.getName();
                    out = new ByteArrayOutputStream();

                    byte[] buffer = new byte[4096];
                    for (int n; (n = zis.read(buffer)) != -1;) {
                        out.write(buffer, 0, n);
                    }
                    out.close();
                    byte[] bytes = out.toByteArray();
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    iconFactoryCache.put(fileName, new ImageIcon(ImageIO.read(bais)));
                    ze = zis.getNextEntry();
                }
                zis.closeEntry();
            }
            fis.close();
        } catch (IOException e) {
        }
    }

}
