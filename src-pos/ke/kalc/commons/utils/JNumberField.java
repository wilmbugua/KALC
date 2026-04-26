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
package ke.kalc.commons.utils;

import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import org.apache.commons.lang.StringUtils;
import static ke.kalc.globals.SystemProperty.USERCOUNTRY;
import static ke.kalc.globals.SystemProperty.USERLANGUAGE;

public class JNumberField extends JTextField {

    private Matcher matcher;
    private Pattern regEx;
    private int decimalPlaces = 4;
    private Boolean decimal = true;
    private StringBuilder dc;
    private Boolean allowNegative = true;

    

    private Locale currentLocale = new Locale(USERLANGUAGE, USERCOUNTRY);
    private DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(currentLocale);
    //  private char sep = df.getDecimalFormatSymbols().getDecimalSeparator();
    private char sep = new DecimalFormatSymbols(currentLocale).getDecimalSeparator();

    FocusListener formatFocusListener;

    public void allowNegativeNumbers(Boolean allowNegative) {
        this.allowNegative = allowNegative;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        checkKeyTyped(this.decimal, decimalPlaces);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(currentLocale));
        setText(df.format(Double.valueOf(getText())));
    }

    public void setDecimal(Boolean decimal) {
        this.decimal = decimal;
        checkKeyTyped(decimal, this.decimalPlaces);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(currentLocale));
        setText(df.format(Double.valueOf(getText())));
    }

    public int getDecimalPlaces() {
        return this.decimalPlaces;
    }

    public Boolean isDecimalNumber() {
        return this.decimal;
    }

    public Boolean allowNegatives() {
        return this.allowNegative;
    }

    public Double getValue() {
        if (getText().isEmpty()) {
            return Double.valueOf(0.0D);
        }

        return Double.valueOf(getText());
    }

    public JNumberField() {
        JNumberField(true);
    }

    public JNumberField(Boolean allowText) {
        JNumberField(allowText);
    }

    private void JNumberField(Boolean numberOnly) {
        if (numberOnly) {
            this.formatFocusListener = new FocusListener() {
                public void focusGained(FocusEvent focusEvent) {
                }

                public void focusLost(FocusEvent focusEvent) {
                    if (getText().isEmpty()) {
                        setText((new DecimalFormat(dc.toString())).format(0.0D));
                    }
                }
            };
            checkKeyTyped(true, this.decimalPlaces);
            setHorizontalAlignment(4);
            setMargin(new Insets(0, 0, 0, 3));
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(currentLocale));
            setText(df.format(0.0D));
        }
    }

    public JNumberField(Boolean decimal, int decimalPlaces) {
        this.formatFocusListener = new FocusListener() {
            public void focusLost(FocusEvent focusEvent) {
                if (JNumberField.this.getText().isEmpty()) {
                    JNumberField.this.setText((new DecimalFormat(JNumberField.this.dc.toString())).format(0.0D));
                }
            }

            public void focusGained(FocusEvent focusEvent) {
            }
        };
        this.decimal = decimal;
        this.decimalPlaces = decimalPlaces;
        checkKeyTyped(decimal, decimalPlaces);
        setHorizontalAlignment(4);
        setMargin(new Insets(0, 0, 0, 3));
        setText(df.format(0.0D));
    }

    private void checkKeyTyped(Boolean decimal, int decimalPlaces) {
        this.dc = new StringBuilder("#,##0");
        //this.dc = new StringBuilder("###0");
        if ((decimal & ((decimalPlaces > 0) ? 1 : 0) != 0)) {
            this.dc.append(sep);
            this.dc.append(StringUtils.repeat("0", decimalPlaces));
        }

        addFocusListener(this.formatFocusListener);

        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!JNumberField.this.allowNumeric(e.getKeyChar(), decimal, decimalPlaces).booleanValue()) {
                    e.consume();
                }
            }
        });
    }

    private Boolean allowNumeric(char typed, Boolean decimal, int decimalPlaces) {
        StringBuilder regex = new StringBuilder("^");
        if (this.allowNegative.booleanValue()) {
            regex.append("[-]?");
        }
        regex.append("[0-9]*");
        if ((decimal.booleanValue() & ((decimalPlaces > 0) ? 1 : 0) != 0)) {
            regex.append("\\");
            regex.append(sep);
            regex.append("?");
            regex.append("\\d{0,");
            regex.append(decimalPlaces);
            regex.append("}");
        }
        this.regEx = Pattern.compile(regex.toString());

        StringBuilder caretCheck = new StringBuilder(getText());
        if (getSelectedText() == null) {
            caretCheck.insert(getCaretPosition(), typed);
        } else {
            caretCheck.replace(getSelectionStart(), getSelectionEnd(), String.valueOf(typed));
        }
        this.matcher = this.regEx.matcher(caretCheck.toString());
        return Boolean.valueOf(this.matcher.matches());
    }

}
