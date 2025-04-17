package com.example.boilerplateapp.ui;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.Restaurant;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.core.LocationHelper;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
                fetchTopRestaurants(latitude, longitude);
            } else {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTopRestaurants(double latitude, double longitude) {
        new Thread(() -> {
            try {
                JSONArray restaurants = getNearbyRestaurants(latitude, longitude);

                runOnUiThread(() -> {
                    if (restaurants.length() == 0) {
                        Toast.makeText(this, "No restaurants found nearby", Toast.LENGTH_SHORT).show();
                    } else {
                        List<Restaurant> restaurantList = new ArrayList<>();

                        int topN = Math.min(restaurants.length(), 30);
                        for (int i = 0; i < topN; i++) {
                            JSONObject restaurantJson = restaurants.optJSONObject(i);
                            if (restaurantJson != null) {

                                double lat = restaurantJson.optDouble("lat");
                                double lon = restaurantJson.optDouble("lon");

                                JSONObject tags = restaurantJson.optJSONObject("tags");
                                if (tags == null) continue;

                                String name = tags.optString("name", null);
                                String city = tags.optString("addr:city", null);
                                String street = tags.optString("addr:street", null);
                                String houseNumber = tags.optString("addr:housenumber", null);
                                String cuisine = tags.optString("cuisine", null);
                                String phone = tags.optString("phone", null);
                                String website = tags.optString("website", null);

                                if (name != null) {
                                    Restaurant restaurant = new Restaurant(name, lat, lon, city, street, houseNumber, cuisine, phone, website);
                                    restaurantList.add(restaurant);
                                }
                            }
                        }


                        // TODO: Remove later, only for demonstration purposes
                        for (Restaurant r : restaurantList) {
                            System.out.println("Restaurant:️ " + r.getName() + " Latitude " + r.getLatitude() + " Longitude: " + r.getLongitude());
                        }



                        // TODO: Pass restaurantList to UI (RecyclerView, Map, etc.)


                    }
                });
            } catch (IOException | JSONException e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to load restaurants", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @NonNull
    private JSONArray getNearbyRestaurants(double latitude, double longitude) throws IOException, JSONException {
        // Constructing Overpass API query for restaurants
        String query = "[out:json];" +
                "node[amenity=restaurant](around:10000," + latitude + "," + longitude + ");" +
                "out;";

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        URL url = new URL("https://overpass-api.de/api/interpreter?data=" + encodedQuery);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Check response code before proceeding
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // Log the full response for debugging
        String fullResponse = response.toString();
        System.out.println("Full Overpass API Response: " + fullResponse); // Logging for debugging

        // Parse the response
        JSONObject jsonResponse = new JSONObject(fullResponse);
        return jsonResponse.getJSONArray("elements");  // Extract the "elements" array
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode, grantResults);
    }
}
