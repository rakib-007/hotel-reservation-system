package com.hotelapp.controllers;

import com.hotelapp.dao.CustomerDAO;
import com.hotelapp.dao.ReservationDAO;
import com.hotelapp.models.Customer;
import com.hotelapp.models.Reservation;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class GuestController {
    @FXML private TableView<CustomerRow> tblGuests;
    @FXML private TableColumn<CustomerRow, Integer> colGuestId;
    @FXML private TableColumn<CustomerRow, String> colGuestName;
    @FXML private TableColumn<CustomerRow, String> colGuestPhone;
    @FXML private TableColumn<CustomerRow, String> colGuestAddress;
    @FXML private TableColumn<CustomerRow, String> colGuestNid;
    @FXML private TableColumn<CustomerRow, Integer> colGuestReservations;
    @FXML private TextField tfSearch;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private ObservableList<CustomerRow> allGuests = FXCollections.observableArrayList();

    // Wrapper class to include reservation count
    public static class CustomerRow {
        private final Customer customer;
        private final int reservationCount;

        public CustomerRow(Customer customer, int reservationCount) {
            this.customer = customer;
            this.reservationCount = reservationCount;
        }

        public Customer getCustomer() { return customer; }
        public int getId() { return customer.getId(); }
        public String getName() { return customer.getName(); }
        public String getPhone() { return customer.getPhone(); }
        public String getEmail() { return customer.getEmail(); }
        public String getAddress() { return customer.getAddress(); }
        public String getNidPassport() { return customer.getNidPassport(); }
        public int getReservationCount() { return reservationCount; }
    }

    @FXML
    public void initialize() {
        // Setup columns
        colGuestId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        colGuestName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colGuestPhone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhone() != null ? cell.getValue().getPhone() : "-"));
        colGuestAddress.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAddress() != null ? cell.getValue().getAddress() : "-"));
        colGuestNid.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNidPassport() != null ? cell.getValue().getNidPassport() : "-"));
        colGuestReservations.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getReservationCount()).asObject());

        // Double-click to view guest details
        tblGuests.setRowFactory(tv -> {
            TableRow<CustomerRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    viewGuestDetails(row.getItem().getCustomer());
                }
            });
            return row;
        });

        loadGuests();
    }

    private void loadGuests() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            allGuests.clear();
            
            for (Customer customer : customers) {
                List<Reservation> reservations = reservationDAO.getReservationsByCustomerId(customer.getId());
                allGuests.add(new CustomerRow(customer, reservations.size()));
            }
            
            tblGuests.setItems(allGuests);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load guests: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefresh() {
        loadGuests();
        tfSearch.clear();
    }

    @FXML
    public void handleSearch() {
        String searchText = tfSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            tblGuests.setItems(allGuests);
            return;
        }

        List<CustomerRow> filtered = allGuests.stream()
                .filter(row -> {
                    Customer c = row.getCustomer();
                    return (c.getName() != null && c.getName().toLowerCase().contains(searchText)) ||
                           (c.getPhone() != null && c.getPhone().contains(searchText)) ||
                           (c.getNidPassport() != null && c.getNidPassport().toLowerCase().contains(searchText));
                })
                .collect(Collectors.toList());
        
        tblGuests.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void handleClear() {
        tfSearch.clear();
        tblGuests.setItems(allGuests);
    }

    private void viewGuestDetails(Customer customer) {
        try {
            // Create a dialog to show guest details and purchase history
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Guest Details");
            dialog.setHeaderText("Guest Information & Purchase History");

            VBox content = new VBox(15);
            content.setStyle("-fx-padding:20;");

            // Guest Information - Editable fields
            GridPane infoGrid = new GridPane();
            infoGrid.setHgap(10);
            infoGrid.setVgap(10);
            
            TextField tfName = new TextField(customer.getName() != null ? customer.getName() : "");
            TextField tfPhone = new TextField(customer.getPhone() != null ? customer.getPhone() : "");
            TextField tfAddress = new TextField(customer.getAddress() != null ? customer.getAddress() : "");
            TextField tfNidPassport = new TextField(customer.getNidPassport() != null ? customer.getNidPassport() : "");
            
            // Make fields editable
            tfName.setEditable(true);
            tfPhone.setEditable(true);
            tfAddress.setEditable(true);
            tfNidPassport.setEditable(true);
            
            infoGrid.add(new Label("Name:"), 0, 0);
            infoGrid.add(tfName, 1, 0);
            
            infoGrid.add(new Label("Phone:"), 0, 1);
            infoGrid.add(tfPhone, 1, 1);
            
            infoGrid.add(new Label("Address:"), 0, 2);
            infoGrid.add(tfAddress, 1, 2);
            
            infoGrid.add(new Label("NID/Passport:"), 0, 3);
            infoGrid.add(tfNidPassport, 1, 3);

            // Buttons for editing
            HBox buttonBox = new HBox(10);
            Button btnSave = new Button("Save Changes");
            Button btnCancel = new Button("Cancel");
            buttonBox.getChildren().addAll(btnSave, btnCancel);

            // Purchase History Table
            TableView<Reservation> historyTable = new TableView<>();
            TableColumn<Reservation, Integer> colId = new TableColumn<>("Reservation ID");
            TableColumn<Reservation, String> colRoom = new TableColumn<>("Room");
            TableColumn<Reservation, String> colCheckin = new TableColumn<>("Check-in");
            TableColumn<Reservation, String> colCheckout = new TableColumn<>("Check-out");
            TableColumn<Reservation, String> colStatus = new TableColumn<>("Status");
            TableColumn<Reservation, Double> colTotal = new TableColumn<>("Total (Tk)");

            colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
            colRoom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRoomNumber() != null ? cell.getValue().getRoomNumber() : "-"));
            colCheckin.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCheckin() != null ? cell.getValue().getCheckin().toString() : "-"));
            colCheckout.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCheckout() != null ? cell.getValue().getCheckout().toString() : "-"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

            historyTable.getColumns().addAll(colId, colRoom, colCheckin, colCheckout, colStatus, colTotal);
            historyTable.setPrefHeight(250);

            List<Reservation> history = reservationDAO.getReservationsByCustomerId(customer.getId());
            historyTable.getItems().addAll(history);

            // Save button action
            btnSave.setOnAction(e -> {
                try {
                    // Validate required fields
                    if (tfName.getText().trim().isEmpty()) {
                        showError("Name is required");
                        return;
                    }
                    if (tfPhone.getText().trim().isEmpty()) {
                        showError("Phone is required");
                        return;
                    }
                    if (tfAddress.getText().trim().isEmpty()) {
                        showError("Address is required");
                        return;
                    }
                    if (tfNidPassport.getText().trim().isEmpty()) {
                        showError("NID/Passport is required");
                        return;
                    }

                    // Update customer
                    customer.setName(tfName.getText().trim());
                    customer.setPhone(tfPhone.getText().trim());
                    customer.setAddress(tfAddress.getText().trim());
                    customer.setNidPassport(tfNidPassport.getText().trim());
                    
                    customerDAO.updateCustomer(customer);
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Guest information updated successfully!");
                    success.showAndWait();
                    
                    // Refresh the table
                    loadGuests();
                    
                    // Close dialog
                    dialog.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Failed to update guest information: " + ex.getMessage());
                }
            });

            // Cancel button action
            btnCancel.setOnAction(e -> dialog.close());

            content.getChildren().addAll(
                    new Label("Guest Information:"),
                    infoGrid,
                    buttonBox,
                    new Label("Purchase History:"),
                    historyTable
            );

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load guest details: " + e.getMessage());
        }
    }

    @FXML
    public void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) tblGuests.getScene().getWindow();
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

