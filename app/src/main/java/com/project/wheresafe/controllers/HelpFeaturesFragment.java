package com.project.wheresafe.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentHelpBinding;
import com.project.wheresafe.databinding.FragmentHelpFeaturesBinding;

public class HelpFeaturesFragment extends Fragment {
    private FragmentHelpFeaturesBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHelpFeaturesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button backButton = binding.buttonBack;
        Button privacyButton = binding.buttonPrivacySecurity;
        Button wheresafe101Button = binding.buttonWheresafe101;

        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help));

        privacyButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_privacy));

        wheresafe101Button.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_wheresafe101));

        return root;
    }


    public HelpFeaturesFragment() {
        // constructor
    }
}
