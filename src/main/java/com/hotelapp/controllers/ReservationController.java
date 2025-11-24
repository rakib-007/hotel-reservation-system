package com.hotelapp.controllers;

import com.hotelapp.dao.RoomDAO;
import com.hotelapp.models.Customer;
import com.hotelapp.models.Room;
import com.hotelapp.services.ReservationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ReservationController {
    @FXML private ComboBox<Room> cmbRoom;
    @FXML private DatePicker dpCheckIn;
    @FXML private DatePicker dpCheckOut;
    @FXML private TextField tfName;
    @FXML private TextField tfPhone;
    @FXML private TextField tfAddress;
    @FXML private TextField tfNidPassport;
    @FXML private Label lblPrice;
    @FXML private Label lblNights;
    @FXML private Label lblTotal;
    @FXML private Label lblStatus;

    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        loadFreeRooms();
        if (lblPrice != null) lblPrice.setText("-");
        if (lblNights != null) lblNights.setText("0");
        if (lblTotal != null) lblTotal.setText("Tk 0.00");
        
        // Update price when room is selected
        cmbRoom.setOnAction(e -> updatePrice());
        
        // Update calculation when dates change
        dpCheckIn.valueProperty().addListener((obs, oldVal, newVal) -> handleCalculate());
        dpCheckOut.valueProperty().addListener((obs, oldVal, newVal) -> handleCalculate());
    }

    private void loadFreeRooms() {
        try {
            var freeRooms = roomDAO.getFreeRooms();
            cmbRoom.getItems().clear();
            cmbRoom.getItems().addAll(freeRooms);
            if (freeRooms.isEmpty()) {
                lblStatus.setText("No free rooms available");
                lblStatus.setStyle("-fx-text-fill:red;");
            } else {
                lblStatus.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load rooms: " + e.getMessage());
        }
    }

    private void updatePrice() {
        Room selected = cmbRoom.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lblPrice.setText("Tk " + String.format("%.2f", selected.getPrice()));
            handleCalculate();
        } else {
            lblPrice.setText("-");
        }
    }

    @FXML
    public void handleCalculate() {
        Room selected = cmbRoom.getSelectionModel().getSelectedItem();
        LocalDate checkIn = dpCheckIn.getValue();
        LocalDate checkOut = dpCheckOut.getValue();

        if (selected == null) {
            lblStatus.setText("Please select a room");
            lblStatus.setStyle("-fx-text-fill:red;");
            return;
        }

        if (checkIn == null || checkOut == null) {
            lblNights.setText("0");
            lblTotal.setText("Tk 0.00");
            return;
        }

        if (!checkIn.isBefore(checkOut)) {
            lblStatus.setText("Check-out date must be after check-in date");
            lblStatus.setStyle("-fx-text-fill:red;");
            lblNights.setText("0");
            lblTotal.setText("Tk 0.00");
            return;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = nights * selected.getPrice();

        lblNights.setText(String.valueOf(nights));
        lblTotal.setText("Tk " + String.format("%.2f", total));
        lblStatus.setText("");
    }

    @FXML
    public void handleBook() {
        Room selected = cmbRoom.getSelectionModel().getSelectedItem();
        LocalDate checkIn = dpCheckIn.getValue();
        LocalDate checkOut = dpCheckOut.getValue();
        String name = tfName.getText().trim();
        String phone = tfPhone.getText().trim();
        String address = tfAddress.getText().trim();
        String nidPassport = tfNidPassport.getText().trim();

        // Validation
        if (selected == null) {
            showError("Please select a room");
            return;
        }

        if (checkIn == null || checkOut == null) {
            showError("Please select check-in and check-out dates");
            return;
        }

        if (!checkIn.isBefore(checkOut)) {
            showError("Check-out date must be after check-in date");
            return;
        }

        if (name.isEmpty() || phone.isEmpty()) {
            showError("Please enter guest name and phone number");
            return;
        }

        if (address.isEmpty()) {
            showError("Please enter permanent address");
            return;
        }

        if (nidPassport.isEmpty()) {
            showError("Please enter NID or Passport number");
            return;
        }

        try {
            // Calculate total
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            double total = nights * selected.getPrice();

            // Create customer with all information
            Customer customer = new Customer(name, phone, null, address, nidPassport);

            // Book reservation
            int reservationId = reservationService.bookReservation(customer, selected.getId(), checkIn, checkOut, total);

            if (reservationId > 0) {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Reservation booked successfully! Reservation ID: " + reservationId);
                Optional<ButtonType> result = success.showAndWait();
                if (result.isPresent()) {
                    // Reload free rooms before going back (in case user wants to book again)
                    loadFreeRooms();
                    goBack();
                }
            } else {
                showError("Failed to book reservation");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Booking failed: " + e.getMessage());
        }
    }

    @FXML
    public void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) (lblStatus != null ? lblStatus.getScene().getWindow() : null);
            if (stage == null) {
                // fallback: try any window
                stage = (Stage) javafx.stage.Window.getWindows().filtered(w -> w.isShowing()).get(0);
            }
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to go back: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
