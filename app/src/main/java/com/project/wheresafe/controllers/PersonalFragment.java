package com.project.wheresafe.controllers;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentPersonalBinding;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.viewmodels.PersonalViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class PersonalFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 234;
    private FragmentPersonalBinding binding;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreHelper firestoreHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double latitude;
    private double longitude;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PersonalViewModel personalViewModel = new PersonalViewModel();

        binding = FragmentPersonalBinding.inflate(inflater, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirestoreHelper firestoreHelper = new FirestoreHelper();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000); // 10 seconds
            locationRequest.setFastestInterval(5000); // 5 seconds
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        firestoreHelper.updateUserLocation(latitude, longitude);
                        Log.d("LOCATION", "Latitude: " + latitude + " Longitude: " + longitude);
                    }
                }
            };

            startLocationUpdates();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        View root = binding.getRoot();
        return root;
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
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
        firestoreHelper.getAllPersonalSensorData(sharedPreferenceHelper.getUid(), new FirestoreCallback() {
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
            LineChart temperatureChart = mActivity.findViewById(R.id.temperatureChart);
            LineChart humidityChart = mActivity.findViewById(R.id.humidityChart);
            LineChart pressureChart = mActivity.findViewById(R.id.pressureChart);
            LineChart gasChart = mActivity.findViewById(R.id.gasChart);
            LineChart altitudeChart = mActivity.findViewById(R.id.altitudeChart);

            // Customize CHART appearance and behavior
            initializeLineChart(temperatureChart);
            initializeLineChart(humidityChart);
            initializeLineChart(pressureChart);
            initializeLineChart(gasChart);
            initializeLineChart(altitudeChart);

            // create ArrayLists for BarCharts & create Lists for LineCharts
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
            LineDataSet temperatureDataSet = new LineDataSet(temperatureReadings, getString(R.string.temperature_data));
            LineDataSet humidityDataSet = new LineDataSet(humidityReadings, getString(R.string.humidity_data));
            LineDataSet pressureDataSet = new LineDataSet(pressureReadings, getString(R.string.pressure_data));
            LineDataSet gasDataSet = new LineDataSet(gasReadings, getString(R.string.gas_data));
            LineDataSet altitudeDataSet = new LineDataSet(altitudeReadings, getString(R.string.altitude_data));

            // for line charts, customizes DATASET appearance and behavior
            customizeLineDataSet(temperatureDataSet);
            customizeLineDataSet(humidityDataSet);
            customizeLineDataSet(pressureDataSet);
            customizeLineDataSet(gasDataSet);
            customizeLineDataSet(altitudeDataSet);

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

