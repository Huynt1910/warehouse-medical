package com.example.warehouse_medical.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onUserClick(User user);
    }

    private final List<User> originalUsers = new ArrayList<>();
    private final List<User> filteredUsers = new ArrayList<>();
    private final OnUserActionListener listener;

    public UserAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<User> users) {
        originalUsers.clear();
        filteredUsers.clear();
        if (users != null) {
            originalUsers.addAll(users);
            filteredUsers.addAll(users);
        }
        notifyDataSetChanged();
    }

    public List<User> getOriginalUsers() {
        return originalUsers;
    }

    public void filter(String keyword) {
        filteredUsers.clear();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredUsers.addAll(originalUsers);
        } else {
            String query = keyword.toLowerCase(Locale.ROOT).trim();
            for (User user : originalUsers) {
                String fullName = user.getFullName() == null ? "" : user.getFullName().toLowerCase(Locale.ROOT);
                String email = user.getEmail() == null ? "" : user.getEmail().toLowerCase(Locale.ROOT);
                if (fullName.contains(query) || email.contains(query)) {
                    filteredUsers.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return filteredUsers.isEmpty();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = filteredUsers.get(position);
        holder.tvFullName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText("Role: " + user.getRole());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onUserClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return filteredUsers.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFullName;
        private final TextView tvEmail;
        private final TextView tvRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
        }
    }
}
