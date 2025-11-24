package com.hotelapp.dao;

import com.hotelapp.models.Room;
import com.hotelapp.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public void addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms(room_number, type, price, status) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getType());
            ps.setDouble(3, room.getPrice());
            ps.setString(4, room.getStatus());
            ps.executeUpdate();
        }
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT id, room_number, type, price, status FROM rooms ORDER BY room_number";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Room r = new Room();
                r.setId(rs.getInt("id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setType(rs.getString("type"));
                r.setPrice(rs.getDouble("price"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        }
        return list;
    }

    public Room findById(int id) throws SQLException {
        String sql = "SELECT id, room_number, type, price, status FROM rooms WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room r = new Room();
                    r.setId(rs.getInt("id"));
                    r.setRoomNumber(rs.getString("room_number"));
                    r.setType(rs.getString("type"));
                    r.setPrice(rs.getDouble("price"));
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        }
        return null;
    }

    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, type = ?, price = ?, status = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getType());
            ps.setDouble(3, room.getPrice());
            ps.setString(4, room.getStatus());
            ps.setInt(5, room.getId());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int roomId, String status) throws SQLException {
        updateStatus(roomId, status, null);
    }

    public void updateStatus(int roomId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        boolean shouldClose = (conn == null);
        if (conn == null) {
            conn = DBUtil.getConnection();
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        } finally {
            if (shouldClose && conn != null) {
                conn.close();
            }
        }
    }

    public void deleteRoom(int id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Room> getFreeRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT id, room_number, type, price, status FROM rooms WHERE status = 'FREE' ORDER BY room_number";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Room r = new Room();
                r.setId(rs.getInt("id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setType(rs.getString("type"));
                r.setPrice(rs.getDouble("price"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        }
        return list;
    }
}
