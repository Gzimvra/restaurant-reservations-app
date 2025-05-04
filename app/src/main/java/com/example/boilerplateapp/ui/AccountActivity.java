package com.example.boilerplateapp.ui;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.Reservation;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.api.services.ReservationService;
import com.example.boilerplateapp.ui.adapters.AccountAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccountActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private View mainContent;
    private User user;
    private RecyclerView recyclerView;
    private static final String TAG = "AccountActivity";
    private final ReservationService reservationService = new ReservationService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        progressBar = findViewById(R.id.progressBarHome);
        mainContent = findViewById(R.id.mainContent);
        recyclerView = findViewById(R.id.reservationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        user = (User) getIntent().getSerializableExtra("USER_OBJECT");
        if (user == null) {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
            return;
        }

        ((TextView) findViewById(R.id.accountUsername)).setText(user.getUsername());
        ((TextView) findViewById(R.id.accountEmail)).setText(user.getEmail());

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(AccountActivity.this)
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to log out? You will need to log in again to access your account.")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        // Clear session
                        getSharedPreferences("UserSession", MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        // Go back to login screen
                        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        fetchReservationHistory(user);
    }

    private void fetchReservationHistory(User user) {
        progressBar.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

        new Thread(() -> {
            List<Reservation> reservations = reservationService.getReservations(user);
            runOnUiThread(() -> {
                if (reservations != null) {
                    AccountAdapter adapter = new AccountAdapter(reservations, user);
                    recyclerView.setAdapter(adapter);
                    mainContent.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load reservations", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
