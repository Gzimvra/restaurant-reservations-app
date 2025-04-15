package com.example.boilerplateapp.api.services;

import android.util.Log;

import com.example.boilerplateapp.api.config.DBConnection;
import com.example.boilerplateapp.api.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private static final String TAG = "UserService";

    // Login user (plain password, consider hashing later)
    public User login(String username, String password) {
        String query = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPassword(null);
                return user;
            }

        } catch (SQLException e) {
            Log.e(TAG, "Error during login", e);
        }

        return null;
    }

    public String register(User user) {
        String checkQuery = "SELECT USER_ID FROM USERS WHERE USERNAME = ? OR EMAIL = ? LIMIT 1";
        String insertQuery = "INSERT INTO USERS (USERNAME, EMAIL, PASSWORD) VALUES (?, ?, ?)";
        String getIdQuery = "SELECT USER_ID FROM USERS WHERE USERNAME = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Check if user exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, user.getUsername());
                checkStmt.setString(2, user.getEmail());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // User already exists
                    return null;
                }
            }

            // 2. Insert new user
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, user.getUsername());
                insertStmt.setString(2, user.getEmail());
                insertStmt.setString(3, user.getPassword()); // Consider hashing

                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
            }

            // 3. Retrieve the generated UUID
            try (PreparedStatement idStmt = conn.prepareStatement(getIdQuery)) {
                idStmt.setString(1, user.getUsername());
                ResultSet rs = idStmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("USER_ID");
                } else {
                    throw new SQLException("Failed to retrieve user_id after insertion.");
                }
            }

        } catch (SQLException e) {
            Log.e("UserDAO", "Error during registration", e);
        }

        return null;
    }
}
