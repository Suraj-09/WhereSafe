package com.project.wheresafe.utils;

import static com.project.wheresafe.controllers.DeviceListFragment.PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.project.wheresafe.R;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {

    private static List<BluetoothDevice> mDeviceList = null;
    private final LayoutInflater mInflater;
    private OnDeviceClickListener mClickListener;
    private final Context mContext;


    public DeviceListAdapter(Context context, List<BluetoothDevice> deviceList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDeviceList = deviceList;
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

        String deviceAddress = device.getAddress();
        if (deviceAddress != null && deviceAddress.startsWith("30:AE:A4")) {
            holder.deviceNameTextView.setText("WhereSafe");
            holder.deviceAddressTextView.setText(deviceAddress);
            holder.itemView.setOnClickListener(v -> mClickListener.onDeviceClick(device));
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
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
        final OnDeviceClickListener clickListener;

        DeviceViewHolder(@NonNull View itemView, OnDeviceClickListener clickListener) {
            super(itemView);
            this.deviceNameTextView = itemView.findViewById(R.id.device_name_textview);
            this.deviceAddressTextView = itemView.findViewById(R.id.device_mac_address_textview);
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
