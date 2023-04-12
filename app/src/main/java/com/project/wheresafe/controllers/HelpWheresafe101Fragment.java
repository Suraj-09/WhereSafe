package com.project.wheresafe.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.project.wheresafe.databinding.FragmentHelpWheresafe101Binding;

public class HelpWheresafe101Fragment extends Fragment {
    private FragmentHelpWheresafe101Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHelpWheresafe101Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        Button backButton = binding.buttonBack;
//        Button featuresButton = binding.buttonWhereSafeFeatures;
//        Button privacyButton = binding.buttonPrivacySecurity;
//
//        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help));
//
//        featuresButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_features));
//
//        privacyButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_privacy));

        return root;
    }

}
