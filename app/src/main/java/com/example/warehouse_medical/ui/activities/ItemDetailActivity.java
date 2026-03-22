package com.example.warehouse_medical.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.dtos.AddToCartRequest;
import com.example.warehouse_medical.dtos.CartResponseData;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.repositories.CartRepository;
import com.example.warehouse_medical.repositories.ItemRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.utils.AppConstants;
import com.example.warehouse_medical.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;

public class ItemDetailActivity extends AppCompatActivity {

    private ItemRepository itemRepository;
    private CartRepository cartRepository;
    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private TextView tvName;
    private TextView tvCode;
    private TextView tvCategory;
    private TextView tvDescription;
    private TextView tvSalePrice;
    private TextView tvStock;
    private TextView tvQuantity;
    private int selectedQuantity = 1;
    private Item currentItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        getSupportActionBar();
        setTitle(R.string.item_detail);

        itemRepository = new ItemRepository(this);
        cartRepository = new CartRepository(this);

        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.contentLayout);
        tvName = findViewById(R.id.tvName);
        tvCode = findViewById(R.id.tvCode);
        tvCategory = findViewById(R.id.tvCategory);
        tvDescription = findViewById(R.id.tvDescription);
        tvSalePrice = findViewById(R.id.tvSalePrice);
        tvStock = findViewById(R.id.tvStock);
        tvQuantity = findViewById(R.id.tvQuantity);
        MaterialButton btnMinus = findViewById(R.id.btnMinus);
        MaterialButton btnPlus = findViewById(R.id.btnPlus);
        MaterialButton btnAddToCart = findViewById(R.id.btnAddToCart);

        btnMinus.setOnClickListener(v -> updateQuantity(false));
        btnPlus.setOnClickListener(v -> updateQuantity(true));
        btnAddToCart.setOnClickListener(v -> addCurrentItemToCart());

        String itemId = getIntent().getStringExtra(AppConstants.EXTRA_ITEM_ID);
        if (itemId == null || itemId.trim().isEmpty()) {
            Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadItemDetail(itemId);
    }

    private void loadItemDetail(String itemId) {
        progressBar.setVisibility(View.VISIBLE);
        itemRepository.getItemDetail(itemId, new RepositoryCallback<Item>() {
            @Override
            public void onSuccess(Item data) {
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                currentItem = data;
                bindItem(data);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ItemDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bindItem(Item item) {
        tvName.setText(item.getName() == null ? "" : item.getName());
        tvCode.setText(item.getCode() == null ? "" : item.getCode());
        tvCategory.setText(item.getCategory() == null ? "" : item.getCategory());
        tvDescription.setText(item.getDescription() == null ? "" : item.getDescription());
        tvSalePrice.setText(getString(
                R.string.sale_price_label,
                CurrencyUtils.formatCurrency(item.getSalePrice() == null ? 0D : item.getSalePrice())
        ));
        tvStock.setText(getString(
                R.string.stock_label,
                item.getQuantityInStock() == null ? 0 : item.getQuantityInStock()
        ));
        tvQuantity.setText(String.valueOf(selectedQuantity));
    }

    private void updateQuantity(boolean increase) {
        int stock = currentItem != null && currentItem.getQuantityInStock() != null
                ? currentItem.getQuantityInStock() : Integer.MAX_VALUE;
        if (increase) {
            if (selectedQuantity < stock) {
                selectedQuantity++;
            }
        } else if (selectedQuantity > 1) {
            selectedQuantity--;
        }
        tvQuantity.setText(String.valueOf(selectedQuantity));
    }

    private void addCurrentItemToCart() {
        if (currentItem == null || currentItem.getId() == null || currentItem.getId().trim().isEmpty()) {
            Toast.makeText(this, R.string.item_id_missing, Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        cartRepository.addToCart(new AddToCartRequest(currentItem.getId(), selectedQuantity), new RepositoryCallback<CartResponseData>() {
            @Override
            public void onSuccess(CartResponseData data) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ItemDetailActivity.this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ItemDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
