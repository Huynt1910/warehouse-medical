package com.example.warehouse_medical.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.adapters.OrderLineAdapter;
import com.example.warehouse_medical.models.Order;
import com.example.warehouse_medical.repositories.OrderRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.storage.SessionManager;
import com.example.warehouse_medical.utils.AppConstants;
import com.example.warehouse_medical.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;

public class CustomerOrderDetailActivity extends AppCompatActivity {

    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private TextView tvOrderCode;
    private TextView tvOrderStatus;
    private TextView tvOrderNote;
    private TextView tvTotalAmount;
    private MaterialButton btnCancelOrder;
    private MaterialButton btnConfirmOrder;
    private OrderLineAdapter adapter;
    private String orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_detail);
        setTitle(R.string.order_detail);

        orderRepository = new OrderRepository(this);
        sessionManager = new SessionManager(this);
        orderId = getIntent().getStringExtra(AppConstants.EXTRA_ORDER_ID);

        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.contentLayout);
        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderNote = findViewById(R.id.tvOrderNote);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new OrderLineAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnCancelOrder.setOnClickListener(v -> cancelOrder());
        btnConfirmOrder.setOnClickListener(v -> confirmOrder());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        if (orderId == null || orderId.trim().isEmpty()) {
            Toast.makeText(this, "Order ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOrderDetail();
    }

    private void loadOrderDetail() {
        progressBar.setVisibility(View.VISIBLE);
        orderRepository.getOrderDetail(orderId, new RepositoryCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                bindOrder(data);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerOrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bindOrder(Order order) {
        tvOrderCode.setText("Order #" + order.getCode());
        tvOrderStatus.setText("Status: " + order.getStatus());
        tvOrderNote.setText("Note: " + (order.getNote() != null ? order.getNote() : "No note"));
        tvTotalAmount.setText("Total: " + CurrencyUtils.formatCurrency(order.getTotalAmount()));
        adapter.submitList(order.getItems());

        String userRole = sessionManager.getRole();
        boolean isStaff = "STAFF".equalsIgnoreCase(userRole) || "ADMIN".equalsIgnoreCase(userRole);
        boolean isPending = "PENDING".equalsIgnoreCase(order.getStatus()) || "PENDING_CONFIRMATION".equalsIgnoreCase(order.getStatus());

        // Show confirm button only for staff on pending orders
        btnConfirmOrder.setVisibility(isStaff && isPending ? View.VISIBLE : View.GONE);
        
        // Customer can only cancel pending confirmation orders
        boolean canCancel = isPending && !isStaff;
        btnCancelOrder.setVisibility(canCancel ? View.VISIBLE : View.GONE);
    }

    private void confirmOrder() {
        progressBar.setVisibility(View.VISIBLE);
        orderRepository.confirmOrder(orderId, new RepositoryCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerOrderDetailActivity.this, "Order confirmed successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back to list
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerOrderDetailActivity.this, "Failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrder() {
        progressBar.setVisibility(View.VISIBLE);
        orderRepository.cancelOrder(orderId, new RepositoryCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerOrderDetailActivity.this, "Order cancelled", Toast.LENGTH_SHORT).show();
                bindOrder(data);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerOrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
