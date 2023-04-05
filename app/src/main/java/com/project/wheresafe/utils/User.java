package com.project.wheresafe.utils;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class User {
    private static String id;
    private String name;
    private String teamCode;
    private String macAddress;
    private String deviceName;
    private String languageCode;
    private String deviceProximity;
    private String teamName;
    private ArrayList<DocumentReference> teamMembers;
    private double longitude;
    private double latitude;


    public User() {
        id = null;
        name = null;
        teamCode = null;
        deviceName = null;
        macAddress = null;
        teamName = null;
        teamMembers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public ArrayList<DocumentReference> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(ArrayList<DocumentReference> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getDeviceProximity() {
        return deviceProximity;
    }

    public void setDeviceProximity(String deviceProximity) {
        this.deviceProximity = deviceProximity;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
