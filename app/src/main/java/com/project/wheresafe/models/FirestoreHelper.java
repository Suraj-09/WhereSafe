package com.project.wheresafe.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.utils.FirestoreConfig;
import com.project.wheresafe.utils.FirestoreData;
import com.project.wheresafe.utils.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestormHelper";
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersCollection;
    private CollectionReference teamsCollection;
    private FirestoreData firestoreData;
    private FirebaseAuth firebaseAuth;

    public FirestoreHelper() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.usersCollection = firebaseFirestore.collection(FirestoreConfig.COLLECTION_USERS);
        this.teamsCollection = firebaseFirestore.collection(FirestoreConfig.COLLECTION_TEAMS);
        this.firestoreData = new FirestoreData();

    }

    public FirestoreData getFirestoreData() {
        return firestoreData;
    }

    public CollectionReference getSensorDataCollection(String uid) {
        return usersCollection.document(uid).collection(FirestoreConfig.COLLECTION_SENSOR_DATA);
    }


    public void addUser(FirebaseUser user, String name) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", name);
        newUser.put("language_code", "en");

        usersCollection.document(user.getUid())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "addUser(): DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "addUser(): Error writing document", e);
                    }
                });
    }


    public void getUser(String uid, FirestoreCallback firestoreCallback) {
        usersCollection.document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                User user = new User();
                                user.setId(document.getId());

                                if (document.getData() != null) {
                                    if (document.getData().containsKey("name")) {
                                        user.setName(document.getData().get("name").toString());
                                    }
                                    if (document.getData().containsKey("team_code")) {
                                        user.setTeamCode(document.getData().get("team_code").toString());
                                    }
                                    if (document.getData().containsKey("mac_address")) {
                                        user.setMacAddress(document.getData().get("mac_address").toString());
                                    }

                                    if (document.getData().containsKey("device_name")) {
                                        user.setDeviceName(document.getData().get("device_name").toString());
                                    } else {
                                        user.setDeviceName("WhereSafe");
                                    }

                                    if (document.getData().containsKey("language_code")) {
                                        user.setLanguageCode(document.getData().get("language_code").toString());
                                    } else {
                                        user.setLanguageCode("en");
                                    }

                                }

                                firestoreData.setUser(user);

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

    public void addBmeData(String uid, BmeData bmeData) {

        Map<String, Object> bmeMap = bmeData.toMap();

        getSensorDataCollection(uid).add(bmeMap)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "addBmeData(): DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "addBmeData(): Error adding document", e));
    }

    public void getLatestPersonalSensorData(String uid, FirestoreCallback firestoreCallback) {
        getSensorDataCollection(uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<BmeData> bmeDataArrayList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bmeDataArrayList.add(new BmeData(document.getId(), document.getData()));
                        }

                        if (!bmeDataArrayList.isEmpty()) {
                            firestoreData.setBmeDataLatest(bmeDataArrayList.get(0));
                        } else {
                            firestoreData.setBmeDataLatest(null);
                        }

                        firestoreCallback.onResultGet();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }


    public void getAllPersonalSensorData(String uid, FirestoreCallback firestoreCallback) {
        usersCollection.document(uid).collection(FirestoreConfig.COLLECTION_SENSOR_DATA)
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
                    }
                });


    }

    public void getTeam(String teamCode, FirestoreCallback firestoreCallback) {
        teamsCollection
                .document(teamCode)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                if (document.getData() != null) {
                                    User user = new User();
                                    if (document.getData().containsKey("team_name")) {
                                        user.setTeamName(document.getData().get("team_name").toString());
                                    }

                                    if (document.getData().containsKey("members")) {
                                        ArrayList<DocumentReference> members = new ArrayList<>();
                                        for (DocumentReference docRef : (List<DocumentReference>) document.getData().get("members")) {
                                            members.add(docRef);
                                        }
                                        user.setTeamMembers(members);
                                    }

                                    firestoreData.setUser(user);

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

    public void getTeamMembers(String teamCode, FirestoreCallback firestoreCallback) {
        usersCollection
                .whereEqualTo("team_code", teamCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<User> teamMembers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                User user = new User();
                                user.setId(document.getId());

                                if (document.getData().containsKey("name")) {
                                    user.setName(document.getData().get("name").toString());
                                }
                                if (document.getData().containsKey("team_code")) {
                                    user.setTeamCode(document.getData().get("team_code").toString());
                                }
                                if (document.getData().containsKey("mac_address")) {
                                    user.setMacAddress(document.getData().get("mac_address").toString());
                                }

                                if (document.getData().containsKey("device_name")) {
                                    user.setDeviceName(document.getData().get("device_name").toString());
                                } else {
                                    user.setDeviceName("WhereSafe");
                                }

                                if (document.getData().containsKey("language_code")) {
                                    user.setLanguageCode(document.getData().get("language_code").toString());
                                } else {
                                    user.setLanguageCode("en");
                                }

                                teamMembers.add(user);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            firestoreData.setTeamMembersArrayList(teamMembers);

                            firestoreCallback.onResultGet();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addMacAddress(String uid, String macAddress) {
        Map<String, Object> macInfo = new HashMap<>();
        macInfo.put("mac_address", macAddress);

        usersCollection.document(uid).update(macInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "addMacAddress(): DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "addMacAddress(): Error writing document", e);
            }
        });
    }

    public void addTeamCode(String uid, String teamCode) {
        Map<String, Object> teamInfo = new HashMap<>();
        teamInfo.put("team_code", teamCode);

        usersCollection.document(uid).update(teamInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "addTeamCode(): DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "addTeamCode(): Error writing document", e);
            }
        });
    }

    public void createTeam(String uid, String teamName, String teamCode) {
        Map<String, Object> teamDoc = new HashMap<>();
        teamDoc.put("team_name", teamName);
        teamDoc.put("members", Arrays.asList(usersCollection.document(uid)));

        teamsCollection.document(teamCode).set(teamDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "createTeam(): DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "createTeam(): Error writing document", e);
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
                Log.d(TAG, "getTeamCodes(): Error getting documents: ", task.getException());
            }
        });
    }

    public void addMember(String uid, String teamCode) {
        teamsCollection.document(teamCode)
                .update("members", FieldValue.arrayUnion(usersCollection.document(uid)))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "addMember(): DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "addMember(): Error : " + e.getMessage());
                    }
                });

    }

    public void updateLanguage(String uid, String languageCode) {
        Map<String, Object> langInfo = new HashMap<>();
        langInfo.put("language_code", languageCode);

        usersCollection.document(uid).update(langInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "updateLanguage(): DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "updateLanguage(): Error writing document", e);
            }
        });
    }

}
