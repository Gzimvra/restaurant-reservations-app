package com.example.boilerplateapp.api.models;

import java.io.Serializable;

public class User implements Serializable {
    private String user_id;
    private String username;
    private String email;
    private String password;

    public User() {}

    // Getters
    public String getUserId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
