package com.project.wheresafe.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.project.wheresafe.databinding.FragmentHelpPrivacyBinding;

public class HelpPrivacyFragment extends Fragment {
    private FragmentHelpPrivacyBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHelpPrivacyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }


    public HelpPrivacyFragment() {
        // constructor
    }
}
