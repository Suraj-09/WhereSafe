package com.project.wheresafe.controllers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentTeamBinding;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.viewmodels.TeamViewModel;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;


public class TeamFragment<TeamMember> extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    FirestoreHelper firestoreHelper;
    private FragmentTeamBinding binding;
    private String teamName;
    private String teamCode;
    private TeamViewModel teamViewModel;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private MapView mapView;
    private GoogleMap googleMap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        binding = FragmentTeamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        // Check if the app has permission to access the user's location
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Request permission to access the user's location
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            // Start updating the user's location
//            firestoreHelper.startUpdatingLocation();
//        }

        firestoreHelper = new FirestoreHelper();
        firestoreHelper.getUser(new FirestoreCallback() {
            @Override
            public void onResultGet() {
                teamCode = firestoreHelper.getFirestoreData().getUser().getTeamCode();
                if (teamCode != null) {
                    showTeamView(teamCode);
                } else {
                    showCreateJoinView();
                }
            }
        });

        return root;
    }

    private void showTeamView(String teamCode) {
        binding.createTeamButton.setVisibility(View.GONE);
        binding.joinTeamButton.setVisibility(View.GONE);

        firestoreHelper.getTeam(teamCode, new FirestoreCallback() {
            @Override
            public void onResultGet() {
                teamName = firestoreHelper.getFirestoreData().getUser().getTeamName();

                TextView textTeam = (TextView) binding.getRoot().findViewById(R.id.text_team);
                String teamText = "Team Name: " + teamName;
                textTeam.setText(teamText);


                TextView textCode = binding.getRoot().findViewById(R.id.code_team);
                String codeText = "Team Code: " + teamCode;
                textCode.setText(codeText);

                // TODO: populate view to show team stuff

                for (DocumentReference docRef : firestoreHelper.getFirestoreData().getUser().getTeamMembers()) {
                    System.out.println(docRef);
                }
            }
        });

    }

    private void showCreateJoinView() {
        binding.createTeamButton.setVisibility(View.VISIBLE);
        binding.joinTeamButton.setVisibility(View.VISIBLE);

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

        // Destroy MapView
       // mapView.onDestroy();
    }

    private void createTeam(String teamName) {
        firestoreHelper.getTeamCodes(new FirestoreCallback() {
            @Override
            public void onResultGet() {

                ArrayList<String> teamCodes = firestoreHelper.firestoreData.getTeamCodeArraylist();

                String teamCode = "";
                boolean exists = true;

                while (exists) {
                    teamCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
                    exists = teamCodes.contains(teamCode);
                }

                firestoreHelper.createTeam(teamName, teamCode);
                firestoreHelper.addTeamCode(teamCode);

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

                ArrayList<String> teamCodes = firestoreHelper.firestoreData.getTeamCodeArraylist();

                if (teamCodes.contains(teamCode)) {
                    firestoreHelper.addTeamCode(teamCode);
                    firestoreHelper.addMember(teamCode);

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
}