package com.project.wheresafe.controllers;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.project.wheresafe.R;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button deviceButton = view.findViewById(R.id.device_button);
        deviceButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_settingsFragment_to_deviceSettingsFragment);
        });

        return view;
    }
}
