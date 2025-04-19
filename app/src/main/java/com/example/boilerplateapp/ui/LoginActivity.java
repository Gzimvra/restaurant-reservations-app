package com.example.boilerplateapp.ui;

import android.content.Intent;
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
                        // Login successful, pass the user object to HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("USER_OBJECT", finalUser); // Pass the User object
                        startActivity(intent);
                        finish(); // Close LoginActivity
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
