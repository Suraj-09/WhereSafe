package com.project.wheresafe.models;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.project.wheresafe.R;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.GattConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BleEspForegroundService extends Service {


    public final static UUID UUID_ENVIRONMENTAL_SENSING = UUID.fromString(GattConfig.ENVIRONMENTAL_SENSING);
    public final static UUID UUID_BME680_DATA = UUID.fromString(GattConfig.BME680_DATA);
    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString(GattConfig.CLIENT_CHARACTERISTIC_CONFIG);
    private static final String TAG = "BleEspForegroundService";
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 3;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 4;
    private static final String NOTIFICATION_CHANNEL_ID = "BleEspForegroundServiceChannel";
    private static final String DEVICE_NAME = "WhereSafe";
    private static final int MODE_DEVICE_MAC_ADDRESS = 1;
    private static final int MODE_DEVICE_NAME = 2;

    private Context mContext;
    //    Activity mActivity;
    private FirestoreHelper firestoreHelper;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private ScanFilter scanFilter;
    private ScanSettings scanSettings;
    private String DEVICE_MAC_ADDRESS;
    private BluetoothGatt bleGatt;
    private double temperature;
    private double humidity;
    private double pressure;
    private double gas;
    private double altitude;
    private BmeData lastBmeData;

    private long lastDataTime = 0;
    long curTime;
    private int notificationId = 1;

    String lastCharacteristicTime;

    Set<String> threadSet = new HashSet<>();

    private final int NOTIFICATION_TEMPERATURE_ID = 2;
    private final int NOTIFICATION_HUMIDITY_ID = 2;
    private final int NOTIFICATION_IAQ_ID = 2;
    private Notification foregroundNotification;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
//        mActivity = getact
        firestoreHelper = new FirestoreHelper();
        sharedPreferenceHelper = new SharedPreferenceHelper(mContext);

        createNotificationChannel();
        createNotificationChannelLow();
        createNotificationChannelDefault();
        createNotificationChannelHigh();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothAdapter.getBluetoothLeScanner();

        scanFilter = new ScanFilter.Builder()
                .setDeviceName(DEVICE_NAME)
                .build();

        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String macAddress = sharedPreferenceHelper.getMacAddress();

        if (macAddress != null) {
            DEVICE_MAC_ADDRESS = macAddress;
            scanConnect(MODE_DEVICE_MAC_ADDRESS);
        } else {
            scanConnect(MODE_DEVICE_NAME);
        }

        foregroundNotification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("WhereSafe Bluetooth Service")
                .setContentText("Connected with " + DEVICE_NAME)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();



        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetNotifications();

        sharedPreferenceHelper.setConnectionStatus(getString(R.string.status_connect));
        stopForeground(true);
        if (bleGatt != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                mActivity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                return;
            }
            bleGatt.disconnect();
            bleGatt.close();
            Log.d(TAG, "DESTROYED");
        }
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // Binder class implementation
    public class LocalBinder extends Binder {
        public BleEspForegroundService getService() {
            // Return this service instance
            return BleEspForegroundService.this;
        }
    }

    // Instantiate the binder
    private final IBinder mBinder = new LocalBinder();

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "BleEspForegroundService Channel", NotificationManager.IMPORTANCE_LOW);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void createNotificationChannelLow() {
        NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id_low),
                "Low Notification Channel", NotificationManager.IMPORTANCE_LOW);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void createNotificationChannelDefault() {
        NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id_default),
                "Low Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void createNotificationChannelHigh() {
        NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id_high),
                "Low Notification Channel", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    private void scanConnect(int mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            mActivity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN_PERMISSION);
            return;
        }

