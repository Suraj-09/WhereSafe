package com.project.wheresafe.ui.personal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.wheresafe.BmeData;
import com.project.wheresafe.DatabaseHelper;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentPersonalBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.SupportMapFragment;


import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PersonalFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private FragmentPersonalBinding binding;
    private boolean paused;
    DatabaseHelper dbHelper;
    Timer timer;
    TimerTask timerTask;
    private MapView mapView;
    private GoogleMap googleMap;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private LocationManager locationManager;
    private Location currentLocation;
    private double latitude;
    private double longitude;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PersonalViewModel personalViewModel =
                new ViewModelProvider(this).get(PersonalViewModel.class);

        binding = FragmentPersonalBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireActivity().getApplicationContext());

        View root = binding.getRoot();

        paused = false;
        runOnTimer();


        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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

        // Initialize the MapView
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Set up the MapView
//        mapView.getMapAsync(new OnMapReadyCallback() {
//
//        });

    }
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Set up the map
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title("Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mapView != null) {
            mapView.onResume();
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        // Permission is already granted, request location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Update latitude and longitude with the new location
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Update the map view with the new location
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                // Set up the map
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // Add a marker
                LatLng location = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(location).title("Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }
        });
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

                // get user's precise location
                FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }

                // Permission is already granted, request location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }

}

