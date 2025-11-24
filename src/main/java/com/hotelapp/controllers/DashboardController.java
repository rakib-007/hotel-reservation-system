package com.hotelapp.controllers;

import com.hotelapp.dao.CustomerDAO;
import com.hotelapp.dao.ReservationDAO;
import com.hotelapp.dao.RoomDAO;
import com.hotelapp.models.Customer;
import com.hotelapp.models.Reservation;
import com.hotelapp.models.Room;
import com.hotelapp.services.ReservationService;
import com.hotelapp.utils.DBInit;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.util.List;

/**
 * DashboardController - main landing screen controller.
 *
 * Guideline document (for reference inside code/UI): file:///mnt/data/OOP Lab CEP Updated.docx
 */
public class DashboardController {

    // Path to the uploaded guideline doc (developer requested to include)
    public static final String GUIDELINE_DOC_URL = "file:///mnt/data/OOP Lab CEP Updated.docx";

    @FXML private Label lblTotalRooms;
    @FXML private Label lblFreeRooms;
    @FXML private Label lblOccupiedRooms;
    @FXML private Label lblTotalReservations;
    @FXML private Label lblTodayCheckIns;
    @FXML private Label lblTodayCheckOuts;

    @FXML private TableView<Reservation> tblReservations;
    @FXML private TableColumn<Reservation, Integer> colResId;
    @FXML private TableColumn<Reservation, String> colResRoom;
    @FXML private TableColumn<Reservation, String> colResGuest;
    @FXML private TableColumn<Reservation, String> colResPhone;
    @FXML private TableColumn<Reservation, LocalDate> colResCheckin;
    @FXML private TableColumn<Reservation, LocalDate> colResCheckout;
    @FXML private TableColumn<Reservation, String> colResStatus;
    @FXML private TableColumn<Reservation, Double> colResTotal;

