package com.project.wheresafe;

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    public static String GENERIC_ATTRIBUTE = "00001801-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_CHANGED = "00002A05-0000-1000-8000-00805f9b34fb";

    public static String GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public static String APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    public static String CENTRAL_ADDRESS_RESOLUTION = "00002AA6-0000-1000-8000-00805f9b34fb";

    public static String ENVIRONMENTAL_SENSING = "0000181A-0000-1000-8000-00805f9b34fb";
    public static String TEMPERATURE_MEASUREMENT = "00002A6E-0000-1000-8000-00805f9b34fb";
    public static String HUMIDITY_MEASUREMENT = "00002A6F-0000-1000-8000-00805f9b34fb";

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final int TEMPERATURE_READ = 1;
    public static final int HUMIDITY_READ = 2;

//    public static String ENVIRONMENTAL_SENSING = "29af0930-9682-44f8-9d61-00e3463cb08c";
    public static String BME680_DATA = "605ddbf0-0540-4c6e-be65-62626797ffe9";


    static {
        // Services
        attributes.put(GENERIC_ATTRIBUTE, "Generic Attribute");
        attributes.put(SERVICE_CHANGED, "Service Changed");

        attributes.put(GENERIC_ACCESS, "Generic Access");
        attributes.put(DEVICE_NAME, "Device Name");
        attributes.put(APPEARANCE, "Appearance");
        attributes.put(CENTRAL_ADDRESS_RESOLUTION, "Central Address Resolution");

        attributes.put(ENVIRONMENTAL_SENSING, "Environmental Sensing");
        attributes.put(BME680_DATA, "BME680 Data");

        attributes.put(TEMPERATURE_MEASUREMENT, "Temperature Measurement");
        attributes.put(HUMIDITY_MEASUREMENT, "Humidity Measurement");
        attributes.put(CLIENT_CHARACTERISTIC_CONFIG, "Client Characteristic Config");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
