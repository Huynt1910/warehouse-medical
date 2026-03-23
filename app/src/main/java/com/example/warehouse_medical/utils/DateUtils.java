package com.example.warehouse_medical.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateUtils {

    private DateUtils() {
    }

    /**
     * Converts ISO 8601 date string (e.g., 2026-03-23T10:03:33.719Z)
     * to a readable format (e.g., 23/03/2026 17:03)
     */
    public static String formatDateTime(String isoDateString) {
        if (isoDateString == null || isoDateString.isEmpty()) {
            return "";
        }

        try {
            // The format from the server is UTC
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            
            Date date = inputFormat.parse(isoDateString);

            // Output format in local timezone (Vietnam is GMT+7)
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            
            return date != null ? outputFormat.format(date) : isoDateString;
        } catch (Exception e) {
            // Try secondary format without milliseconds if the first fails
            try {
                SimpleDateFormat altFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                altFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = altFormat.parse(isoDateString);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                return date != null ? outputFormat.format(date) : isoDateString;
            } catch (Exception e2) {
                return isoDateString;
            }
        }
    }
}
