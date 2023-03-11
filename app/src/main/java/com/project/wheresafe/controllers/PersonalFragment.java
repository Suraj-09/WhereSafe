package com.project.wheresafe.controllers;


import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;

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
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.models.DatabaseHelper;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentPersonalBinding;
import com.project.wheresafe.viewmodels.PersonalViewModel;

import java.util.ArrayList;
import java.util.List;
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
        populateCharts(); // populates da charts
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

    public void populateCharts(){
        // Populate charts!
        FragmentActivity mActivity = getActivity();
        BarChart temperatureChart = mActivity.findViewById(R.id.temperatureChart);
        LineChart humidityChart = mActivity.findViewById(R.id.humidityChart);
        LineChart pressureChart = mActivity.findViewById(R.id.pressureChart);
        LineChart gasChart = mActivity.findViewById(R.id.gasChart);
        LineChart altitudeChart = mActivity.findViewById(R.id.altitudeChart);

        // Customize CHART appearance and behavior
        initializeBarChart(temperatureChart);
        initializeLineChart(humidityChart);
        initializeLineChart(pressureChart);
        initializeLineChart(gasChart);
        initializeLineChart(altitudeChart);

        // create ArrayLists for BarCharts & create Lists for LineCharts
        ArrayList<BarEntry> temperatureReadings = new ArrayList<>();
        List<Entry> humidityReadings = new ArrayList<>();
        List<Entry> pressureReadings = new ArrayList<>();
        List<Entry> gasReadings = new ArrayList<>();
        List<Entry> altitudeReadings = new ArrayList<>();

        BmeData[] bmeDataArray = new BmeData[] {
                new BmeData(20.0, 50.0, 1013.0, 100.0, 100.0),
                new BmeData(25.0, 52.0, 1014.0, 110.0, 105.0),
                new BmeData(18.0, 58.0, 1019.0, 90.0, 102.0),
                new BmeData(27.0, 42.0, 1032.0, 95.0, 95.0),
                new BmeData(19.0, 49.0, 1002.0, 99.0, 99.0),
                new BmeData(22.0, 55.0, 1010.0, 102.0, 103.0),
                // add more BmeData objects here
        };

        // loops through bmeDataArray, reads a given data metric to be plotted (temp, humidity, etc)
        for (int i = 0; i < bmeDataArray.length; i++) {
            // bar chart to plot temperature readings
            float temperature = (float) bmeDataArray[i].getTemperature();
            temperatureReadings.add(new BarEntry(i, temperature));

            // line chart to plot humidity readings
            float humidity = (float) bmeDataArray[i].getHumidity();
            humidityReadings.add(new Entry(i, humidity));

            // line chart to plot pressure readings
            float pressure = (float) bmeDataArray[i].getPressure();
            pressureReadings.add(new Entry(i, pressure));

            // line chart to plot gas readings
            float gas = (float) bmeDataArray[i].getGas();
            gasReadings.add(new Entry(i, gas));

            // line chart to plot altitude readings
            float altitude = (float) bmeDataArray[i].getAltitude();
            altitudeReadings.add(new Entry(i, altitude));
        }

        // declare datasets
        BarDataSet temperatureDataSet = new BarDataSet(temperatureReadings, "Temperature Data");
        LineDataSet humidityDataSet = new LineDataSet(humidityReadings, "Humidity Data");
        LineDataSet pressureDataSet = new LineDataSet(pressureReadings, "Pressure Data");
        LineDataSet gasDataSet = new LineDataSet(gasReadings, "Gas Data");
        LineDataSet altitudeDataSet = new LineDataSet(altitudeReadings, "Altitude Data");

        // for line charts, customizes DATASET appearance and behavior
        customizeLineDataSet(humidityDataSet);
        customizeLineDataSet(pressureDataSet);
        customizeLineDataSet(gasDataSet);
        customizeLineDataSet(altitudeDataSet);

        // set data objects for the charts with their corresponding data sets
        BarData temperatureData = new BarData(temperatureDataSet);
        temperatureChart.setData(temperatureData);
        temperatureChart.invalidate();    // call this whenever a chart needs to get updated

        LineData humidityData = new LineData(humidityDataSet);
        humidityChart.setData(humidityData);
        humidityChart.invalidate();

        LineData pressureData = new LineData(pressureDataSet);
        pressureChart.setData(pressureData);
        pressureChart.invalidate();

        LineData gasData = new LineData(gasDataSet);
        gasChart.setData(gasData);
        gasChart.invalidate();

        LineData altitudeData = new LineData(altitudeDataSet);
        altitudeChart.setData(altitudeData);
        altitudeChart.invalidate();
    }

    private void initializeLineChart(LineChart lineChart) {
        // Customizes LINE CHART appearance and behavior
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setPinchZoom(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisRight().setDrawGridLines(true);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(0.5f); // sets value for x-axis to be incremented
        //chart.setMarker(marker);      // could be implemented later...
                                        // displays a customized pop-up whenever a value in the chart is clicked on
                                        // https://github.com/PhilJay/MPAndroidChart/wiki/MarkerView
        lineChart.invalidate();
    }

    private void initializeBarChart(BarChart barChart) {
        // Customizes BAR CHART appearance and behavior
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(true);
        barChart.setPinchZoom(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisRight().setDrawGridLines(true);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        barChart.invalidate();
    }
    private void customizeLineDataSet(LineDataSet lineDataSet) {
        // Customize DATASET appearance and behavior (for line charts)
        lineDataSet.setDrawIcons(false);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawValues(true);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
    }
}

