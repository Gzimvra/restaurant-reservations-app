package com.example.boilerplateapp.core;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationHelper {
    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationResultCallback callback;

    public interface LocationResultCallback {
        void onLocationResult(Double latitude, Double longitude);
    }

    public LocationHelper(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void getCurrentLocation(LocationResultCallback callback) {
        this.callback = callback;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
        }
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            if (callback != null) callback.onLocationResult(null, null);
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (callback != null) {
                        if (location != null) {
                            callback.onLocationResult(location.getLatitude(), location.getLongitude());
                        } else {
                            callback.onLocationResult(null, null);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onLocationResult(null, null);
                });
    }
}
