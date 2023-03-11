package com.project.wheresafe.utils;

import java.util.ArrayList;

public class FirestoreData {
    BmeData bmeDataLatest;
    ArrayList<BmeData> bmeDataArrayList;

    public FirestoreData() {
        bmeDataLatest = null;
        bmeDataArrayList = null;
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
}
