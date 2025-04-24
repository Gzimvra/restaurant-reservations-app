package com.example.boilerplateapp.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.*;

public class QRCodeActivity extends AppCompatActivity {
    private User user;
    private Restaurant restaurant;
    private Reservation reservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        user = (User) getIntent().getSerializableExtra("USER_OBJECT");
        restaurant = (Restaurant) getIntent().getSerializableExtra("RESTAURANT_OBJECT");
        reservation = (Reservation) getIntent().getSerializableExtra("RESERVATION_OBJECT");

        if (user == null || restaurant == null || reservation == null) {
            Toast.makeText(this, "No user, restaurant or reservation data found", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, reservation.getReservationId(), Toast.LENGTH_SHORT).show();
    }
}
