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
package ke.kalc.beans;

import javax.swing.JDialog;
import ke.kalc.pos.forms.AppUser;
import ke.kalc.pos.forms.JRootFrame;

/**
 *
 * @author John
 */
public class JPasswordPanel extends JDialog {

    public static Object[] requestPassword(String userName) {
        JPasswordDialog pass = new JPasswordDialog();
        pass.buildPasswordRequest(userName);
        pass.pack();
        pass.setLocationRelativeTo(JRootFrame.PARENTFRAME);
        pass.setLocation(pass.getX(), pass.getY() - 200);
        pass.setVisible(true);
        return new Object[]{pass.getChoice(), pass.getPassword()};
    }

    public static Object[] changePassword(AppUser user) {
        JPasswordDialog pass = new JPasswordDialog();
        pass.buildPasswordChange(user);
        pass.pack();
        pass.setLocationRelativeTo(JRootFrame.PARENTFRAME);
        pass.setLocation(pass.getX(), pass.getY() - 200);
        pass.setVisible(true);
        return new Object[]{pass.getChoice(), pass.getPassword()};
    }

}
