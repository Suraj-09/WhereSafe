package com.project.wheresafe.utils;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirestoreData<TeamMember> {
    BmeData bmeDataLatest;
    User user;
    ArrayList<BmeData> bmeDataArrayList;
    ArrayList<String> teamCodeArraylist;

    String teamName;
    ArrayList<User> teamMembersArrayList;

    public FirestoreData() {
        bmeDataLatest = null;
        bmeDataArrayList = new ArrayList<>();
        teamCodeArraylist = new ArrayList<>();
        teamName = null;
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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public ArrayList<User> getTeamMembersArrayList() {
        return teamMembersArrayList;
    }

    public void setTeamMembersArrayList(ArrayList<User> teamMembersArrayList) {
        this.teamMembersArrayList = teamMembersArrayList;
    }

}
