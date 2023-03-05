package com.project.wheresafe;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.project.wheresafe.databinding.ActivityMainBinding;
import com.project.wheresafe.ui.personal.PersonalFragment;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final String DEVICE_NAME = "WhereSafe";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    public final static UUID UUID_ENVIRONMENTAL_SENSING = UUID.fromString(GattAttributes.ENVIRONMENTAL_SENSING);
    public final static UUID UUID_BME680_DATA = UUID.fromString(GattAttributes.BME680_DATA);
    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
    private double temperature;
    private double humidity;
    private double pressure;
    private double gas;
    private double altitude;

    List<BluetoothGattCharacteristic> chars = new ArrayList<>();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        dbHelper = new DatabaseHelper(getApplicationContext());

        //  FLOATING ACTION BAR (has potential future use??)
        //  Go to app_bar_main.xml and remove comment to see what this is
        /*  binding.appBarMain.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show());   */
        checkBluetooth();
        checkLocation();
        checkBluetoothScan();
        checkBluetoothConnect();

        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setDeviceName(DEVICE_NAME)
                .build();

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        scanConnect(scanner, scanFilter, scanSettings);

    }

    @Override
    protected void onStart() {
        super.onStart();
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


    void scanConnect(BluetoothLeScanner scanner, ScanFilter scanFilter, ScanSettings scanSettings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN_PERMISSION);
            return;
        }

        scanner.startScan(Collections.singletonList(scanFilter), scanSettings, new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                    return;
                }
                System.out.println("DEVICE_NAME = " + device.getName());

                if (device.getName() != null && device.getName().equals(DEVICE_NAME)) {
                    scanner.stopScan(this);
                    device.connectGatt(getApplicationContext(), false, new BluetoothGattCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                                    return;
                                }
                                gatt.discoverServices();
                            }
                        }

                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                            if (status == BluetoothGatt.GATT_SUCCESS) {
                                setCharacteristics(gatt);
                            }
                        }

                        @Override
                        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                            readCharacteristics(characteristic);
                        }
                    });
                }
            }
        });

    }


    void setCharacteristics(BluetoothGatt gatt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
            return;
        }

        BluetoothGattService service = gatt.getService(UUID_ENVIRONMENTAL_SENSING);
        BluetoothGattCharacteristic dataCharacteristic = service.getCharacteristic(UUID_BME680_DATA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
            return;
        }

        gatt.setCharacteristicNotification(dataCharacteristic, true);
        BluetoothGattDescriptor dataDescriptor = dataCharacteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
        dataDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(dataDescriptor);
    }

    void readCharacteristics(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        String dataStr = new String(data);

        String[] arrStr = dataStr.split("\\|", 4);
        if (arrStr[0].equals("1")) {
            temperature = (Double.parseDouble(arrStr[1]) / 100.0);
            humidity = (Double.parseDouble(arrStr[2]) / 100.0);
            pressure = (Double.parseDouble(arrStr[3]) / 10.0);
        } else {
            gas = (Double.parseDouble(arrStr[1]) / 100.0);
            altitude = (Double.parseDouble(arrStr[2]) / 100.0);

            // second part of data received, store object
            storeData();
        }

    }

    private void storeData(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BmeData bmeData = new BmeData(temperature, humidity, pressure, gas, altitude);
                dbHelper.insertBmeData(bmeData);
            }
        });
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

}