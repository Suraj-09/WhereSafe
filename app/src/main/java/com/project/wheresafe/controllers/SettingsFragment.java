package com.project.wheresafe.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentSettingsBinding;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.viewmodels.LanguageSelectionDialogFragment;
import com.project.wheresafe.viewmodels.SettingsViewModel;

import java.util.Locale;

public class SettingsFragment extends Fragment implements LanguageSelectionDialogFragment.LanguageSelectionListener {

    private FragmentSettingsBinding binding;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSettings;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button languageSelectionButton = binding.languageSelectionButton;
        languageSelectionButton.setOnClickListener(v -> {
            LanguageSelectionDialogFragment dialogFragment = new LanguageSelectionDialogFragment();
            dialogFragment.setLanguageSelectionListener(this);
            dialogFragment.show(requireActivity().getSupportFragmentManager(), "languageSelection");
        });

        Button helpButton = binding.helpButton;
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_settings_to_help);
            }
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void onLanguageSelected(String languageCode) {
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.updateLanguage(sharedPreferenceHelper.getUid(), languageCode);

        setLocale(languageCode);
        // Restart MainActivity after changing the language
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = requireActivity().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

}