package com.project.wheresafe.controllers;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.os.LocaleListCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.ActivityMainBinding;
import com.project.wheresafe.models.BleEspForegroundService;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.FirestoreCallback;

public class MainActivity extends AppCompatActivity {
    final private String TAG = "MainActivity";
    private final String DEVICE_NAME = "WhereSafe";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    private static final int REQUEST_ENABLE_BT = 4;
    private static final int REQUEST_ENABLE_BT_PERMISSION = 5;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentFirebaseUser;
    private FirestoreHelper firestoreHelper;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private boolean initFlag;


    private Button btnConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);
        firestoreHelper = new FirestoreHelper();
        sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (sharedPreferenceHelper.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.Theme_WhereSafe_DarkTheme);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.Theme_WhereSafe);
        }

        sharedPreferenceHelper.getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        String lang = sharedPreferenceHelper.getLanguageCode();
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(lang);
        AppCompatDelegate.setApplicationLocales(appLocale);


        // on create
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = mAuth.getCurrentUser();
        if (currentFirebaseUser == null) {
            initFlag = false;
            goToSignIn();
        } else {
            init();
        }

    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.connection_status_key))) {
                String newStatus = sharedPreferences.getString(getString(R.string.connection_status_key), getString(R.string.status_connect));

                if (newStatus != null) {
                    btnConnection = findViewById(R.id.btnConnection);

                    if (newStatus.equals(getString(R.string.status_reconnect)) || newStatus.equals(getString(R.string.status_connect)) ) {
                        btnConnection.setEnabled(true);
                        stopBleEspService();
                    } else {
                        btnConnection.setEnabled(false);
                    }

                    btnConnection.setText(newStatus);
                }


                Log.d(TAG, "connection status = " + newStatus);
            }
        }
    };

    private BleEspForegroundService mBleEspService;

    public void startBleEspService() {
        Intent serviceIntent = new Intent(this, BleEspForegroundService.class);
        startForegroundService(serviceIntent);
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BleEspForegroundService.LocalBinder binder = (BleEspForegroundService.LocalBinder) iBinder;
            mBleEspService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mBleEspService = null;
        }
    };

    public void stopBleEspService() {
        if (mBleEspService != null) {
            unbindService(mServiceConnection);
            mBleEspService = null;
        }

        Intent serviceIntent = new Intent(this, BleEspForegroundService.class);
        stopService(serviceIntent);
    }

    private void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        // Bluetooth is disabled, show a message to the user and prompt them to enable it
                        Toast.makeText(MainActivity.this, "Bluetooth has been disabled. Please enable it to use this app.", Toast.LENGTH_SHORT).show();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted, request it
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT_PERMISSION);
                            return;
                        }
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // Bluetooth is turning off, do something here if needed
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // Bluetooth is enabled, do something here if needed
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // Bluetooth is turning on, do something here if needed
                        break;
                }
            }
        }
    };


    private void init() {
        Log.d(TAG, "init()");
        mAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = mAuth.getCurrentUser();
        sharedPreferenceHelper.saveUser(currentFirebaseUser);

        String lang = sharedPreferenceHelper.getLanguageCode();
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(lang);
        AppCompatDelegate.setApplicationLocales(appLocale);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        checkBluetooth();
        checkLocation();
        checkBluetoothScan();
        checkBluetoothConnect();

        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        setUserInfo();

        initFlag = true;

        // Register a broadcast receiver to listen for Bluetooth state changes
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);

        // Check Bluetooth state
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth, show an error message to the user
            Toast.makeText(MainActivity.this, "This device does not support Bluetooth.", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                // Bluetooth is disabled, show a message to the user and prompt them to enable it
                Toast.makeText(MainActivity.this, "Bluetooth is currently disabled. Please enable it to use this app.", Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT_PERMISSION);
                        return;
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT_PERMISSION);
                        return;
                    }
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_BT_PERMISSION);
                        return;
                    }
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_BT_PERMISSION);
                        return;
                    }
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        btnConnection = findViewById(R.id.btnConnection);
//        btnConnection.setText(sharedPreferenceHelper.getConnectionStatus());
        btnConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBleEspService();
                startBleEspService();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                // Permission granted, enable Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT_PERMISSION);
                        return;
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT_PERMISSION);
                        return;
                    }
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_BT_PERMISSION);
                        return;
                    }
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_BT_PERMISSION);
                        return;
                    }
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // Permission denied, show a message or handle the error
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // User enabled Bluetooth, do something here if needed
            } else {
                // User did not enable Bluetooth, prompt the user to turn it on again
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        if (!initFlag) {
            init();
            finish();
            startActivity(getIntent());
        } else {
            BottomNavigationView navViewBottom = findViewById(R.id.nav_view_bottom);
            NavigationView navigationView = binding.navView; // same thing as findViewById(R.id.nav_view);
            DrawerLayout drawer = binding.drawerLayout;

            navigationView.getMenu().findItem(R.id.sign_out).setOnMenuItemClickListener(menuItem -> {
                stopBleEspService();
                FirebaseAuth.getInstance().signOut();
                goToSignIn();
                return true;
            });

            // creates a specific set of top level destinations
            // the Up button (back arrow) will not be displayed when on these destinations
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
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        String lang = sharedPreferenceHelper.getLanguageCode();
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(lang);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }

    private void setUserInfo() {
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        TextView txtName = headerView.findViewById(R.id.drawerName);
        TextView txtEmail = headerView.findViewById(R.id.drawerEmail);

        if (currentFirebaseUser.getDisplayName() != null) {
             txtName.setText(currentFirebaseUser.getDisplayName());
            txtEmail.setText(currentFirebaseUser.getEmail());
        } else {
            setDisplayName();
        }

    }

    private void setDisplayName() {
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        FirebaseUser user = mAuth.getCurrentUser();
        firestoreHelper.getUser(user.getUid(), new FirestoreCallback() {
            @Override
            public void onResultGet() {
                String name = firestoreHelper.getFirestoreData().getUser().getName();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                currentFirebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                    setUserInfo();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // top left icon, opens drawer or navigates back to home if at Personal Metrics or Team Metrics
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothStateReceiver);
    }

    void checkBluetooth() {
        // First, check if Bluetooth is supported
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            // Bluetooth is not supported, show a message to the user
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register the BroadcastReceiver to listen for changes to the Bluetooth state
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);

        // Check if Bluetooth is currently enabled
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // Bluetooth is enabled, unregister the BroadcastReceiver
            unregisterReceiver(bluetoothStateReceiver);
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

}