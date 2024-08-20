package com.example.parkmanagement;
public class Booking {
    private int bookingId;
    private String code;
    private String category;
    private String vehicleNumber;
    private String entryTime;
    private int extraAmount;
    private String exitTime;
    private String paymentStatus;
    private String status;

    public Booking(int bookingId, String code, String category, String vehicleNumber, String entryTime, int extraAmount, String exitTime, String paymentStatus, String status) {
        this.bookingId = bookingId;
        this.code = code;
        this.category = category;
        this.vehicleNumber = vehicleNumber;
        this.entryTime = entryTime;
        this.extraAmount = extraAmount;
        this.exitTime = exitTime;
        this.paymentStatus = paymentStatus;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public int getExtraAmount() {
        return extraAmount;
    }

    public String getExitTime() {
        return exitTime;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getStatus() {
        return status;
    }
}




