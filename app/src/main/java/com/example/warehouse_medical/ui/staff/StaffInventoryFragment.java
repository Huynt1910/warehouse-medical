package com.example.warehouse_medical.ui.staff;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.warehouse_medical.dtos.ItemListData;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.repositories.ItemRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class StaffInventoryFragment extends Fragment {

    private ItemRepository itemRepository;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TextView tvError;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Pagination & Search states
    private int currentPage = 1;
    private final int limit = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentKeyword = "";
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

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

        adapter = new ProductAdapter(new ProductAdapter.OnProductActionListener() {
            @Override
            public void onItemClick(Item item) {
                Toast.makeText(requireContext(), item.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionClick(Item item) {
                // Staff Inventory does not use action button
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Infinite scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // check for scroll down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loadMoreItems();
                        }
                    }
                }
            }
        });

        fabAction.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    currentKeyword = s.toString();
                    refreshData();
                };
                searchHandler.postDelayed(searchRunnable, 500); // 500ms debounce
            }
        });

        loadItems(true);
        return view;
    }

    private void refreshData() {
        currentPage = 1;
        isLastPage = false;
        loadItems(true);
    }

    private void loadMoreItems() {
        currentPage++;
        loadItems(false);
    }

    private void loadItems(boolean clearOld) {
        if (isLoading) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        itemRepository.getItems(currentPage, limit, currentKeyword, new RepositoryCallback<ItemListData>() {
            @Override
            public void onSuccess(ItemListData data) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                tvError.setVisibility(View.GONE);

                List<Item> newItems = data.getItems();
                if (clearOld) {
                    adapter.submitList(newItems);
                } else {
                    List<Item> currentList = new ArrayList<>(adapter.getCurrentList());
                    currentList.addAll(newItems);
                    adapter.submitList(currentList);
                }

                isLastPage = currentPage >= data.getPagination().getTotalPages();
                tvEmpty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.error_prefix, message));
            }
        });
    }
}
