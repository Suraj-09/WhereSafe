package com.project.wheresafe.controllers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.wheresafe.R;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.DeviceListAdapter;
import com.project.wheresafe.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeviceListFragment extends Fragment implements DeviceListAdapter.OnDeviceClickListener {

    public static final int PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private final String TAG = "DeviceListFragment";
    private final BroadcastReceiver bondStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (bondState == BluetoothDevice.BOND_BONDED) {
                    // The device is successfully bonded, you can perform further actions if needed
                }
            }
        }
    };
    private RecyclerView recyclerView;
    private DeviceListAdapter adapter;
    private BluetoothAdapter bluetoothAdapter;
    private final List<BluetoothDevice> deviceList = new ArrayList<>();
    private final Map<BluetoothDevice, String> proximityMap = new HashMap<>();
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                double distance = Util.calculateDistance(rssi);
                String distanceString = String.format(Locale.getDefault(), "%.2f m", distance);

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                }
                Log.d("PROX TEST", "Device: " + device.getName() + " Address: " + device.getAddress() + " RSSI: " + rssi + " Distance: " + distanceString);

                if (!deviceList.contains(device) && isValidDevice(device)) {
                    deviceList.add(device);
                    proximityMap.put(device, distanceString);
                    adapter.updateDeviceRssi(device, rssi);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };
    private SharedPreferenceHelper sharedPreferenceHelper;

    private boolean isValidDevice(BluetoothDevice device) {
        return device.getAddress().startsWith("30:AE:A4:58:3E:DA") || device.getAddress().startsWith("3C:61:05:08:AC:5A") || device.getAddress().startsWith("30:AE:A4:2D:53:2A");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        // Get reference to RecyclerView
        recyclerView = view.findViewById(R.id.device_list_recycler_view);

        // Set the RecyclerView layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create adapter and set it to RecyclerView
        adapter = new DeviceListAdapter(getContext(), deviceList, proximityMap);
        adapter.setOnDeviceClickListener(this);
        recyclerView.setAdapter(adapter);

        // Get the BluetoothAdapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if Bluetooth is available on this device
        if (bluetoothAdapter == null) {
            // Bluetooth is not available on this device
            return view;
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt the user to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            // Bluetooth is enabled, set the default device name and start scanning for devices
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            }
            bluetoothAdapter.setName("WhereSafe");
            startDiscovery();
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void startDiscovery() {
        // Cancel ongoing discovery process before starting a new one
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(receiver, filter);

        // Check if the app has the required permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        // Start discovery
        bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDiscovery();
            } else {
                Toast.makeText(getContext(), "Location permission required to discover Bluetooth devices", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered, ignore the exception
        }
    }

    @Override
    public void onDeviceClick(BluetoothDevice device) { // When the user selects a BT device from the displayed list
        View view = getView();
        final String device_name = "WhereSafe";
        String deviceMacAddress = device.getAddress();
        String deviceProximity = proximityMap != null ? proximityMap.get(device) : "Unknown";

        // Update the device name and MAC address in Firestore
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.updateDeviceName(sharedPreferenceHelper.getUid(), device_name);
        firestoreHelper.removeMacAddress(sharedPreferenceHelper.getUid());

        if (deviceMacAddress != null && deviceMacAddress.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
            firestoreHelper.addMacAddress(sharedPreferenceHelper.getUid(), deviceMacAddress);
        }
        firestoreHelper.addDeviceProximity(sharedPreferenceHelper.getUid(), deviceProximity);

        ((MainActivity) requireActivity()).startBleEspService();

        // Navigate back to the DeviceSettingsFragment
        Navigation.findNavController(view).navigateUp();
    }
}
