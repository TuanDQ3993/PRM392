package com.example.fastfood.Model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class CartItem implements Parcelable {
    @SerializedName("itemID")
    private int itemId;

    private int userId;

    @SerializedName("name")
    private String itemName;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("price")
    private double price;

    @SerializedName("quantity")
    private int quantity;

    public CartItem() {
    }

    public CartItem(int itemId, int userId, String itemName, String imageUrl, double price, int quantity) {
        this.itemId = itemId;
        this.userId = userId;
        this.itemName = itemName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    protected CartItem(Parcel in) {
        itemId = in.readInt();
        userId = in.readInt();
        itemName = in.readString();
        imageUrl = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return price * quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemId);
        dest.writeInt(userId);
        dest.writeString(itemName);
        dest.writeString(imageUrl);
        dest.writeDouble(price);
        dest.writeInt(quantity);
    }
} 