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

public class HelpFragment extends Fragment {

    private FragmentHelpBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHelpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button wheresafe101Button = binding.buttonWheresafe101;
        Button featuresButton = binding.buttonWhereSafeFeatures;
        Button privacyButton = binding.buttonPrivacySecurity;

        wheresafe101Button.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_wheresafe101));

        featuresButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_features));

        privacyButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_privacy));

        return root;
    }


    public HelpFragment() {
        // constructor
    }

}