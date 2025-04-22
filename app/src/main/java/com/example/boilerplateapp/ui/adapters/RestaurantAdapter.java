package com.example.boilerplateapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.Restaurant;
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.ui.ReservationActivity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final List<Restaurant> restaurantList;
    private final User user;

    public RestaurantAdapter(List<Restaurant> restaurantList, User user) {
        this.restaurantList = restaurantList;
        this.user = user;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, cuisineTextView;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            addressTextView = view.findViewById(R.id.addressTextView);
            cuisineTextView = view.findViewById(R.id.cuisineTextView);
        }
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.nameTextView.setText(restaurant.getName());

        String addressText = Stream.of(restaurant.getStreet(), restaurant.getHouseNumber(), restaurant.getCity())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ", "", ""));

        if (addressText.isEmpty()) {
            addressText = "No Address Info";
        }

        holder.addressTextView.setText(addressText);
        holder.cuisineTextView.setText(restaurant.getCuisine() != null ? restaurant.getCuisine() : "No cuisine info");

        // Handle click on the whole itemView (the card)
        holder.itemView.setOnClickListener(v -> {
            System.out.println("Restaurant ID: " + restaurant.getRestaurantId());
            System.out.println("Clicked by user: " + (user != null ? user.getUsername() : "Unknown"));

            // Create intent and pass both User and Restaurant
            android.content.Intent intent = new android.content.Intent(v.getContext(), ReservationActivity.class);
            intent.putExtra("USER_OBJECT", user);
            intent.putExtra("RESTAURANT_OBJECT", restaurant);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }
}
