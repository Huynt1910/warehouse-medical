package com.example.warehouse_medical.ui.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.storage.SessionManager;
import com.example.warehouse_medical.ui.admin.AdminItemsFragment;
import com.example.warehouse_medical.ui.admin.AdminProfileFragment;
import com.example.warehouse_medical.ui.admin.AdminUsersFragment;
import com.example.warehouse_medical.ui.customer.CartFragment;
import com.example.warehouse_medical.ui.customer.CustomerOrdersFragment;
import com.example.warehouse_medical.ui.customer.CustomerProfileFragment;
import com.example.warehouse_medical.ui.customer.ShopFragment;
import com.example.warehouse_medical.ui.staff.StaffInputInvoiceHistoryFragment;
import com.example.warehouse_medical.ui.staff.StaffInventoryFragment;
import com.example.warehouse_medical.ui.staff.StaffOrdersFragment;
import com.example.warehouse_medical.ui.staff.StaffProfileFragment;
import com.example.warehouse_medical.utils.AppConstants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        SessionManager sessionManager = new SessionManager(this);
        role = getIntent().getStringExtra(AppConstants.EXTRA_ROLE);
        if (role == null || role.trim().isEmpty()) {
            role = sessionManager.getRole();
        }

        setupMenuForRole(role);
    }

    private void setupMenuForRole(String role) {
        bottomNavigationView.getMenu().clear();
        if (AppConstants.ROLE_ADMIN.equals(role)) {
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_admin);
        } else if (AppConstants.ROLE_STAFF.equals(role)) {
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_staff);
        } else {
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_customer);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleNavigation(item.getItemId());
            return true;
        });

        if (bottomNavigationView.getMenu().size() > 0) {
            int firstItemId = bottomNavigationView.getMenu().getItem(0).getItemId();
            bottomNavigationView.setSelectedItemId(firstItemId);
        }
    }

    private void handleNavigation(int itemId) {
        Fragment fragment = resolveFragment(itemId);
        toolbar.setTitle(resolveTitle(itemId));
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private Fragment resolveFragment(int itemId) {
        if (itemId == R.id.nav_admin_users) {
            return new AdminUsersFragment();
        }
        if (itemId == R.id.nav_admin_items) {
            return new AdminItemsFragment();
        }
        if (itemId == R.id.nav_admin_profile) {
            return new AdminProfileFragment();
        }
        if (itemId == R.id.nav_staff_inventory) {
            return new StaffInventoryFragment();
        }
        if (itemId == R.id.nav_staff_import) {
            return new StaffInputInvoiceHistoryFragment();
        }
        if (itemId == R.id.nav_staff_orders) {
            return new StaffOrdersFragment();
        }
        if (itemId == R.id.nav_staff_profile) {
            return new StaffProfileFragment();
        }
        if (itemId == R.id.nav_customer_shop) {
            return new ShopFragment();
        }
        if (itemId == R.id.nav_customer_cart) {
            return new CartFragment();
        }
        if (itemId == R.id.nav_customer_orders) {
            return new CustomerOrdersFragment();
        }
        if (itemId == R.id.nav_customer_profile) {
            return new CustomerProfileFragment();
        }
        return null;
    }

    @NonNull
    private String resolveTitle(int itemId) {
        if (itemId == R.id.nav_admin_users) {
            return getString(R.string.admin_users);
        }
        if (itemId == R.id.nav_admin_items) {
            return getString(R.string.admin_items);
        }
        if (itemId == R.id.nav_staff_inventory) {
            return getString(R.string.staff_inventory);
        }
        if (itemId == R.id.nav_staff_import) {
            return getString(R.string.import_history);
        }
        if (itemId == R.id.nav_staff_orders) {
            return getString(R.string.staff_orders);
        }
        if (itemId == R.id.nav_customer_shop) {
            return getString(R.string.medicine_catalog);
        }
        if (itemId == R.id.nav_customer_cart) {
            return getString(R.string.customer_cart);
        }
        if (itemId == R.id.nav_customer_orders) {
            return getString(R.string.customer_orders);
        }
        return getString(R.string.profile);
    }
}
