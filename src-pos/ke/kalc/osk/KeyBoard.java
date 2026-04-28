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
package ke.kalc.osk;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import ke.kalc.pos.forms.KALCFonts;

/**
 *
 * @author John Lewis
 */
public class KeyBoard {

    private final Font keyTop = KALCFonts.DEFAULTFONTBOLD.deriveFont(20f);

    private Boolean capsLock = false;
    private Boolean shiftState = false;
    private Boolean altGRState = false;
    private ImageIcon caps = null;
    private ImageIcon capsOn = null;
    private ImageIcon enter = null;
    private ImageIcon blankKey = null;
    private ImageIcon keyPressed = null;
    private ImageIcon capsPressed = null;
    private ImageIcon space = null;
    private ImageIcon spacePressed = null;
    private ImageIcon lshiftPressed = null;
    private ImageIcon rshiftPressed = null;
    private ImageIcon lshift = null;
    private ImageIcon rshift = null;
    private ImageIcon leftPressed = null;
    private ImageIcon rightPressed = null;
    private ImageIcon left = null;
    private ImageIcon right = null;
    private ImageIcon tabPressed = null;
    private ImageIcon tab = null;
    private ImageIcon altgrPressed = null;
    private ImageIcon altgr = null;
    private ImageIcon backSpace = null;
    private ImageIcon bsPressed = null;
    private ImageIcon enter_us = null;
    private ImageIcon enter_usPressed = null;

    private HashMap<String, CustomJLabel> keyList;
    private HashMap<String, CustomJLabel> shiftList;
    private HashMap<String, String> altGRList;
    private HashMap<String, String> chars;
    private HashMap<String, Integer[]> events;

    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static int centreX;
    private static int centreY;

    private Robot robot;

    private String[][] keyMap;

    private static final String[][] qwerty = {
        {"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Backspace"},
        {"Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"},
        {"Caps", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "#", "Enter"},
        {"LShift", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "/", "RShift"},
        {"<", " ", "AltGR", ">"}
    };

    private static final String[][] qwerty_us = {
        {"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Backspace"},
        {"Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"},
        {"Caps", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "EnterUS"},
        {"LShift", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "/", "RShift"},
        {"<", " ", "AltGR", ">"}
    };

    private static final String[][] qwertz = {
        {"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Backspace"},
        {"Tab", "Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P", "[", "]", "\\"},
        {"Caps", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "#", "Enter"},
        {"LShift", "Y", "X", "C", "V", "B", "N", "M", ",", ".", "/", "RShift"},
        {"<", " ", "AltGR", ">"}
    };

