package com.project.wheresafe.controllers;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.models.DatabaseHelper;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentPersonalBinding;
import com.project.wheresafe.viewmodels.PersonalViewModel;

import java.util.Timer;
import java.util.TimerTask;


public class PersonalFragment extends Fragment {
    private FragmentPersonalBinding binding;
    private boolean paused;
    DatabaseHelper dbHelper;
    Timer timer;
    TimerTask timerTask;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PersonalViewModel personalViewModel =
                new ViewModelProvider(this).get(PersonalViewModel.class);

        binding = FragmentPersonalBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireActivity().getApplicationContext());

        View root = binding.getRoot();

        paused = false;
        runOnTimer();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void runOnTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                if (!paused) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateTextView();
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, 0, 2000);
    }

    @SuppressLint("DefaultLocale")
    public void updateTextView() {
        // get activity before getting TextViews
        FragmentActivity mActivity = getActivity();

        if (mActivity != null) {
            // get data stored in database
            BmeData bmeData = dbHelper.getBmeData();
            if (bmeData != null) {
                String temperatureStr = String.format("%.2f", bmeData.getTemperature());
                String humidityStr = String.format("%.2f", bmeData.getHumidity());
                String pressureStr = String.format("%.2f", bmeData.getPressure());
                String gasStr = String.format("%.2f", bmeData.getGas());
                String altitudeStr = String.format("%.2f", bmeData.getAltitude());
                String timestamp = bmeData.getTimestamp();

                TextView txtTemperature = mActivity.findViewById(R.id.txtTemperature);
                txtTemperature.setText(temperatureStr);

                TextView txtHumidity = mActivity.findViewById(R.id.txtHumidity);
                txtHumidity.setText(humidityStr);

                TextView txtPressure = mActivity.findViewById(R.id.txtPressure);
                txtPressure.setText(pressureStr);

                TextView txtGas = mActivity.findViewById(R.id.txtGas);
                txtGas.setText(gasStr);

                TextView txtAltitude = mActivity.findViewById(R.id.txtAltitude);
                txtAltitude.setText(altitudeStr);

                TextView txtTimestamp = mActivity.findViewById(R.id.txtTimestamp);
                txtTimestamp.setText(timestamp);
            }
        }
    }

}