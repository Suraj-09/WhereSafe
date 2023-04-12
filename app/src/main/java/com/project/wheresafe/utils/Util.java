package com.project.wheresafe.utils;

public class Util {
    public static double calculateDistance(int rssi) {
        int txPower = -20; // Calibrated for ESP32 Thing
        double ratio = rssi*1.0/txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        } else {
            double distance = (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return distance;
        }
    }
}