    private static final String[][] azerty = {
        {"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Backspace"},
        {"Tab", "A", "Z", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"},
        {"Caps", "Q", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "#", "Enter"},
        {"LShift", "W", "X", "C", "V", "B", "N", "M", ",", ".", "/", "RShift"},
        {"<", " ", "AltGR", ">"}
    };

    private static JDialog OSK;

    public static enum Layout {
        QWERTY,
        QWERTY_US,
        QWERTZ,
        AZERTY;
    }

    private static JPanel kbLayout;
    //private static JPanel kbLayout2;

    private static KeyBoard kbInstance = null;
    private static KeyBoard kbInstance2 = null;

    private static KeyBoard getInstance(Layout charSet) {
        if (kbInstance == null) {
            synchronized (KeyBoard.class) {
                if (kbInstance == null) {
                    kbInstance = new KeyBoard(charSet);
                }
            }
        }
        return kbInstance;
    }

    private static KeyBoard getInstance2(Layout charSet) {
        if (kbInstance2 == null) {
            synchronized (KeyBoard.class) {
                if (kbInstance2 == null) {
                    kbInstance2 = new KeyBoard(charSet);
                }
            }
        }
        return kbInstance2;
    }

    public static void locateTop(Layout layout) {
        getInstance(layout);
        OSK.setLocation(centreX - 400, 0);
        kbInstance.resetKeyboard();
        OSK.setVisible(true);
    }

    public static void locateBottom(Layout layout) {
        getInstance(layout);
        OSK.setLocation(centreX - 400, centreY + (centreY - 330));
        kbInstance.resetKeyboard();
        OSK.setVisible(true);
    }

    public static void locateCentre(Layout layout) {
        getInstance(layout);
        OSK.setLocation(centreX - 400, centreY - 155);
        kbInstance.resetKeyboard();
        OSK.setVisible(true);
    }

    public static void locateCentre(Layout layout, int centre) {
        getInstance(layout);
        OSK.setLocation(centreX - 400, centreY - 155 + centre);
        kbInstance.resetKeyboard();
        OSK.setVisible(true);
    }

    public static void createKeyBoard(Layout layout) {
        getInstance(layout);
        OSK.setLocation(centreX - 400, centreY - 155);
        kbInstance.resetKeyboard();
    }

    public static JPanel getKeyboard(Layout layout) {
        getInstance(layout);
        kbInstance.resetKeyboard();
        return kbLayout;
    }

    public static JPanel getKeyboard2(Layout layout) {
        getInstance2(layout);
        kbInstance2.resetKeyboard();
        return kbLayout;
    }

    public static void hideKeyBoard() {
        if (kbInstance != null) {
            OSK.setVisible(false);
        }
    }

    public static JPanel getLayout(Layout layout, int centre) {
        getInstance(layout);
        OSK.setLocation(centreX - 400, centreY - 155 + centre);
        kbInstance.resetKeyboard();
        return kbLayout;
    }

    private void resetKeyboard() {
        ((JLabel) keyList.get("Caps")).setIcon(caps);
        capsLock = false;

        ((JLabel) shiftList.get("LShift")).setIcon(lshift);
        ((JLabel) shiftList.get("RShift")).setIcon(rshift);
        shiftState = false;

        ((JLabel) keyList.get("AltGR")).setIcon(altgr);
        altGRState = false;
        resetChars();
    }

    private void mapEvents() {
        events = new HashMap<>();

        events.put("a", new Integer[]{KeyEvent.VK_A});
        events.put("b", new Integer[]{KeyEvent.VK_B});
        events.put("c", new Integer[]{KeyEvent.VK_C});
        events.put("d", new Integer[]{KeyEvent.VK_D});
        events.put("e", new Integer[]{KeyEvent.VK_E});
        events.put("f", new Integer[]{KeyEvent.VK_F});
        events.put("g", new Integer[]{KeyEvent.VK_G});
        events.put("h", new Integer[]{KeyEvent.VK_H});
        events.put("i", new Integer[]{KeyEvent.VK_I});
        events.put("j", new Integer[]{KeyEvent.VK_J});
        events.put("k", new Integer[]{KeyEvent.VK_K});
        events.put("l", new Integer[]{KeyEvent.VK_L});
        events.put("m", new Integer[]{KeyEvent.VK_M});
        events.put("n", new Integer[]{KeyEvent.VK_N});
        events.put("o", new Integer[]{KeyEvent.VK_O});
        events.put("p", new Integer[]{KeyEvent.VK_P});
        events.put("q", new Integer[]{KeyEvent.VK_Q});
        events.put("r", new Integer[]{KeyEvent.VK_R});
        events.put("s", new Integer[]{KeyEvent.VK_S});
        events.put("t", new Integer[]{KeyEvent.VK_T});
        events.put("u", new Integer[]{KeyEvent.VK_U});
        events.put("v", new Integer[]{KeyEvent.VK_V});
        events.put("w", new Integer[]{KeyEvent.VK_W});
        events.put("x", new Integer[]{KeyEvent.VK_X});
        events.put("y", new Integer[]{KeyEvent.VK_Y});
        events.put("z", new Integer[]{KeyEvent.VK_Z});
        events.put("A", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_A});
        events.put("B", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_B});
        events.put("C", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_C});
        events.put("D", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_D});
        events.put("E", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_E});
        events.put("F", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_F});
        events.put("G", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_G});
        events.put("H", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_H});
        events.put("I", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_I});
        events.put("J", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_J});
        events.put("K", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_K});
        events.put("L", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_L});
        events.put("M", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_M});
        events.put("N", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_N});
        events.put("O", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_O});
        events.put("P", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_P});
        events.put("Q", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Q});
        events.put("R", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_R});
        events.put("S", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_S});
        events.put("T", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_T});
        events.put("U", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_U});
        events.put("V", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_V});
        events.put("W", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_W});
        events.put("X", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_X});
        events.put("Y", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Y});
        events.put("Z", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Z});
        events.put("`", new Integer[]{KeyEvent.VK_BACK_QUOTE});
        events.put("0", new Integer[]{KeyEvent.VK_0});
        events.put("1", new Integer[]{KeyEvent.VK_1});
        events.put("2", new Integer[]{KeyEvent.VK_2});
        events.put("3", new Integer[]{KeyEvent.VK_3});
        events.put("4", new Integer[]{KeyEvent.VK_4});
        events.put("5", new Integer[]{KeyEvent.VK_5});
        events.put("6", new Integer[]{KeyEvent.VK_6});
        events.put("7", new Integer[]{KeyEvent.VK_7});
        events.put("8", new Integer[]{KeyEvent.VK_8});
        events.put("9", new Integer[]{KeyEvent.VK_9});
        events.put("-", new Integer[]{KeyEvent.VK_MINUS});
        events.put("=", new Integer[]{KeyEvent.VK_EQUALS});
        events.put("~", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_NUMBER_SIGN});
        events.put("@", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE});
        events.put("#", new Integer[]{KeyEvent.VK_NUMBER_SIGN});
        events.put("!", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_1});
        events.put("\"", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_2});
        events.put("£", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_3});
        events.put("$", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_4});
        events.put("%", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_5});
        events.put("^", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_6});
        events.put("&", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_7});
        events.put("*", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_8});
        events.put("(", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_9});
        events.put(")", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_0});
        events.put("_", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS});
        events.put("+", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS});
        events.put("\t", new Integer[]{KeyEvent.VK_TAB});
        events.put("\n", new Integer[]{KeyEvent.VK_ENTER});
        events.put("[", new Integer[]{KeyEvent.VK_OPEN_BRACKET});
        events.put("]", new Integer[]{KeyEvent.VK_CLOSE_BRACKET});
        events.put("\\", new Integer[]{KeyEvent.VK_BACK_SLASH});
        events.put("{", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET});
        events.put("}", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET});
        events.put("|", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH});
        events.put(";", new Integer[]{KeyEvent.VK_SEMICOLON});
        events.put(":", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON});
        events.put(",", new Integer[]{KeyEvent.VK_COLON});
        events.put("\'", new Integer[]{KeyEvent.VK_QUOTE});
        events.put(",", new Integer[]{KeyEvent.VK_COMMA});
        events.put("<", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA});
        events.put(".", new Integer[]{KeyEvent.VK_PERIOD});
        events.put(">", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD});
        events.put("/", new Integer[]{KeyEvent.VK_SLASH});
        events.put("?", new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH});
        events.put(" ", new Integer[]{KeyEvent.VK_SPACE});
        events.put("Backspace", new Integer[]{KeyEvent.VK_BACK_SPACE});
        events.put("Tab", new Integer[]{KeyEvent.VK_TAB});
        events.put("Enter", new Integer[]{KeyEvent.VK_ENTER});
        events.put("Caps", new Integer[]{KeyEvent.VK_CAPS_LOCK});
        events.put("\u20AC", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_4});  // Euro
        events.put("\u00E1", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_A}); // a-acute
        events.put("\u00C1", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_SHIFT, KeyEvent.VK_A}); // A-acute
        events.put("\u00E9", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_E}); // e-acute
        events.put("\u00C9", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_SHIFT, KeyEvent.VK_E}); // E-acute
        events.put("\u00ED", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_I}); // i-acute
        events.put("\u00CD", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_SHIFT, KeyEvent.VK_I}); // I-acute
        events.put("\u00FA", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_U}); // u-acute
        events.put("\u00DA", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_SHIFT, KeyEvent.VK_U}); // U-acute
        events.put("\u00A6", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_BACK_QUOTE}); // broken bar
        events.put("\u00F3", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_O}); // o-acute
        events.put("\u00D3", new Integer[]{KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_SHIFT, KeyEvent.VK_O}); // O-acute
    }

    private void mapAltGr() {
        altGRList = new HashMap<>();

        altGRList.put("`", "¦");
        altGRList.put("4", "€");
        altGRList.put("A", "á");
        altGRList.put("E", "é");
        altGRList.put("I", "í");
        altGRList.put("U", "ú");
        altGRList.put("O", "ó");
    }

    private void mapChars() {
        chars = new HashMap<>();

        chars.put("`", "¬");
        chars.put("1", "!");
        chars.put("2", "\"");
        chars.put("3", "£");
        chars.put("4", "$");
        chars.put("5", "%");
        chars.put("6", "^");
        chars.put("7", "&");
        chars.put("8", "*");
        chars.put("9", "(");
        chars.put("0", ")");
        chars.put("-", "_");
        chars.put("=", "+");
        chars.put("[", "{");
        chars.put("]", "}");
        chars.put(";", ":");
        chars.put("'", "@");
        chars.put("#", "~");
        chars.put("\\", "|");
        chars.put(",", "<");
        chars.put(".", ">");
        chars.put("/", "?");
    }

    private void mapCharsUS() {
        chars = new HashMap<>();

        chars.put("`", "~");
        chars.put("1", "!");
        chars.put("2", "@");
        chars.put("3", "#");
        chars.put("4", "$");
        chars.put("5", "%");
        chars.put("6", "^");
        chars.put("7", "&");
        chars.put("8", "*");
        chars.put("9", "(");
        chars.put("0", ")");
        chars.put("-", "_");
        chars.put("=", "+");
        chars.put("[", "{");
        chars.put("]", "}");
        chars.put(";", ":");
        chars.put("'", "\"");
        chars.put("\\", "|");
        chars.put(",", "<");
        chars.put(".", ">");
        chars.put("/", "?");
    }

    public KeyBoard(Layout layout) {
        centreX = screenSize.width / 2;
        centreY = screenSize.height / 2;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        keyMap = qwerty;
        if (layout != null) {
            switch (layout) {
                case QWERTY:
                    keyMap = qwerty;
                    mapChars();
                    break;
                case QWERTY_US:
                    keyMap = qwerty_us;
                    mapCharsUS();
                    break;
                case QWERTZ:
                    keyMap = qwertz;
                    mapChars();
                    break;
                case AZERTY:
                    keyMap = azerty;
                    mapChars();
                    break;
            }
        }

        mapEvents();
        mapAltGr();

        try {
            caps = new ImageIcon(ImageIO.read(getClass().getResource("caps.png")));
            capsOn = new ImageIcon(ImageIO.read(getClass().getResource("capson.png")));
            enter = new ImageIcon(ImageIO.read(getClass().getResource("caps.png")));
            backSpace = new ImageIcon(ImageIO.read(getClass().getResource("bs.png")));
            bsPressed = new ImageIcon(ImageIO.read(getClass().getResource("bspressed.png")));
            blankKey = new ImageIcon(ImageIO.read(getClass().getResource("blank.png")));
            keyPressed = new ImageIcon(ImageIO.read(getClass().getResource("keypressed.png")));
            capsPressed = new ImageIcon(ImageIO.read(getClass().getResource("capspressed.png")));
            space = new ImageIcon(ImageIO.read(getClass().getResource("space.png")));
            spacePressed = new ImageIcon(ImageIO.read(getClass().getResource("spacepressed.png")));
            lshiftPressed = new ImageIcon(ImageIO.read(getClass().getResource("lshiftpressed.png")));
            rshiftPressed = new ImageIcon(ImageIO.read(getClass().getResource("rshiftpressed.png")));
            lshift = new ImageIcon(ImageIO.read(getClass().getResource("lshift.png")));
            rshift = new ImageIcon(ImageIO.read(getClass().getResource("rshift.png")));
            leftPressed = new ImageIcon(ImageIO.read(getClass().getResource("leftpressed.png")));
            rightPressed = new ImageIcon(ImageIO.read(getClass().getResource("rightpressed.png")));
            left = new ImageIcon(ImageIO.read(getClass().getResource("left.png")));
            right = new ImageIcon(ImageIO.read(getClass().getResource("right.png")));
            tab = new ImageIcon(ImageIO.read(getClass().getResource("tab.png")));
            tabPressed = new ImageIcon(ImageIO.read(getClass().getResource("tabpressed.png")));
            altgr = new ImageIcon(ImageIO.read(getClass().getResource("altgr.png")));
            altgrPressed = new ImageIcon(ImageIO.read(getClass().getResource("altgrpressed.png")));
            enter_us = new ImageIcon(ImageIO.read(getClass().getResource("enter_us.png")));
            enter_usPressed = new ImageIcon(ImageIO.read(getClass().getResource("enter_uspressed.png")));

        } catch (IOException ex) {
            Logger.getLogger(KeyBoard.class.getName()).log(Level.SEVERE, null, ex);
        }

        kbLayout = new JPanel();

        kbLayout.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pRow1 = new JPanel();
        JPanel pRow2 = new JPanel();
        JPanel pRow3 = new JPanel();
        JPanel pRow4 = new JPanel();
        JPanel pRow5 = new JPanel();

        keyList = new HashMap<>();
        shiftList = new HashMap<>();

        FlowLayout flow = new FlowLayout(FlowLayout.LEFT);
        GridLayout grid = new GridLayout(5, 0);

        flow.setHgap(0);
        flow.setVgap(0);
        grid.setVgap(1);

        kbLayout.setLayout(grid);

        //Create numbers key row
        pRow1.setLayout(flow);
        for (int col = 0; col < keyMap[0].length; ++col) {
            CustomJLabel a = new CustomJLabel();
            if (keyMap[0][col].equalsIgnoreCase("backspace")) {
                keyCreator(a, backSpace, keyMap[0][col]);
                keyActionHandler(a, backSpace, bsPressed);
            } else {
                keyCreator(a, blankKey, keyMap[0][col]);
                keyActionHandler(a, blankKey, keyPressed);
            }
            keyList.put(keyMap[0][col], a);
            pRow1.add(a);
        }

        //Create Q-P key row
        pRow2.setLayout(flow);
        for (int col = 0; col < keyMap[1].length; ++col) {
            CustomJLabel a = new CustomJLabel();
            if (!keyMap[1][col].equalsIgnoreCase("tab")) {
                keyCreator(a, blankKey, (capsLock) ? keyMap[1][col].toUpperCase() : keyMap[1][col].toLowerCase());
                keyActionHandler(a, blankKey, keyPressed);
                pRow2.add(a, "align left");
            } else {
                keyCreator(a, tab, "Tab");
                keyActionHandler(a, tab, tabPressed);
                pRow2.add(a);
            }
            keyList.put(keyMap[1][col], a);
        }

        //Create A-L key row
        pRow3.setLayout(flow);
        for (int col = 0; col < keyMap[2].length; ++col) {
            CustomJLabel a = new CustomJLabel();
            switch (keyMap[2][col]) {
                case "Caps":
                    keyCreator(a, caps, "Caps");
                    a.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (!shiftState) {
                                capsLock = !capsLock;
                                a.setIcon((capsLock) ? capsOn : caps);
                                changeKeyCase();
                            }
                        }
                    });
                    break;
                case "Enter":
                    keyCreator(a, enter, keyMap[2][col]);
                    keyActionHandler(a, caps, capsPressed);
                    break;
                case "EnterUS":
                    keyCreator(a, enter_us, "Enter");
                    keyActionHandler(a, enter_us, enter_usPressed);
                    break;
                default:
                    keyCreator(a, blankKey, (capsLock) ? keyMap[2][col].toUpperCase() : keyMap[2][col].toLowerCase());
                    keyActionHandler(a, blankKey, keyPressed);
            }
            keyList.put(keyMap[2][col], a);
            pRow3.add(a);
        }

        //Create Z-M key row
        pRow4.setLayout(flow);
        for (int col = 0; col < keyMap[3].length; ++col) {
            CustomJLabel a = new CustomJLabel();
            switch (keyMap[3][col]) {
                case "LShift":
                case "RShift":
                    keyCreator(a, (keyMap[3][col].equals("LShift")) ? lshift : rshift, "Shift");
                    a.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            setShiftState();
                        }
                    });
                    shiftList.put(keyMap[3][col], a);
                    break;
                default:
                    keyCreator(a, blankKey, (capsLock) ? keyMap[3][col].toUpperCase() : keyMap[3][col].toLowerCase());
                    keyActionHandler(a, blankKey, keyPressed);
                    keyList.put(keyMap[3][col], a);
            }
            pRow4.add(a);
        }

        //Create bottom key row
        pRow5.setLayout(flow);
        for (int col = 0; col < keyMap[4].length; ++col) {
            CustomJLabel a = new CustomJLabel();
            switch (keyMap[4][col]) {
                case " ":
                    keyCreator(a, space, " ");
                    keyActionHandler(a, space, spacePressed);
                    break;
                case "<":
                    keyCreator(a, left, "");
                    a.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            a.setIcon(leftPressed);
                            robot.keyPress(KeyEvent.VK_LEFT);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            a.setIcon(left);
                        }
                    });
                    break;
                case ">":
                    keyCreator(a, right, "");
                    a.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            a.setIcon(rightPressed);
                            robot.keyPress(KeyEvent.VK_RIGHT);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            a.setIcon(right);
                        }
                    });
                    break;
                case "AltGR":
                    keyCreator(a, altgr, keyMap[4][col]);
                    a.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            if (altGRState) {
                                resetChars();
                            } else {
                                changeAltGRChars();
                            }
                            altGRState = !altGRState;
                            a.setIcon((altGRState) ? altgrPressed : altgr);
                        }
                    });
                    break;
                default:
                    keyCreator(a, blankKey, keyMap[4][col]);
            }
            keyList.put(keyMap[4][col], a);
            pRow5.add(a);
        }

        kbLayout.add(pRow1);
        kbLayout.add(pRow2);
        kbLayout.add(pRow3);
        kbLayout.add(pRow4);
        kbLayout.add(pRow5);

        OSK = new JDialog();
        OSK.setTitle("On-Screen Keyboard");
        try {
            OSK.setIconImage(ImageIO.read(getClass().getResource("keyboard.png")));
        } catch (IOException ex) {
            OSK.setIconImage(null);
        }
        OSK.setSize(800, 315);
        OSK.add(kbLayout);
        OSK.setAlwaysOnTop(true);
        OSK.setFocusableWindowState(false);
