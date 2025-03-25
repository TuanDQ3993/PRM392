package com.example.fastfood.Model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Order {
    @SerializedName("orderId")
    private int orderId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("orderDate")
    private Date orderDate;

    @SerializedName("statusId")
    private int statusId;

    @SerializedName("statusName")
    private String statusName;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("orderDetails")
    private List<OrderDetail> orderDetails;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getStatus() {
        switch (statusId) {
            case 1:
                return "Đang chờ xử lý";
            case 2:
                return "Đang xử lý";
            case 3:
                return "Đã giao hàng";
            case 4:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }
} 