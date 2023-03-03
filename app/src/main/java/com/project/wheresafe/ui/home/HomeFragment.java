package com.project.wheresafe.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentHomeBinding;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        if (getArguments() != null) {
//            String temperatureStr = getArguments().getString("temperature");
//            TextView txtTemperature = requireView().findViewById(R.id.txtTemperature);
//            txtTemperature.setText(temperatureStr);
//        } else {
//            System.out.println("getArguments() = null");
//        }

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String temperatureStr = getArguments().getString("temperature");
            TextView txtTemperature = requireActivity().findViewById(R.id.txtTemperature);
            if (txtTemperature != null) {
                txtTemperature.setText(temperatureStr);
            }
        } else {
            System.out.println("getArguments() = null");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}