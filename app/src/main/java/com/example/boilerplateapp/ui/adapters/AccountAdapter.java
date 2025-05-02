package com.example.boilerplateapp.ui.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boilerplateapp.R;
import com.example.boilerplateapp.api.models.Reservation;
import com.example.boilerplateapp.api.services.ReservationService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        holder.editButton.setOnClickListener(v -> System.out.println("EDIT CLICKED"));

        holder.deleteButton.setOnClickListener(v -> {
            String reservationId = reservation.getReservationId();

            // Run the deletion logic on a background thread
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                boolean deleted = new ReservationService().deleteReservation(reservationId);

                // After background task, update the UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (deleted) {
                        // If deletion was successful, update the UI
                        reservations.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, reservations.size()); // Keeps positions in sync
                    } else {
                        System.out.println("Failed to delete reservation");
                    }
                });
            });
        });

        holder.qrcodeButton.setOnClickListener(v -> System.out.println("QRCODE CLICKED"));
    }

    @Override
    public int getItemCount() {
        return reservations.size(); // Return the number of items in the list
    }

    // ViewHolder to hold the views for each reservation item
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView dateTime, guestCount, notes;
        Button editButton, deleteButton, qrcodeButton;

        // Constructor to initialize the views
        public ReservationViewHolder(View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.reservationDateTime);
            guestCount = itemView.findViewById(R.id.guestCount);
            notes = itemView.findViewById(R.id.notes);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            qrcodeButton = itemView.findViewById(R.id.qrcodeButton);
        }
    }
}
