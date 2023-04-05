package com.project.wheresafe.controllers;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentPersonalBinding;
import com.project.wheresafe.databinding.FragmentTeammateBinding;
import com.project.wheresafe.models.DatabaseHelper;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.viewmodels.PersonalViewModel;
import com.project.wheresafe.viewmodels.TeammateViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TeammateFragment extends Fragment {
    DatabaseHelper dbHelper;
    private FragmentTeammateBinding binding;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private String teammateId;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TeammateViewModel teammateViewModel = new TeammateViewModel();

        binding = FragmentTeammateBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireActivity().getApplicationContext());

        Bundle args = getArguments();
        if (args != null) {
            teammateId = args.getString("teammate_id");
        }

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.getAllPersonalSensorData(teammateId, new FirestoreCallback() {
            @Override
            public void onResultGet() {

                populateCharts(firestoreHelper.getFirestoreData().getBmeDataArrayList());
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
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

    public void populateCharts(ArrayList<BmeData> bmeDataArrayList) {
        if (bmeDataArrayList.isEmpty()) {
            return;
        }

        bmeDataArrayList.sort(new Comparator<BmeData>() {
            @Override
            public int compare(BmeData o1, BmeData o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        // Populate charts!
        FragmentActivity mActivity = getActivity();

        if (mActivity != null) {
//        BarChart temperatureChart = mActivity.findViewById(R.id.temperatureChartTeammate);
            LineChart temperatureChart = mActivity.findViewById(R.id.temperatureChartTeammate);
            LineChart humidityChart = mActivity.findViewById(R.id.humidityChartTeammate);
            LineChart pressureChart = mActivity.findViewById(R.id.pressureChartTeammate);
            LineChart gasChart = mActivity.findViewById(R.id.gasChartTeammate);
            LineChart altitudeChart = mActivity.findViewById(R.id.altitudeChartTeammate);

            // Customize CHART appearance and behavior
//        initializeBarChart(temperatureChart);
            initializeLineChart(temperatureChart);
            initializeLineChart(humidityChart);
            initializeLineChart(pressureChart);
            initializeLineChart(gasChart);
            initializeLineChart(altitudeChart);

            // create ArrayLists for BarCharts & create Lists for LineCharts
//        ArrayList<BarEntry> temperatureReadings = new ArrayList<>();
            List<Entry> temperatureReadings = new ArrayList<>();
            List<Entry> humidityReadings = new ArrayList<>();
            List<Entry> pressureReadings = new ArrayList<>();
            List<Entry> gasReadings = new ArrayList<>();
            List<Entry> altitudeReadings = new ArrayList<>();

            // loops through bmeDataArray, reads a given data metric to be plotted (temp, humidity, etc)
            for (int i = 0; i < bmeDataArrayList.size(); i++) {
                // bar chart to plot temperature readings
                float temperature = (float) bmeDataArrayList.get(i).getTemperature();
                temperatureReadings.add(new Entry(i, temperature));

                // line chart to plot humidity readings
                float humidity = (float) bmeDataArrayList.get(i).getHumidity();
                humidityReadings.add(new Entry(i, humidity));

                // line chart to plot pressure readings
                float pressure = (float) bmeDataArrayList.get(i).getPressure();
                pressureReadings.add(new Entry(i, pressure));

                // line chart to plot gas readings
                float gas = (float) bmeDataArrayList.get(i).getGas();
                gasReadings.add(new Entry(i, gas));

                // line chart to plot altitude readings
                float altitude = (float) bmeDataArrayList.get(i).getAltitude();
                altitudeReadings.add(new Entry(i, altitude));
            }

            // declare datasets
//        BarDataSet temperatureDataSet = new BarDataSet(temperatureReadings, "Temperature Data");
        LineDataSet temperatureDataSet = new LineDataSet(temperatureReadings,  getString(R.string.temperature_data));
        LineDataSet humidityDataSet = new LineDataSet(humidityReadings,  getString(R.string.humidity_data));
        LineDataSet pressureDataSet = new LineDataSet(pressureReadings,  getString(R.string.pressure_data));
        LineDataSet gasDataSet = new LineDataSet(gasReadings,  getString(R.string.gas_data));
        LineDataSet altitudeDataSet = new LineDataSet(altitudeReadings,  getString(R.string.altitude_data));

            // for line charts, customizes DATASET appearance and behavior
            customizeLineDataSet(temperatureDataSet);
            customizeLineDataSet(humidityDataSet);
            customizeLineDataSet(pressureDataSet);
            customizeLineDataSet(gasDataSet);
            customizeLineDataSet(altitudeDataSet);

            // set data objects for the charts with their corresponding data sets
//        BarData temperatureData = new BarData(temperatureDataSet);
//        temperatureChart.setData(temperatureData);
//        temperatureChart.invalidate();    // call this whenever a chart needs to get updated

            LineData temperatureData = new LineData(temperatureDataSet);
            temperatureChart.setData(temperatureData);
            temperatureChart.invalidate();

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

