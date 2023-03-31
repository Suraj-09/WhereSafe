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

    public FirestoreData() {
        bmeDataLatest = null;
        bmeDataArrayList = null;
        teamCodeArraylist = null;
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



}
