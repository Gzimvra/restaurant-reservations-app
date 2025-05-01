package com.example.boilerplateapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.Reservation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ReservationViewHolder> {
    private final List<Reservation> reservations;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public AccountAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each reservation item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view); // Return ViewHolder with the inflated view
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.dateTime.setText("Time: " + dateFormat.format(reservation.getReservationTime()));
        holder.guestCount.setText("Guests: " + reservation.getGuestCount());
        holder.notes.setText("Notes: " + (reservation.getNotes() == null ? "N/A" : reservation.getNotes()));
    }

    @Override
    public int getItemCount() {
        return reservations.size(); // Return the number of items in the list
    }

    // Make sure this is public or protected to be accessible
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView dateTime, guestCount, notes;

        // Constructor to initialize the views
        public ReservationViewHolder(View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.reservationDateTime);
            guestCount = itemView.findViewById(R.id.guestCount);
            notes = itemView.findViewById(R.id.notes);
        }
    }
}
