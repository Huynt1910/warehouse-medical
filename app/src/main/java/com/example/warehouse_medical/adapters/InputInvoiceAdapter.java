package com.example.warehouse_medical.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warehouse_medical.R;
import com.example.warehouse_medical.models.Item;

import java.util.ArrayList;
import java.util.List;

public class InputInvoiceAdapter extends RecyclerView.Adapter<InputInvoiceAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final List<Item> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public InputInvoiceAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Item> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * Thêm phương thức isEmpty để kiểm tra danh sách rỗng
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("In Stock: " + (item.getQuantityInStock() != null ? item.getQuantityInStock() : 0));
        holder.tvPrice.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_500));
        holder.tvDescription.setText(item.getDescription());
        
        // Load image from backend using helper method
        Glide.with(holder.itemView.getContext())
                .load(item.getFullImageUrl())
                .placeholder(R.drawable.product_placeholder)
                .error(R.drawable.product_placeholder)
                .into(holder.ivProduct);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ lại các view để khớp với item_product_card.xml
            ivProduct = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvStock); // Dùng tvStock để hiển thị số lượng tồn kho thấp
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
