package com.hotelapp.dao;

import com.hotelapp.models.Reservation;
import com.hotelapp.utils.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public int insertReservation(int customerId, int roomId, LocalDate checkin, LocalDate checkout, String status, double total) throws SQLException {
        return insertReservation(customerId, roomId, checkin, checkout, status, total, null);
    }

    public int insertReservation(int customerId, int roomId, LocalDate checkin, LocalDate checkout, String status, double total, Connection conn) throws SQLException {
        String sql = "INSERT INTO reservations(customer_id, room_id, checkin, checkout, status, total) VALUES (?, ?, ?, ?, ?, ?)";
        boolean shouldClose = (conn == null);
        if (conn == null) {
            conn = DBUtil.getConnection();
        }
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.setInt(2, roomId);
            ps.setString(3, checkin.toString());
            ps.setString(4, checkout.toString());
            ps.setString(5, status);
            ps.setDouble(6, total);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } finally {
            if (shouldClose && conn != null) {
                conn.close();
            }
        }
        return -1;
    }

    public Reservation findById(int id) throws SQLException {
        String sql = "SELECT r.id, r.customer_id, r.room_id, r.checkin, r.checkout, r.status, r.total, " +
                "c.name AS customer_name, rm.room_number " +
                "FROM reservations r " +
                "LEFT JOIN customers c ON r.customer_id = c.id " +
                "LEFT JOIN rooms rm ON r.room_id = rm.id " +
                "WHERE r.id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setCustomerId(rs.getInt("customer_id"));
                    r.setRoomId(rs.getInt("room_id"));
                    r.setCheckin(LocalDate.parse(rs.getString("checkin")));
                    r.setCheckout(LocalDate.parse(rs.getString("checkout")));
                    r.setStatus(rs.getString("status"));
                    r.setTotal(rs.getDouble("total"));
                    r.setCustomerName(rs.getString("customer_name"));
                    r.setRoomNumber(rs.getString("room_number"));
                    return r;
                }
            }
        }
        return null;
    }

    public List<Reservation> findReservationsBetween(LocalDate from, LocalDate to) throws SQLException {
        return findReservationsBetween(from, to, null);
    }

    public List<Reservation> findReservationsBetween(LocalDate from, LocalDate to, Connection conn) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT id, customer_id, room_id, checkin, checkout, status, total FROM reservations " +
                "WHERE NOT (checkout <= ? OR checkin >= ?) AND status = 'CONFIRMED'";
        boolean shouldClose = (conn == null);
        if (conn == null) {
            conn = DBUtil.getConnection();
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setCustomerId(rs.getInt("customer_id"));
                    r.setRoomId(rs.getInt("room_id"));
                    r.setCheckin(LocalDate.parse(rs.getString("checkin")));
                    r.setCheckout(LocalDate.parse(rs.getString("checkout")));
                    r.setStatus(rs.getString("status"));
                    r.setTotal(rs.getDouble("total"));
                    list.add(r);
                }
            }
        } finally {
            if (shouldClose && conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.customer_id, r.room_id, r.checkin, r.checkout, r.status, r.total, " +
                "c.name AS customer_name, c.phone AS customer_phone, rm.room_number " +
                "FROM reservations r " +
                "LEFT JOIN customers c ON r.customer_id = c.id " +
                "LEFT JOIN rooms rm ON r.room_id = rm.id " +
                "ORDER BY r.id DESC";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setCustomerId(rs.getInt("customer_id"));
                r.setRoomId(rs.getInt("room_id"));
                r.setCheckin(LocalDate.parse(rs.getString("checkin")));
                r.setCheckout(LocalDate.parse(rs.getString("checkout")));
                r.setStatus(rs.getString("status"));
                r.setTotal(rs.getDouble("total"));
                r.setCustomerName(rs.getString("customer_name"));
                r.setCustomerPhone(rs.getString("customer_phone"));
                r.setRoomNumber(rs.getString("room_number"));
                list.add(r);
            }
        }
        return list;
    }

    public void updateReservationDates(int id, LocalDate checkin, LocalDate checkout, double total) throws SQLException {
        updateReservationDates(id, checkin, checkout, total, null);
    }

    public void updateReservationDates(int id, LocalDate checkin, LocalDate checkout, double total, Connection conn) throws SQLException {
        String sql = "UPDATE reservations SET checkin = ?, checkout = ?, total = ? WHERE id = ?";
        boolean shouldClose = (conn == null);
        if (conn == null) {
            conn = DBUtil.getConnection();
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, checkin.toString());
            ps.setString(2, checkout.toString());
            ps.setDouble(3, total);
            ps.setInt(4, id);
            ps.executeUpdate();
        } finally {
            if (shouldClose && conn != null) {
                conn.close();
            }
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        updateStatus(id, status, null);
    }

    public void updateStatus(int id, String status, Connection conn) throws SQLException {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        boolean shouldClose = (conn == null);
        if (conn == null) {
            conn = DBUtil.getConnection();
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } finally {
            if (shouldClose && conn != null) {
                conn.close();
            }
        }
    }

    public void deleteReservation(int id) throws SQLException {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Reservation> getReservationsByCustomerId(int customerId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.customer_id, r.room_id, r.checkin, r.checkout, r.status, r.total, " +
                "c.name AS customer_name, rm.room_number " +
                "FROM reservations r " +
                "LEFT JOIN customers c ON r.customer_id = c.id " +
                "LEFT JOIN rooms rm ON r.room_id = rm.id " +
                "WHERE r.customer_id = ? " +
                "ORDER BY r.checkin DESC";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setCustomerId(rs.getInt("customer_id"));
                    r.setRoomId(rs.getInt("room_id"));
                    r.setCheckin(LocalDate.parse(rs.getString("checkin")));
                    r.setCheckout(LocalDate.parse(rs.getString("checkout")));
                    r.setStatus(rs.getString("status"));
                    r.setTotal(rs.getDouble("total"));
                    r.setCustomerName(rs.getString("customer_name"));
                    r.setRoomNumber(rs.getString("room_number"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    public List<Reservation> getReservationsByCheckInDate(LocalDate date) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.customer_id, r.room_id, r.checkin, r.checkout, r.status, r.total, " +
                "c.name AS customer_name, rm.room_number " +
                "FROM reservations r " +
                "LEFT JOIN customers c ON r.customer_id = c.id " +
                "LEFT JOIN rooms rm ON r.room_id = rm.id " +
                "WHERE r.checkin = ? AND r.status = 'CONFIRMED' " +
                "ORDER BY r.checkin";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, date.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setCustomerId(rs.getInt("customer_id"));
                    r.setRoomId(rs.getInt("room_id"));
                    r.setCheckin(LocalDate.parse(rs.getString("checkin")));
                    r.setCheckout(LocalDate.parse(rs.getString("checkout")));
                    r.setStatus(rs.getString("status"));
                    r.setTotal(rs.getDouble("total"));
                    r.setCustomerName(rs.getString("customer_name"));
                    r.setRoomNumber(rs.getString("room_number"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    public List<Reservation> getReservationsByCheckOutDate(LocalDate date) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        // Include both CHECKED_IN (ready to check out) and CONFIRMED (scheduled to check out today)
        // Exclude CANCELLED and COMPLETED reservations
        String sql = "SELECT r.id, r.customer_id, r.room_id, r.checkin, r.checkout, r.status, r.total, " +
                "c.name AS customer_name, c.phone AS customer_phone, rm.room_number " +
                "FROM reservations r " +
                "LEFT JOIN customers c ON r.customer_id = c.id " +
                "LEFT JOIN rooms rm ON r.room_id = rm.id " +
                "WHERE r.checkout = ? AND r.status IN ('CHECKED_IN', 'CONFIRMED') " +
                "ORDER BY r.checkout";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, date.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setCustomerId(rs.getInt("customer_id"));
                    r.setRoomId(rs.getInt("room_id"));
                    r.setCheckin(LocalDate.parse(rs.getString("checkin")));
                    r.setCheckout(LocalDate.parse(rs.getString("checkout")));
                    r.setStatus(rs.getString("status"));
                    r.setTotal(rs.getDouble("total"));
                    r.setCustomerName(rs.getString("customer_name"));
                    r.setCustomerPhone(rs.getString("customer_phone"));
                    r.setRoomNumber(rs.getString("room_number"));
                    list.add(r);
                }
            }
        }
        return list;
    }
}
