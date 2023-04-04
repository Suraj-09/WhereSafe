package com.project.wheresafe.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentPersonalBinding;
import com.project.wheresafe.models.DatabaseHelper;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.viewmodels.PersonalViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class PersonalFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    DatabaseHelper dbHelper;
    Timer timer;
    TimerTask timerTask;
    private FragmentPersonalBinding binding;
    private boolean paused;

//    private MapView mapView;
//    private GoogleMap googleMap;
    private FirebaseFirestore firebaseFirestore;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private LocationManager locationManager;
    private Location currentLocation;
    private double latitude;
    private double longitude;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PersonalViewModel personalViewModel = new ViewModelProvider(this).get(PersonalViewModel.class);

        binding = FragmentPersonalBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireActivity().getApplicationContext());
        firebaseFirestore = FirebaseFirestore.getInstance();

        View root = binding.getRoot();

        paused = false;


//        runOnTimer();


//        mapView = root.findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);

        locationManager = (LocationManager) requireActivity().getSystemService(Activity.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return null;
        }

        // Permission is already granted, request location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        return root;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.getAllPersonalSensorData(new FirestoreCallback() {
            @Override
            public void onResultGet() {

                populateCharts(firestoreHelper.getFirestoreData().getBmeDataArrayList());
            }
        });


//        // Initialize the MapView
//        mapView = (MapView) view.findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//
////        getPreciseLocation();
    }

    @Override
    public void onMapReady(GoogleMap map) {
//        googleMap = map;
//
//        // Set up the map
//        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//
//        // Add a marker
//        LatLng location = new LatLng(latitude, longitude);
//        googleMap.addMarker(new MarkerOptions().position(location).title("Location"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
//        System.out.println("MAP READY");
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
//        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mapView.onResume();
//        if (mapView != null) {
//            mapView.onResume();
//        }

//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, request it from the user
//            ActivityCompat.requestPermissions(requireActivity(),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//            return;
//        }
//
//        // Permission is already granted, request location updates
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//
//        getPreciseLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
//        // Update latitude and longitude with the new location
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//
//        // Update the map view with the new location
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull GoogleMap map) {
//                googleMap = map;
//
//                // Set up the map
//                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                googleMap.getUiSettings().setZoomControlsEnabled(true);
//
//                // Add a marker
//                LatLng location = new LatLng(latitude, longitude);
//                googleMap.addMarker(new MarkerOptions().position(location).title("Location"));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
//            }
//        });
    }


//    private void getPreciseLocation() {
//        FragmentActivity mActivity = getActivity();
//
//        // get user's precise location
//        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, request it from the user
//            ActivityCompat.requestPermissions(requireActivity(),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//        }
//
//        // Permission is already granted, request location updates
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//
//    }

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
//        BarChart temperatureChart = mActivity.findViewById(R.id.temperatureChart);
            LineChart temperatureChart = mActivity.findViewById(R.id.temperatureChart);
            LineChart humidityChart = mActivity.findViewById(R.id.humidityChart);
            LineChart pressureChart = mActivity.findViewById(R.id.pressureChart);
            LineChart gasChart = mActivity.findViewById(R.id.gasChart);
            LineChart altitudeChart = mActivity.findViewById(R.id.altitudeChart);

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
            LineDataSet temperatureDataSet = new LineDataSet(temperatureReadings, "Temperature Data");
            LineDataSet humidityDataSet = new LineDataSet(humidityReadings, "Humidity Data");
            LineDataSet pressureDataSet = new LineDataSet(pressureReadings, "Pressure Data");
            LineDataSet gasDataSet = new LineDataSet(gasReadings, "Gas Data");
            LineDataSet altitudeDataSet = new LineDataSet(altitudeReadings, "Altitude Data");

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

