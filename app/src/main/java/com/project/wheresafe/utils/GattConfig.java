package com.project.wheresafe.utils;

import java.util.HashMap;

public class
GattConfig {
    public static String ENVIRONMENTAL_SENSING = "0000181A-0000-1000-8000-00805f9b34fb";
    public static String BME680_DATA = "605ddbf0-0540-4c6e-be65-62626797ffe9";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static HashMap<String, String> attributes = new HashMap();

    static {
        // Services
        attributes.put(ENVIRONMENTAL_SENSING, "Environmental Sensing");
        attributes.put(BME680_DATA, "BME680 Data");
        attributes.put(CLIENT_CHARACTERISTIC_CONFIG, "Client Characteristic Config");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
