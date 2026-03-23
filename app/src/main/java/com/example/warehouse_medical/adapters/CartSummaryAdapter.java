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
import com.example.warehouse_medical.models.CartItem;
import com.example.warehouse_medical.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class CartSummaryAdapter extends RecyclerView.Adapter<CartSummaryAdapter.CartSummaryViewHolder> {

    private final List<CartItem> items = new ArrayList<>();

    public void submitList(List<CartItem> cartItems) {
        items.clear();
        if (cartItems != null) {
            items.addAll(cartItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_line, parent, false);
        return new CartSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartSummaryViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.tvName.setText(resolveItemLabel(item));
        holder.tvQuantity.setText(holder.itemView.getContext().getString(
                R.string.quantity
        ) + ": " + (item.getQuantity() == null ? 0 : item.getQuantity()));
        holder.tvPrice.setText(holder.itemView.getContext().getString(
                R.string.sale_price_label,
                CurrencyUtils.formatCurrency(resolveSalePrice(item))
        ));
        holder.tvSubtotal.setText(holder.itemView.getContext().getString(
                R.string.subtotal_label,
                CurrencyUtils.formatCurrency(resolveSubtotal(item))
        ));

        // Load image using Glide
        if (item.getItemId() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getItemId().getFullImageUrl())
                    .placeholder(R.drawable.product_placeholder)
                    .error(R.drawable.product_placeholder)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.product_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String resolveItemLabel(CartItem item) {
        if (item == null || item.getItemId() == null) {
            return "";
        }
        String name = item.getItemId().getName() == null ? "" : item.getItemId().getName();
        String code = item.getItemId().getCode() == null ? "" : item.getItemId().getCode();
        if (!name.isEmpty() && !code.isEmpty()) {
            return name + " (" + code + ")";
        }
        return !name.isEmpty() ? name : code;
    }

    private Double resolveSalePrice(CartItem item) {
        if (item == null) {
            return 0D;
        }
        if (item.getSalePrice() != null) {
            return item.getSalePrice();
        }
        return item.getItemId() != null && item.getItemId().getSalePrice() != null
                ? item.getItemId().getSalePrice() : 0D;
    }

    private Double resolveSubtotal(CartItem item) {
        if (item == null) {
            return 0D;
        }
        if (item.getSubtotal() != null) {
            return item.getSubtotal();
        }
        int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
        return resolveSalePrice(item) * quantity;
    }

    static class CartSummaryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProductImage;
        private final TextView tvName;
        private final TextView tvQuantity;
        private final TextView tvPrice;
        private final TextView tvSubtotal;

        public CartSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
