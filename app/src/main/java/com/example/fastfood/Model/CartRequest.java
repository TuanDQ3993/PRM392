package com.example.fastfood.Model;

import com.google.gson.annotations.SerializedName;

public class CartRequest {
    @SerializedName("itemID")
    private int itemId;

    @SerializedName("quantity")
    private int quantity;

    public CartRequest(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
} 