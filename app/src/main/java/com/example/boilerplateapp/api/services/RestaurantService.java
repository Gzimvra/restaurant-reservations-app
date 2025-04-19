package com.example.boilerplateapp.api.services;

import androidx.annotation.NonNull;

import com.example.boilerplateapp.api.models.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RestaurantService {

    public JSONArray getNearbyRestaurants(double latitude, double longitude) throws IOException, JSONException {
        String query = "[out:json];" +
                "node[amenity=restaurant](around:10000," + latitude + "," + longitude + ");" +
                "out;";
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        URL url = new URL("https://overpass-api.de/api/interpreter?data=" + encodedQuery);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        JSONObject jsonResponse = getJsonResponse(conn);
        return jsonResponse.getJSONArray("elements");
    }

    @NonNull
    private static JSONObject getJsonResponse(HttpURLConnection conn) throws IOException, JSONException {
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

        return new JSONObject(response.toString());
    }

    public List<Restaurant> parseRestaurantsFromJson(JSONArray restaurants) {
        List<Restaurant> restaurantList = new ArrayList<>();
        int topN = Math.min(restaurants.length(), 30);

        for (int i = 0; i < topN; i++) {
            JSONObject restaurantJson = restaurants.optJSONObject(i);
            if (restaurantJson == null) continue;

            long idLong = restaurantJson.optLong("id", -1);
            if (idLong == -1) continue; // Skip if invalid
            String id = String.valueOf(idLong);

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
                restaurantList.add(new Restaurant(id, name, lat, lon, city, street, houseNumber, cuisine, phone, website));
            }
        }

        return restaurantList;
    }

}
