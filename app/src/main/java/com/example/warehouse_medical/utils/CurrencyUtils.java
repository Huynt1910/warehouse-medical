package com.example.warehouse_medical.utils;

import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtils {

    private CurrencyUtils() {
    }

    public static String formatCurrency(Double amount) {
        double safeAmount = amount == null ? 0D : amount;
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(safeAmount) + " VND";
    }
}
