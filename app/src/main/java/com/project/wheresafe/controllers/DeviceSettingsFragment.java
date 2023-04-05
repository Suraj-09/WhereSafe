package com.project.wheresafe.controllers;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.project.wheresafe.R;
//import com.project.wheresafe.models.BleEspService;
import com.project.wheresafe.models.BleEspForegroundService;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.FirestoreCallback;

import java.util.HashMap;
import java.util.Map;

public class DeviceSettingsFragment extends Fragment {
    private TextView deviceNameTextView;
    private TextView deviceMacAddressTextView;
    private TextView deviceProximityTextView;
    private Button renameDeviceButton;
    private Button unpairDeviceButton;
    private Button pairNewDeviceButton;

//    private BleEspService bleEspService;
    private FirestoreHelper firestoreHelper;
    private SharedPreferenceHelper sharedPreferenceHelper;
//    private BleEspForegroundService bleEspForegroundService;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_settings, container, false);

        // Get references to TextViews
        deviceNameTextView = view.findViewById(R.id.device_name_textview);
        deviceMacAddressTextView = view.findViewById(R.id.device_mac_address_textview);
        //deviceProximityTextView = view.findViewById(R.id.device_proximity_textview);

        // Get references to EditText and Buttons
        renameDeviceButton = view.findViewById(R.id.rename_device_button);
        unpairDeviceButton = view.findViewById(R.id.unpair_device_button);
        pairNewDeviceButton = view.findViewById(R.id.pair_new_device_button);

        // Initialize FirestoreHelper object
        firestoreHelper = new FirestoreHelper();

        // Update the UI with device details from Firestore
        loadDeviceDetails();

        // Set up onClickListeners for buttons
        setupOnClickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDeviceDetails();
    }

    private void loadDeviceDetails() {
        firestoreHelper.getUser(sharedPreferenceHelper.getUid(), new FirestoreCallback() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResultGet() {

                String deviceName = firestoreHelper.getFirestoreData().getUser().getDeviceName();
                String macAddress = firestoreHelper.getFirestoreData().getUser().getMacAddress();
                String deviceProximity = firestoreHelper.getFirestoreData().getUser().getDeviceProximity();

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (deviceName != null && !deviceName.isEmpty()) {
                            deviceNameTextView.setText(deviceName);
                        } else {
                            deviceNameTextView.setText("");
                        }

                        updateMacAddressUI(macAddress); // Update the device's MAC address in the UI
                        //updateDeviceProximityUI(deviceProximity);
                    }
                });

//                if (macAddress != null && macAddress.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
//                    // Get BluetoothAdapter
//                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                    if (bluetoothAdapter == null) {
//                        Log.e(TAG, "Bluetooth not supported");
//                        return;
//                    }
//
//                    // Calculate proximity and update deviceProximityTextView
//                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
//                    Integer rssi = deviceRSSIMap.get(device.getAddress()); // Retrieve the latest RSSI value for the device
//                    if (rssi != null) {
//                        double distance = calculateDistance(rssi); // Calculate the distance based on the RSSI value
//                        String proximity = getProximity(distance); // Get the proximity label based on the distance
//                        String distanceText = String.format("%.2f m", distance); // Format the distance text
//                        requireActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                deviceProximityTextView.setText(String.format("%s (%s)", proximity, distanceText)); // Update the deviceProximityTextView
//                            }
//                        });
//                    }
//                } else {
//                    Log.e("DeviceSettingsFragment", "Invalid Bluetooth address: " + macAddress);
//                }
            }
        });
    }



    private void updateMacAddressUI(String macAddress) {
        if (macAddress != null && macAddress.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
            deviceMacAddressTextView.setText(macAddress);
        } else {
            deviceMacAddressTextView.setText("");
        }
    }

    private void updateDeviceProximityUI(String deviceProximity){
        if(deviceProximity != null){
            deviceProximityTextView.setText(deviceProximity);
        } else {
            deviceProximityTextView.setText("");
        }
    }


//    private void updateDeviceProximity(String proximity) {
//        firestoreHelper.getDeviceProximity(new FirestoreCallback() {
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onResultGet() {
//                Log.d(TAG, "Device proximity updated");
//            }
//        }, proximity);
//    }

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
                        // Update device name in Firestore database
                        firestoreHelper.updateDeviceName(sharedPreferenceHelper.getUid(), newDeviceName);

                        // Update device name in the UI
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
            ((MainActivity)getActivity()).stopBleEspService();

            //firestoreHelper
            firestoreHelper.removeDeviceName(sharedPreferenceHelper.getUid());
            firestoreHelper.removeMacAddress(sharedPreferenceHelper.getUid());
            firestoreHelper.removeDeviceProximity(sharedPreferenceHelper.getUid());
            //firestoreHelper.removeDeviceProximity();
            Toast.makeText(requireContext(), "Device unpaired", Toast.LENGTH_SHORT).show();

            // Clear displayed device
            deviceNameTextView.setText("");
            updateMacAddressUI(null); // Clear the displayed MAC address
            //deviceProximityTextView.setText("");
        });



        pairNewDeviceButton.setOnClickListener(view -> {
            // Stop current BLE service
            ((MainActivity)getActivity()).stopBleEspService();

            // Set device name to "WhereSafe" when a new device is paired
//            firestoreHelper.updateDeviceName(sharedPreferenceHelper.getUid(),"WhereSafe");

            // Navigate to Device List screen to pair a new device
            Navigation.findNavController(view).navigate(R.id.action_deviceSettingsFragment_to_deviceListFragment);
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        ((MainActivity)getActivity()).stopBleEspService();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
    }
}
