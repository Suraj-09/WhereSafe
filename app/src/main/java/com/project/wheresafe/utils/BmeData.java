package com.project.wheresafe.utils;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BmeData {
    private int dataId;
    private String docId;
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

    public BmeData(int dataId, double temperature, double humidity, double pressure, double gas, double altitude, String timestamp) {
        this.dataId = dataId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.gas = gas;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }

    public BmeData(String docId, Map<String, Object> bmeMap) {
        this.docId = (String) docId;
        this.temperature = objectToDouble(bmeMap.get("temperature"));
        this.humidity = objectToDouble(bmeMap.get("humidity"));
        this.pressure = objectToDouble(bmeMap.get("pressure"));
        this.gas = objectToDouble(bmeMap.get("gas"));
        this.altitude = objectToDouble(bmeMap.get("altitude"));
        this.timestamp = objectToTimestampStr(bmeMap.get("timestamp"));
    }

    public BmeData(BmeData bmeData) {
        this.dataId = bmeData.getDataId();
        this.docId = bmeData.getDocId();
        this.temperature = bmeData.getTemperature();
        this.humidity = bmeData.getHumidity();
        this.pressure = bmeData.getPressure();
        this.gas = bmeData.getGas();
        this.altitude = bmeData.getAltitude();
        this.timestamp = bmeData.getTimestamp();
    }

    private double objectToDouble(Object o) {
        if (o instanceof Double) {
            return (Double) o;
        }

        return 0;
    }

    private String objectToTimestampStr(Object o) {
        if (o instanceof Timestamp) {
            return ((Timestamp) o).toDate().toString();
        }

        return null;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
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

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> bmeMap = new HashMap<>();
        bmeMap.put("temperature", this.temperature);
        bmeMap.put("humidity", this.humidity);
        bmeMap.put("pressure", this.pressure);
        bmeMap.put("gas", this.gas);
        bmeMap.put("altitude", this.altitude);
        bmeMap.put("timestamp", new Timestamp(new Date()));

        return bmeMap;
    }

    public String toBetterString() {
        return "BmeData\n" +
                "doc_id=" + docId +
                "\ntemperature=" + temperature +
                "\nhumidity=" + humidity +
                "\npressure=" + pressure +
                "\ngas=" + gas +
                "\naltitude=" + altitude +
                "\ntimestamp='" + timestamp;
    }

    @Override
    public String toString() {
        return "BmeData{" +
                "doc_id=" + docId +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", gas=" + gas +
                ", altitude=" + altitude +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
