package com.example.fastfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.auth0.android.jwt.JWT;
import com.example.fastfood.Model.LoginRequest;
import com.example.fastfood.Model.User;
import com.example.fastfood.network.RetrofitClient;
import com.example.fastfood.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        
        setContentView(R.layout.activity_login);

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private boolean validateInput() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return false;
        }
        return true;
    }

    private void performLogin() {
        if (!validateInput()) {
            return;
        }

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        LoginRequest loginRequest = new LoginRequest(email, password);

        RetrofitClient.getInstance().login(loginRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String token = user.getToken();
                    
                    try {
                        // Parse JWT token to get user ID
                        JWT jwt = new JWT(token);
                        String userIdClaim = jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier").asString();
                        int userId = Integer.parseInt(userIdClaim);
                        
                        Log.d(TAG, "Login successful - Token: " + (token.isEmpty() ? "empty" : "exists") + 
                                  ", UserId from JWT: " + userId);
                        
                        // Save user session
                        sessionManager.setUserId(userId);
                        sessionManager.setToken(token);
                        sessionManager.setLogin(true);

                        // Navigate to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JWT token", e);
                        Toast.makeText(LoginActivity.this, 
                            "Error processing login response", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Login failed: ";
                    if (response.code() == 401) {
                        errorMessage += "Invalid credentials";
                    } else if (response.code() == 404) {
                        errorMessage += "User not found";
                    } else {
                        errorMessage += "Server error";
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Login request failed", t);
                Toast.makeText(LoginActivity.this,
                        "Login error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
} 