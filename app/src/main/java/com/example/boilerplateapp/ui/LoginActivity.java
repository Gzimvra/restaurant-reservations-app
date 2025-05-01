package com.example.boilerplateapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.api.services.UserService;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    EditText editTextUsername, editTextPassword;
    Button buttonLogin;
    TextView textViewError, textViewRegister;
    ProgressBar progressBarLogin;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedUserId = sharedPref.getString("USER_ID", null);

        if (savedUserId != null) {
            // Reconstruct User object from saved session
            String savedUsername = sharedPref.getString("USERNAME", "");
            String savedEmail = sharedPref.getString("EMAIL", "");

            User savedUser = new User();
            savedUser.setUserId(savedUserId);
            savedUser.setUsername(savedUsername);
            savedUser.setEmail(savedEmail);

            // Skip login, go to HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("USER_OBJECT", savedUser);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsernameLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewError = findViewById(R.id.textViewErrorLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        // Initialize UserService
        userService = new UserService();

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText("Username and password are required");
                return;
            }

            // Start loading
            progressBarLogin.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);
            textViewError.setVisibility(View.GONE); // Hide previous errors

            new Thread(() -> {
                User user = null;
                try {
                    user = userService.login(username, password);
                } catch (Exception e) {
                    Log.e(TAG, "Error during login", e);
                }

                User finalUser = user;
                runOnUiThread(() -> {
                    // Stop loading
                    progressBarLogin.setVisibility(View.GONE);
                    buttonLogin.setEnabled(true);

                    if (finalUser != null) {
                        // Save session
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("USER_ID", finalUser.getUserId());
                        editor.putString("USERNAME", finalUser.getUsername());
                        editor.putString("EMAIL", finalUser.getEmail());
                        editor.apply();

                        // Go to HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("USER_OBJECT", finalUser);
                        startActivity(intent);
                        finish();
                    } else {
                        textViewError.setVisibility(View.VISIBLE);
                        textViewError.setText("Invalid username or password");
                        editTextPassword.setText(""); // Clear password field
                    }
                });
            }).start();
        });

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
