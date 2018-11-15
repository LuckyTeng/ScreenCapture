package com.example.android.screencapture;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ERPConnectionFactory {
    private static final String driver = "net.sourceforge.jtds.jdbc.Driver";

    public static Connection GetConnection() throws SQLException {
        try {
            Class.forName(driver).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.18.77;instance=SQLEXPRESS;DatabaseName=CITEST;TDS=8.0", "lacty", "wu0g3tp6");
    }
}
