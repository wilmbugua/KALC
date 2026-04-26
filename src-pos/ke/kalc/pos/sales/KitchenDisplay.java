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


package ke.kalc.pos.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import ke.kalc.data.loader.Session;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.datalogic.DataLogicSystem;


public class KitchenDisplay {
    private Session s;
    private Connection con;  
    private Statement stmt;
    private PreparedStatement pstmt;
    private String SQL;
    private ResultSet rs;
    private AppView m_App;

    /**
     *
     */
    protected DataLogicSystem dlSystem;

    /**
     *
     * @param oApp
     */
    public KitchenDisplay(AppView oApp) {
        m_App=oApp;
                                    
//get database connection details        
       try{
            s=m_App.getSession();
            con=s.getConnection();                      
        }
        catch (Exception e){
        }   
    }

    /**
     *
     * @param ID
     * @param table
     * @param pickupID
     * @param product
     * @param multiply
     * @param attributes
     */
    public void addRecord(String ID, String table, String pickupID, String product, String multiply, String attributes){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
     
     
     try{
            SQL = "insert into kitchendisplay (id, ordertime, place, pickupid, product, multiply, attributes) values (?, ?, ?, ?, ?, ?, ?) "; 
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,ID);            
            pstmt.setString(2,dateFormat.format(date)); 
            pstmt.setString(3,table);
            pstmt.setString(4,pickupID);  
            pstmt.setString(5,product);  
            pstmt.setString(6,multiply);
            pstmt.setString(7,attributes);            
            pstmt.executeUpdate();
        }catch(Exception e){
            }
     
 }       

 
        
}
