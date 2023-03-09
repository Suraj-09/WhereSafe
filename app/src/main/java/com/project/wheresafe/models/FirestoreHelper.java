package com.project.wheresafe.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    FirebaseFirestore firebaseFirestore;
    CollectionReference usersCollection;
    DocumentReference userDocument;
    CollectionReference sensorDataCollection;

    public FirestoreHelper() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.usersCollection = firebaseFirestore.collection(FirestoreConfig.COLLECTION_USERS);
        this.userDocument = usersCollection.document("25BcSPUgtlXysuAWJVvWqLvHjjm1");
        this.sensorDataCollection = userDocument.collection(FirestoreConfig.COLLECTION_SENSOR_DATA);

        System.out.println(firebaseFirestore.getApp());
        System.out.println(usersCollection.getId());
        System.out.println(userDocument.getId());
        System.out.println(sensorDataCollection.getId());

    }

    public void addBmeData(BmeData bmeData) {
        Map<String, Object> data = bmeData.toMap();

        sensorDataCollection.add(data)
            .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
            .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public BmeData getLatest() {
        return null;
    }

    interface PersonalDataCallback {
        void isPersonalDataExist(boolean exist);
    }
    // TODO: create callback function for getAllPersonalData()
    public ArrayList<BmeData> getAllPersonalData() {
        ArrayList<BmeData> bmeDataArrayList = new ArrayList<>();
        sensorDataCollection
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bmeDataArrayList.add(new BmeData(document.getData()));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        };
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        System.out.println("error getting documents");
                    }
                }
            });

        return bmeDataArrayList;
    }

    private void addUserListener() {
        userDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.getData());
                } else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });
    }



}
