package com.project.wheresafe.controllers;

import android.content.Context;
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
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        CircularProgressBar circularProgressBar = binding.humidityGauge;
//        TextView humidityValue = binding.humidityValue;  // textView above progress bar. "Humidity"

        ArcGauge temperatureGauge = binding.temperatureGauge;
        ArcGauge pressureGauge = binding.pressureGauge;
        ArcGauge airQualityGauge = binding.airQualityGauge;
        ArcGauge altitudeGauge = binding.altitudeGauge;
        ArcGauge humidityGauge = binding.humidityGauge;

        // set color ranges and other parameters for gauges
        setTemperatureGauge(temperatureGauge);
        setPressureGauge(pressureGauge);
        setAirQualityGauge(airQualityGauge);
        setAltitudeGauge(altitudeGauge);
        setHumidityGauge(humidityGauge);

        final Observer<BmeData> latestBmeDataObserver = new Observer<BmeData>() {
            @Override
            public void onChanged(BmeData bmeData) {
//                System.out.println(bmeData);
//                circularProgressBar.setProgress((float) bmeData.getHumidity());
//                String humidityText = bmeData.getHumidity() + "%";
//                humidityValue.setText(humidityText);

                temperatureGauge.setValue(bmeData.getTemperature());
                pressureGauge.setValue(bmeData.getPressure());
                humidityGauge.setValue(bmeData.getHumidity());
                airQualityGauge.setValue(bmeData.getGas());
                altitudeGauge.setValue(bmeData.getAltitude());
            }
        };

        homeViewModel.getLatestBmeData().observe(getViewLifecycleOwner(), latestBmeDataObserver);

        return root;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);

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

    private void setHumidityGauge(ArcGauge humidityGauge) {
        // set color ranges to gauge and other parameters
        humidityGauge.setUnit("%");
        Range range = new Range();
        range.setColor(Color.parseColor("#22B2FF")); // green
        range.setFrom(0.0);
        range.setTo(100.0);

        humidityGauge.addRange(range);

        //set min and max
        humidityGauge.setMinValue(0.0);
        humidityGauge.setMaxValue(100.0);
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

    private void setAirQualityGauge(ArcGauge airQualityGauge) {
        // set color ranges to gauge and other parameters
        airQualityGauge.setUnit("");

        Range range1 = new Range();
        range1.setColor(Color.parseColor("#90EE90")); // light green
        range1.setFrom(0.0);
        range1.setTo(50.9);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#FFFF00")); // green
        range2.setFrom(51.0);
        range2.setTo(100.9);

        Range range3 = new Range();
        range2.setColor(Color.parseColor("#FFFF00")); // yellow
        range2.setFrom(101.0);
        range2.setTo(150.9);

        Range range4 = new Range();
        range2.setColor(Color.parseColor("#FFA500")); // orange
        range2.setFrom(151.0);
        range2.setTo(200.9);

        Range range5 = new Range();
        range2.setColor(Color.parseColor("#f07e7a")); // light red
        range2.setFrom(201.0);
        range2.setTo(250.9);

        Range range6 = new Range();
        range2.setColor(Color.parseColor("#A020F0")); // purple
        range2.setFrom(250.0);
        range2.setTo(350.9);

        Range range7 = new Range();
        range2.setColor(Color.parseColor("#964B00")); // brown
        range2.setFrom(350.0);
        range2.setTo(500.0);

        airQualityGauge.addRange(range1);
        airQualityGauge.addRange(range2);
        airQualityGauge.addRange(range3);
        airQualityGauge.addRange(range4);
        airQualityGauge.addRange(range5);
        airQualityGauge.addRange(range6);
        airQualityGauge.addRange(range7);

        //set min and max
        airQualityGauge.setMinValue(0.0);
        airQualityGauge.setMaxValue(500.0);
    }

    private void setAltitudeGauge(ArcGauge altitudeGauge) {
        // set color ranges to gauge and other parameters
        altitudeGauge.setUnit("m");

//        Range range1 = new Range();
//        range1.setColor(Color.parseColor("#FF4CAF50")); // green
//        range1.setFrom(0.0);
//        range1.setTo(50.9);
//
//        Range range2 = new Range();
//        range2.setColor(Color.parseColor("#FFFF00")); // yellow
//        range2.setFrom(51.0);
//        range2.setTo(100.9);
//
//        altitudeGauge.addRange(range1);
//        altitudeGauge.addRange(range2);

        //set min and max
        altitudeGauge.setMinValue(0.0);
        altitudeGauge.setMaxValue(1000.0);
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