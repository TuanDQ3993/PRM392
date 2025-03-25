package com.example.fastfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fastfood.Model.CartRequest;
import com.example.fastfood.Model.MenuItem;
import com.example.fastfood.R;
import com.example.fastfood.network.ApiService;
import com.example.fastfood.network.RetrofitClient;
import com.example.fastfood.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private static final String TAG = "MenuAdapter";
    private List<MenuItem> menuItems;
    private Context context;
    private SessionManager sessionManager;

    public MenuAdapter(Context context, List<MenuItem> menuItems, SessionManager sessionManager) {
        this.context = context;
        this.menuItems = menuItems;
        this.sessionManager = sessionManager;
        Log.d(TAG, "MenuAdapter initialized with SessionManager - isLoggedIn: " + sessionManager.isLoggedIn());
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        Log.d(TAG, "Binding menu item: " + menuItem.toString());
        holder.bind(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView menuItemImage;
        private TextView menuItemName;
        private TextView menuItemPrice;
        private MaterialButton addToCartButton;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuItemImage = itemView.findViewById(R.id.menuItemImage);
            menuItemName = itemView.findViewById(R.id.menuItemName);
            menuItemPrice = itemView.findViewById(R.id.menuItemPrice);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }

        public void bind(MenuItem menuItem) {
            menuItemName.setText(menuItem.getName());
            menuItemPrice.setText(String.format("$%.1f", menuItem.getPrice()));
            
            if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(menuItem.getImageUrl())
                    .placeholder(R.drawable.ic_shopping_cart)
                    .into(menuItemImage);
            }

            addToCartButton.setOnClickListener(v -> addToCart(menuItem));
        }

        private void addToCart(MenuItem menuItem) {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(context, "Please login to add items to cart", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = sessionManager.getUserId();
            int menuItemId = menuItem.getMenuItemId();

            Log.d(TAG, "Adding to cart - MenuItem: " + menuItem.toString());
            Log.d(TAG, "User ID from session: " + userId);
            Log.d(TAG, "Menu Item ID: " + menuItemId);

            if (userId <= 0) {
                Toast.makeText(context, "Invalid user ID. Please login again.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (menuItemId <= 0) {
                Toast.makeText(context, "Invalid menu item ID: " + menuItemId, Toast.LENGTH_SHORT).show();
                return;
            }

            CartRequest cartRequest = new CartRequest(menuItemId, 1);
            Log.d(TAG, "Cart Request - itemId: " + cartRequest.getItemId() + 
                      ", quantity: " + cartRequest.getQuantity());

            ApiService apiService = RetrofitClient.getInstance();
            String token = sessionManager.getToken();

            if (token == null || token.isEmpty()) {
                Toast.makeText(context, "Invalid token. Please login again.", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.addToCart("Bearer " + token, cartRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, 
                            menuItem.getName() + " added to cart", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage;
                        if (response.code() == 401) {
                            errorMessage = "Session expired. Please login again.";
                        } else if (response.code() == 400) {
                            errorMessage = "Invalid request. Please try again.";
                        } else {
                            errorMessage = "Failed to add to cart. Error code: " + response.code();
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Add to cart failed with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    String errorMessage = "Error adding to cart: " + t.getMessage();
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Add to cart failed", t);
                }
            });
        }
    }
}