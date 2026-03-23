package com.example.warehouse_medical.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class CustomerOrdersFragment extends Fragment {

    private OrderRepository orderRepository;
    private OrderAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TextView tvError;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_screen, container, false);
        orderRepository = new OrderRepository(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvError = view.findViewById(R.id.tvError);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        FloatingActionButton fabAction = view.findViewById(R.id.fabAction);

        adapter = new OrderAdapter(order -> {
            Intent intent = new Intent(requireContext(), CustomerOrderDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_ORDER_ID, order.getId());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        fabAction.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                tvEmpty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadOrders();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded() && progressBar != null) {
            loadOrders();
        }
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        orderRepository.getOrders(new RepositoryCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> data) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                tvError.setVisibility(View.GONE);
                adapter.submitList(data);
                tvEmpty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.error_prefix, message));
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
