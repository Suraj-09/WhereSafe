package com.project.wheresafe.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.project.wheresafe.databinding.FragmentHelpFeaturesBinding;

public class HelpFeaturesFragment extends Fragment {
    private FragmentHelpFeaturesBinding binding;

    public HelpFeaturesFragment() {
        // constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHelpFeaturesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
}
