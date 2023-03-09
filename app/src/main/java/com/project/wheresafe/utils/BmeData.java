package com.project.wheresafe.utils;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BmeData {
    private int data_id;
    private double temperature;
    private double humidity;
    private double pressure;
    private double gas;
    private double altitude;
    private String timestamp;

    public BmeData(double temperature, double humidity, double pressure, double gas, double altitude) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.gas = gas;
        this.altitude = altitude;
        this.timestamp = null;
    }

    public BmeData(int data_id, double temperature, double humidity, double pressure, double gas, double altitude, String timestamp) {
        this.data_id = data_id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.gas = gas;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }
    
    public BmeData(Map<String, Object> bmeMap) {
        this.temperature = (double) bmeMap.get("temperature");
        this.humidity = (double) bmeMap.get("humidity");
        this.pressure = (double) bmeMap.get("pressure");
        this.gas = (double) bmeMap.get("gas");
        this.altitude = (double) bmeMap.get("altitude");
        this.timestamp = (String) bmeMap.get("timestamp").toString();
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getGas() {
        return gas;
    }

    public void setGas(double gas) {
        this.gas = gas;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> bmeMap  = new HashMap<>();
        bmeMap.put("temperature", this.temperature);
        bmeMap.put("humidity", this.humidity);
        bmeMap.put("pressure", this.pressure);
        bmeMap.put("gas", this.gas);
        bmeMap.put("altitude", this.altitude);
        bmeMap.put("timestamp", new Timestamp(new Date()));

        return bmeMap;
    }

    @Override
    public String toString() {
        return "BmeData{" +
                "data_id=" + data_id +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", gas=" + gas +
                ", altitude=" + altitude +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
