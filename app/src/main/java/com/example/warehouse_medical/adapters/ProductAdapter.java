package com.example.warehouse_medical.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warehouse_medical.R;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductActionListener {
        void onItemClick(Item item);
        void onActionClick(Item item);
    }

    private final List<Item> items = new ArrayList<>();
    private final OnProductActionListener listener;
    private final boolean isShopMode;

    public ProductAdapter(OnProductActionListener listener) {
        this(false, listener);
    }

    public ProductAdapter(boolean isShopMode, OnProductActionListener listener) {
        this.isShopMode = isShopMode;
        this.listener = listener;
    }

    // Constructor cũ để tránh lỗi compile ở các nơi chưa cập nhật
    public ProductAdapter(String actionLabel, boolean showActionButton, OnProductActionListener listener) {
        this(showActionButton, listener);
    }

    public void submitList(List<Item> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public List<Item> getCurrentList() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isShopMode ? R.layout.item_shop_product_card : R.layout.item_product_card;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ProductViewHolder(view, isShopMode);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Item item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvCode.setText("Code: " + item.getCode());
        
        if (holder.tvCategory != null) {
            holder.tvCategory.setText(item.getCategory());
        }
        
        holder.tvDescription.setText(item.getDescription());
        holder.tvSalePrice.setText(CurrencyUtils.formatCurrency(item.getSalePrice()));

        int stock = item.getQuantityInStock() == null ? 0 : item.getQuantityInStock();
        holder.tvStock.setText(isShopMode ? "In Stock: " + stock : "Stock: " + stock);

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getFullImageUrl())
                .placeholder(R.drawable.product_placeholder)
                .error(R.drawable.product_placeholder)
                .into(holder.ivProductImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        if (isShopMode && holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActionClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProductImage;
        private final TextView tvName;
        private final TextView tvCode;
        private TextView tvCategory;
        private final TextView tvDescription;
        private final TextView tvSalePrice;
        private final TextView tvStock;
        private MaterialButton btnAddToCart;

        public ProductViewHolder(@NonNull View itemView, boolean isShopMode) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSalePrice = itemView.findViewById(R.id.tvSalePrice);
            tvStock = itemView.findViewById(R.id.tvStock);
            
            if (isShopMode) {
                btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            } else {
                tvCategory = itemView.findViewById(R.id.tvCategory);
            }
        }
    }
}
