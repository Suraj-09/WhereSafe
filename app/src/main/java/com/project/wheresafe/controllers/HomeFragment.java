package com.project.wheresafe.controllers;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ekndev.gaugelibrary.ArcGauge;
import com.ekndev.gaugelibrary.Range;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.project.wheresafe.databinding.FragmentHomeBinding;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CircularProgressBar circularProgressBar = binding.humidityProgressBar;
        TextView humidityValue = binding.humidityValue;  // textView above progress bar. "Humidity"

        ArcGauge temperatureGauge = binding.temperatureGauge;
        ArcGauge pressureGauge = binding.pressureGauge;

        // set color ranges and other parameters for gauges
        setTemperatureGauge(temperatureGauge);
        setPressureGauge(pressureGauge);

        final Observer<BmeData> latestBmeDataObserver = new Observer<BmeData>() {
            @Override
            public void onChanged(BmeData bmeData) {
                System.out.println(bmeData);
                circularProgressBar.setProgress((float) bmeData.getHumidity());
                String humidityText = bmeData.getHumidity() + "%";
                humidityValue.setText(humidityText);

                temperatureGauge.setValue(bmeData.getTemperature());
                pressureGauge.setValue(bmeData.getPressure());
            }
        };

        homeViewModel.getLatestBmeData().observe(getViewLifecycleOwner(), latestBmeDataObserver);

        return root;

    }

    private void setTemperatureGauge(ArcGauge temperatureGauge) {
        // set color ranges to gauge and other parameters
        temperatureGauge.setUnit("\u00B0C"); // degrees celsius
        // BME680 reads from -40 to 85 degrees C, but set this range from -50 to 50
        Range range = new Range();
        range.setColor(Color.parseColor("#22B2FF")); // blue
        range.setFrom(-50.0);
        range.setTo(0.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#FFBF00")); // orange
        range2.setFrom(0.0);
        range2.setTo(25.0);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#D22B2B")); // red
        range3.setFrom(25.0);
        range3.setTo(50.0);

        // add color ranges to gauge
        temperatureGauge.addRange(range);
        temperatureGauge.addRange(range2);
        temperatureGauge.addRange(range3);

        //set min and max
        temperatureGauge.setMinValue(-50.0);
        temperatureGauge.setMaxValue(50.0);
    }
    private void setPressureGauge(ArcGauge pressureGauge) {
        // set color ranges to gauge and other parameters
        pressureGauge.setUnit("hPa");
        // BME680 reads altitude from 300 to 1100 hPa
        // average atmospheric pressure at sea level is 1013.25 hPa
        // 300 hPa would be approx pressure on top of Mount Everest
        // Set range from 300 - 2000 hPa
        Range range = new Range();
        range.setColor(Color.parseColor("#FF4CAF50")); // green
        range.setFrom(300.0);
        range.setTo(2000.0);

        pressureGauge.addRange(range);

        //set min and max
        pressureGauge.setMinValue(300.0);
        pressureGauge.setMaxValue(1700.0);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        homeViewModel.attachListener();
        homeViewModel.init();
    }

    @Override
    public void onStop() {
        super.onStop();
        homeViewModel.detachListener();
    }

    @Override
    public void onDestroyView() {
        homeViewModel.detachListener();
        super.onDestroyView();
        binding = null;
    }

}