package com.example.boilerplateapp.ui;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.Restaurant;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.api.services.RestaurantService;
import com.example.boilerplateapp.core.LocationHelper;
import com.example.boilerplateapp.ui.adapters.RestaurantAdapter;

import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private View mainContent;
    private LocationHelper locationHelper;
    private User user;
    private final RestaurantService restaurantService = new RestaurantService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar = findViewById(R.id.progressBarHome);
        mainContent = findViewById(R.id.mainContent);

        progressBar.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

        user = (User) getIntent().getSerializableExtra("USER_OBJECT");

        if (user == null) {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
            return;
        }

        locationHelper = new LocationHelper(this);
        locationHelper.getCurrentLocation((latitude, longitude) -> {
            if (latitude != null && longitude != null) {
                fetchTopRestaurants(latitude, longitude);
            } else {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
            }
        });

        // NAVIGATION MENU
        findViewById(R.id.tabHome).setOnClickListener(v -> {
            Toast.makeText(this, "Home tab clicked", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.tabAccount).setOnClickListener(v -> {
            Toast.makeText(this, "Account tab clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchTopRestaurants(double latitude, double longitude) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        new Thread(() -> {
            try {
                JSONArray restaurants = restaurantService.getNearbyRestaurants(latitude, longitude);
                List<Restaurant> restaurantList = restaurantService.parseRestaurantsFromJson(restaurants);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    mainContent.setVisibility(View.VISIBLE);
                    updateUIWithRestaurants(restaurantList);
                });
            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load restaurants", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void updateUIWithRestaurants(List<Restaurant> restaurantList) {
        if (restaurantList.isEmpty()) {
            Toast.makeText(this, "No restaurants found nearby", Toast.LENGTH_SHORT).show();
        } else {
            RecyclerView recyclerView = findViewById(R.id.restaurantRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            RestaurantAdapter adapter = new RestaurantAdapter(restaurantList, user);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode, grantResults);
    }
}
