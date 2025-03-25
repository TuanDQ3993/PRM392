package com.example.fastfood.network;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.fastfood.utils.SessionManager;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://10.0.2.2:7254/api/";
    private static ApiService instance;
    private static SessionManager sessionManager;

    public static ApiService getInstance() {
        if (instance == null) {
            // Create a custom Gson instance with date format
            Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

            // Create OkHttpClient with SSL trust
            OkHttpClient.Builder clientBuilder = UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder();

            // Add logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
                Log.d("API Response", message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);

            // Add authorization interceptor if token exists
            if (sessionManager != null) {
                clientBuilder.addInterceptor(chain -> {
                    Request original = chain.request();
                    // Only add token if not already present in request
                    if (original.header("Authorization") == null && sessionManager.isLoggedIn()) {
                        Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + sessionManager.getToken());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                    return chain.proceed(original);
                });
            }

            // Create Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

            instance = retrofit.create(ApiService.class);
        }
        return instance;
    }

    public static void setSessionManager(SessionManager manager) {
        sessionManager = manager;
        // Reset instance to recreate with new session manager
        instance = null;
    }
} 