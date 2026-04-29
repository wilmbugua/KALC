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


package ke.kalc.commons.dbmanager;

import ke.kalc.pos.forms.AppConfig;

public class DbUser {

    private String dbLibrary;
    private String dbClass;
    private String serverName;
    private String serverPort;
    private String databaseName;
    private String userName;
    private String userPassword;
    private String encryptedUserPassword;
    private String URL;

    public DbUser() {
        dbClass = AppConfig.getDatabaseProviderClass();
        dbLibrary = AppConfig.getDatabaseLibraryPath();
        serverName = AppConfig.getDatabaseServer();
        serverPort = AppConfig.getDatabasePort();
        databaseName = AppConfig.getDatabaseName();
        URL = AppConfig.getDatabaseURL();
        encryptedUserPassword = AppConfig.getDatabasePassword();
        userName = AppConfig.getDatabaseUser();
        userPassword = AppConfig.getClearDatabasePassword();
    }

    public DbUser(String serverName, String serverPort, String databaseName, String userName, String userPassword) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.databaseName = databaseName;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public void getUserDetails() {
        dbClass = AppConfig.getDatabaseProviderClass();
        dbLibrary = AppConfig.getDatabaseLibraryPath();
        serverName = AppConfig.getDatabaseServer();
        serverPort = AppConfig.getDatabasePort();
        databaseName = AppConfig.getDatabaseName();
        URL = AppConfig.getDatabaseURL();
        encryptedUserPassword = AppConfig.getDatabasePassword();
        userName = AppConfig.getDatabaseUser();
        userPassword = AppConfig.getClearDatabasePassword();
    }

    private String buildURL() {
        StringBuilder url;
        url = new StringBuilder();
        url.append("jdbc:mysql://");
        url.append(serverName);
        url.append(":");
        url.append(serverPort);
        url.append("/");
        url.append(databaseName);
        url.append("?zeroDateTimeBehavior=convert_To_NULL");
        return url.toString();
    }

    public void save() {
        AppConfig.setDatabaseServer(serverName);
        AppConfig.setDatabasePort(serverPort);
        AppConfig.setDatabaseName(databaseName);
        AppConfig.setDatabasePassword(userPassword);
        AppConfig.setDatabaseUser(userName);
    }

    public void displayUser() {
        System.out.println("SeverName      : " + serverName);
        System.out.println("Port           : " + serverPort);
        System.out.println("Database       : " + databaseName);
        System.out.println("UserName       : " + userName);
        System.out.println("Password       : " + userPassword);
        System.out.println("DriverClass    : " + dbClass);
        System.out.println("URL            : " + buildURL());
        System.out.println("**********************************************");
    }

    public String getURL() {
        URL = buildURL();
        return URL;
    }

    public String getDbLibrary() {
         String dirname = System.getProperty("user.dir") == null ? "./" : System.getProperty("user.dir");
        return dirname + dbLibrary;
    }

    public String getDbClass() {
        return dbClass;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getEncryptedUserPassword() {
        return encryptedUserPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

}
