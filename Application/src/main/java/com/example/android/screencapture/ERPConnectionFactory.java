package com.example.android.screencapture;

import android.view.WindowManager;

import java.sql.*;

public class ERPConnectionFactory {
    private WindowManager mWindowManager;
    private Connection mConnection;
    private static String driver = "net.sourceforge.jtds.jdbc.Driver";

    public static Connection GetConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.18.77:1433/CITest;instance=SQLEXPRESS", "lacty", "wu0g3tp6");
    }
}
