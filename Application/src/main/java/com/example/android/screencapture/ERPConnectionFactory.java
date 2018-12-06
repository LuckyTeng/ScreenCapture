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

//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8)
//        {
//            // starting from SDK 9, NETWORK operation is not allow in main thread
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//        return DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.18.77;instance=SQLEXPRESS;DatabaseName=CITEST;TDS=8.0", "lacty", "wu0g3tp6");
        return DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.1;DatabaseName=LIY_ERP;TDS=8.0", "lacty", "wu0g3tp6");

    }
}
