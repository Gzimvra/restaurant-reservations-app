package com.example.boilerplateapp.api.services;

import android.util.Log;

import com.example.boilerplateapp.api.config.DBConnection;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.utils.ValidationHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

/**
 * A service class that handles user-related operations such as login and registration.
 */
public class UserService {
    private static final String TAG = "UserService";

    /**
     * Attempts to log in a user by verifying their credentials.
     *
     * @param username the username entered by the user.
     * @param password the password entered by the user.
     * @return the User object if login is successful, null otherwise.
     */
    public User login(String username, String password) {
        // Validate the username format
        if (!ValidationHelper.isValidUsername(username)) {
            Log.e(TAG, "Invalid username format");
            return null;
        }

        // Check if user exists and retrieve the stored hashed password
        String storedHashedPassword = getStoredHashedPassword(username);

        if (storedHashedPassword == null) {
            Log.e(TAG, "User not found");
            return null;
        }

        // Verify if the entered password matches the stored hashed password
        if (ValidationHelper.verifyPassword(password, storedHashedPassword)) {
            // Build and return the User object if login is successful
            return buildUserFromDatabase(username);
        } else {
            Log.e(TAG, "Invalid password");
            return null;
        }
    }

    /**
     * Retrieves the stored hashed password from the database for the given username.
     *
     * @param username the username whose password is to be retrieved.
     * @return the hashed password if found, or null if the user does not exist.
     */
    private String getStoredHashedPassword(String username) {
        String query = "SELECT PASSWORD FROM USERS WHERE USERNAME = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("PASSWORD");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error fetching password", e);
        }
        return null; // User not found
    }

    /**
     * Creates a User object by fetching the user's details from the database.
     *
     * @param username the username of the user to be retrieved.
     * @return the User object if found, null otherwise.
     */
    private User buildUserFromDatabase(String username) {
        String query = "SELECT * FROM USERS WHERE USERNAME = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPassword(null); // Don't return the password, it's not needed anymore
                return user;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error building user", e);
        }
        return null; // User not found or error occurred
    }

    /**
     * Registers a new user by inserting their information into the database.
     * This includes validating the username and email, hashing the password, and storing the data.
     *
     * @param user the User object containing the user's registration details.
     * @return the USER_ID if the user is successfully registered, null otherwise.
     */
    public String register(User user) {
        // Validate the username and email format
        if (!ValidationHelper.isValidUsername(user.getUsername())) {
            Log.e(TAG, "Invalid username format");
            return null;
        }

        if (!ValidationHelper.isValidEmail(user.getEmail())) {
            Log.e(TAG, "Invalid email format");
            return null;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Check if the user already exists
            if (userExists(user)) {
                return null;
            }

            // 2. Hash the password using bcrypt (moved to ValidationHelper)
            String hashedPassword = ValidationHelper.hashPassword(user.getPassword());

            // 3. Insert new user with hashed password into the database
            if (!insertNewUser(user, hashedPassword)) {
                return null;
            }

            // 4. Retrieve and return the generated USER_ID
            return getUserId(user.getUsername());

        } catch (SQLException e) {
            Log.e(TAG, "Error during registration", e);
        }

        return null;
    }

    /**
     * Checks if a user already exists in the database by username or email.
     *
     * @param user the User object containing the registration details.
     * @return true if the user already exists; false otherwise.
     */
    private boolean userExists(User user) {
        String checkQuery = "SELECT USER_ID FROM USERS WHERE USERNAME = ? OR EMAIL = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setString(1, user.getUsername());
            checkStmt.setString(2, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                Log.e(TAG, "User with this username or email already exists");
                return true;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error checking if user exists", e);
        }
        return false;
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user the User object containing the registration details.
     * @param hashedPassword the hashed password to be stored.
     * @return true if the user was successfully inserted; false otherwise.
     */
    private boolean insertNewUser(User user, String hashedPassword) {
        String insertQuery = "INSERT INTO USERS (USERNAME, EMAIL, PASSWORD) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            insertStmt.setString(1, user.getUsername());
            insertStmt.setString(2, user.getEmail());
            insertStmt.setString(3, hashedPassword);

            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows == 0) {
                Log.e(TAG, "Creating user failed, no rows affected.");
                return false;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting new user", e);
            return false;
        }
        return true;
    }

    /**
     * Retrieves the generated USER_ID for the given username.
     *
     * @param username the username of the newly registered user.
     * @return the USER_ID if found; null otherwise.
     */
    private String getUserId(String username) {
        String getIdQuery = "SELECT USER_ID FROM USERS WHERE USERNAME = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement idStmt = conn.prepareStatement(getIdQuery)) {

            idStmt.setString(1, username);
            ResultSet rs = idStmt.executeQuery();
            if (rs.next()) {
                return rs.getString("USER_ID");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving USER_ID", e);
        }
        return null;
    }
}
