package com.project.wheresafe.controllers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentHelpBinding;

public class HelpFragment extends Fragment {

    private FragmentHelpBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
     //   SettingsViewModel slideshowViewModel =
     //           new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentHelpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button wheresafe101Button = binding.buttonWheresafe101;
        Button featuresButton = binding.buttonWhereSafeFeatures;
        Button privacyButton = binding.buttonPrivacySecurity;

        wheresafe101Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_help_to_whereSafe101);
            }
        });

        featuresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_help_to_features);
            }
        });

        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_help_to_privacy);
            }
        });

        return root;
    }


    public HelpFragment() {
        // constructor
    }

}