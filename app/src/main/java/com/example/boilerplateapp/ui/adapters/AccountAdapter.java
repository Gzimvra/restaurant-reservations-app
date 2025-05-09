package com.example.boilerplateapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.api.services.ReservationService;
import com.example.boilerplateapp.ui.QRCodeActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ReservationViewHolder> {
    private final List<Reservation> reservations;
    private final User user;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public AccountAdapter(List<Reservation> reservations, User user) {
        this.reservations = reservations;
        this.user = user;
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

        holder.deleteButton.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Reservation")
                    .setMessage("Are you sure you want to delete this reservation? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        String reservationId = reservation.getReservationId();

                        // Run the deletion logic on a background thread
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            boolean deleted = new ReservationService().deleteReservation(reservationId);

                            // After background task, update the UI on the main thread
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (deleted) {
                                    reservations.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());
                                    notifyItemRangeChanged(holder.getAdapterPosition(), reservations.size());
                                } else {
                                    System.out.println("Failed to delete reservation");
                                }
                            });
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.qrcodeButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, QRCodeActivity.class);

            intent.putExtra("USER_OBJECT", user);
            intent.putExtra("RESERVATION_OBJECT", reservation);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size(); // Return the number of items in the list
    }

    // ViewHolder to hold the views for each reservation item
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView dateTime, guestCount, notes;
        Button deleteButton, qrcodeButton;

        // Constructor to initialize the views
        public ReservationViewHolder(View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.reservationDateTime);
            guestCount = itemView.findViewById(R.id.guestCount);
            notes = itemView.findViewById(R.id.notes);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            qrcodeButton = itemView.findViewById(R.id.qrcodeButton);
        }
    }
}
