package com.example.warehouse_medical.ui.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.adapters.OrderAdapter;
import com.example.warehouse_medical.models.Order;
import com.example.warehouse_medical.repositories.OrderRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.ui.activities.CustomerOrderDetailActivity;
import com.example.warehouse_medical.utils.AppConstants;

import java.util.List;

public class StaffOrdersFragment extends Fragment {

    private OrderRepository orderRepository;
    private OrderAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_screen, container, false);

        orderRepository = new OrderRepository(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        view.findViewById(R.id.fabAction).setVisibility(View.GONE);

        adapter = new OrderAdapter(order -> {
            Intent intent = new Intent(requireContext(), CustomerOrderDetailActivity.class);
            // Use the correct constant key to avoid null in activity
            intent.putExtra(AppConstants.EXTRA_ORDER_ID, order.getId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
        loadOrders();

        return view;
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        orderRepository.getOrders(new RepositoryCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> data) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                adapter.submitList(data);
                tvEmpty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
