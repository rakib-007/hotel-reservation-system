package com.hotelapp.controllers;

import com.hotelapp.models.Reservation;
import com.hotelapp.services.ReservationService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class CheckInOutController {
    @FXML private TableView<Reservation> tblCheckIns;
    @FXML private TableColumn<Reservation, Integer> colCheckInId;
    @FXML private TableColumn<Reservation, String> colCheckInRoom;
    @FXML private TableColumn<Reservation, String> colCheckInGuest;
    @FXML private TableColumn<Reservation, LocalDate> colCheckInCheckIn;
    @FXML private TableColumn<Reservation, LocalDate> colCheckInCheckOut;
    @FXML private TableColumn<Reservation, Double> colCheckInTotal;
    @FXML private TableColumn<Reservation, String> colCheckInAction;

    @FXML private TableView<Reservation> tblCheckOuts;
    @FXML private TableColumn<Reservation, Integer> colCheckOutId;
    @FXML private TableColumn<Reservation, String> colCheckOutRoom;
    @FXML private TableColumn<Reservation, String> colCheckOutGuest;
    @FXML private TableColumn<Reservation, LocalDate> colCheckOutCheckIn;
    @FXML private TableColumn<Reservation, LocalDate> colCheckOutCheckOut;
    @FXML private TableColumn<Reservation, Double> colCheckOutTotal;
    @FXML private TableColumn<Reservation, String> colCheckOutAction;

    private final ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        setupCheckInTable();
        setupCheckOutTable();
        loadData();
    }

    private void setupCheckInTable() {
        colCheckInId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        colCheckInRoom.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getRoomNumber()));
        colCheckInGuest.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCustomerName()));
        colCheckInCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkin"));
        colCheckInCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkout"));
        colCheckInTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // Action column with Check-in button
        colCheckInAction.setCellFactory(column -> new TableCell<Reservation, String>() {
            private final Button btnCheckIn = new Button("Check In");
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    btnCheckIn.setOnAction(e -> handleCheckIn(reservation.getId()));
                    setGraphic(btnCheckIn);
                }
            }
        });
    }

    private void setupCheckOutTable() {
        colCheckOutId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        colCheckOutRoom.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getRoomNumber()));
        colCheckOutGuest.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCustomerName()));
        colCheckOutCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkin"));
        colCheckOutCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkout"));
        colCheckOutTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // Action column with Check-out button
        colCheckOutAction.setCellFactory(column -> new TableCell<Reservation, String>() {
            private final Button btnCheckOut = new Button("Check Out");
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    btnCheckOut.setOnAction(e -> handleCheckOut(reservation.getId()));
                    setGraphic(btnCheckOut);
                }
            }
        });
    }

    private void loadData() {
        try {
            // Load today's check-ins
            List<Reservation> checkIns = reservationService.getTodayCheckIns();
            tblCheckIns.getItems().clear();
            tblCheckIns.getItems().addAll(checkIns);

            // Load today's check-outs
            List<Reservation> checkOuts = reservationService.getTodayCheckOuts();
            tblCheckOuts.getItems().clear();
            tblCheckOuts.getItems().addAll(checkOuts);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load check-in/check-out data: " + e.getMessage());
        }
    }

    private void handleCheckIn(int reservationId) {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Check In");
            confirm.setHeaderText(null);
            confirm.setContentText("Check in this guest? The room will be marked as OCCUPIED.");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                reservationService.checkIn(reservationId);
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Guest checked in successfully!");
                success.showAndWait();
                loadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Check-in failed: " + e.getMessage());
        }
    }

    private void handleCheckOut(int reservationId) {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Check Out");
            confirm.setHeaderText(null);
            confirm.setContentText("Check out this guest? The room will be marked as FREE.");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                reservationService.checkOut(reservationId);
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Guest checked out successfully!");
                success.showAndWait();
                loadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Check-out failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefresh() {
        loadData();
    }

    @FXML
    public void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) tblCheckIns.getScene().getWindow();
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

