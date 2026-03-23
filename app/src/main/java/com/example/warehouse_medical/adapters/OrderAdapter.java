package com.example.warehouse_medical.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.models.Order;
import com.example.warehouse_medical.utils.CurrencyUtils;
import com.example.warehouse_medical.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private final List<Order> originalOrders = new ArrayList<>();
    private final List<Order> filteredOrders = new ArrayList<>();
    private final OnOrderClickListener listener;

    public OrderAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Order> orders) {
        originalOrders.clear();
        filteredOrders.clear();
        if (orders != null) {
            originalOrders.addAll(orders);
            filteredOrders.addAll(orders);
        }
        notifyDataSetChanged();
    }

    public void filter(String keyword) {
        filteredOrders.clear();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredOrders.addAll(originalOrders);
        } else {
            String query = keyword.toLowerCase(Locale.ROOT).trim();
            for (Order order : originalOrders) {
                String code = safe(order.getCode());
                String status = safe(order.getStatus());
                String customer = safe(resolveCustomerName(order));
                if (code.contains(query) || status.contains(query) || customer.contains(query)) {
                    filteredOrders.add(order);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return filteredOrders.isEmpty();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = filteredOrders.get(position);
        holder.tvCode.setText(nullSafe(order.getCode()));
        holder.tvCustomer.setText(resolveCustomerName(order));
        
        // Cập nhật định dạng ngày tháng mới tại đây
        holder.tvDate.setText(DateUtils.formatDateTime(order.getCreatedAt()));
        
        holder.tvAmount.setText(CurrencyUtils.formatCurrency(order.getTotalAmount() == null ? 0D : order.getTotalAmount()));
        holder.tvStatus.setText(nullSafe(order.getStatus()));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredOrders.size();
    }

    private String resolveCustomerName(Order order) {
        if (order == null) {
            return "";
        }
        if (order.getCustomerId() != null && order.getCustomerId().getFullName() != null) {
            return order.getCustomerId().getFullName();
        }
        return nullSafe(order.getCustomerName());
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCode;
        private final TextView tvCustomer;
        private final TextView tvDate;
        private final TextView tvAmount;
        private final TextView tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
