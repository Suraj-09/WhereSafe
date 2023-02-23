package com.project.wheresafe.ui.advanced_data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.wheresafe.databinding.FragmentAdvancedDataBinding;

public class AdvancedDataFragment extends Fragment {

    private FragmentAdvancedDataBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdvancedDataViewModel galleryViewModel =
                new ViewModelProvider(this).get(AdvancedDataViewModel.class);

        binding = FragmentAdvancedDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAdvancedData;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}