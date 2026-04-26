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
package uk.kalc.pos.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import uk.kalc.basic.BasicException;
import uk.kalc.commons.dialogs.JAlertPane;
import static uk.kalc.custom.CustomColour.getBorderColour;
import uk.kalc.custom.CustomJLabel;
import uk.kalc.custom.CustomJTextField;
import uk.kalc.custom.ExtendedJButton;
import uk.kalc.data.gui.ComboBoxValModel;
import uk.kalc.data.loader.QBFCompareEnum;
import uk.kalc.data.loader.SentenceList;
import uk.kalc.data.user.ListProvider;
import uk.kalc.data.user.ListProviderCreator;
import uk.kalc.globals.IconFactory;
import uk.kalc.osk.KeyBoard;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.datalogic.DataLogicSales;
import uk.kalc.pos.forms.KALCFonts;
import uk.kalc.pos.forms.JRootFrame;
import uk.kalc.pos.ticket.ProductInfoExt;
import uk.kalc.pos.ticket.ProductRenderer;


public class JProductFinder extends JDialog {

    private ListProvider lpr;
    private String siteGuid;

    public final static int PRODUCT_ALL = 0;
    public final static int PRODUCT_NORMAL = 1;
    public final static int PRODUCT_AUXILIAR = 2;
    public final static int PRODUCT_RECIPE = 3;

    //new requirements     
    private final Font lblFont = KALCFonts.DEFAULTFONT;
    private final Font txtFont = KALCFonts.DEFAULTFONT;
    private final Font btnFont = KALCFonts.DEFAULTFONT;
    private final Font listFont = KALCFonts.DEFAULTFONT.deriveFont(14f);

    private final JPanel mainPanel = new JPanel(new MigLayout("insets 10 10 10 10 ", "[400]20[350]", "[][60][]"));
    private final JPanel productFinder = new JPanel(new MigLayout("insets 0 0 0 0 ", "[100][330]", "[][][]"));
    private final JPanel listPanel = new JPanel(new MigLayout("insets 0 0 0 0 ", "[350]", "[300]"));
    private final JPanel keyboardPanel = new JPanel();

    private JPanel btnMainPanel;
    private JPanel btnPanel;
    private final JPanel keyBoard;
    private final JPanel listPane = new JPanel();

    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final JList jListItems = new JList();

    private ExtendedJButton btn;
    private JButton btnOK;
    private final JButton customerBtn = new JButton();

    private final JComboBox jComboBoxCategories = new JComboBox();
    private final JCheckBox inStock = new JCheckBox();
    private final CustomJTextField barcode = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField name = new CustomJTextField(new Dimension(300, 25), txtFont);

    private final SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;

    private ProductInfoExt selectedProduct;
    private final DataLogicSales dlSales;

    public JProductFinder(DataLogicSales dlSales) {
        super(new JFrame());
        this.dlSales = dlSales;
        m_sentcat = dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel();
        keyBoard = KeyBoard.getKeyboard2(KeyBoard.Layout.QWERTY);
        productFinderPane(this.dlSales);
        pack();
        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + 50;
        setLocation(x, y);
    }

    private void productFinderPane(DataLogicSales dlSales) {
        lpr = new ListProviderCreator(dlSales.getProductList(siteGuid));

        createListPanel(440, 130);
        setButtonPanel(new Dimension(105, 35));

        jComboBoxCategories.setPreferredSize(new Dimension(300, 25));

        try {
            List catlist = m_sentcat.list();
            catlist.add(0, null);
            m_CategoryModel = new ComboBoxValModel(catlist);
            jComboBoxCategories.setModel(m_CategoryModel);
        } catch (BasicException eD) {

        }

        customerBtn.setPreferredSize(new Dimension(27, 27));
        customerBtn.setMaximumSize(new Dimension(27, 27));
        customerBtn.setIcon(IconFactory.getResizedIcon("customer_sml.png", new Dimension(25, 25)));

        productFinder.add(new CustomJLabel(AppLocal.getIntString("label.prodbarcode"), lblFont));
        productFinder.add(barcode, "wrap");

        listPanel.add(listPane, "growy");

        productFinder.add(new CustomJLabel(AppLocal.getIntString("label.name"), lblFont));
        productFinder.add(name, "wrap");
        productFinder.add(new CustomJLabel(AppLocal.getIntString("label.prodcategory"), lblFont));
        productFinder.add(jComboBoxCategories, "wrap");
        productFinder.add(new CustomJLabel(AppLocal.getIntString("label.showinstockonly"), lblFont));
        productFinder.add(inStock, "wrap");
        productFinder.add(btnPanel, "gaptop 20, span 2,  align right, wrap");

        mainPanel.add(productFinder, "gapbottom 100, align left");
        mainPanel.add(listPanel, "wrap");

        mainPanel.add(btnMainPanel, "span ,  align right, wrap");

        mainPanel.add(keyboardPanel, "span");
        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(getBorderColour(), 2));
        setTitle("Product Finder");
        getContentPane().add(mainPanel);

