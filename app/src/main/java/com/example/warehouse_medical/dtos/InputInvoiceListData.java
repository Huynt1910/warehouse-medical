package com.example.warehouse_medical.dtos;

import com.example.warehouse_medical.models.InputInvoice;
import com.example.warehouse_medical.models.Pagination;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InputInvoiceListData {
    @SerializedName(value = "inputInvoices", alternate = {"input_invoices", "items", "results"})
    private List<InputInvoice> inputInvoices;

    @SerializedName(value = "pagination", alternate = {"meta"})
    private Pagination pagination;

    public List<InputInvoice> getInputInvoices() {
        return inputInvoices;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
