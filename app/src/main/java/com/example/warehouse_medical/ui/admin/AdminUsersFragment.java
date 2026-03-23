package com.example.warehouse_medical.ui.admin;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.adapters.UserAdapter;
import com.example.warehouse_medical.dtos.UserListData;
import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.repositories.UserRepository;
import com.example.warehouse_medical.ui.activities.AddEditUserActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private UserRepository userRepository;
    private UserAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Pagination & Search states
    private int currentPage = 1;
    private final int limit = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentKeyword = "";
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private final ActivityResultLauncher<Intent> addEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refreshData();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_screen, container, false);

        userRepository = new UserRepository(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextInputLayout tilSearch = view.findViewById(R.id.tilSearch);
        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        FloatingActionButton fabAction = view.findViewById(R.id.fabAction);

        tilSearch.setHint("Search Users");
        adapter = new UserAdapter(user -> {
            Intent intent = new Intent(requireContext(), AddEditUserActivity.class);
            intent.putExtra("user", user);
            addEditLauncher.launch(intent);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Infinite scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loadMoreUsers();
                        }
                    }
                }
            }
        });

        fabAction.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditUserActivity.class);
            addEditLauncher.launch(intent);
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
                searchHandler.postDelayed(searchRunnable, 500); // Debounce 500ms
            }
        });

        loadUsers(true);
        return view;
    }

    private void refreshData() {
        currentPage = 1;
        isLastPage = false;
        loadUsers(true);
    }

    private void loadMoreUsers() {
        currentPage++;
        loadUsers(false);
    }

    private void loadUsers(boolean clearOld) {
        if (isLoading) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        userRepository.getUsers(currentPage, limit, currentKeyword, new RepositoryCallback<UserListData>() {
            @Override
            public void onSuccess(UserListData data) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                List<User> newUsers = data.getUsers();
                if (clearOld) {
                    adapter.submitList(newUsers);
                } else {
                    List<User> currentList = new ArrayList<>(adapter.getOriginalUsers());
                    currentList.addAll(newUsers);
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
            }
        });
    }
}
