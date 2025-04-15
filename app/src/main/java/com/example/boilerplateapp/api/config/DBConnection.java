package com.example.boilerplateapp.api.config;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String TAG = "DatabaseConnection";
    private static final String URL = "jdbc:mysql://ipv4.kosmidis.me:33066/gzymvragos22b_db1"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "gzymvragos22b";
    private static final String PASSWORD = "969a21a5";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // MySQL Connector/J v5.1.49
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "MySQL Driver init failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Log.d(TAG, "Connecting to MySQL...");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            Log.e(TAG, "Connection failed", e);
            throw e;
        }
    }
}