    @FXML private TextField tfGuestSearch;
    @FXML private GridPane gridGuestInfo;
    @FXML private Label lblGuestName;
    @FXML private Label lblGuestPhone;
    @FXML private Label lblGuestAddress;
    @FXML private Label lblGuestNid;
    @FXML private TableView<Reservation> tblGuestHistory;

    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        // Configure table columns (matching Reservation model getters)
        try {
            colResId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
            colResRoom.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getRoomNumber()));
            colResGuest.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCustomerName()));
            colResPhone.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCustomerPhone() != null ? cell.getValue().getCustomerPhone() : "-"));
            colResCheckin.setCellValueFactory(new PropertyValueFactory<>("checkin"));
            colResCheckout.setCellValueFactory(new PropertyValueFactory<>("checkout"));
            colResStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colResTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        } catch (Exception e) {
            // In case columns not wired in FXML exactly (defensive)
            e.printStackTrace();
        }

        // Double-click to open reservation details
        tblReservations.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) {
                Reservation sel = tblReservations.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    openReservationDetail(sel.getId());
                }
            }
        });

        // Style rows based on reservation status (red for cancelled)
        tblReservations.setRowFactory(tv -> {
            TableRow<Reservation> row = new TableRow<Reservation>() {
                @Override
                protected void updateItem(Reservation item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else {
                        // Set red background for cancelled reservations
                        if ("CANCELLED".equalsIgnoreCase(item.getStatus())) {
                            setStyle("-fx-background-color: #ffcccc;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
            return row;
        });

        loadStats();
        loadRecentReservations();
    }

    /**
     * Load counts / summary on the dashboard.
     */
    @FXML
    public void loadStats() {
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            int total = rooms.size();
            int free = (int) rooms.stream().filter(r -> "FREE".equalsIgnoreCase(r.getStatus())).count();
            int occupied = (int) rooms.stream().filter(r -> "BOOKED".equalsIgnoreCase(r.getStatus())).count();

            lblTotalRooms.setText(String.valueOf(total));
            lblFreeRooms.setText(String.valueOf(free));
            lblOccupiedRooms.setText(String.valueOf(occupied));

            // total reservations count
            List<Reservation> all = reservationDAO.getAllReservations();
            lblTotalReservations.setText(String.valueOf(all.size()));

            // Today's check-ins and check-outs
            List<Reservation> todayCheckIns = reservationService.getTodayCheckIns();
            lblTodayCheckIns.setText(String.valueOf(todayCheckIns.size()));

            List<Reservation> todayCheckOuts = reservationService.getTodayCheckOuts();
            lblTodayCheckOuts.setText(String.valueOf(todayCheckOuts.size()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load dashboard stats: " + e.getMessage());
        }
    }

    /**
     * Load recent reservations into the table.
     */
    @FXML
    public void loadRecentReservations() {
        try {
            tblReservations.getItems().clear();
            // show last N recent reservations (DAO returns ordered by created_at desc)
            List<Reservation> recent = reservationDAO.getAllReservations();
            tblReservations.getItems().addAll(recent);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load reservations: " + e.getMessage());
        }
    }

    /**
     * Open the reservation detail view for a reservation id.
     */
    private void openReservationDetail(int reservationId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reservation_detail.fxml"));
            Parent root = loader.load();

            // pass id to controller
            ReservationDetailController ctrl = loader.getController();
            ctrl.setReservationId(reservationId);

            Stage stage = (Stage) lblTotalRooms.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to open reservation details: " + e.getMessage());
        }
    }

    /**
     * Open the Rooms Management screen.
     */
    @FXML
    public void openRooms() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/rooms.fxml"));
            Stage stage = (Stage) lblTotalRooms.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to open Rooms management: " + e.getMessage());
        }
    }

    /**
     * Open the Guests Management screen.
     */
    @FXML
    public void openGuests() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/guests.fxml"));
            Stage stage = (Stage) lblTotalRooms.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to open Guests management: " + e.getMessage());
        }
    }

    /**
     * Open the New Reservation screen (reserve.fxml).
     */
    @FXML
    public void openNewReservation() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/reserve.fxml"));
            Stage stage = (Stage) lblTotalRooms.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to open New Reservation: " + e.getMessage());
        }
    }

    /**
     * Open the Check-in/Check-out Management screen.
     */
    @FXML
    public void openCheckInOut() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/checkinout.fxml"));
            Stage stage = (Stage) lblTotalRooms.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to open Check-in/Check-out: " + e.getMessage());
        }
    }

    /**
     * Refresh dashboard data (button binding).
     */
    @FXML
    public void handleRefresh() {
        loadStats();
        loadRecentReservations();
    }

    @FXML
    public void searchGuest() {
        String searchText = tfGuestSearch.getText().trim();
        if (searchText.isEmpty()) {
            showError("Please enter phone number or NID/Passport number");
            return;
        }

        try {
            // Search by phone or nid_passport (handle case where columns might not exist)
            String sql = "SELECT id, name, phone, " +
                    "COALESCE(address, '') as address, " +
                    "COALESCE(nid_passport, '') as nid_passport " +
                    "FROM customers WHERE phone = ? OR nid_passport = ?";
            Customer customer = null;
            try (var conn = com.hotelapp.utils.DBUtil.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                ps.setString(1, searchText);
                ps.setString(2, searchText);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        customer = new Customer();
                        customer.setId(rs.getInt("id"));
                        customer.setName(rs.getString("name"));
                        customer.setPhone(rs.getString("phone"));
                        String address = rs.getString("address");
                        customer.setAddress(address != null && !address.isEmpty() ? address : null);
                        String nid = rs.getString("nid_passport");
                        customer.setNidPassport(nid != null && !nid.isEmpty() ? nid : null);
                    }
                }
            }

            if (customer == null) {
                showError("Customer not found");
                gridGuestInfo.setVisible(false);
                tblGuestHistory.setVisible(false);
                return;
            }

            // Display customer information
            lblGuestName.setText(customer.getName() != null ? customer.getName() : "-");
            lblGuestPhone.setText(customer.getPhone() != null ? customer.getPhone() : "-");
            lblGuestAddress.setText(customer.getAddress() != null ? customer.getAddress() : "-");
            lblGuestNid.setText(customer.getNidPassport() != null ? customer.getNidPassport() : "-");
            gridGuestInfo.setVisible(true);

            // Load purchase history - show ALL reservations for this customer
            var history = reservationDAO.getReservationsByCustomerId(customer.getId());
            tblGuestHistory.getItems().clear();
            if (!history.isEmpty()) {
                tblGuestHistory.getItems().addAll(history);
                tblGuestHistory.setVisible(true);
            } else {
                tblGuestHistory.setVisible(false);
            }

            // Configure history table columns (configure once in initialize if needed, but this works too)
            if (tblGuestHistory.getColumns().size() == 6 && tblGuestHistory.getColumns().get(0).getCellValueFactory() == null) {
                @SuppressWarnings("unchecked")
                var cols = tblGuestHistory.getColumns();
                ((TableColumn<Reservation, Integer>) cols.get(0)).setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
                ((TableColumn<Reservation, String>) cols.get(1)).setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getRoomNumber()));
                ((TableColumn<Reservation, LocalDate>) cols.get(2)).setCellValueFactory(new PropertyValueFactory<>("checkin"));
                ((TableColumn<Reservation, LocalDate>) cols.get(3)).setCellValueFactory(new PropertyValueFactory<>("checkout"));
                ((TableColumn<Reservation, String>) cols.get(4)).setCellValueFactory(new PropertyValueFactory<>("status"));
                ((TableColumn<Reservation, Double>) cols.get(5)).setCellValueFactory(new PropertyValueFactory<>("total"));
            }
            
            // Style history table rows (red for cancelled)
            tblGuestHistory.setRowFactory(tv -> {
                TableRow<Reservation> row = new TableRow<Reservation>() {
                    @Override
                    protected void updateItem(Reservation item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setStyle("");
                        } else {
                            if ("CANCELLED".equalsIgnoreCase(item.getStatus())) {
                                setStyle("-fx-background-color: #ffcccc;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                };
                return row;
            });
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to search guest: " + e.getMessage());
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
