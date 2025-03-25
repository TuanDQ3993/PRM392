package com.example.fastfood.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "FastFoodSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_TOKEN = "token";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        
        // Log current session state
        int currentUserId = pref.getInt(KEY_USER_ID, -1);
        String currentToken = pref.getString(KEY_TOKEN, "");
        boolean currentLoginState = pref.getBoolean(KEY_IS_LOGGED_IN, false);
        
        Log.d(TAG, "Session initialized - UserId: " + currentUserId + 
                   ", Token: " + (currentToken.isEmpty() ? "empty" : "exists") + 
                   ", IsLoggedIn: " + currentLoginState);
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "Login state set to: " + isLoggedIn);
    }

    public void setUserId(int userId) {
        if (userId <= 0) {
            Log.w(TAG, "Setting userId: " + userId + " (warning: userId <= 0)");
        } else {
            Log.d(TAG, "Setting userId: " + userId);
        }
        editor.putInt(KEY_USER_ID, userId);
        editor.commit();
    }

    public void setToken(String token) {
        if (token == null || token.isEmpty()) {
            Log.w(TAG, "Setting empty token");
        } else {
            Log.d(TAG, "Setting token successfully");
        }
        editor.putString(KEY_TOKEN, token != null ? token : "");
        editor.commit();
    }

    public boolean isLoggedIn() {
        int storedUserId = pref.getInt(KEY_USER_ID, -1);
        String storedToken = pref.getString(KEY_TOKEN, "");
        boolean storedLoginState = pref.getBoolean(KEY_IS_LOGGED_IN, false);
        
        boolean hasUserId = storedUserId > 0;
        boolean hasToken = !storedToken.isEmpty();
        
        Log.d(TAG, "Login check - HasUserId: " + hasUserId + 
                   " (id=" + storedUserId + ")" +
                   ", HasToken: " + hasToken + 
                   ", StoredLoginState: " + storedLoginState);
                   
        return storedLoginState && hasToken;
    }

    public int getUserId() {
        int userId = pref.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Getting userId: " + userId);
        return userId;
    }

    public String getToken() {
        String token = pref.getString(KEY_TOKEN, "");
        Log.d(TAG, "Getting token: " + (token.isEmpty() ? "empty" : "exists"));
        return token;
    }

    public void logout() {
        Log.d(TAG, "Logging out - clearing all session data");
        editor.clear();
        editor.commit();
    }
} 