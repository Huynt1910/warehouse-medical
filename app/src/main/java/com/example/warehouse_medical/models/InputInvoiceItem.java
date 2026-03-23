package com.example.warehouse_medical.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InputInvoiceItem implements Serializable {
    @SerializedName(value = "id", alternate = {"_id"})
    private String id;

    // Changed to Object to handle both String (ID) and Item (Object) from Backend
    @SerializedName(value = "itemId", alternate = {"item"})
    private Object itemId;

    private String itemName;
    private Integer quantity;
    private Double salePrice;
    private Double subtotal;
    private String expiryDate;
    private String batchNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getItemId() {
        return itemId;
    }

    public void setItemId(Object itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
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
