package com.example.warehouse_medical.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.warehouse_medical.R;
import com.example.warehouse_medical.databinding.ActivityAddEditItemBinding;
import com.example.warehouse_medical.dtos.CreateItemRequest;
import com.example.warehouse_medical.dtos.UpdateItemRequest;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.repositories.ItemRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddEditItemActivity extends AppCompatActivity {

    private ActivityAddEditItemBinding binding;
    private ItemRepository itemRepository;
    private Item currentItem;
    private boolean isEditMode = false;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Hiển thị preview ngay lập tức
                    Glide.with(this).load(selectedImageUri).into(binding.ivItemImage);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemRepository = new ItemRepository(this);
        setupToolbar();

        if (getIntent().hasExtra("item")) {
            currentItem = (Item) getIntent().getSerializableExtra("item");
            isEditMode = true;
            fillData();
        }

        binding.btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            pickImageLauncher.launch(Intent.createChooser(intent, "Select Image from Downloads"));
        });

        binding.btnSave.setOnClickListener(v -> saveItem());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "Edit Item" : "Add New Item");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void fillData() {
        if (currentItem != null) {
            binding.etName.setText(currentItem.getName());
            binding.etCode.setText(currentItem.getCode());
            binding.etCategory.setText(currentItem.getCategory());
            binding.etUnit.setText(currentItem.getUnit());
            binding.etBasePrice.setText(String.valueOf(currentItem.getBasePrice()));
            binding.etSalePrice.setText(String.valueOf(currentItem.getSalePrice()));
            binding.etDescription.setText(currentItem.getDescription());
            binding.btnSave.setText("Update Item");

            if (currentItem.getFullImageUrl() != null) {
                Glide.with(this).load(currentItem.getFullImageUrl())
                        .placeholder(R.drawable.ic_launcher_background).into(binding.ivItemImage);
            }
        }
    }

    private void saveItem() {
        String name = binding.etName.getText().toString().trim();
        String code = binding.etCode.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Name and Code are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double basePrice = tryParse(binding.etBasePrice.getText().toString());
        double salePrice = tryParse(binding.etSalePrice.getText().toString());

        if (isEditMode) {
            UpdateItemRequest request = new UpdateItemRequest();
            request.setName(name);
            request.setCode(code);
            request.setCategory(binding.etCategory.getText().toString());
            request.setUnit(binding.etUnit.getText().toString());
            request.setBasePrice(basePrice);
            request.setSalePrice(salePrice);
            request.setDescription(binding.etDescription.getText().toString());

            itemRepository.updateItem(currentItem.getId(), request, new RepositoryCallback<Item>() {
                @Override
                public void onSuccess(Item data) {
                    if (selectedImageUri != null) {
                        uploadImage(data.getId());
                    } else {
                        Toast.makeText(AddEditItemActivity.this, "Item updated", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(AddEditItemActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            CreateItemRequest request = new CreateItemRequest();
            request.setName(name);
            request.setCode(code);
            request.setCategory(binding.etCategory.getText().toString());
            request.setUnit(binding.etUnit.getText().toString());
            request.setBasePrice(basePrice);
            request.setSalePrice(salePrice);
            request.setDescription(binding.etDescription.getText().toString());

            itemRepository.createItem(request, new RepositoryCallback<Item>() {
                @Override
                public void onSuccess(Item data) {
                    if (selectedImageUri != null) {
                        uploadImage(data.getId());
                    } else {
                        Toast.makeText(AddEditItemActivity.this, "Item created", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(AddEditItemActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImage(String itemId) {
        try {
            // Tạo file tạm để nén ảnh vào
            File file = new File(getCacheDir(), "upload_image.jpg");
            
            // 1. Giải mã kích thước ảnh trước để tính toán inSampleSize (tránh tràn bộ nhớ)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) inputStream.close();

            // Tính toán sample size (mục tiêu tối đa 1024px)
            options.inSampleSize = calculateInSampleSize(options, 1024, 1024);
            options.inJustDecodeBounds = false;

            // 2. Giải mã ảnh thật với kích thước đã tối ưu
            inputStream = getContentResolver().openInputStream(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) inputStream.close();

            if (bitmap == null) {
                Toast.makeText(this, "Cannot decode image", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Nén ảnh và ghi vào file với chất lượng 75% (để giảm dung lượng file xuống mức thấp)
            try (OutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                outputStream.flush();
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            itemRepository.uploadItemImage(itemId, body, new RepositoryCallback<Item>() {
                @Override
                public void onSuccess(Item data) {
                    Toast.makeText(AddEditItemActivity.this, "Saved and Image uploaded", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(AddEditItemActivity.this, "Data saved but Image failed: " + message, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm helper để tính toán kích thước nén
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private double tryParse(String val) {
        try {
            return TextUtils.isEmpty(val) ? 0.0 : Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
