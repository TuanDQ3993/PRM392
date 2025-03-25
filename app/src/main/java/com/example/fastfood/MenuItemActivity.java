//package com.example.fastfood;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.fastfood.adapter.CategoryAdapter;
//import com.example.fastfood.adapter.MenuAdapter;
//import com.example.fastfood.Model.Category;
//import com.example.fastfood.Model.MenuItem;
//import com.example.fastfood.network.RetrofitClient;
//import com.example.fastfood.network.ApiService;
//
//import java.util.ArrayList;
//import java.util.List;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MenuItemActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {
//    private static final String TAG = "MenuItemActivity";
//    private RecyclerView categoryRecyclerView;
//    private RecyclerView menuRecyclerView;
//    private CategoryAdapter categoryAdapter;
//    private MenuAdapter menuAdapter;
//    private List<MenuItem> menuItems = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_menu_item);
//
//        // Initialize RecyclerViews
//        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
//        menuRecyclerView = findViewById(R.id.menuRecyclerView);
//
//        // Set up category RecyclerView
//        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        categoryAdapter = new CategoryAdapter(this);
//        categoryRecyclerView.setAdapter(categoryAdapter);
//
//        // Set up menu RecyclerView
//        menuAdapter = new MenuAdapter(menuItems);
//        menuRecyclerView.setAdapter(menuAdapter);
//
//        // Load initial data
//        loadCategories();
//    }
//
//    private void loadCategories() {
//        Log.d(TAG, "Loading categories...");
//        try {
//            ApiService apiService = RetrofitClient.getInstance();
//            apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
//                @Override
//                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
//                    Log.d(TAG, "Categories response code: " + response.code());
//                    if (response.isSuccessful() && response.body() != null) {
//                        List<Category> categories = response.body();
//                        Log.d(TAG, "Categories loaded: " + categories.size());
//                        categoryAdapter.setCategories(categories);
//
//                        // Load menu items for first category
//                        if (!categories.isEmpty()) {
//                            loadMenuByCategory(categories.get(0).getCategoryId());
//                        }
//                    } else {
//                        Log.e(TAG, "Failed to load categories: " + response.code());
//                        Toast.makeText(MenuItemActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<Category>> call, Throwable t) {
//                    Log.e(TAG, "Error loading categories", t);
//                    Toast.makeText(MenuItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "Exception in loadCategories", e);
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void loadMenuByCategory(int categoryId) {
//        Log.d(TAG, "Loading menu items for category: " + categoryId);
//        try {
//            ApiService apiService = RetrofitClient.getInstance();
//            apiService.getMenuByCategory(categoryId).enqueue(new Callback<List<MenuItem>>() {
//                @Override
//                public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
//                    Log.d(TAG, "Menu items response code: " + response.code());
//                    if (response.isSuccessful() && response.body() != null) {
//                        menuItems.clear();
//                        menuItems.addAll(response.body());
//                        Log.d(TAG, "Menu items loaded: " + menuItems.size());
//                        menuAdapter.notifyDataSetChanged();
//                    } else {
//                        Log.e(TAG, "Failed to load menu items: " + response.code());
//                        Toast.makeText(MenuItemActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<MenuItem>> call, Throwable t) {
//                    Log.e(TAG, "Error loading menu items", t);
//                    Toast.makeText(MenuItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "Exception in loadMenuByCategory", e);
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void loadMenuItems() {
//        Log.d(TAG, "LoadMenuItems: Starting API call");
//        try {
//            ApiService apiService = RetrofitClient.getInstance();
//            Call<List<MenuItem>> call = apiService.getAllMenu();
//
//            call.enqueue(new Callback<List<MenuItem>>() {
//                @Override
//                public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        menuItems.clear();
//                        menuItems.addAll(response.body());
//                        menuAdapter.notifyDataSetChanged();
//                        Log.d(TAG, "Menu items loaded successfully: " + menuItems.size() + " items");
//                    } else {
//                        Log.e(TAG, "Failed to load menu items: " + response.code());
//                        Toast.makeText(MenuItemActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<MenuItem>> call, Throwable t) {
//                    Log.e(TAG, "Error loading menu items", t);
//                    Toast.makeText(MenuItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "Exception in loadMenuItems", e);
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onCategoryClick(Category category) {
//        Log.d(TAG, "Category clicked: " + category.getName());
//        loadMenuByCategory(category.getCategoryId());
//    }
//}