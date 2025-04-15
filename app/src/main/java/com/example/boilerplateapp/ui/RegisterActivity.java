package com.example.boilerplateapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.api.services.UserService;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    EditText editTextUsername, editTextEmail, editTextPassword;
    Button buttonRegister;
    TextView textViewError, textViewLogin;
    ProgressBar progressBarRegister;

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsernameRegister);
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewError = findViewById(R.id.textViewErrorRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        progressBarRegister = findViewById(R.id.progressBarRegister);

        userService = new UserService();

        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText("Username, email, and password are required");
                return;
            }

            // Start loading
            progressBarRegister.setVisibility(View.VISIBLE);
            buttonRegister.setEnabled(false);
            textViewError.setVisibility(View.GONE);

            new Thread(() -> {
                String userId = null;
                User user = new User();
                try {
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password); // Consider hashing before this if needed

                    userId = userService.register(user);

                    // If registration successful, update the user object
                    if (userId != null) {
                        user.setUserId(userId); // Set the new user ID
                        user.setPassword(null);  // Set password to null for security reasons
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error during registration", e);
                }

                String finalUserId = userId;
                runOnUiThread(() -> {
                    // Stop loading
                    progressBarRegister.setVisibility(View.GONE);
                    buttonRegister.setEnabled(true);

                    if (finalUserId != null) {
                        // Registration successful, create an intent and pass the User object
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.putExtra("USER_OBJECT", user);  // Pass the user object to HomeActivity
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish(); // Close the RegisterActivity
                    } else {
                        textViewError.setVisibility(View.VISIBLE);
                        textViewError.setText("User already exists or registration failed");
                    }
                });
            }).start();
        });

        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }
}
