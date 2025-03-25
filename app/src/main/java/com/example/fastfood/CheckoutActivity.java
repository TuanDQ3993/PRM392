package com.example.fastfood;

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
import com.example.fastfood.Model.CheckoutInfo;
import com.example.fastfood.adapter.CartAdapter;
import com.example.fastfood.network.ApiService;
import com.example.fastfood.network.RetrofitClient;
import com.example.fastfood.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private TextInputEditText etCustomerName, etCustomerEmail, etCustomerPhone, etCustomerAddress;
    private RecyclerView rvOrderItems;
    private TextView tvTotalAmount;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private SessionManager sessionManager;
    private ApiService apiService;
    private NumberFormat currencyFormatter;
    private List<CartItem> cartItems;
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize
        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getInstance();
        currencyFormatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initViews();

        // Load checkout info
        loadCheckoutInfo();

        // Setup cart items
        setupCartItems();

        // Setup checkout button
        setupCheckoutButton();
    }

    private void initViews() {
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerEmail = findViewById(R.id.etCustomerEmail);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCheckoutInfo() {
        int userId = sessionManager.getUserId();
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.getCheckoutInfo(token, userId).enqueue(new Callback<CheckoutInfo>() {
            @Override
            public void onResponse(Call<CheckoutInfo> call, Response<CheckoutInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CheckoutInfo info = response.body();
                    etCustomerName.setText(info.getCustomerName());
                    etCustomerEmail.setText(info.getCustomerEmail());
                    etCustomerPhone.setText(info.getCustomerPhone());
                    etCustomerAddress.setText(info.getCustomerAddress());
                } else {
                    Log.e(TAG, "Failed to load checkout info: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CheckoutInfo> call, Throwable t) {
                Log.e(TAG, "Error loading checkout info", t);
                Toast.makeText(CheckoutActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCartItems() {
        // Get cart items from intent
        cartItems = getIntent().getParcelableArrayListExtra("cartItems");
        if (cartItems != null) {
            cartAdapter = new CartAdapter(this, cartItems, false); // false to disable editing
            rvOrderItems.setAdapter(cartAdapter);

            // Calculate total
            totalAmount = 0;
            for (CartItem item : cartItems) {
                totalAmount += item.getPrice() * item.getQuantity();
            }
            tvTotalAmount.setText("Tổng tiền: " + currencyFormatter.format(totalAmount) + " VND");
        }
    }

    private void setupCheckoutButton() {
        btnCheckout.setOnClickListener(v -> {
            if (validateInput()) {
                String token = "Bearer " + sessionManager.getToken();
                CheckoutInfo checkoutInfo = new CheckoutInfo(
                    sessionManager.getUserId(),
                    etCustomerName.getText().toString(),
                    etCustomerEmail.getText().toString(),
                    etCustomerPhone.getText().toString(),
                    etCustomerAddress.getText().toString()
                );

                apiService.checkout(token, checkoutInfo).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateInput() {
        if (etCustomerName.getText().toString().trim().isEmpty()) {
            etCustomerName.setError("Vui lòng nhập họ tên");
            return false;
        }
        if (etCustomerEmail.getText().toString().trim().isEmpty()) {
            etCustomerEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (etCustomerPhone.getText().toString().trim().isEmpty()) {
            etCustomerPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (etCustomerAddress.getText().toString().trim().isEmpty()) {
            etCustomerAddress.setError("Vui lòng nhập địa chỉ");
            return false;
        }
        return true;
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