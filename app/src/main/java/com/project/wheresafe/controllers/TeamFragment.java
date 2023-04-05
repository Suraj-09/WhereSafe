package com.project.wheresafe.controllers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentTeamBinding;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.utils.TeamListAdapter;
import com.project.wheresafe.utils.User;
import com.project.wheresafe.utils.UserArrayAdapter;
import com.project.wheresafe.viewmodels.TeamViewModel;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;


public class TeamFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private final String TAG = "TeamFragment";
    FirestoreHelper firestoreHelper;
    private FragmentTeamBinding binding;
    private String teamName;
    private String teamCode;
    private TeamViewModel teamViewModel;
    private SharedPreferenceHelper sharedPreferenceHelper;

    private MapView mapView;
    private GoogleMap googleMap;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private LocationManager locationManager;
    private Location currentLocation;
    private double latitude;
    private double longitude;
    private RecyclerView recyclerView;
    private ListView listView;
    private Activity mActivity;
    private TextView txtMyTeam;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        teamViewModel = new TeamViewModel();

        binding = FragmentTeamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        while (mActivity == null) {
            mActivity = getActivity();
        }

        txtMyTeam = binding.getRoot().findViewById(R.id.txtMyTeam);
        listView = binding.getRoot().findViewById(R.id.team_list_view);


        firestoreHelper = new FirestoreHelper();
        teamCode = sharedPreferenceHelper.getTeamCode();
        if (teamCode != null) {
            showTeamView(teamCode);
        } else {
            showCreateJoinView();
        }

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, this);

//        sharedPreferenceHelper = new SharedPreferenceHelper(mActivity.getApplicationContext());

////        firestoreHelper.getUser(new FirestoreCallback() {
////            @Override
////            public void onResultGet() {
////                teamCode = firestoreHelper.getFirestoreData().getUser().getTeamCode();
////                if (teamCode != null) {
////                    showTeamView(teamCode);
////                } else {
////                    showCreateJoinView();
////                }
////            }
////        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize the MapView
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

//        getPreciseLocation();
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
        System.out.println("MAP READY");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);

        mActivity = getActivity();


    }

    private void showTeamView(String teamCode) {
        Log.d(TAG, "showTeamView()");
        binding.noTeamLayout.setVisibility(View.GONE);
        binding.teamLayout.setVisibility(View.VISIBLE);

        firestoreHelper.getTeamName(teamCode, new FirestoreCallback() {
            @Override
            public void onResultGet() {
                teamName = firestoreHelper.getFirestoreData().getTeamName();

                if (binding != null) {
                    TextView txtMyTeam = (TextView) binding.getRoot().findViewById(R.id.txtMyTeam);
                    String teamText = teamName;
                    txtMyTeam.setText(teamText);
                }

            }
        });

        firestoreHelper.getTeamMembers(teamCode, new FirestoreCallback() {
            @Override
            public void onResultGet() {
                User currentUser = sharedPreferenceHelper.getCurrentUser();
                ArrayList<User> teamList = firestoreHelper.getFirestoreData().getTeamMembersArrayList();

                // remove self from team list
                for (int i = 0; i < teamList.size(); i++) {
                    if (teamList.get(i).getId().equals(currentUser.getId())) {
                        teamList.remove(i);
                        break;
                    }
                }

                if (mActivity != null) {


                    if (listView != null) {

                        UserArrayAdapter userAdapter;
                        userAdapter = new UserArrayAdapter (mActivity, 0, teamList);
                        listView.setAdapter(userAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                User item = (User) parent.getItemAtPosition(position);
                                Log.d(TAG, item.toString());
                                // Create a new instance of the TeammateFragment

                                Bundle args = new Bundle();
                                args.putString("teammate_id", item.getId());

                                Navigation.findNavController(view).navigate(R.id.navigation_teammate, args);

                            }
                        });

                    }

                }

            }
        });

    }

    private void showCreateJoinView() {
        Log.d(TAG, "showCreateJoinView()");
        binding.noTeamLayout.setVisibility(View.VISIBLE);
        binding.teamLayout.setVisibility(View.GONE);

        Button createTeamButton = binding.createTeamButton;
        Button joinTeamButton = binding.joinTeamButton;

        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTeamDialog();
            }
        });

        joinTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinTeamDialog();
            }
        });

    }

    private void createTeamDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create Team");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String teamName = input.getText().toString().trim();

                createTeam(teamName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void joinTeamDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Join Team");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String teamCode = input.getText().toString().trim();
                joinTeam(teamCode);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createTeam(String teamName) {
        firestoreHelper.getTeamCodes(new FirestoreCallback() {
            @Override
            public void onResultGet() {

                ArrayList<String> teamCodes = firestoreHelper.getFirestoreData().getTeamCodeArraylist();

                String teamCode = "";
                boolean exists = true;

                while (exists) {
                    teamCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
                    exists = teamCodes.contains(teamCode);
                }

                firestoreHelper.createTeam(sharedPreferenceHelper.getUid(), teamName, teamCode);
                firestoreHelper.addTeamCode(sharedPreferenceHelper.getUid(), teamCode);

                // Create a new AlertDialog to display the team code
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Team Code");
                builder.setMessage("Your team code is:\n\n" + teamCode);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                // change view
                showTeamView(teamCode);
            }
        });

    }

    private void joinTeam(String teamCode) {
        firestoreHelper.getTeamCodes(new FirestoreCallback() {
            @Override
            public void onResultGet() {

                ArrayList<String> teamCodes = firestoreHelper.getFirestoreData().getTeamCodeArraylist();

                if (teamCodes.contains(teamCode)) {
                    firestoreHelper.addTeamCode(sharedPreferenceHelper.getUid(), teamCode);
                    firestoreHelper.addMember(sharedPreferenceHelper.getUid(), teamCode);

                    // change view
                    showTeamView(teamCode);
                } else {
                    // Create a new AlertDialog to display the team code
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("INVALID CODE");
                    builder.setMessage("Please try again");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mapView != null) {
            mapView.onResume();
        }


        while (mActivity == null) {
            mActivity = getActivity();
        }

        teamCode = sharedPreferenceHelper.getTeamCode();
        if (teamCode != null) {
            showTeamView(teamCode);
        } else {
            showCreateJoinView();
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        // Permission is already granted, request location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, (LocationListener) this);

        getPreciseLocation();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Update latitude and longitude with the new location
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Update the map view with the new location
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
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

    private void getPreciseLocation() {
        FragmentActivity mFragmentActivity = getActivity();

        // get user's precise location
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mFragmentActivity);
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