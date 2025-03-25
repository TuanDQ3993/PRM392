package com.example.fastfood.Model;

import android.util.Log;
import com.google.gson.annotations.SerializedName;

// MenuItem.java
public class MenuItem {
    private static final String TAG = "MenuItem";

    @SerializedName("itemId")
    private int menuItemId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    // Default constructor for Gson
    public MenuItem() {
        Log.d(TAG, "Creating new MenuItem instance");
    }

    // Constructors
    public MenuItem(int menuItemId, String name, String description, double price, String imageUrl, int categoryId, String categoryName) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        Log.d(TAG, "Created MenuItem with id: " + menuItemId);
    }

    // Getters and setters
    public int getMenuItemId() {
        Log.d(TAG, "Getting menuItemId: " + menuItemId);
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        Log.d(TAG, "Setting menuItemId from " + this.menuItemId + " to " + menuItemId);
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "menuItemId=" + menuItemId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
