package com.example.warehouse_medical.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_MESSAGE = "arg_message";

    public static PlaceholderFragment newInstance(String message) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(requireContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(32, 32, 32, 32);
        String message = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : getDefaultMessage();
        textView.setText(message);
        return textView;
    }

    protected String getDefaultMessage() {
        return "Coming soon";
    }
}
