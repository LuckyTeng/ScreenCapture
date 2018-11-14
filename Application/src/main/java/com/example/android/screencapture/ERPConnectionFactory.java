package com.example.android.screencapture;

import java.sql.*;

public class ERPConnectionFactory {
    private static String driver = "net.sourceforge.jtds.jdbc.Driver";

    public static Connection GetConnection() throws ClassNotFoundException, SQLException {
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        return DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.18.77:1523;instance=SQLEXPRESS;DatabaseName=master", "lacty", "wu0g3tp6");
    }
}
