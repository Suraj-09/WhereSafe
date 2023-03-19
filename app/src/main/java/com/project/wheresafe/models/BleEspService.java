package com.project.wheresafe.models;

import android.Manifest;
import android.app.Activity;
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
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.GattConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class BleEspService {

    private static final String TAG = "BleEspService";
    private final String DEVICE_NAME = "WhereSafe";

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    public final static UUID UUID_ENVIRONMENTAL_SENSING = UUID.fromString(GattConfig.ENVIRONMENTAL_SENSING);
    public final static UUID UUID_BME680_DATA = UUID.fromString(GattConfig.BME680_DATA);
    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString(GattConfig.CLIENT_CHARACTERISTIC_CONFIG);
    private double temperature;
    private double humidity;
    private double pressure;
    private double gas;
    private double altitude;

    DatabaseHelper dbHelper;

    Context context;
    Activity activity;
    BluetoothLeScanner scanner;
    ScanFilter scanFilter;
    ScanSettings scanSettings;
    FirestoreHelper firestoreHelper;
    public BleEspService(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void run() {
        dbHelper = new DatabaseHelper(context);
        firestoreHelper = new FirestoreHelper();
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        scanner = bluetoothAdapter.getBluetoothLeScanner();
        scanFilter = new ScanFilter.Builder()
                .setDeviceName(DEVICE_NAME)
                .build();

        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        scanConnect();
    }
    private void scanConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN_PERMISSION);
            return;
        }

        scanner.startScan(Collections.singletonList(scanFilter), scanSettings, new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                    return;
                }
                System.out.println("DEVICE_NAME = " + device.getName());

                if (device.getName() != null && device.getName().equals(DEVICE_NAME)) {
                    scanner.stopScan(this);
                    device.connectGatt(context.getApplicationContext(), false, new BluetoothGattCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    activity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
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
                            System.out.println("data read");
                        }
                    });
                }
            }
        });

    }
    private void setCharacteristics(BluetoothGatt gatt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
            return;
        }

        BluetoothGattService service = gatt.getService(UUID_ENVIRONMENTAL_SENSING);
        BluetoothGattCharacteristic dataCharacteristic = service.getCharacteristic(UUID_BME680_DATA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
            return;
        }

        gatt.setCharacteristicNotification(dataCharacteristic, true);
        BluetoothGattDescriptor dataDescriptor = dataCharacteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
        dataDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(dataDescriptor);
    }

    private void readCharacteristics(BluetoothGattCharacteristic characteristic) {

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
            Log.d(TAG, "BME680 Data received");
            // second part of data received, store object
//            storeData();
            storeFirestore();
        }
    }

    private void storeData(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BmeData bmeData = new BmeData(temperature, humidity, pressure, gas, altitude);
//                dbHelper.insertBmeData(bmeData);

//                System.out.println(bmeData);


                FirestoreHelper firestoreHelper = new FirestoreHelper();
                System.out.println("store");
                firestoreHelper.addBmeData(bmeData);
            }
        });
    }

    private void storeFirestore() {
        BmeData bmeData = new BmeData(temperature, humidity, pressure, gas, altitude);
        Log.d(TAG, bmeData.toString());
        firestoreHelper.addBmeData(bmeData);
    }
}