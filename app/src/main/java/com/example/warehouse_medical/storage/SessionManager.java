package com.example.warehouse_medical.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.utils.AppConstants;

public class SessionManager {

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(AppConstants.KEY_TOKEN, token).apply();
    }

    public void saveUser(User user) {
        if (user == null) {
            return;
        }
        sharedPreferences.edit()
                .putString(AppConstants.KEY_USER_ID, user.getId())
                .putString(AppConstants.KEY_FULL_NAME, user.getFullName())
                .putString(AppConstants.KEY_EMAIL, user.getEmail())
                .putString(AppConstants.KEY_ROLE, user.getRole())
                .apply();
    }

    public void saveSession(String token, User user) {
        saveToken(token);
        saveUser(user);
    }

    public String getToken() {
        return sharedPreferences.getString(AppConstants.KEY_TOKEN, null);
    }

    public String getRole() {
        return sharedPreferences.getString(AppConstants.KEY_ROLE, null);
    }

    public String getFullName() {
        return sharedPreferences.getString(AppConstants.KEY_FULL_NAME, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(AppConstants.KEY_EMAIL, "");
    }

    public boolean hasToken() {
        String token = getToken();
        return token != null && !token.trim().isEmpty();
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }
}
