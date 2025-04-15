package com.example.boilerplateapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

/**
 * A utility class containing common validation methods and utilities for user input.
 * This includes email validation, password verification, and other user-related validations.
 */
public class ValidationHelper {

    /**
     * Validates the format of an email address using a regular expression.
     *
     * @param email the email address to be validated.
     * @return true if the email matches the pattern; false otherwise.
     */
    public static boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Verifies whether the entered password matches the stored hashed password.
     * Uses BCrypt for secure password verification.
     *
     * @param enteredPassword the password entered by the user.
     * @param storedHashedPassword the stored hashed password from the database.
     * @return true if the entered password matches the stored hashed password; false otherwise.
     */
    public static boolean verifyPassword(String enteredPassword, String storedHashedPassword) {
        return BCrypt.checkpw(enteredPassword, storedHashedPassword);
    }

    /**
     * Validates the format of a username. A valid username must contain only
     * alphanumeric characters and be at least 3 characters long.
     *
     * @param username the username to be validated.
     * @return true if the username matches the pattern; false otherwise.
     */
    public static boolean isValidUsername(String username) {
        String usernamePattern = "^[a-zA-Z0-9]{3,}$"; // Must be at least 3 characters long and alphanumeric
        Pattern pattern = Pattern.compile(usernamePattern);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    /**
     * Hashes the given password using BCrypt.
     *
     * @param password the password to be hashed.
     * @return the hashed password.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
