package com.project.wheresafe.controllers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.ActivityMainBinding;
import com.project.wheresafe.models.BleEspService;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String DEVICE_NAME = "WhereSafe";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    private boolean initFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            initFlag = false;
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else {
            init();
        }







//        FirestoreHelper firestoreHelper = new FirestoreHelper();
//
//        // get latest object stored
//        firestoreHelper.getLatestPersonalSensorData(new FirestoreCallback() {
//            @Override
//            public void onResultGet() {
//                BmeData bmeData = firestoreHelper.getBmeDataLatest();
//                // do stuff
//            }
//        });
//
//        // Get an Arraylist of BmeData
//        firestoreHelper.getAllPersonalSensorData(new FirestoreCallback() {
//            @Override
//            public void onResultGet() {
//                ArrayList<BmeData> bmeDataArrayList = firestoreHelper.getBmeDataArrayList();
//                // do stuff
//
//                //
//            }
//        });

    }

    private void init() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        System.out.println(currentUser.getEmail());
//        Toast.makeText(MainActivity.this, "Welcome, " + currentUser.getEmail() , Toast.LENGTH_SHORT).show();

        checkBluetooth();
        checkLocation();
        checkBluetoothScan();
        checkBluetoothConnect();

        BleEspService bleEspService = new BleEspService(getApplicationContext(), (Activity) this);
        bleEspService.run();

        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        initFlag = true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!initFlag) {
            init();
            finish();
            startActivity(getIntent());
        } else {
            BottomNavigationView navViewBottom = findViewById(R.id.nav_view_bottom);
            NavigationView navigationView = binding.navView; // same thing as findViewById(R.id.nav_view);
            DrawerLayout drawer = binding.drawerLayout;

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_advanced_data, R.id.navigation_settings)
                    .setOpenableLayout(drawer)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            NavigationUI.setupWithNavController(navViewBottom, navController);
        }
    }


    @Override
    protected void onStop() {
        FirebaseAuth.getInstance().signOut();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // top left icon, opens drawer or navigates back to home if at Personal Metrics or Team Metrics
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    void checkBluetooth() {
        // First, check if Bluetooth is supported and enabled
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth is not supported or not enabled, show a message to the user
            Toast.makeText(this, "Please enable Bluetooth and try again", Toast.LENGTH_SHORT).show();
        }
    }

    void checkBluetoothScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // Bluetooth scan permission is not granted, request it from the user
                requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN_PERMISSION);
            }
        }
    }

    void checkBluetoothConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Bluetooth scan permission is not granted, request it from the user
                requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
            }
        }
    }

    void checkLocation() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location permission is not granted, request it from the user
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

//    @Override
//    public void getPresonalData(ArrayList<BmeData> bmeDataArrayList) {
//        System.out.println(bmeDataArrayList);
//    }
}