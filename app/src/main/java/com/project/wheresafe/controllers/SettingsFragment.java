package com.project.wheresafe.controllers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.DialogPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentSettingsBinding;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.viewmodels.LanguageSelectionDialogFragment;
import com.project.wheresafe.viewmodels.SettingsViewModel;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat implements LanguageSelectionDialogFragment.LanguageSelectionListener {

    final private String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private Activity mActivity;
    private int actionBarSize;

    private Preference prefHelp;

    private Preference prefDevice;
    private Preference prefTeamCode;
    private Preference prefTeamLeave;
    private ListPreference prefLanguage;
    private SwitchPreferenceCompat notificationsSwitch;
    private SwitchPreferenceCompat darkModeSwitch;
    private FirestoreHelper firestoreHelper;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        firestoreHelper = new FirestoreHelper<>();

        prefHelp = findPreference("help");
        prefLanguage = (ListPreference) findPreference("language");
//        notificationsSwitch = findPreference("notifications");
        darkModeSwitch = findPreference("dark_mode");
        prefDevice = findPreference("device_settings");
        prefTeamCode = findPreference("team_code");
        prefTeamLeave = findPreference("team_leave");

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
//        if (notificationsSwitch != null) {
//            notificationsSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    // Do something when the notifications switch is toggled
//                    boolean enabled = (Boolean) newValue;
//                    if (enabled) {
//                        // Enable notifications
//                    } else {
//                        // Disable notifications
//                    }
//                    return true;
//                }
//            });
//        }
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

        prefDevice.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_deviceSettingsFragment);
                return true;
            }
        });




        prefTeamCode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                String teamCode = sharedPreferenceHelper.getTeamCode();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Team Code");

                if (teamCode != null) {
                    builder.setMessage("Your team code is:\n\n" + teamCode);
                } else {
                    builder.setMessage("You are not in a team");
                }

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });

        prefTeamLeave.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {

                String teamCode = sharedPreferenceHelper.getTeamCode();

                if (teamCode == null) {
                    Toast toast = Toast.makeText(getContext(),"You are not in a team",Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    // Create a new AlertDialog to display the team code
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Leave Team");
                    builder.setMessage("Are you sure you would like to leave your team?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firestoreHelper.leaveTeam(sharedPreferenceHelper.getUid(), teamCode);
                            sharedPreferenceHelper.setTeamCode(null);
                            Toast toast = Toast.makeText(getContext(),"Successfully left team",Toast.LENGTH_SHORT);
                            toast.show();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }




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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        mActivity = getActivity();
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

        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }


//    private void setLocale(String languageCode) {
//        Locale locale = new Locale(languageCode);
//        Locale.setDefault(locale);
//
//        Resources resources = requireActivity().getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//    }

}
//=======
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//
//        Button deviceButton = view.findViewById(R.id.device_button);
//        deviceButton.setOnClickListener(view1 -> {
//            Navigation.findNavController(view1).navigate(R.id.action_settingsFragment_to_deviceSettingsFragment);
//        });
//
//        return view;
//    }
//}
//>>>>>>> origin/devicesettings_2
