package com.example.warehouse_medical.ui.staff;

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
import com.example.warehouse_medical.adapters.ProductAdapter;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.repositories.ItemRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class StaffInventoryFragment extends Fragment {

    private ItemRepository itemRepository;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TextView tvError;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_screen, container, false);

        itemRepository = new ItemRepository(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvError = view.findViewById(R.id.tvError);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        FloatingActionButton fabAction = view.findViewById(R.id.fabAction);

        adapter = new ProductAdapter("View", false, new ProductAdapter.OnProductActionListener() {
            @Override
            public void onItemClick(Item item) {
                Toast.makeText(requireContext(), item.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionClick(Item item) {
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        fabAction.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this::loadItems);
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

        loadItems();
        return view;
    }

    private void loadItems() {
        progressBar.setVisibility(View.VISIBLE);
        itemRepository.getItems(new RepositoryCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> data) {
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
                tvEmpty.setVisibility(View.GONE);
            }
        });
    }
}
