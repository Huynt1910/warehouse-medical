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
import com.example.warehouse_medical.utils.AppConstants;
import com.example.warehouse_medical.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;

public class CustomerOrderDetailActivity extends AppCompatActivity {

    private OrderRepository orderRepository;
    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private TextView tvOrderCode;
    private TextView tvOrderStatus;
    private TextView tvOrderNote;
    private TextView tvTotalAmount;
    private MaterialButton btnCancelOrder;
    private OrderLineAdapter adapter;
    private String orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_detail);
        setTitle(R.string.order_detail);

        orderRepository = new OrderRepository(this);
        orderId = getIntent().getStringExtra(AppConstants.EXTRA_ORDER_ID);

        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.contentLayout);
        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderNote = findViewById(R.id.tvOrderNote);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new OrderLineAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnCancelOrder.setOnClickListener(v -> cancelOrder());

        if (orderId == null || orderId.trim().isEmpty()) {
            Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
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
        tvOrderCode.setText(getString(R.string.order_code_label, order.getCode()));
        tvOrderStatus.setText(getString(R.string.order_status_label, order.getStatus()));
        String note = order.getNote() == null || order.getNote().trim().isEmpty()
                ? getString(R.string.batch_not_available) : order.getNote();
        tvOrderNote.setText(getString(R.string.order_note_label, note));
        tvTotalAmount.setText(getString(R.string.total_amount, CurrencyUtils.formatCurrency(order.getTotalAmount())));
        adapter.submitList(order.getItems());

        boolean canCancel = "PENDING_CONFIRMATION".equals(order.getStatus());
        btnCancelOrder.setVisibility(canCancel ? View.VISIBLE : View.GONE);
    }

    private void cancelOrder() {
        progressBar.setVisibility(View.VISIBLE);
        orderRepository.cancelOrder(orderId, new RepositoryCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerOrderDetailActivity.this, R.string.order_cancelled, Toast.LENGTH_SHORT).show();
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
