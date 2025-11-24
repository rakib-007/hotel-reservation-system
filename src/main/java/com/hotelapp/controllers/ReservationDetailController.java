package com.hotelapp.controllers;

import com.hotelapp.models.Reservation;
import com.hotelapp.services.ReservationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.util.Optional;

public class ReservationDetailController {
    @FXML private Label lblId;
    @FXML private Label lblGuest;
    @FXML private Label lblRoom;
    @FXML private DatePicker dpCheckIn;
    @FXML private DatePicker dpCheckOut;
    @FXML private Label lblTotal;
    @FXML private Label lblStatus;
    @FXML private Button btnCheckIn;
    @FXML private Button btnCheckOut;

    private final ReservationService reservationService = new ReservationService();
    private Reservation current;

    private int reservationId;

    public void setReservationId(int id) {
        this.reservationId = id;
        loadReservation();
    }

    private void loadReservation() {
        try {
            current = reservationService.getReservationById(reservationId);
            if (current == null) {
                showError("Reservation not found.");
                return;
            }
            lblId.setText(String.valueOf(current.getId()));
            lblGuest.setText(current.getCustomerName());
            lblRoom.setText(current.getRoomNumber() != null ? current.getRoomNumber() : String.valueOf(current.getRoomId()));
            dpCheckIn.setValue(current.getCheckin());
            dpCheckOut.setValue(current.getCheckout());
            lblTotal.setText(String.format("Tk %.2f", current.getTotal()));
            lblStatus.setText(current.getStatus());
            
            // Show/hide check-in/check-out buttons based on status
            if ("CONFIRMED".equalsIgnoreCase(current.getStatus())) {
                btnCheckIn.setVisible(true);
                btnCheckOut.setVisible(false);
            } else if ("CHECKED_IN".equalsIgnoreCase(current.getStatus())) {
                btnCheckIn.setVisible(false);
                btnCheckOut.setVisible(true);
            } else {
                btnCheckIn.setVisible(false);
                btnCheckOut.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load reservation: " + e.getMessage());
        }
    }

    @FXML
    public void handleSave() {
        try {
            LocalDate in = dpCheckIn.getValue();
            LocalDate out = dpCheckOut.getValue();
            if (in == null || out == null || !in.isBefore(out)) {
                showError("Invalid dates.");
                return;
            }
            long nights = java.time.temporal.ChronoUnit.DAYS.between(in, out);
            // fetch room price simple query - compute total = nights * price
            // For simplicity, keep same total if nights==0 we keep existing total
            double pricePerNight = current.getTotal() / Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(current.getCheckin(), current.getCheckout()));
            double total = nights * pricePerNight;

            reservationService.updateReservationDates(current.getId(), in, out, total);
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Reservation updated", ButtonType.OK);
            ok.showAndWait();
            goBack();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Save failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleCheckIn() {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Check in this guest? The room will be marked as OCCUPIED.", ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> r = confirm.showAndWait();
            if (r.isEmpty() || r.get() != ButtonType.OK) return;
            reservationService.checkIn(current.getId());
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Guest checked in successfully!", ButtonType.OK);
            ok.showAndWait();
            loadReservation(); // Reload to update status and buttons
        } catch (Exception e) {
            e.printStackTrace();
            showError("Check-in failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleCheckOut() {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Check out this guest? The room will be marked as FREE.", ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> r = confirm.showAndWait();
            if (r.isEmpty() || r.get() != ButtonType.OK) return;
            reservationService.checkOut(current.getId());
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Guest checked out successfully!", ButtonType.OK);
            ok.showAndWait();
            loadReservation(); // Reload to update status and buttons
        } catch (Exception e) {
            e.printStackTrace();
            showError("Check-out failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel this reservation?", ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> r = confirm.showAndWait();
            if (r.isEmpty() || r.get() != ButtonType.OK) return;
            reservationService.cancelReservation(current.getId());
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Reservation cancelled", ButtonType.OK);
            ok.showAndWait();
            goBack();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Cancel failed: " + e.getMessage());
        }
    }

    public void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) lblId.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to go back: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
