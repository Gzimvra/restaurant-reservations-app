package com.example.boilerplateapp.ui;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.User;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private ProgressBar progressBar;
    private View mainContent;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        progressBar = findViewById(R.id.progressBarHome);
        mainContent = findViewById(R.id.mainContent);

        progressBar.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

        // Get the User object from the intent
        user = (User) getIntent().getSerializableExtra("USER_OBJECT");

        if (user == null) {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find the TextView by ID
        TextView accountUsernameInput = findViewById(R.id.accountUsername);
        TextView accountEmailInput = findViewById(R.id.accountEmail);

        // Set the text to "Hello, <username>"
        accountUsernameInput.setText(user.getUsername());
        accountEmailInput.setText(user.getEmail());
    }
}
