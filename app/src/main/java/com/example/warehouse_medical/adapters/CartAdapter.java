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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartActionListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
    }

    private final List<CartItem> items = new ArrayList<>();
    private final CartActionListener listener;

    public CartAdapter(CartActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CartItem> cartItems) {
        items.clear();
        if (cartItems != null) {
            items.addAll(cartItems);
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_row, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.tvName.setText(resolveItemLabel(item));
        holder.tvPrice.setText(CurrencyUtils.formatCurrency(resolveSalePrice(item)));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity() == null ? 0 : item.getQuantity()));
        holder.tvSubtotal.setText("Subtotal: " + CurrencyUtils.formatCurrency(resolveSubtotal(item)));

        // Load image using Glide
        if (item.getItemId() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getItemId().getFullImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.ivProductImage);
        }

        holder.btnPlus.setOnClickListener(v -> listener.onIncrease(item));
        holder.btnMinus.setOnClickListener(v -> listener.onDecrease(item));
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(item));
        
        // Hide quantity controls if it's used in Checkout (optional logic)
        // If you want to reuse this adapter for Checkout and hide buttons, 
        // you can pass a flag in constructor.
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String resolveItemLabel(CartItem item) {
        if (item == null || item.getItemId() == null) return "";
        return item.getItemId().getName();
    }

    private Double resolveSalePrice(CartItem item) {
        if (item == null) return 0D;
        return item.getSalePrice() != null ? item.getSalePrice() : 
               (item.getItemId() != null ? item.getItemId().getSalePrice() : 0D);
    }

    private Double resolveSubtotal(CartItem item) {
        if (item == null) return 0D;
        if (item.getSubtotal() != null) return item.getSubtotal();
        return resolveSalePrice(item) * (item.getQuantity() != null ? item.getQuantity() : 0);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProductImage;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvQuantity;
        private final TextView tvSubtotal;
        private final MaterialButton btnMinus;
        private final MaterialButton btnPlus;
        private final MaterialButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