        defaultValues();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                selectedProduct = null;
                dispose();
            }
        });
        setAlwaysOnTop(true);
    }

    private void createListPanel(Integer width, Integer height) {

        listPane.setPreferredSize(new Dimension(width, height));
        listPane.setLayout(new java.awt.BorderLayout());
        listPane.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jListItems.setCellRenderer(new ProductRenderer());
        jListItems.setFont(listFont);
        jListItems.setFixedCellHeight(60);
        jListItems.setFocusable(false);
        jListItems.setRequestFocusEnabled(false);
        jListItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    selectedProduct = (ProductInfoExt) jListItems.getSelectedValue();
                    dispose();
                }
            }
        });

        jListItems.addListSelectionListener((javax.swing.event.ListSelectionEvent evt) -> {
            btnOK.setEnabled(jListItems.getSelectedValue() != null);
        });

        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(30, 30));
        jScrollPane1.setViewportView(jListItems);
    }

    private void defaultValues() {
        jListItems.setModel(new MyListData(new ArrayList()));
        inStock.setSelected(false);
        barcode.setText(null);
        name.setText("");
        jComboBoxCategories.setSelectedIndex(0);

        barcode.requestFocus();
    }

    private void setButtonPanel(Dimension dimension) {
        btnPanel = new JPanel();

        btn = new ExtendedJButton("Reset", "reload.png", 6);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {;
            defaultValues();
        });
        btnPanel.add(btn);

        btn = new ExtendedJButton("Search", "ok.png", 5);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            executeSearch();
        });
        btnPanel.add(btn);

        btnMainPanel = new JPanel();

        btnOK = new ExtendedJButton(AppLocal.getIntString("button.ok"), JAlertPane.OK);
        btnOK.setPreferredSize(dimension);
        btnOK.setFont(btnFont);
        btnOK.setFocusable(false);
        btnOK.addActionListener((ActionEvent e) -> {
            selectedProduct = (ProductInfoExt) jListItems.getSelectedValue();
            dispose();
        });
        btnMainPanel.add(btnOK);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            selectedProduct = null;
            dispose();
        });
        btnMainPanel.add(btn);

        JButton kbButton = new JButton();
        kbButton.setBorderPainted(false);
        kbButton.setOpaque(false);
        kbButton.setPreferredSize(new Dimension(75, 35));
        kbButton.setIcon(IconFactory.getResizedIcon("keyboard.png", new Dimension(75, 35)));
        kbButton.addActionListener((ActionEvent e) -> {
            kbButton.setEnabled(false);
            keyboardPanel.add(keyBoard);
            int x = (this.getX() + (this.getWidth() / 2)) - 400;
            this.setLocation(x, this.getY());
            this.pack();
        });

        btnMainPanel.add(kbButton);
    }

    public ProductInfoExt getSelectedProduct() {
        return selectedProduct;
    }

    public void executeSearch() {
        if (barcode.getText().isBlank() && name.getText().isBlank() && jComboBoxCategories.getSelectedIndex() == -1) {
            return;
        }

        try {
            jListItems.setModel(new MyListData(lpr.setData(createValue())));
            if (jListItems.getModel().getSize() > 0) {
                jListItems.setSelectedIndex(0);
            }
        } catch (BasicException e) {
        }
    }

    /*
            switch (productsType) {
            case PRODUCT_NORMAL:
                lpr = new ListProviderCreator(dlSales.getProductListNormal(siteGuid), jproductfilter);
                break;
            case PRODUCT_AUXILIAR:
                lpr = new ListProviderCreator(dlSales.getProductListAuxiliar(siteGuid), jproductfilter);
                break;
            default: // PRODUCT_ALL
                lpr = new ListProviderCreator(dlSales.getProductList(siteGuid), jproductfilter);
                break;
        }
     */
    public Object createValue() throws BasicException {

        Object[] afilter = new Object[8];

        if (inStock.isSelected()) {
            afilter[2] = QBFCompareEnum.COMP_GREATER;
            afilter[3] = 0.0;
        } else {
            afilter[2] = QBFCompareEnum.COMP_NONE;
            afilter[3] = null;
        }

        if (barcode.getText() == null || barcode.getText().equals("")) {
            afilter[0] = QBFCompareEnum.COMP_NONE;
            afilter[1] = null;
        } else {
            afilter[0] = QBFCompareEnum.COMP_RE;
            afilter[1] = barcode.getText();
            afilter[4] = QBFCompareEnum.COMP_NONE;
            afilter[5] = null;
            afilter[6] = QBFCompareEnum.COMP_NONE;
            afilter[7] = null;
            return afilter;
        }

        if (name.getText() == null || name.getText().equals("")) {
            afilter[4] = QBFCompareEnum.COMP_NONE;
            afilter[5] = null;
        } else {
            afilter[4] = QBFCompareEnum.COMP_RE;
            afilter[5] = name.getText();
        }

        if (m_CategoryModel.getSelectedKey() == null) {
            afilter[6] = QBFCompareEnum.COMP_NONE;
            afilter[7] = null;
        } else {
            afilter[6] = QBFCompareEnum.COMP_EQUALS;
            afilter[7] = m_CategoryModel.getSelectedKey();
        }

        return afilter;
    }

    private static class MyListData extends javax.swing.AbstractListModel {

        private final java.util.List m_data;

        public MyListData(java.util.List data) {
            m_data = data;
        }

        @Override
        public Object getElementAt(int index) {
            return m_data.get(index);
        }

        @Override
        public int getSize() {
            return m_data.size();
        }
    }

}
