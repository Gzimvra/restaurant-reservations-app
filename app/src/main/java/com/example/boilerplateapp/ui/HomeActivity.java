package com.example.boilerplateapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.User;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrieve the User object passed from the LoginActivity
        User user = (User) getIntent().getSerializableExtra("USER_OBJECT");

        if (user != null) {
            // Create a Toast message with the user's data (for example, username and email)
            String userInfo = "\nUserID: " + user.getUserId() + "Username: " + user.getUsername() + "\nEmail: " + user.getEmail();
            Toast.makeText(HomeActivity.this, userInfo, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(HomeActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
        }
    }
}
