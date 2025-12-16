package com.hotelapp.services;

import com.hotelapp.dao.CustomerDAO;
import com.hotelapp.dao.ReservationDAO;
import com.hotelapp.dao.RoomDAO;
import com.hotelapp.models.Customer;
import com.hotelapp.models.Reservation;
import com.hotelapp.utils.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    /**
     * Books a reservation transactionally: creates customer (if new) and reservation,
     * ensures availability.
     * Returns reservationId (>0) on success, -1 on failure.
     */
    public int bookReservation(Customer customer, int roomId, LocalDate checkIn, LocalDate checkOut, double total) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);

                // Create or find customer
                int customerId = customerDAO.findOrCreate(customer, conn);

                // Check availability (simple SQL in ReservationDAO) - use transaction connection
                List<Reservation> conflicts = reservationDAO.findReservationsBetween(checkIn, checkOut, conn);
                boolean conflict = conflicts.stream().anyMatch(r -> r.getRoomId() == roomId);
                if (conflict) {
                    conn.rollback();
                    throw new Exception("Room is not available for selected dates.");
                }

                // Insert reservation using transaction connection
                int resId = reservationDAO.insertReservation(customerId, roomId, checkIn, checkOut, "CONFIRMED", total, conn);

                // Update room status to BOOKED when reservation is confirmed - use transaction connection
                roomDAO.updateStatus(roomId, "BOOKED", conn);

                conn.commit();
                return resId;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public Reservation getReservationById(int id) throws Exception {
        return reservationDAO.findById(id);
    }

    public void cancelReservation(int reservationId) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);
                
                // Get reservation to find the room ID
                Reservation reservation = reservationDAO.findById(reservationId);
                if (reservation == null) {
                    throw new Exception("Reservation not found");
                }
                
                // Update reservation status to CANCELLED
                reservationDAO.updateStatus(reservationId, "CANCELLED", conn);
                
                // Update room status to FREE when reservation is cancelled
                // Check if room is BOOKED or OCCUPIED (both should become FREE on cancellation)
                roomDAO.updateStatus(reservation.getRoomId(), "FREE", conn);
                
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updateReservationDates(int reservationId, LocalDate checkin, LocalDate checkout, double total) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);
                reservationDAO.updateReservationDates(reservationId, checkin, checkout, total, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Check-in: Mark reservation as CHECKED_IN and update room status to OCCUPIED
     */
    public void checkIn(int reservationId) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);
                Reservation reservation = reservationDAO.findById(reservationId);
                if (reservation == null) {
                    throw new Exception("Reservation not found");
                }
                if (!"CONFIRMED".equalsIgnoreCase(reservation.getStatus())) {
                    throw new Exception("Only CONFIRMED reservations can be checked in");
                }
                
                reservationDAO.updateStatus(reservationId, "CHECKED_IN", conn);
                roomDAO.updateStatus(reservation.getRoomId(), "OCCUPIED", conn);
                
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Check-out: Mark reservation as COMPLETED and update room status to FREE
     */
    public void checkOut(int reservationId) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);
                Reservation reservation = reservationDAO.findById(reservationId);
                if (reservation == null) {
                    throw new Exception("Reservation not found");
                }
                if (!"CHECKED_IN".equalsIgnoreCase(reservation.getStatus())) {
                    throw new Exception("Only CHECKED_IN reservations can be checked out");
                }
                
                reservationDAO.updateStatus(reservationId, "COMPLETED", conn);
                roomDAO.updateStatus(reservation.getRoomId(), "FREE", conn);
                
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Get reservations that need to be checked in today
     */
    public List<Reservation> getTodayCheckIns() throws Exception {
        return reservationDAO.getReservationsByCheckInDate(LocalDate.now());
    }

    /**
     * Get reservations that need to be checked out today
     */
    public List<Reservation> getTodayCheckOuts() throws Exception {
        return reservationDAO.getReservationsByCheckOutDate(LocalDate.now());
    }

    /**
     * Automatically complete past reservations and free rooms for
     * any stays where the checkout date is before today but the
     * reservation is still CONFIRMED or CHECKED_IN.
     *
     * This helps ensure rooms do not remain stuck as BOOKED/OCCUPIED
     * after the stay has already ended by date.
     */
    public void autoCompletePastCheckouts() throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);

                List<Reservation> overdue = reservationDAO.getOverdueReservations(LocalDate.now());
                for (Reservation r : overdue) {
                    // Mark reservation as COMPLETED and free the room
                    reservationDAO.updateStatus(r.getId(), "COMPLETED", conn);
                    roomDAO.updateStatus(r.getRoomId(), "FREE", conn);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                // If the database is locked, skip auto-complete silently
                String msg = e.getMessage();
                if (msg != null && msg.toUpperCase().contains("SQLITE_BUSY")) {
                    return;
                }
                throw e;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
