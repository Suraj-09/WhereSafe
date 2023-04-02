package com.project.wheresafe.controllers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentSettingsBinding;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.viewmodels.LanguageSelectionDialogFragment;
import com.project.wheresafe.viewmodels.SettingsViewModel;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat implements LanguageSelectionDialogFragment.LanguageSelectionListener {

    final private String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private Activity mActivity;
    private int actionBarSize;

    Preference prefHelp;
    ListPreference prefLanguage;
    SwitchPreferenceCompat notificationsSwitch;
    SwitchPreferenceCompat darkModeSwitch;


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        prefHelp = findPreference("help");
        prefLanguage = (ListPreference) findPreference("language");
        notificationsSwitch = findPreference("notifications");
        darkModeSwitch = findPreference("dark_mode");

//        if (sharedPreferenceHelper.getDarkMode()) {
//            darkModeSwitch.setSwitchTextOn(0);
//        } else {
//            darkModeSwitch.setSwitchTextOff(0);
//        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (prefHelp != null) {
            prefHelp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                            .navigate(R.id.navigation_help);
                    return true;
                }
            });
        }


        // Set up listeners for the SwitchPreferenceCompat objects
        if (notificationsSwitch != null) {
            notificationsSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Do something when the notifications switch is toggled
                    boolean enabled = (Boolean) newValue;
                    if (enabled) {
                        // Enable notifications
                    } else {
                        // Disable notifications
                    }
                    return true;
                }
            });
        }
        if (darkModeSwitch != null) {
            darkModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Do something when the dark mode switch is toggled
                    boolean enabled = (Boolean) newValue;
                    if (enabled) {
                        sharedPreferenceHelper.setDarkMode(true);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        getActivity().recreate();
                        // Enable dark mode
                    } else {
                        sharedPreferenceHelper.setDarkMode(false);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        getActivity().recreate();
                    }
                    return true;
                }
            });
        }

        prefLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                onLanguageSelected(newValue.toString());
                return true;
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "stop");
    }

    //    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//        return view;
//    }

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        SettingsViewModel slideshowViewModel =
//                new ViewModelProvider(this).get(SettingsViewModel.class);
//
//        binding = FragmentSettingsBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
////        final TextView textView = binding.textSettings;
////        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//
//        Button languageSelectionButton = binding.languageSelectionButton;
//        languageSelectionButton.setOnClickListener(v -> {
//            LanguageSelectionDialogFragment dialogFragment = new LanguageSelectionDialogFragment();
//            dialogFragment.setLanguageSelectionListener(this);
//            dialogFragment.show(requireActivity().getSupportFragmentManager(), "languageSelection");
//        });
//
//        Button helpButton = binding.helpButton;
//        helpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.navigation_help);
//            }
//        });
//
//        return root;
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding = null;
        }
    }

    public void onLanguageSelected(String languageCode) {
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.updateLanguage(sharedPreferenceHelper.getUid(), languageCode);

//        setLocale(languageCode);
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
        // Recreate the current activity to apply the language changes
//        requireActivity().recreate();
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