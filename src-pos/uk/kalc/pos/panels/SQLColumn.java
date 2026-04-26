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

import java.util.Enumeration;
import javax.swing.tree.TreeNode;


public class SQLColumn implements TreeNode {
    
    private SQLTable m_table;
    private String m_sName;
    
    /** Creates a new instance of SQLColumn
     * @param t
     * @param name */
    public SQLColumn(SQLTable t, String name) {
        m_table = t;
        m_sName = name;
    }
    @Override
    public String toString() {
        return m_sName;
    }
    
    @Override
    public Enumeration children(){
        return null;
    }
    @Override
    public boolean getAllowsChildren() {
        return false;
    }
    @Override
    public TreeNode getChildAt(int childIndex) {
        throw new ArrayIndexOutOfBoundsException();
    }
    @Override
    public int getChildCount() {
        return 0;
    }
    @Override
    public int getIndex(TreeNode node){
        throw new ArrayIndexOutOfBoundsException();
    }
    @Override
    public TreeNode getParent() {
        return m_table;
    }
    @Override
    public boolean isLeaf() {
        return true;
    }      
}
