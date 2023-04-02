package com.project.wheresafe.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentHelpWheresafe101Binding;
import com.project.wheresafe.viewmodels.AdvancedDataViewModel;
import com.project.wheresafe.viewmodels.SettingsViewModel;

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
