package com.example.boilerplateapp.ui;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.core.LocationHelper;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        User user = (User) getIntent().getSerializableExtra("USER_OBJECT");

        if (user == null) {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
            return;
        }

        locationHelper = new LocationHelper(this);
        locationHelper.getCurrentLocation((latitude, longitude) -> {
            if (latitude != null && longitude != null) {
                Toast.makeText(this, "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_SHORT).show();


                // Do whatever we need with latitude and longitude here




            } else {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode, grantResults);
    }
}
