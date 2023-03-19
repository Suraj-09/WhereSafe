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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentHomeBinding;
import com.project.wheresafe.models.DatabaseHelper;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CircularProgressBar circularProgressBar = binding.humidityProgressBar;
        TextView humidityValue = binding.humidityValue;  // textView above progress bar. "Humidity"

        int humidityRandomNumber = 75;
        circularProgressBar.setProgress(humidityRandomNumber); // set humidity to 75%
        humidityValue.setText("75 %");

        ArcGauge temperatureGauge = binding.temperatureGauge;
        temperatureGauge.setValue(30); // set temperature to 30 degrees Celsius
        setTemperatureGaugeRange(temperatureGauge); // set color ranges

        ArcGauge pressureGauge = binding.pressureGauge;
        pressureGauge.setValue(800); // set pressure to 800 hPa
        setPressureGaugeRange(pressureGauge); // set color ranges

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

    private void setTemperatureGaugeRange(ArcGauge temperatureGauge) {
        // set color ranges to gauge
        // BME680 reads from -40 to 85 degrees C, but set this range from -40 to 50
        Range range = new Range();
        range.setColor(Color.parseColor("#22B2FF")); // blue
        range.setFrom(-40.0);
        range.setTo(0.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#FFBF00")); // orange
        range2.setFrom(0.0);
        range2.setTo(25.0);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#D22B2B")); // red
        range3.setFrom(25.0);
        range3.setTo(60.0);

        // add color ranges to gauge
        temperatureGauge.addRange(range);
        temperatureGauge.addRange(range2);
        temperatureGauge.addRange(range3);

        //set min and max
        temperatureGauge.setMinValue(-40.0);
        temperatureGauge.setMaxValue(50.0);
    }
    private void setPressureGaugeRange(ArcGauge pressureGauge) {
        // set color ranges to gauge
        // BME680 reads altitude from 300 to 1100 hPa
        Range range = new Range();
        range.setColor(Color.parseColor("#FFBF00")); // orange
        range.setFrom(300.0);
        range.setTo(1100.0);

      //  TODO: add altitude ranges
      //  Range range2 = new Range();
      //  range2.setColor(Color.parseColor("#FFBF00")); // orange
      //  range2.setFrom(0.0);
      //  range2.setTo(25.0);

     //   Range range3 = new Range();
     //   range3.setColor(Color.parseColor("#D22B2B")); // red
     //   range3.setFrom(25.0);
     //   range3.setTo(60.0);

        // add color ranges to gauge
        pressureGauge.addRange(range);
    //    pressureGauge.addRange(range2);
    //    pressureGauge.addRange(range3);

        //set min and max
        pressureGauge.setMinValue(300.0);
        pressureGauge.setMaxValue(1100.0);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        homeViewModel.detachListener();
        super.onDestroyView();
        binding = null;
    }

}