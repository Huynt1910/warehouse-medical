package com.example.warehouse_medical.ui.customer;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.warehouse_medical.adapters.CartAdapter;
import com.example.warehouse_medical.dtos.CartResponseData;
import com.example.warehouse_medical.models.CartItem;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.repositories.CartRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.ui.activities.CheckoutActivity;
import com.example.warehouse_medical.utils.AppConstants;
import com.example.warehouse_medical.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;

public class CartFragment extends Fragment {

    private CartRepository cartRepository;
    private CartAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmpty;
    private TextView tvTotalAmount;
    private CartResponseData currentCart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRepository = new CartRepository(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        MaterialButton btnClearCart = view.findViewById(R.id.btnClearCart);
        MaterialButton btnCheckout = view.findViewById(R.id.btnCheckout);

        adapter = new CartAdapter(new CartAdapter.CartActionListener() {
            @Override
            public void onIncrease(CartItem item) {
                String itemId = resolveItemId(item);
                if (itemId.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.item_id_missing, Toast.LENGTH_SHORT).show();
                    return;
                }
                int quantity = item.getQuantity() == null ? 1 : item.getQuantity() + 1;
                updateItemQuantity(itemId, quantity);
            }

            @Override
            public void onDecrease(CartItem item) {
                String itemId = resolveItemId(item);
                if (itemId.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.item_id_missing, Toast.LENGTH_SHORT).show();
                    return;
                }
                int currentQty = item.getQuantity() == null ? 1 : item.getQuantity();
                if (currentQty <= 1) {
                    removeItem(itemId);
                } else {
                    updateItemQuantity(itemId, currentQty - 1);
                }
            }

            @Override
            public void onRemove(CartItem item) {
                String itemId = resolveItemId(item);
                if (itemId.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.item_id_missing, Toast.LENGTH_SHORT).show();
                    return;
                }
                removeItem(itemId);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this::loadCart);
        btnClearCart.setOnClickListener(v -> clearCart());
        btnCheckout.setOnClickListener(v -> openCheckout());

        loadCart();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            loadCart();
        }
    }

    private void loadCart() {
        if (progressBar == null) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        cartRepository.getCart(new RepositoryCallback<CartResponseData>() {
            @Override
            public void onSuccess(CartResponseData data) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                currentCart = data;
                bindCart(data);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindCart(CartResponseData cart) {
        adapter.submitList(cart != null ? cart.getItems() : null);
        tvEmpty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
        tvTotalAmount.setText(getString(
                R.string.total_amount,
                CurrencyUtils.formatCurrency(cart != null && cart.getTotalAmount() != null ? cart.getTotalAmount() : 0D)
        ));
    }

    private void updateItemQuantity(String itemId, int quantity) {
        cartRepository.updateCartItem(itemId, quantity, new RepositoryCallback<CartResponseData>() {
            @Override
            public void onSuccess(CartResponseData data) {
                currentCart = data;
                bindCart(data);
                Toast.makeText(requireContext(), R.string.cart_updated, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeItem(String itemId) {
        cartRepository.removeCartItem(itemId, new RepositoryCallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                loadCart();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart() {
        cartRepository.clearCart(new RepositoryCallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                currentCart = null;
                adapter.submitList(null);
                tvEmpty.setVisibility(View.VISIBLE);
                tvTotalAmount.setText(getString(R.string.total_amount, CurrencyUtils.formatCurrency(0D)));
                Toast.makeText(requireContext(), R.string.cart_cleared, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCheckout() {
        if (currentCart == null || currentCart.getItems() == null || currentCart.getItems().isEmpty()) {
            Toast.makeText(requireContext(), R.string.empty_cart, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(requireContext(), CheckoutActivity.class);
        intent.putExtra(AppConstants.EXTRA_CART, currentCart);
        startActivity(intent);
    }

    private String resolveItemId(CartItem item) {
        Item nestedItem = item != null ? item.getItemId() : null;
        if (nestedItem == null || nestedItem.getId() == null) {
            return "";
        }
        return nestedItem.getId();
    }
}
