package com.example.warehouse_medical.dtos;

import java.util.List;

public class InputInvoiceRequest {
    private String note;
    private List<InputInvoiceRequestItem> items;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<InputInvoiceRequestItem> getItems() {
        return items;
    }

    public void setItems(List<InputInvoiceRequestItem> items) {
        this.items = items;
    }

    public static class InputInvoiceRequestItem {
        private String itemId;
        private int quantity;
        private double salePrice;
        private String expiryDate;
        private String batchNumber;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(double salePrice) {
            this.salePrice = salePrice;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getBatchNumber() {
            return batchNumber;
        }

        public void setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
        }
    }
}
