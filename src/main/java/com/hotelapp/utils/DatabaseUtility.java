package com.hotelapp.utils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Standalone utility application for database management.
 * Can be run separately to manage the database.
 * 
 * Usage: Run this class as a Java application
 */
public class DatabaseUtility extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Database Management Utility");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Database Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Delete All Data Section
        Label deleteLabel = new Label("Delete All Data");
        deleteLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label deleteInfo = new Label("This will delete all rooms, customers, and reservations.\nUsers table will be preserved.");
        deleteInfo.setStyle("-fx-text-fill: red;");
        
        Button btnDeleteAll = new Button("Delete All Data");
        btnDeleteAll.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        btnDeleteAll.setOnAction(e -> handleDeleteAll());
        
        // Change Admin Password Section
        Label passwordLabel = new Label("Change Admin Password");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        GridPane passwordGrid = new GridPane();
        passwordGrid.setHgap(10);
        passwordGrid.setVgap(10);
        
        Label lblNewPassword = new Label("New Password:");
        PasswordField pfNewPassword = new PasswordField();
        pfNewPassword.setPromptText("Enter new password");
        
        Button btnChangePassword = new Button("Change Password");
        btnChangePassword.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        
        passwordGrid.add(lblNewPassword, 0, 0);
        passwordGrid.add(pfNewPassword, 1, 0);
        passwordGrid.add(btnChangePassword, 2, 0);
        
        btnChangePassword.setOnAction(e -> {
            String newPassword = pfNewPassword.getText();
            if (newPassword == null || newPassword.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter a password");
                return;
            }
            handleChangePassword(newPassword);
            pfNewPassword.clear();
        });
        
        root.getChildren().addAll(
            titleLabel,
            new Separator(),
            deleteLabel,
            deleteInfo,
            btnDeleteAll,
            new Separator(),
            passwordLabel,
            passwordGrid
        );
        
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleDeleteAll() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete All Data?");
        confirm.setContentText("This will permanently delete all rooms, customers, and reservations.\n\nThis action cannot be undone!\n\nAre you sure?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    DBManager.deleteAllData();
                    showAlert(Alert.AlertType.INFORMATION, "All data deleted successfully!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error deleting data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleChangePassword(String newPassword) {
        try {
            DBManager.changeAdminPassword(newPassword);
            showAlert(Alert.AlertType.INFORMATION, "Admin password changed successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        // Initialize database first
        DBInit.initDatabase();
        launch(args);
    }
}