//        OSK.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });

    }

    private void keyCreator(CustomJLabel lbl, ImageIcon img, String keyText) {
        lbl.setIcon(img);
        lbl.setFont(keyTop);
        lbl.setForeground(Color.black);
        lbl.setText(keyText);
        lbl.setHomeKey(keyText);
        lbl.setHorizontalTextPosition(SwingConstants.CENTER);
        lbl.setVerticalTextPosition(SwingConstants.CENTER);
        lbl.setFocusable(false);
    }

    public class CustomJLabel extends JLabel {

        private String homeKey;

        public CustomJLabel() {
            super();
        }

        public void setHomeKey(String text) {
            this.homeKey = text;
        }

        public String getHomeKey() {
            return homeKey;
        }
    }

    public void registerEvent() {
        MouseAdapter mouse = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                processKeyPressed((JLabel) e.getSource());
                if (shiftState) {
                    setShiftState();
                }
                if (altGRState) {
                    setAltGRState();
                }
            }

            public void mouseReleased(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
            }

        };
    }

    private void keyActionHandler(JLabel lbl, ImageIcon unPressed, ImageIcon pressed) {
        lbl.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lbl.setIcon(pressed);
                processKeyPressed((JLabel) e.getSource());
                if (shiftState) {
                    setShiftState();
                }
                if (altGRState) {
                    setAltGRState();
                }
            }

            public void mouseReleased(MouseEvent e) {
                lbl.setIcon(unPressed);
            }
        });
    }

    private void processKeyPressed(JLabel button) {
        Integer[] results = events.get(button.getText());

        if (results != null) {
            for (int k : results) {
                robot.keyPress(k);
            }

            for (int k : results) {
                robot.keyRelease(k);
            }
        }
    }

    private void setAltGRState() {
        for (Map.Entry<String, CustomJLabel> s : keyList.entrySet()) {
            if (s.getValue().getText().equalsIgnoreCase("altgr")) {
                ((CustomJLabel) s.getValue()).setIcon(altgr);
                altGRState = !altGRState;
                resetChars();
            }
        }

    }

    private void setShiftState() {
        for (Map.Entry<String, CustomJLabel> s : shiftList.entrySet()) {
            if (s.getKey().equalsIgnoreCase("lshift")) {
                s.getValue().setIcon((shiftState) ? lshift : lshiftPressed);
            } else {
                s.getValue().setIcon((shiftState) ? rshift : rshiftPressed);
            }
        }
        shiftState = !shiftState;
        changeKeyChars();
    }

    private void changeKeyCase() {
        for (Map.Entry<String, CustomJLabel> s : keyList.entrySet()) {
            if (s.getKey().length() == 1) {
                if (capsLock) {
                    s.getValue().setText(s.getValue().getText().toUpperCase());
                } else {
                    s.getValue().setText(s.getValue().getText().toLowerCase());
                }
            }
        }
    }

    private void changeKeyChars() {
        if (!altGRState) {
            for (Map.Entry<String, CustomJLabel> s : keyList.entrySet()) {
                if (s.getKey().length() == 1) {
                    if (chars.containsKey(s.getKey())) {
                        s.getValue().setText((shiftState) ? chars.get(s.getKey()) : s.getKey());
                    }
                }
            }
        }

        for (Map.Entry<String, CustomJLabel> s : keyList.entrySet()) {
            if (s.getKey().length() == 1) {
                if (capsLock && shiftState) {
                    s.getValue().setText(s.getValue().getText().toLowerCase());
                } else if (capsLock && !shiftState) {
                    s.getValue().setText(s.getValue().getText().toUpperCase());
                } else if (!capsLock && shiftState) {
                    s.getValue().setText(s.getValue().getText().toUpperCase());
                } else {
                    s.getValue().setText(s.getValue().getText().toLowerCase());
                }
            }
        }
    }

    private void changeAltGRChars() {
        for (Map.Entry<String, CustomJLabel> s : keyList.entrySet()) {
            if (altGRList.containsKey(s.getKey())) {
                if (capsLock || shiftState) {
                    s.getValue().setText(altGRList.get(s.getKey()).toUpperCase());
                } else {
                    s.getValue().setText(altGRList.get(s.getKey()).toLowerCase());
                }
            } else if (!s.getValue().getText().equalsIgnoreCase("backspace") & !s.getValue().getText().equalsIgnoreCase("tab")
                    & !s.getValue().getText().equalsIgnoreCase("caps") & !s.getValue().getText().equalsIgnoreCase("shift")
                    & !s.getValue().getText().equalsIgnoreCase("enter") & !s.getValue().getText().equalsIgnoreCase("altgr")) {
                s.getValue().setText("");
            }
        }
    }

    private void resetChars() {
        for (Map.Entry<String, CustomJLabel> s : keyList.entrySet()) {
            if (!s.getValue().getText().equals("Backspace") & !s.getValue().getText().equals("Tab")
                    & !s.getValue().getText().equals("Caps") & !s.getValue().getText().equals("Shift")
                    & !s.getValue().getText().equals("Enter") & !s.getValue().getText().equals("AltGR")) {
                if (capsLock || shiftState) {
                    s.getValue().setText(((CustomJLabel) s.getValue()).getHomeKey().toUpperCase());
                } else {
                    s.getValue().setText(((CustomJLabel) s.getValue()).getHomeKey().toLowerCase());
                }
            }
        }
    }
}
