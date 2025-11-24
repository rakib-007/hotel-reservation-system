package com.hotelapp.controllers;

import com.hotelapp.utils.DBUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DB-backed login: checks users table in database.
 * On successful login, loads dashboard.fxml
 *
 * Guideline file (for reference): file:///mnt/data/OOP Lab CEP Updated.docx
 */
public class LoginController {
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;

    @FXML
    public void handleLogin() {
        String user = txtUser.getText() == null ? "" : txtUser.getText().trim();
        String pass = txtPass.getText() == null ? "" : txtPass.getText();

        if (user.isEmpty()) {
            show(AlertType.WARNING, "Please enter username");
            return;
        }

        String sql = "SELECT id, role FROM users WHERE username = ? AND password = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    System.out.println("User logged in: " + user + " role=" + role);
                    show(AlertType.INFORMATION, "Login successful. Welcome, " + user + " (" + role + ")");
                    // Load dashboard
                    openDashboard();
                    return;
                } else {
                    show(AlertType.WARNING, "Invalid username or password.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            show(AlertType.ERROR, "Login failed: " + e.getMessage());
        }
    }

    private void show(AlertType type, String msg) {
        Alert a = new Alert(type);
        a.setTitle(null);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Loads dashboard.fxml into the same Stage
    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hotel Reservation System - Dashboard");
            stage.centerOnScreen();
            stage.toFront();
            stage.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
            show(AlertType.ERROR, "Unable to open dashboard: " + e.getMessage());
        }
    }
}
