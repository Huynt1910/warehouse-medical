package com.example.warehouse_medical.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.databinding.ActivityCreateInputInvoiceBinding;
import com.example.warehouse_medical.dtos.ItemListData;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.repositories.ItemRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class CreateInputInvoiceActivity extends AppCompatActivity {

    private ActivityCreateInputInvoiceBinding binding;
    private ItemRepository itemRepository;
    private List<Item> itemList = new ArrayList<>();
    private Item selectedItem;
    private boolean isPreSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateInputInvoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemRepository = new ItemRepository(this);

        setupToolbar();
        
        // Nhận item từ Intent nếu có (từ màn hình Low Stock)
        if (getIntent().hasExtra("selected_item")) {
            selectedItem = (Item) getIntent().getSerializableExtra("selected_item");
            if (selectedItem != null) {
                isPreSelected = true;
                binding.actvItems.setText(selectedItem.getName() + " (" + selectedItem.getCode() + ")");
                binding.actvItems.setEnabled(false); // Khóa không cho chọn item khác nếu đã chọn từ list
                binding.tilSelectItem.setEndIconVisible(false); // Ẩn icon dropdown
            }
        }

        if (!isPreSelected) {
            loadItems();
        }

        binding.btnSave.setOnClickListener(v -> saveStockUpdate());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        
        if (isPreSelected) {
            toolbar.setTitle("Add Stock: " + selectedItem.getName());
        }
    }

    private void loadItems() {
        itemRepository.getItems(1, 100, "", new RepositoryCallback<ItemListData>() {
            @Override
            public void onSuccess(ItemListData data) {
                itemList = data.getItems();
                String[] itemNames = new String[itemList.size()];
                for (int i = 0; i < itemList.size(); i++) {
                    itemNames[i] = itemList.get(i).getName() + " (" + itemList.get(i).getCode() + ")";
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateInputInvoiceActivity.this,
                        android.R.layout.simple_dropdown_item_1line, itemNames);
                binding.actvItems.setAdapter(adapter);
                binding.actvItems.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedName = (String) parent.getItemAtPosition(position);
                    for (Item item : itemList) {
                        if ((item.getName() + " (" + item.getCode() + ")").equals(selectedName)) {
                            selectedItem = item;
                            break;
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CreateInputInvoiceActivity.this, "Failed to load items: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveStockUpdate() {
        if (selectedItem == null) {
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
            return;
        }

        String qtyStr = binding.etQuantity.getText().toString();
        
        if (TextUtils.isEmpty(qtyStr)) {
            Toast.makeText(this, "Quantity is required", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        if (quantity <= 0) {
            Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        itemRepository.updateQuantity(selectedItem.getId(), quantity, new RepositoryCallback<Item>() {
            @Override
            public void onSuccess(Item data) {
                Toast.makeText(CreateInputInvoiceActivity.this, "Stock updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CreateInputInvoiceActivity.this, "Failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
