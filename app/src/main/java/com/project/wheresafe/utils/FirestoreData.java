package com.project.wheresafe.utils;

import java.util.ArrayList;

public class FirestoreData {
    BmeData bmeDataLatest;
    User user;
    ArrayList<BmeData> bmeDataArrayList;
    ArrayList<String> teamCodeArraylist;

    ArrayList<User> teamMembersArrayList;

    public FirestoreData() {
        bmeDataLatest = null;
        bmeDataArrayList = new ArrayList<>();
        teamCodeArraylist = new ArrayList<>();
        user = null;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<User> getTeamMembersArrayList() {
        return teamMembersArrayList;
    }

    public void setTeamMembersArrayList(ArrayList<User> teamMembersArrayList) {
        this.teamMembersArrayList = teamMembersArrayList;
    }
}
