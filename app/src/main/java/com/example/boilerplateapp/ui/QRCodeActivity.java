package com.example.boilerplateapp.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.*;
import com.example.boilerplateapp.core.QRCodeHelper;

public class QRCodeActivity extends AppCompatActivity {
    private User user;
    private Restaurant restaurant;
    private Reservation reservation;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        user = (User) getIntent().getSerializableExtra("USER_OBJECT");
        restaurant = (Restaurant) getIntent().getSerializableExtra("RESTAURANT_OBJECT");
        reservation = (Reservation) getIntent().getSerializableExtra("RESERVATION_OBJECT");

        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        if (user == null || restaurant == null || reservation == null) {
            Toast.makeText(this, "No user, restaurant or reservation data found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate the QR code
        generateQRCode();
    }

    private void generateQRCode() {
        // Create QR content
        String qrContent =
                "ReservationID: " + reservation.getReservationId()
                        + "\nUserID: " + reservation.getUserId()
                        + "\nRestaurantID: " + reservation.getRestaurantId()
                        + "\nTime Scheduled: " + reservation.getReservationTime()
                        + "\nGuests: " + reservation.getGuestCount()
                        + "\nNotes: " + reservation.getNotes();

        // Generate QR code bitmap
        Bitmap qrBitmap = QRCodeHelper.generateQRCode(qrContent);

        // Set the QR code image to the ImageView
        if (qrBitmap != null) {
            qrCodeImageView.setImageBitmap(qrBitmap);
        } else {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