//        Log.d(TAG, "STARTING SCAN");
        scanner.startScan(Collections.singletonList(scanFilter), scanSettings, new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    mActivity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                    return;
                }

                boolean connectionCondition = false;
                if (mode == MODE_DEVICE_MAC_ADDRESS) {
                    connectionCondition = device.getAddress() != null && device.getAddress().equals(DEVICE_MAC_ADDRESS);
                } else {
                    connectionCondition = device.getName() != null && device.getName().equals(DEVICE_NAME);
                }

                // Check the MAC address or name of the discovered device
                if (connectionCondition) {
//                    Log.d(TAG, "scanner.stopScan()");
                    scanner.stopScan(this);

                    if (device.getAddress() != null) {
                        saveMacAddress(device.getAddress());
                    }

                    device.connectGatt(mContext.getApplicationContext(), false, new BluetoothGattCallback() {

                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                startForeground(notificationId, foregroundNotification);

                                Log.d(TAG, "sharedPreferenceHelper.getConnectionStatus()");
                                Log.d(TAG, sharedPreferenceHelper.getConnectionStatus());
                                sharedPreferenceHelper.setConnectionStatus(getString(R.string.status_connect));
                                sharedPreferenceHelper.setConnectionStatus(getString(R.string.status_connected));
                                Log.d(TAG, sharedPreferenceHelper.getConnectionStatus());
                                Log.d(TAG, "sharedPreferenceHelper.getConnectionStatus()");

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                                    mActivity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                                    return;
                                }
                                gatt.requestMtu(64);
                                Log.d(TAG, "onConnectionStateChange() CONNECTED => newState = " + newState);
                            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                sharedPreferenceHelper.setConnectionStatus(getString(R.string.status_reconnect));
                                Log.d(TAG, "onConnectionStateChange() DISCONNECTED => newState = " + newState);
                            } else {
                                Log.d(TAG, "onConnectionStateChange() OTHER => newState = " + newState);
                            }

                        }

                        @Override
                        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                            if (status == BluetoothGatt.GATT_SUCCESS) {
//                                Log.d(TAG, "MTU size changed to " + mtu);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                                    mActivity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                                    return;
                                }
                                gatt.discoverServices();
                            } else {
                                Log.d(TAG, "MTU negotiation failed with status: " + status);
                            }
                        }

                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                            if (status == BluetoothGatt.GATT_SUCCESS) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                                    mActivity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                                    return;
                                }
                                bleGatt = gatt;
                                setCharacteristics(gatt);
                            }
                        }

                        @Override
                        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//                            Log.d(TAG, "CHARACTERISTIC READ THREAD " + Thread.currentThread().getName());
                            readCharacteristics(characteristic);
                        }

                    });
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(TAG, "onScanFailed: " + errorCode);
            }
        });

        // Add notification for foreground service
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, mContext.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Bluetooth Scan")
                .setContentText("Scanning for devices...")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notification = builder.build();
        startForeground(1, notification);
    }

    private void setCharacteristics(BluetoothGatt gatt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            mActivity.requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
            return;
        }

        BluetoothGattService service = gatt.getService(UUID_ENVIRONMENTAL_SENSING);
        BluetoothGattCharacteristic dataCharacteristic = service.getCharacteristic(UUID_BME680_DATA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
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

        String[] arrStr = dataStr.split("\\|", 10);

        temperature = (Double.parseDouble(arrStr[1]) / 100.0);
        humidity = (Double.parseDouble(arrStr[2]) / 100.0);
        pressure = (Double.parseDouble(arrStr[3]) / 10.0);
        gas = (Double.parseDouble(arrStr[4]) / 100.0);
        altitude = (Double.parseDouble(arrStr[5]) / 100.0);

        storeFirestore();
    }

    private void storeFirestore() {
        BmeData bmeData = new BmeData(temperature, humidity, pressure, gas, altitude);

        if (lastBmeData == null) {
            lastBmeData = new BmeData(bmeData);
        }

        boolean isEqual = bmeData.equals(lastBmeData);
        if (!isEqual) {
            Log.d(TAG, "storing  " + bmeData.toString());
//            Log.d(TAG, "last bme " + lastBmeData.toString());

            lastBmeData = new BmeData(bmeData);

//            firestoreHelper.addBmeData(sharedPreferenceHelper.getUid(), bmeData);
            handleNotifications();
        }

    }

    private void saveMacAddress(String macAddress) {
        firestoreHelper.addMacAddress(sharedPreferenceHelper.getUid(), macAddress);
    }

    private void handleNotifications() {
        String textTitle = "";
        String textContent = "";

        long currentTime = System.currentTimeMillis();

        if (lastBmeData.getTemperature() > 30 && (sharedPreferenceHelper.getLastTemperatureNotification() == -1)) {
            sharedPreferenceHelper.setLastTemperatureNotification(currentTime);
            textTitle = "High Temperature";
            textContent = "The surrounding temperature is " + lastBmeData.getTemperature()  + " °C";
            sendNotificationLow(textTitle, textContent, NotificationCompat.PRIORITY_LOW, NOTIFICATION_TEMPERATURE_ID);
        }

        if (lastBmeData.getTemperature() < -10 && (sharedPreferenceHelper.getLastTemperatureNotification() == -1)) {
            sharedPreferenceHelper.setLastTemperatureNotification(currentTime);
            textTitle = "Low Temperature";
            textContent = "The surrounding temperature is " + lastBmeData.getTemperature()  + " °C";
            sendNotificationLow(textTitle, textContent, NotificationCompat.PRIORITY_LOW, NOTIFICATION_TEMPERATURE_ID);
        }

        if (lastBmeData.getHumidity() > 60 && (sharedPreferenceHelper.getLastHumidityNotification() == -1)) {
            sharedPreferenceHelper.setLastHumidityNotification(currentTime);
            textTitle = "High Humidity";
            textContent = "The surrounding humidity is " + lastBmeData.getHumidity()  + " %";
            sendNotificationLow(textTitle, textContent, NotificationCompat.PRIORITY_LOW, NOTIFICATION_HUMIDITY_ID);
        }

        if (lastBmeData.getHumidity() < 25 && (sharedPreferenceHelper.getLastHumidityNotification() == -1)) {
            sharedPreferenceHelper.setLastHumidityNotification(currentTime);
            textTitle = "Low Humidity";
            textContent = "The surrounding humidity is " + lastBmeData.getHumidity()  + " %";
            sendNotificationLow(textTitle, textContent, NotificationCompat.PRIORITY_LOW, NOTIFICATION_HUMIDITY_ID);
        }

        if (lastBmeData.getGas() > 350 && (sharedPreferenceHelper.getLastIaqExtremeNotification() == -1)) {
            sharedPreferenceHelper.setLastIaqExtremeNotification(currentTime);
            textTitle = "Extremely Polluted Air";
            textContent = "Contamination needs to be identified! Avoid presence in location, maximize ventilation";

            sendNotificationHigh(textTitle, textContent, NotificationCompat.PRIORITY_DEFAULT, NOTIFICATION_IAQ_ID);
        } else if (lastBmeData.getGas() > 250 && (sharedPreferenceHelper.getLastIaqSevereNotification() == -1)) {
            sharedPreferenceHelper.setLastIaqSevereNotification(currentTime);
            textTitle = "Severely Polluted Air";
            textContent = "Contamination should be identified! Maximize ventilation";

            sendNotificationHigh(textTitle, textContent, NotificationCompat.PRIORITY_DEFAULT, NOTIFICATION_IAQ_ID);
        } else if (lastBmeData.getGas() > 200 && (sharedPreferenceHelper.getLastIaqHeavyNotification() == -1)) {
            sharedPreferenceHelper.setLastIaqHeavyNotification(currentTime);
            textTitle = "Heavily Polluted Air";
            textContent = "Optimize ventilation";
            sendNotificationHigh(textTitle, textContent, NotificationCompat.PRIORITY_DEFAULT, NOTIFICATION_IAQ_ID);
        } else if (lastBmeData.getGas() > 150 && (sharedPreferenceHelper.getLastIaqModerateNotification() == -1)) {
            sharedPreferenceHelper.setLastIaqModerateNotification(currentTime);
            textTitle = "Moderately Polluted Air";
            textContent = "Increase ventilation with clean air";
            sendNotificationDefault(textTitle, textContent, NotificationCompat.PRIORITY_DEFAULT, NOTIFICATION_IAQ_ID);
        } else if (lastBmeData.getGas() > 100 && (sharedPreferenceHelper.getLastIaqLightNotification() == -1)) {
            sharedPreferenceHelper.setLastIaqLightNotification(currentTime);
            textTitle = "Lightly Polluted Air";
            textContent = "Ventilation suggested";
            sendNotificationDefault(textTitle, textContent, NotificationCompat.PRIORITY_DEFAULT, NOTIFICATION_IAQ_ID);
        }

    }

    private void sendNotificationLow(String title, String text, int priority, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, getResources().getString(R.string.channel_id_low))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(priority);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_POST_NOTIFICATIONS);
            return;
        }
        notificationManager.notify(id, builder.build());
        Log.d(TAG, "NOTIFICATIONS => " + title);
    }


    private void sendNotificationDefault(String title, String text, int priority, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, getResources().getString(R.string.channel_id_default))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(priority);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_POST_NOTIFICATIONS);
            return;
        }
        notificationManager.notify(id, builder.build());
        Log.d(TAG, "NOTIFICATIONS => " + title);
    }

    private void sendNotificationHigh(String title, String text, int priority, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, getResources().getString(R.string.channel_id_high))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(priority);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_POST_NOTIFICATIONS);
            return;
        }
        notificationManager.notify(id, builder.build());
        Log.d(TAG, "NOTIFICATIONS => " + title);
    }

    private void resetNotifications() {
        sharedPreferenceHelper.setLastTemperatureNotification(-1);
        sharedPreferenceHelper.setLastHumidityNotification(-1);
        sharedPreferenceHelper.setLastIaqLightNotification(-1);
        sharedPreferenceHelper.setLastIaqModerateNotification(-1);
        sharedPreferenceHelper.setLastIaqHeavyNotification(-1);
        sharedPreferenceHelper.setLastIaqSevereNotification(-1);
        sharedPreferenceHelper.setLastIaqExtremeNotification(-1);
    }


}


