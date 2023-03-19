package com.project.wheresafe.utils;

import java.util.ArrayList;

public class FirestoreData {
    BmeData bmeDataLatest;
    String teamCode;
    String teamName;

    String name;

    ArrayList<BmeData> bmeDataArrayList;
    ArrayList<String> teamCodeArraylist;

//    ArrayList<String> team

    public FirestoreData() {
        bmeDataLatest = null;
        bmeDataArrayList = null;
        teamCodeArraylist = null;
        teamCode = null;
        teamName = null;
        name = null;
    }

    public BmeData getBmeDataLatest() {
        return bmeDataLatest;
    }

    public void setBmeDataLatest(BmeData bmeDataLatest) {
        this.bmeDataLatest = bmeDataLatest;
    }

    public ArrayList<BmeData> getBmeDataArrayList() {
        return bmeDataArrayList;
    }

    public void setBmeDataArrayList(ArrayList<BmeData> bmeDataArrayList) {
        this.bmeDataArrayList = bmeDataArrayList;
    }

    public ArrayList<String> getTeamCodeArraylist() {
        return teamCodeArraylist;
    }

    public void setTeamCodeArraylist(ArrayList<String> teamCodeArraylist) {
        this.teamCodeArraylist = teamCodeArraylist;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
