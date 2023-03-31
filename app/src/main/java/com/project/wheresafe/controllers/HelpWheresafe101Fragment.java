package com.project.wheresafe.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

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
import com.project.wheresafe.viewmodels.SettingsViewModel;

public class HelpWheresafe101Fragment extends Fragment {
    private FragmentHelpWheresafe101Binding binding;
    private AppBarConfiguration mAppBarConfiguration;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHelpWheresafe101Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Enable the back button in the AppBar
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavHostFragment.findNavController(this).navigate(R.id.action_back_to_help);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public HelpWheresafe101Fragment() {
        // constructor
    }
}
