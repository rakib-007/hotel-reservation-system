// src/main/java/com/hotelapp/models/Reservation.java
package com.hotelapp.models;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private int customerId;
    private int roomId;
    private LocalDate checkin;
    private LocalDate checkout;
    private String status;
    private double total;
    // convenience fields used by controllers/DAOs for display
    private String customerName;
    private String customerPhone;
    private String roomNumber;

    public Reservation() {}

    public Reservation(int customerId, int roomId, LocalDate checkin, LocalDate checkout, String status, double total) {
        this.customerId = customerId;
        this.roomId = roomId;
        this.checkin = checkin;
        this.checkout = checkout;
        this.status = status;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public LocalDate getCheckin() { return checkin; }
    public void setCheckin(LocalDate checkin) { this.checkin = checkin; }

    public LocalDate getCheckout() { return checkout; }
    public void setCheckout(LocalDate checkout) { this.checkout = checkout; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
}
