package com.example.boilerplateapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.boilerplateapp.R;

public class RegisterActivity extends AppCompatActivity {
    EditText editTextUsername, editTextEmail, editTextPassword;
    Button buttonRegister;
    TextView textViewError, textViewLogin;

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

        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText("Username, email and password are required");
                return;
            }

            if (username.equals("user") || email.equals("example@gmail.com")) {
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText("User already exists!");
            } else {
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                // Redirect to HomeActivity
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                finish();
            }
        });

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });



//        inputName = findViewById(R.id.editTextName);
//        Button submitButton = findViewById(R.id.btnSubmit);
//
//        submitButton.setOnClickListener(v -> {
//            String name = inputName.getText().toString();
//            Intent intent = new Intent(RegisterActivity.this, DetailActivity.class);
//            intent.putExtra("USER_NAME", name);
//            startActivity(intent);
//        });
    }
}
