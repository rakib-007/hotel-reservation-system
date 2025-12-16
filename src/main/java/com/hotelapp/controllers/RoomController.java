package com.hotelapp.controllers;

import com.hotelapp.dao.RoomDAO;
import com.hotelapp.models.Room;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.List;
import java.util.Optional;

/**
 * RoomController - full CRUD for rooms.
 * Guideline reference: file:///mnt/data/OOP Lab CEP Updated.docx
 */
public class RoomController {
    @FXML private TableView<Room> tblRooms;
    @FXML private TableColumn<Room, Integer> colId;
    @FXML private TableColumn<Room, String> colNumber;
    @FXML private TableColumn<Room, String> colType;
    @FXML private TableColumn<Room, Double> colPrice;
    @FXML private TableColumn<Room, String> colStatus;

    @FXML private TextField tfRoomNumber;
    @FXML private TextField tfType;
    @FXML private TextField tfPrice;
    @FXML private ComboBox<String> cmbStatus;

    private final RoomDAO roomDAO = new RoomDAO();

    @FXML
    public void initialize() {
        // Setup columns
        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Handle row selection
        tblRooms.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                loadRoomToForm(newSel);
            }
        });

        // Default status options (in case FXML doesn't populate)
        if (cmbStatus.getItems().isEmpty()) {
            cmbStatus.getItems().addAll("FREE", "BOOKED", "MAINTENANCE");
        }

        refreshTable();
    }

    private void loadRoomToForm(Room r) {
        tfRoomNumber.setText(r.getRoomNumber());
        tfType.setText(r.getType());
        tfPrice.setText(String.valueOf(r.getPrice()));
        cmbStatus.getSelectionModel().select(r.getStatus());
    }

    @FXML
    public void handleRefresh() {
        refreshTable();
    }

    private void refreshTable() {
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            tblRooms.getItems().clear();
            tblRooms.getItems().addAll(rooms);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load rooms: " + e.getMessage());
        }
    }

    @FXML
    public void handleAdd() {
        try {
            String number = Optional.ofNullable(tfRoomNumber.getText()).orElse("").trim();
            String type = Optional.ofNullable(tfType.getText()).orElse("").trim();
            String priceText = Optional.ofNullable(tfPrice.getText()).orElse("").trim();
            String status = Optional.ofNullable(cmbStatus.getSelectionModel().getSelectedItem()).orElse("FREE");

            if (number.isEmpty() || type.isEmpty() || priceText.isEmpty()) {
                showError("Please fill room number, type and price.");
                return;
            }
            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                showError("Invalid price value.");
                return;
            }

            Room r = new Room(number, type, price, status);
            roomDAO.addRoom(r);
            showInfo("Room added.");
            clearForm();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Add failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdate() {
        try {
            Room selected = tblRooms.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a room to update.");
                return;
            }

            String number = Optional.ofNullable(tfRoomNumber.getText()).orElse("").trim();
            String type = Optional.ofNullable(tfType.getText()).orElse("").trim();
            String priceText = Optional.ofNullable(tfPrice.getText()).orElse("").trim();
            String status = Optional.ofNullable(cmbStatus.getSelectionModel().getSelectedItem()).orElse("FREE");

            if (number.isEmpty() || type.isEmpty() || priceText.isEmpty()) {
                showError("Please fill room number, type and price.");
                return;
            }
            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                showError("Invalid price value.");
                return;
            }

            selected.setRoomNumber(number);
            selected.setType(type);
            selected.setPrice(price);
            selected.setStatus(status);

            roomDAO.updateRoom(selected);
            showInfo("Room updated.");
            clearForm();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Update failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        try {
            Room selected = tblRooms.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a room to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm delete");
            confirm.setHeaderText(null);
            confirm.setContentText("Delete room " + selected.getRoomNumber() + " ?");
            Optional<ButtonType> r = confirm.showAndWait();
            if (r.isEmpty() || r.get() != ButtonType.OK) return;

            roomDAO.deleteRoom(selected.getId());
            showInfo("Room deleted.");
            clearForm();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Delete failed: " + e.getMessage());
        }
    }

    public void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) tblRooms.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to go back: " + e.getMessage());
        }
    }

    /**
     * Logout back to the login screen.
     */
    @FXML
    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) tblRooms.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hotel Reservation System - Login");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to logout: " + e.getMessage());
        }
    }

    private void clearForm() {
        tfRoomNumber.clear();
        tfType.clear();
        tfPrice.clear();
        cmbStatus.getSelectionModel().clearSelection();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
