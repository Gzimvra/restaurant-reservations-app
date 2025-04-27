package com.example.boilerplateapp.ui;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.Restaurant;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.api.services.RestaurantService;
import com.example.boilerplateapp.core.LocationHelper;
import com.example.boilerplateapp.ui.adapters.RestaurantAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private View mainContent;
    private LocationHelper locationHelper;
    private User user;
    private final RestaurantService restaurantService = new RestaurantService();
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final String TAG = "HomeActivity"; // For logging
    private double latitude = 0.0, longitude = 0.0; // Declare as instance variables

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
        locationHelper.getCurrentLocation((lat, lon) -> {
            if (lat != null && lon != null) {
                latitude = lat;  // Set latitude once
                longitude = lon; // Set longitude once
                fetchTopRestaurants(latitude, longitude, "");  // Load all restaurants initially
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

        setupSearchBar();
    }

    private void setupSearchBar() {
        EditText searchEditText = findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().strip();

                // Debounced search (2 seconds delay)
                if (!query.isEmpty()) {
                    searchRunnable = () -> fetchTopRestaurants(latitude, longitude, query);
                    searchHandler.postDelayed(searchRunnable, 2000); // Only call after 2 seconds
                } else {
                    // If the query is empty, fetch all restaurants again
                    searchRunnable = () -> fetchTopRestaurants(latitude, longitude, "");
                    searchHandler.postDelayed(searchRunnable, 2000); // Only call after 2 seconds
                }
            }
        });
    }

    private void fetchTopRestaurants(double latitude, double longitude, String query) {
        // First, hide the main content and show the progress bar
        runOnUiThread(() -> {
            mainContent.setVisibility(View.GONE);  // Hide current content
            progressBar.setVisibility(View.VISIBLE);  // Show progress bar
        });

        new Thread(() -> {
            try {
                // Fetch all nearby restaurants initially
                JSONArray restaurants = restaurantService.getNearbyRestaurants(latitude, longitude, "");
                List<Restaurant> restaurantList = restaurantService.parseRestaurantsFromJson(restaurants);

                // Filter the list if the search query is not empty
                if (!query.isEmpty()) {
                    restaurantList = filterRestaurantsByName(restaurantList, query);
                }

                // Create a final local variable to be used in the lambda
                final List<Restaurant> finalRestaurantList = restaurantList;

                // Update the UI on the main thread
                runOnUiThread(() -> {
                    // Hide the progress bar and show the main content
                    progressBar.setVisibility(View.GONE);
                    mainContent.setVisibility(View.VISIBLE);
                    updateUIWithRestaurants(finalRestaurantList);  // Pass finalRestaurantList here
                });
            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    // If an error occurs, hide the progress bar and show an error message
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load restaurants", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private List<Restaurant> filterRestaurantsByName(List<Restaurant> restaurantList, String query) {
        // Filter the list of restaurants based on the query string
        List<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            if (restaurant.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(restaurant);
            }
        }
        return filteredList;
    }

    private void updateUIWithRestaurants(List<Restaurant> restaurantList) {
        if (restaurantList.isEmpty()) {
            Toast.makeText(this, "No restaurants found", Toast.LENGTH_SHORT).show();
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
