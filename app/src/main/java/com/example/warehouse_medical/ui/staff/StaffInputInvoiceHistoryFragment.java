package com.example.warehouse_medical.ui.staff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.adapters.InputInvoiceAdapter;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.repositories.ItemRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.ui.activities.CreateInputInvoiceActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class StaffInputInvoiceHistoryFragment extends Fragment {

    private ItemRepository itemRepository;
    private InputInvoiceAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final ActivityResultLauncher<Intent> createInvoiceLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadLowStockItems(); 
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_screen, container, false);

        itemRepository = new ItemRepository(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextInputLayout tilSearch = view.findViewById(R.id.tilSearch);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        FloatingActionButton fabAction = view.findViewById(R.id.fabAction);

        tilSearch.setHint("Low Stock Items");
        
        adapter = new InputInvoiceAdapter(item -> {
            Intent intent = new Intent(requireContext(), CreateInputInvoiceActivity.class);
            intent.putExtra("selected_item", item); // Pass item to create invoice
            createInvoiceLauncher.launch(intent);
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        fabAction.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateInputInvoiceActivity.class);
            createInvoiceLauncher.launch(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(this::loadLowStockItems);
        
        loadLowStockItems();
        return view;
    }

    private void loadLowStockItems() {
        progressBar.setVisibility(View.VISIBLE);
        itemRepository.getLowStockItems(new RepositoryCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> data) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                adapter.submitList(data);
                tvEmpty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
                if (adapter.isEmpty()) {
                    tvEmpty.setText("All items are well stocked!");
                }
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
