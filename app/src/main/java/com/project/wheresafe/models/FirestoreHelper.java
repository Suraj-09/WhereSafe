package com.project.wheresafe.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.utils.FirestoreConfig;
import com.project.wheresafe.utils.FirestoreData;
import com.project.wheresafe.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestormHelper";
    FirebaseFirestore firebaseFirestore;
    CollectionReference usersCollection;
    DocumentReference userDocument;
    CollectionReference sensorDataCollection;
    FirestoreData firestoreData;
    HomeViewModel homeViewModel;

    public FirestoreHelper() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.usersCollection = firebaseFirestore.collection(FirestoreConfig.COLLECTION_USERS);
        this.userDocument = usersCollection.document("25BcSPUgtlXysuAWJVvWqLvHjjm1");
        this.sensorDataCollection = userDocument.collection(FirestoreConfig.COLLECTION_SENSOR_DATA);

        firestoreData = new FirestoreData();
    }

    public CollectionReference getSensorDataCollection() {
        return sensorDataCollection;
    }

    public ArrayList<BmeData> getBmeDataArrayList() {
        return firestoreData.getBmeDataArrayList();
    }

    public BmeData getBmeDataLatest() {
        return firestoreData.getBmeDataLatest();
    }

    public void addBmeData(BmeData bmeData) {
        Map<String, Object> bmeMap = bmeData.toMap();

        sensorDataCollection.add(bmeMap)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void getLatestPersonalSensorData(FirestoreCallback firestoreCallback) {
        sensorDataCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<BmeData> bmeDataArrayList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bmeDataArrayList.add(new BmeData(document.getId(), document.getData()));
                        }

                        firestoreData.setBmeDataLatest(bmeDataArrayList.get(0));
                        firestoreCallback.onResultGet();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        System.out.println("error getting documents");
                    }
                });
    }

    public void getAllPersonalSensorData(FirestoreCallback firestoreCallback) {
        sensorDataCollection
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(100)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<BmeData> bmeDataArrayList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bmeDataArrayList.add(new BmeData(document.getId(), document.getData()));
                        }

                        firestoreData.setBmeDataArrayList(bmeDataArrayList);
                        firestoreCallback.onResultGet();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        System.out.println("error getting documents");
                    }
                });
    }

}
