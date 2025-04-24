package com.example.boilerplateapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.Reservation;
import com.example.boilerplateapp.api.models.Restaurant;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.api.services.ReservationService;

import android.view.View;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {
    private User user;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        user = (User) getIntent().getSerializableExtra("USER_OBJECT");
        restaurant = (Restaurant) getIntent().getSerializableExtra("RESTAURANT_OBJECT");

        if (user == null || restaurant == null) {
            Toast.makeText(this, "No user or restaurant data found", Toast.LENGTH_SHORT).show();
            return;
        }

        setupRestaurantInfo();
        setupReservationForm();
    }

    private void setupRestaurantInfo() {
        TextView nameText = findViewById(R.id.restaurantNameText);
        TextView cuisineText = findViewById(R.id.restaurantCuisineText);
        TextView addressText = findViewById(R.id.restaurantAddressText);
        TextView phoneText = findViewById(R.id.restaurantPhoneText);
        TextView websiteText = findViewById(R.id.restaurantWebsiteText);

        nameText.setText(restaurant.getName());

        String cuisine = restaurant.getCuisine() != null ? restaurant.getCuisine() : "N/A";
        cuisineText.setText("Cuisine: " + cuisine);

        String address = String.join(" ",
                restaurant.getStreet() != null ? restaurant.getStreet() : "",
                restaurant.getHouseNumber() != null ? restaurant.getHouseNumber() : "",
                restaurant.getCity() != null ? restaurant.getCity() : ""
        ).trim();

        addressText.setText("Address: " + (address.isEmpty() ? "N/A" : address));

        String phone = restaurant.getPhone() != null ? restaurant.getPhone() : "N/A";
        phoneText.setText("Phone: " + phone);

        String website = restaurant.getWebsite() != null ? restaurant.getWebsite() : "N/A";
        websiteText.setText("Website: " + website);
    }

    private void setupReservationForm() {
        EditText guestCountEditText = findViewById(R.id.editTextGuestCount);
        EditText notesEditText = findViewById(R.id.editTextNotes);
        EditText reservationTimeEditText = findViewById(R.id.editTextReservationTime);
        TextView errorTextView = findViewById(R.id.textViewError);
        ImageView qrCodeImageView = findViewById(R.id.imageViewQrCode);

        reservationTimeEditText.setOnClickListener(view -> showDateTimePicker(reservationTimeEditText));

        findViewById(R.id.buttonReserve).setOnClickListener(view -> {
            String guestCountStr = guestCountEditText.getText().toString().trim();
            String notes = notesEditText.getText().toString().trim();
            String reservationTimeStr = reservationTimeEditText.getText().toString().trim();

            // Validate
            if (guestCountStr.isEmpty() || reservationTimeStr.isEmpty()) {
                errorTextView.setText("Please fill in all the fields.");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            } else {
                errorTextView.setVisibility(View.GONE);
            }

            try {
                int guestCount = Integer.parseInt(guestCountStr);
                if (guestCount <= 0) {
                    errorTextView.setText("Guest count must be greater than 0.");
                    errorTextView.setVisibility(View.VISIBLE);
                    return;
                }

                Reservation reservation = getReservation(reservationTimeStr, guestCount, notes);

                new Thread(() -> {
                    ReservationService reservationService = new ReservationService();
                    String reservationId = reservationService.insertReservation(reservation);

                    runOnUiThread(() -> {
                        if (reservationId != null) {
                            reservation.setReservationId(reservationId); // Optional, for reuse
                            Toast.makeText(this, "Reservation saved successfully!", Toast.LENGTH_SHORT).show();

                            // Clear input fields after success
                            guestCountEditText.setText("");
                            notesEditText.setText("");
                            reservationTimeEditText.setText("");

                            Intent intent = new Intent(this, QRCodeActivity.class);
                            intent.putExtra("USER_OBJECT", user);
                            intent.putExtra("RESTAURANT_OBJECT", restaurant);
                            intent.putExtra("RESERVATION_OBJECT", reservation);
                            startActivity(intent);

                        } else {
                            errorTextView.setText("Failed to make reservation.");
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                    });
                }).start();

            } catch (NumberFormatException e) {
                errorTextView.setText("Invalid guest count.");
                errorTextView.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                errorTextView.setText("Invalid date format.");
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @NonNull
    private Reservation getReservation(String reservationTimeStr, int guestCount, String notes) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date reservationTime = sdf.parse(reservationTimeStr);

        // Create Reservation object
        // ReservationID and Created_At will be given default values in database
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getUserId());
        reservation.setRestaurantId(restaurant.getRestaurantId());
        reservation.setRestaurantLatitude(restaurant.getLatitude());
        reservation.setRestaurantLongitude(restaurant.getLongitude());
        reservation.setReservationTime(reservationTime);
        reservation.setGuestCount(guestCount);
        reservation.setNotes(notes);
        return reservation;
    }

    private void showDateTimePicker(EditText targetEditText) {
        final Calendar now = Calendar.getInstance();

        // Set minimum selectable date to tomorrow
        Calendar minDate = (Calendar) now.clone();
        minDate.add(Calendar.DAY_OF_MONTH, 1);

        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH);
        int currentDay = now.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    int hour = now.get(Calendar.HOUR_OF_DAY);
                    int minute = now.get(Calendar.MINUTE);

                    new TimePickerDialog(
                            this,
                            (view1, hourOfDay, minute1) -> {
                                String formatted = String.format(Locale.getDefault(),
                                        "%04d-%02d-%02d %02d:%02d",
                                        year, month + 1, dayOfMonth, hourOfDay, minute1);
                                targetEditText.setText(formatted);
                            },
                            hour, minute, true
                    ).show();
                },
                currentYear, currentMonth, currentDay
        );

        // Disable today and earlier
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }
}
