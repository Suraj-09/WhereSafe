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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.utils.FirestoreConfig;
import com.project.wheresafe.utils.FirestoreData;
import com.project.wheresafe.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestormHelper";
    private static final String USER_ID = "25BcSPUgtlXysuAWJVvWqLvHjjm1"; // temporary
    public FirebaseFirestore firebaseFirestore;
    public CollectionReference usersCollection;
    public CollectionReference teamsCollection;
    public DocumentReference userDocument;
    public CollectionReference sensorDataCollection;
    public FirestoreData firestoreData;

    public FirestoreHelper() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.usersCollection = firebaseFirestore.collection(FirestoreConfig.COLLECTION_USERS);
        this.userDocument = usersCollection.document(USER_ID);
        this.sensorDataCollection = userDocument.collection(FirestoreConfig.COLLECTION_SENSOR_DATA);
        this.teamsCollection = firebaseFirestore.collection(FirestoreConfig.COLLECTION_TEAMS);

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

    public ArrayList<String> getTeamCodeArrayList() {
        return firestoreData.getTeamCodeArraylist();
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
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(20)
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

    public void getTeamCode(FirestoreCallback firestoreCallback) {
        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        if (document.getData().containsKey("team_code")) {
                            firestoreData.setTeamCode(document.getData().get("team_code").toString());
                        }

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        firestoreCallback.onResultGet();
                    } else {
                        Log.d(TAG, "No such document");
                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getTeam(String teamCode, FirestoreCallback firestoreCallback) {
        teamsCollection.document(teamCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        firestoreData.setTeamName(document.getData().get("team_name").toString());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        firestoreCallback.onResultGet();
                    } else {
                        Log.d(TAG, "No such document");
                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void addTeamCode(String teamCode) {
        Map<String, Object> teamInfo = new HashMap<>();
        teamInfo.put("team_code", teamCode);

        userDocument.update(teamInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    public void createTeam(String teamName, String teamCode) {
        Map<String, Object> teamDoc = new HashMap<>();
        teamDoc.put("team_name", teamName);
        teamDoc.put("members", Arrays.asList(USER_ID));

        teamsCollection.document(teamCode).set(teamDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    public void getTeamCodes(FirestoreCallback firestoreCallback) {
        teamsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> teamCodeArraylist = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    teamCodeArraylist.add(document.getId());
                }

                firestoreData.setTeamCodeArraylist(teamCodeArraylist);
                firestoreCallback.onResultGet();

            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
                System.out.println("error getting documents");
            }
        });
    }

    public void addMember(String teamCode) {
        teamsCollection.document(teamCode).update("members", FieldValue.arrayUnion(userDocument)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error : " + e.getMessage());
            }
        });

    }

}
