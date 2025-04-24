package com.example.boilerplateapp.api.services;

import android.util.Log;

import com.example.boilerplateapp.api.config.DBConnection;
import com.example.boilerplateapp.api.models.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service for handling reservation database operations.
 */
public class ReservationService {
    private static final String TAG = "ReservationService";

    /**
     * Inserts a reservation into the RESERVATIONS table and returns its ID.
     *
     * @param reservation the reservation object containing the data to be inserted.
     * @return the reservation_id (UUID) if successful, null otherwise.
     */
    public String insertReservation(Reservation reservation) {
        String insertQuery = "INSERT INTO RESERVATIONS " +
                "(USER_ID, RESTAURANT_ID, RESTAURANT_LAT, RESTAURANT_LONG, RESERVATION_TIME, GUEST_COUNT, NOTES) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, reservation.getUserId());
            stmt.setString(2, reservation.getRestaurantId());
            stmt.setDouble(3, reservation.getRestaurantLatitude());
            stmt.setDouble(4, reservation.getRestaurantLongitude());
            stmt.setTimestamp(5, new java.sql.Timestamp(reservation.getReservationTime().getTime()));
            stmt.setInt(6, reservation.getGuestCount());
            stmt.setString(7, reservation.getNotes().isEmpty() ? null : reservation.getNotes());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                String selectQuery = "SELECT RESERVATION_ID FROM RESERVATIONS " +
                        "WHERE USER_ID = ? AND RESTAURANT_ID = ? AND RESERVATION_TIME = ? " +
                        "ORDER BY CREATED_AT DESC LIMIT 1";

                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                    selectStmt.setString(1, reservation.getUserId());
                    selectStmt.setString(2, reservation.getRestaurantId());
                    selectStmt.setTimestamp(3, new java.sql.Timestamp(reservation.getReservationTime().getTime()));

                    ResultSet rs = selectStmt.executeQuery();
                    if (rs.next()) {
                        return rs.getString("RESERVATION_ID");
                    }
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Error inserting reservation", e);
        }

        return null;
    }
}
