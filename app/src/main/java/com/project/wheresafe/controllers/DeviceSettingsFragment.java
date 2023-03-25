package com.project.wheresafe.controllers;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.project.wheresafe.R;
import com.project.wheresafe.models.BleEspService;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.utils.FirestoreCallback;

import java.util.HashMap;
import java.util.Map;

public class DeviceSettingsFragment extends Fragment {
    private TextView deviceNameTextView;
    private TextView deviceMacAddressTextView;
    private TextView deviceProximityTextView;
    private EditText deviceNameEditText;
    private Button renameDeviceButton;
    private Button unpairDeviceButton;
    private Button pairNewDeviceButton;

    private BleEspService bleEspService;
    private FirestoreHelper firestoreHelper;
    private Map<String, Integer> deviceRSSIMap = new HashMap<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_settings, container, false);

        // Get references to TextViews
        deviceNameTextView = view.findViewById(R.id.device_name_textview);
        deviceMacAddressTextView = view.findViewById(R.id.device_mac_address_textview);
        deviceProximityTextView = view.findViewById(R.id.device_proximity_textview);

        // Get references to EditText and Buttons
        //deviceNameEditText = view.findViewById(R.id.device_name_edittext);
        renameDeviceButton = view.findViewById(R.id.rename_device_button);
        unpairDeviceButton = view.findViewById(R.id.unpair_device_button);
        pairNewDeviceButton = view.findViewById(R.id.pair_new_device_button);

        // Initialize FirestoreHelper object
        firestoreHelper = new FirestoreHelper();

        // Get the SharedPreferences object
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Update the UI with the saved device name and MAC address
        String deviceName = sharedPreferences.getString("device_name", "");
        String deviceMacAddress = sharedPreferences.getString("device_mac_address", "");
        String deviceProximity = sharedPreferences.getString("device_proximity","");
        deviceNameTextView.setText(deviceName);
        deviceMacAddressTextView.setText(deviceMacAddress);
        deviceProximityTextView.setText(deviceProximity);

        // Set up onClickListeners for buttons
        setupOnClickListeners();

        // Load device details
        loadDeviceDetails();

        return view;
    }

    private void loadDeviceDetails() {
        firestoreHelper.getUser(new FirestoreCallback() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResultGet() {
                String deviceName = firestoreHelper.getFirestoreData().getUser().getDeviceName();
                String macAddress = firestoreHelper.getFirestoreData().getUser().getMacAddress();

                if (deviceName != null) {
                    deviceNameTextView.setText(deviceName);
                }

                if (macAddress != null) {
                    deviceMacAddressTextView.setText(macAddress);
                }

                // Get BluetoothAdapter
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    Log.e(TAG, "Bluetooth not supported");
                    return;
                }

                // Calculate proximity and update deviceProximityTextView
                if (macAddress != null && macAddress.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
                    Integer rssi = deviceRSSIMap.get(device.getAddress()); // Retrieve the latest RSSI value for the device
                    if (rssi != null) {
                        double distance = calculateDistance(rssi); // Calculate the distance based on the RSSI value
                        String proximity = getProximity(distance); // Get the proximity label based on the distance
                        String distanceText = String.format("%.2f m", distance); // Format the distance text
                        deviceProximityTextView.setText(String.format("%s (%s)", proximity, distanceText)); // Update the deviceProximityTextView
                    }
                } else {
                    Log.e("DeviceSettingsFragment", "Invalid Bluetooth address: " + macAddress);
                }

            }
        });
    }

    private double calculateDistance(int rssi) {
        int txPower = -59; // TODO: calibrate based on device's signal strength
        double ratio = rssi*1.0/txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        } else {
            double accuracy = (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    private String getProximity(double distance) {
        if (distance < 1.0) {
            return "Immediate";
        } else if (distance < 3.0) {
            return "Near";
        } else {
            return "Far";
        }
    }

    private String getDeviceName() {
        if (bleEspService != null && bleEspService.getBluetoothGatt() != null) {
            BluetoothDevice device = bleEspService.getBluetoothGatt().getDevice();
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BleEspService.REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                return null;
            }
            return device.getName();
        }
        return null;
    }


    private void setupOnClickListeners() {
        renameDeviceButton.setOnClickListener(view -> {
            // Create a new AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Rename Device");

            // Set up the layout for the dialog
            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the button actions
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newDeviceName = input.getText().toString().trim();
                    if (!newDeviceName.isEmpty()) {
                        firestoreHelper.updateDeviceName(newDeviceName);
                        deviceNameTextView.setText(newDeviceName);
                        Toast.makeText(requireContext(), "Device name updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Please enter a new device name", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Show the dialog
            builder.show();
        });

        unpairDeviceButton.setOnClickListener(view -> {
            if (bleEspService != null) { // check if bleEspService is not null
                bleEspService.stop();
            }
            firestoreHelper.removeMacAddress();
            Toast.makeText(requireContext(), "Device unpaired", Toast.LENGTH_SHORT).show();

            // Clear displayed device
            deviceNameTextView.setText("");
            deviceMacAddressTextView.setText("");
            deviceProximityTextView.setText("");
        });

        pairNewDeviceButton.setOnClickListener(view -> {
            // Stop current BLE service
            if (bleEspService != null) { // check if bleEspService is not null
                bleEspService.stop();
            }

            // Navigate to Device List screen to pair a new device
            Navigation.findNavController(view).navigate(R.id.action_deviceSettingsFragment_to_deviceListFragment);
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bleEspService != null) {
            bleEspService.stop();
        }
    }

    public static SettingsFragment newInstance(String macAddress, String deviceName) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("macAddress", macAddress);
        args.putString("deviceName", deviceName);
        fragment.setArguments(args);
        return fragment;
    }
}
