package com.example.fastfood.Model;

import com.google.gson.annotations.SerializedName;

public class CheckoutInfo {
    @SerializedName("userId")
    private int userId;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerEmail")
    private String customerEmail;

    @SerializedName("customerPhone")
    private String customerPhone;

    @SerializedName("customerAddress")
    private String customerAddress;

    public CheckoutInfo(int userId, String customerName, String customerEmail, String customerPhone, String customerAddress) {
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
} 