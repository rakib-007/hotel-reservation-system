// src/main/java/com/hotelapp/dao/CustomerDAO.java
package com.hotelapp.dao;

import com.hotelapp.models.Customer;
import com.hotelapp.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public int createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers(name, phone, email, address, nid_passport) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getNidPassport());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    customer.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Failed to create customer");
    }

    public Customer findById(int id) throws SQLException {
        String sql = "SELECT id, name, phone, email, " +
                    "COALESCE(address, '') as address, " +
                    "COALESCE(nid_passport, '') as nid_passport " +
                    "FROM customers WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer cust = new Customer();
                    cust.setId(rs.getInt("id"));
                    cust.setName(rs.getString("name"));
                    cust.setPhone(rs.getString("phone"));
                    cust.setEmail(rs.getString("email"));
                    String address = rs.getString("address");
                    cust.setAddress(address != null && !address.isEmpty() ? address : null);
                    String nid = rs.getString("nid_passport");
                    cust.setNidPassport(nid != null && !nid.isEmpty() ? nid : null);
                    return cust;
                }
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        // Use COALESCE to handle missing columns gracefully
        String sql = "SELECT id, name, phone, email, " +
                    "COALESCE(address, '') as address, " +
                    "COALESCE(nid_passport, '') as nid_passport " +
                    "FROM customers ORDER BY name";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Customer cust = new Customer();
                cust.setId(rs.getInt("id"));
                cust.setName(rs.getString("name"));
                cust.setPhone(rs.getString("phone"));
                cust.setEmail(rs.getString("email"));
                String address = rs.getString("address");
                cust.setAddress(address != null && !address.isEmpty() ? address : null);
                String nid = rs.getString("nid_passport");
                cust.setNidPassport(nid != null && !nid.isEmpty() ? nid : null);
                list.add(cust);
            }
        }
        return list;
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ?, nid_passport = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail()); // Keep existing email or null
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getNidPassport());
            ps.setInt(6, customer.getId());
            ps.executeUpdate();
        }
    }

    public void deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * findOrCreate: look up customer by (name, phone) and return id; if not found insert and return new id.
     * This variant accepts a Connection so the caller can use it inside a transaction.
     */
    public int findOrCreate(Customer customer, Connection conn) throws SQLException {
        String findSql = "SELECT id FROM customers WHERE name = ? AND phone = ?";
        try (PreparedStatement ps = conn.prepareStatement(findSql)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }

        String insertSql = "INSERT INTO customers(name, phone, email, address, nid_passport) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getNidPassport());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create or find customer");
    }
}
