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
import com.project.wheresafe.databinding.FragmentHelpPrivacyBinding;

public class HelpPrivacyFragment extends Fragment {
    private FragmentHelpPrivacyBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHelpPrivacyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button backButton = binding.buttonBack;
        Button featuresButton = binding.buttonWhereSafeFeatures;
        Button wheresafe101Button = binding.buttonWheresafe101;

        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help));

        featuresButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_features));

        wheresafe101Button.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_help_wheresafe101));

        return root;
    }


    public HelpPrivacyFragment() {
        // constructor
    }
}
