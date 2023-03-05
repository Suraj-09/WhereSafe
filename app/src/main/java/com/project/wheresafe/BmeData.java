package com.project.wheresafe;

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
