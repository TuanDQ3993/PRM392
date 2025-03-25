package com.example.fastfood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fastfood.adapter.CategoryAdapter;
import com.example.fastfood.adapter.MenuAdapter;
import com.example.fastfood.Model.Category;
import com.example.fastfood.Model.MenuItem;
import com.example.fastfood.network.ApiService;
import com.example.fastfood.network.RetrofitClient;
import com.example.fastfood.utils.SessionManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {
    private static final String TAG = "MainActivity";
    private RecyclerView categoryRecyclerView;
    private RecyclerView menuRecyclerView;
    private CategoryAdapter categoryAdapter;
    private MenuAdapter menuAdapter;
    private List<MenuItem> menuItems = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize SessionManager and check login status before setting content view
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);

        RetrofitClient.setSessionManager(sessionManager);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Fast Food");
        }

        // Initialize RecyclerViews
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        menuRecyclerView = findViewById(R.id.menuRecyclerView);

        // Initialize lists
        menuItems = new ArrayList<>();

        // Set up category RecyclerView
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Set up menu RecyclerView
        menuAdapter = new MenuAdapter(this, menuItems, sessionManager);
        menuRecyclerView.setAdapter(menuAdapter);

        // Load initial data
        loadCategories();
        loadAllMenuItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Make sure the cart icon is visible
        android.view.MenuItem cartItem = menu.findItem(R.id.action_cart);
        if (cartItem != null) {
            cartItem.setVisible(true);
            cartItem.setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS);
            Log.d(TAG, "Cart icon configured - Visible: " + cartItem.isVisible());
        } else {
            Log.e(TAG, "Cart menu item not found!");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "Menu item selected: " + id);
        
        if (id == R.id.action_logout) {
            Log.d(TAG, "Logout selected");
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_cart) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, CartActivity.class));
            } else {
                Toast.makeText(this, "Please login to view cart", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_order_history) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, OrderHistoryActivity.class));
            } else {
                Toast.makeText(this, "Please login to view order history", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status when activity resumes
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadCategories() {
        Log.d(TAG, "Loading categories...");
        try {
            ApiService apiService = RetrofitClient.getInstance();
            apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
                @Override
                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                    Log.d(TAG, "Categories response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        List<Category> categories = response.body();
                        Log.d(TAG, "Categories loaded: " + categories.size());
                        categoryAdapter.setCategories(categories);
                    } else {
                        Log.e(TAG, "Failed to load categories: " + response.code());
                        if (response.code() == 401) {
                            // Token expired or invalid
                            sessionManager.logout();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Category>> call, Throwable t) {
                    Log.e(TAG, "Error loading categories", t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in loadCategories", e);
        }
    }

    private void loadAllMenuItems() {
        Log.d(TAG, "Loading all menu items...");
        RetrofitClient.getInstance().getAllMenu().enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Raw API Response: " + response.raw().toString());
                    menuItems.clear();
                    List<MenuItem> receivedItems = response.body();
                    Log.d(TAG, "Received items (raw): " + receivedItems);
                    
                    for (MenuItem item : receivedItems) {
                        Log.d(TAG, "Processing item - Raw data: id=" + item.getMenuItemId() + 
                                  ", name=" + item.getName() + 
                                  ", price=" + item.getPrice());
                    }
                    
                    menuItems.addAll(receivedItems);
                    Log.d(TAG, "Added " + menuItems.size() + " items to menu list");
                    menuAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Failed to load menu items. Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Log.e(TAG, "Error loading menu items", t);
                Toast.makeText(MainActivity.this, 
                    "Failed to load menu items: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        Log.d(TAG, "Category clicked: " + category.getName());
        loadMenuByCategory(category.getCategoryId());
    }

    private void loadMenuByCategory(int categoryId) {
        Log.d(TAG, "Loading menu items for category: " + categoryId);
        try {
            ApiService apiService = RetrofitClient.getInstance();
            apiService.getMenuByCategory(categoryId).enqueue(new Callback<List<MenuItem>>() {
                @Override
                public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                    Log.d(TAG, "Menu items response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        menuItems.clear();
                        menuItems.addAll(response.body());
                        Log.d(TAG, "Menu items loaded: " + menuItems.size());
                        menuAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Failed to load menu items: " + response.code());
                        if (response.code() == 401) {
                            // Token expired or invalid
                            sessionManager.logout();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                    Log.e(TAG, "Error loading menu items", t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in loadMenuByCategory", e);
        }
    }
} 