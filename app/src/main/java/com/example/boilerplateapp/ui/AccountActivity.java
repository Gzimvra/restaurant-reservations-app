package com.example.boilerplateapp.ui;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.User;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity"; // For logging
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Get the User object from the intent
        user = (User) getIntent().getSerializableExtra("USER_OBJECT");

        if (user == null) {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find the TextView by ID
        TextView textViewHelloWorld = findViewById(R.id.textViewHelloWorld);

        // Set the text to "Hello, <username>"
        textViewHelloWorld.setText("Hello, " + user.getUsername());
    }
}
