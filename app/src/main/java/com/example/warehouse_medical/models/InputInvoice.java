package com.example.warehouse_medical.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class InputInvoice implements Serializable {
    @SerializedName(value = "id", alternate = {"_id", "inputInvoiceId"})
    private String id;

    private String code;
    private String note;
    private String createdAt;
    private Double totalAmount;
    
    // Changed to Object to handle both String (ID) and User (Object) from Backend
    private Object createdBy; 
    
    private List<InputInvoiceItem> items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public List<InputInvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InputInvoiceItem> items) {
        this.items = items;
    }
}
