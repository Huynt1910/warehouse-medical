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
import com.example.warehouse_medical.models.OrderItem;
import com.example.warehouse_medical.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderLineAdapter extends RecyclerView.Adapter<OrderLineAdapter.OrderLineViewHolder> {

    private final List<OrderItem> items = new ArrayList<>();

    public void submitList(List<OrderItem> orderItems) {
        items.clear();
        if (orderItems != null) {
            items.addAll(orderItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_line, parent, false);
        return new OrderLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLineViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.tvName.setText(resolveItemLabel(item));
        holder.tvQuantity.setText("Quantity: " + (item.getQuantity() == null ? 0 : item.getQuantity()));
        holder.tvPrice.setText("Price: " + CurrencyUtils.formatCurrency(resolveSalePrice(item)));
        holder.tvSubtotal.setText("Subtotal: " + CurrencyUtils.formatCurrency(resolveSubtotal(item)));

        // Load image using Glide
        if (item.getItemId() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getItemId().getFullImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.ivProductImage);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String resolveItemLabel(OrderItem item) {
        if (item == null || item.getItemId() == null) return item != null ? item.getItemName() : "";
        return item.getItemId().getName();
    }

    private Double resolveSalePrice(OrderItem item) {
        if (item == null) return 0D;
        return item.getSalePrice() != null ? item.getSalePrice() : 
               (item.getItemId() != null ? item.getItemId().getSalePrice() : 0D);
    }

    private Double resolveSubtotal(OrderItem item) {
        if (item == null) return 0D;
        if (item.getSubtotal() != null) return item.getSubtotal();
        return resolveSalePrice(item) * (item.getQuantity() != null ? item.getQuantity() : 0);
    }

    static class OrderLineViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProductImage;
        private final TextView tvName;
        private final TextView tvQuantity;
        private final TextView tvPrice;
        private final TextView tvSubtotal;

        public OrderLineViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
