package com.project.wheresafe.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.wheresafe.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {

    private static List<BluetoothDevice> mDeviceList = null;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private OnDeviceClickListener mClickListener;
    private final Map<BluetoothDevice, String> mProximityMap;
    private final Map<BluetoothDevice, Integer> mDeviceRssiMap;


    public DeviceListAdapter(Context context, List<BluetoothDevice> deviceList, Map<BluetoothDevice, String> mProximityMap) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mDeviceList = deviceList;
        this.mProximityMap = mProximityMap;
        this.mDeviceRssiMap = new HashMap<>();
    }

    public void updateDeviceRssi(BluetoothDevice device, int rssi) {
        mDeviceRssiMap.put(device, rssi);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.device_list_item, parent, false);
        return new DeviceViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BluetoothDevice device = mDeviceList.get(position);
        Integer rssiValue = mDeviceRssiMap.get(device);
        int rssi = (rssiValue != null) ? rssiValue : 0;
        double distance = calculateProximityFromRssi(rssi);
        String distanceString = mProximityMap.getOrDefault(device, "Unknown");

        String deviceAddress = device.getAddress();
        if (deviceAddress != null) {
            holder.deviceNameTextView.setText("WhereSafe");
            holder.deviceAddressTextView.setText(deviceAddress);
            holder.deviceProximityTextView.setText(distanceString);
            holder.itemView.setOnClickListener(v -> mClickListener.onDeviceClick(device));
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }


    private double calculateProximityFromRssi(int rssi) {
        double txPower = -20.0; // Default txPower for SparkFun ESP32 Thing
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return distance;
        }
    }


    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnDeviceClickListener {
        void onDeviceClick(BluetoothDevice device);
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView deviceNameTextView;
        final TextView deviceAddressTextView;
        final TextView deviceProximityTextView;
        final OnDeviceClickListener clickListener;

        DeviceViewHolder(@NonNull View itemView, OnDeviceClickListener clickListener) {
            super(itemView);
            this.deviceNameTextView = itemView.findViewById(R.id.device_name_textview);
            this.deviceAddressTextView = itemView.findViewById(R.id.device_mac_address_textview);
            this.deviceProximityTextView = itemView.findViewById(R.id.device_proximity_textview);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                BluetoothDevice device = mDeviceList.get(position);
                clickListener.onDeviceClick(device);
            }
        }
    }
}
