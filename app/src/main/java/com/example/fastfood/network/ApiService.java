package com.example.fastfood.network;

import com.example.fastfood.Model.CartItem;
import com.example.fastfood.Model.CartRequest;
import com.example.fastfood.Model.Category;
import com.example.fastfood.Model.CheckoutInfo;
import com.example.fastfood.Model.LoginRequest;
import com.example.fastfood.Model.MenuItem;
import com.example.fastfood.Model.Order;
import com.example.fastfood.Model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/login")
    Call<User> login(@Body LoginRequest loginRequest);

    @GET("Category/getAllCategories")
    Call<List<Category>> getAllCategories();

    @GET("Menu/getAllMenu")
    Call<List<MenuItem>> getAllMenu();

    @GET("Menu/getMenuByCategory")
    Call<List<MenuItem>> getMenuByCategory(@Query("categoryId") int categoryId);

    @POST("cart/add")
    Call<Void> addToCart(@Header("Authorization") String token, @Body CartRequest cartRequest);

    @GET("cart")
    Call<List<CartItem>> getCartItems(@Header("Authorization") String token);

    @PUT("cart/update/{itemId}")
    Call<Void> updateCartItemQuantity(
        @Header("Authorization") String token,
        @Path("itemId") int cartItemId,
        @Body int quantity
    );

    @DELETE("cart/remove/{itemId}")
    Call<Void> removeFromCart(
        @Header("Authorization") String token,
        @Path("itemId") int cartItemId
    );

    @POST("cart/checkout")
    Call<Void> checkout(@Header("Authorization") String token);

    @GET("Order/user-orders")
    Call<List<Order>> getUserOrders(@Header("Authorization") String token, @Query("userId") int userId);

    @GET("Checkout/checkout-info")
    Call<CheckoutInfo> getCheckoutInfo(@Header("Authorization") String token, @Query("userId") int userId);

    @POST("Checkout/checkout")
    Call<Void> checkout(@Header("Authorization") String token, @Body CheckoutInfo checkoutInfo);
} 