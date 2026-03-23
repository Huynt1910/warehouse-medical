package com.example.warehouse_medical.utils;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Response;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static String getErrorMessage(Response<?> response) {
        if (response == null) {
            return "No response from server";
        }
        try {
            if (response.errorBody() != null) {
                String raw = response.errorBody().string();
                if (raw != null && !raw.trim().isEmpty()) {
                    JSONObject jsonObject = new JSONObject(raw);
                    String message = jsonObject.optString("message");
                    if (!message.isEmpty()) {
                        return message;
                    }
                }
            }
        } catch (IOException ignored) {
        } catch (Exception ignored) {
        }
        return "Request failed with code " + response.code();
    }
}
