package com.example.warehouse_medical.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.adapters.CartSummaryAdapter;
import com.example.warehouse_medical.dtos.CartResponseData;
import com.example.warehouse_medical.models.Order;
import com.example.warehouse_medical.repositories.OrderRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.utils.AppConstants;
import com.example.warehouse_medical.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CheckoutActivity extends AppCompatActivity {

    private CartResponseData cart;
    private ProgressBar progressBar;
    private TextInputEditText etNote;
    private OrderRepository orderRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setTitle(R.string.checkout);

        orderRepository = new OrderRepository(this);
        progressBar = findViewById(R.id.progressBar);
        etNote = findViewById(R.id.etNote);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        MaterialButton btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cart = getIntent().getSerializableExtra(AppConstants.EXTRA_CART, CartResponseData.class);
        } else {
            cart = readLegacyCartExtra();
        }
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            Toast.makeText(this, R.string.empty_cart, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CartSummaryAdapter adapter = new CartSummaryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.submitList(cart.getItems());
        tvTotalAmount.setText(getString(
                R.string.total_amount,
                CurrencyUtils.formatCurrency(cart.getTotalAmount() == null ? 0D : cart.getTotalAmount())
        ));

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
        btnBack.setOnClickListener(v -> finish());
    }

    @SuppressWarnings("deprecation")
    private CartResponseData readLegacyCartExtra() {
        return (CartResponseData) getIntent().getSerializableExtra(AppConstants.EXTRA_CART);
    }

    private void placeOrder() {
        progressBar.setVisibility(View.VISIBLE);
        String note = etNote.getText() == null ? "" : etNote.getText().toString().trim();
        orderRepository.checkout(note, new RepositoryCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CheckoutActivity.this, R.string.order_placed, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CheckoutActivity.this, CustomerOrderDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_ORDER_ID, data.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CheckoutActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
