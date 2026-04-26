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
