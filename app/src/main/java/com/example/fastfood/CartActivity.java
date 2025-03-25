package com.example.fastfood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fastfood.Model.CartItem;
import com.example.fastfood.Model.CartRequest;
import com.example.fastfood.Model.QuantityRequest;
import com.example.fastfood.adapter.CartAdapter;
import com.example.fastfood.network.ApiService;
import com.example.fastfood.network.RetrofitClient;
import com.example.fastfood.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceValue;
    private MaterialButton checkoutButton;
    private SessionManager sessionManager;
    private List<CartItem> cartItems;
    private static final int REQUEST_CHECKOUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sessionManager = new SessionManager(this);
        cartItems = new ArrayList<>();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceValue = findViewById(R.id.totalPriceValue);
        checkoutButton = findViewById(R.id.checkoutButton);

        // Setup RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);

        // Load cart items
        loadCartItems();

        // Setup checkout button
        setupCheckoutButton();
    }

    private void loadCartItems() {
        ApiService apiService = RetrofitClient.getInstance();
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.getCartItems(token).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CartActivity", "Raw cart items response: " + response.body());
                    cartItems.clear();
                    List<CartItem> items = response.body();
                    for (CartItem item : items) {
                        Log.d("CartActivity", "Cart item: " + 
                            "id=" + item.getItemId
                                () +
                            ", name=" + item.getItemName() + 
                            ", price=" + item.getPrice() + 
                            ", quantity=" + item.getQuantity() + 
                            ", imageUrl=" + item.getImageUrl());
                    }
                    cartItems.addAll(items);
                    cartAdapter.updateItems(cartItems);
                    updateTotalPrice();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("CartActivity", "Failed to load cart items. Code: " + response.code() + 
                          ", Error: " + errorBody);
                    Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        totalPriceValue.setText(formatter.format(total));
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        ApiService apiService = RetrofitClient.getInstance();
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.updateCartItemQuantity(token, item.getItemId(), newQuantity).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    item.setQuantity(newQuantity);
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();
                } else {
                    String errorMessage = "Failed to update quantity";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += ": " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("CartActivity", errorMessage);
                    Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "Error: " + t.getMessage();
                Log.e("CartActivity", "Update quantity failed", t);
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemDeleted(CartItem item) {
        ApiService apiService = RetrofitClient.getInstance();
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.removeFromCart(token, item.getItemId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    cartItems.remove(item);
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();
                    Toast.makeText(CartActivity.this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCheckoutButton() {
        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems));
            startActivityForResult(intent, REQUEST_CHECKOUT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECKOUT && resultCode == RESULT_OK) {
            // Clear cart after successful checkout
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
            Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 