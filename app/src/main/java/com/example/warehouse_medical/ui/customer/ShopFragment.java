package com.example.warehouse_medical.ui.customer;

import android.content.Intent;
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
import com.example.warehouse_medical.dtos.AddToCartRequest;
import com.example.warehouse_medical.dtos.CartResponseData;
import com.example.warehouse_medical.dtos.ItemListData;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.repositories.CartRepository;
import com.example.warehouse_medical.repositories.ItemRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.ui.activities.ItemDetailActivity;
import com.example.warehouse_medical.utils.AppConstants;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private ItemRepository itemRepository;
    private ProductAdapter adapter;
    private CartRepository cartRepository;
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
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        itemRepository = new ItemRepository(requireContext());
        cartRepository = new CartRepository(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvError = view.findViewById(R.id.tvError);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Khởi tạo adapter với isShopMode = true để hiện nút Add to Cart
        adapter = new ProductAdapter(true, new ProductAdapter.OnProductActionListener() {
            @Override
            public void onItemClick(Item item) {
                if (item.getId() == null || item.getId().trim().isEmpty()) {
                    Toast.makeText(requireContext(), R.string.item_id_missing, Toast.LENGTH_SHORT).show();
                    return;
                }
                // Trong Shop, nhấn vào Card sẽ mở chi tiết sản phẩm
                Intent intent = new Intent(requireContext(), ItemDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }

            @Override
            public void onActionClick(Item item) {
                if (item.getId() == null || item.getId().trim().isEmpty()) {
                    Toast.makeText(requireContext(), R.string.item_id_missing, Toast.LENGTH_SHORT).show();
                    return;
                }
                // Xử lý thêm vào giỏ hàng khi nhấn nút Add to Cart
                cartRepository.addToCart(new AddToCartRequest(item.getId(), 1), new RepositoryCallback<CartResponseData>() {
                    @Override
                    public void onSuccess(CartResponseData data) {
                        Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(requireContext(), "Add to cart failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Infinite Scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Scroll down
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

        itemRepository.getAvailableItems(currentPage, limit, currentKeyword, new RepositoryCallback<ItemListData>() {
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
