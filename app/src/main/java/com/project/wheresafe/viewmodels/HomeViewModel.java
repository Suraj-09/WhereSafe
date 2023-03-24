package com.project.wheresafe.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;
import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.FirestoreCallback;

import java.util.ArrayList;
import java.util.Objects;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "HomeViewModel";
    private final MutableLiveData<String> mText;
    private final MutableLiveData<ArrayList<BmeData>> bmeArraylist;
    private final MutableLiveData<BmeData> latestBmeData;

    private SharedPreferenceHelper sharedPreferenceHelper;
    ListenerRegistration registration;
    FirestoreHelper firestoreHelper;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        sharedPreferenceHelper = new SharedPreferenceHelper(getApplication().getApplicationContext());
        bmeArraylist = new MutableLiveData<>();
        latestBmeData = new MutableLiveData<>();
        mText = new MutableLiveData<>();

        firestoreHelper = new FirestoreHelper();
        attachListener();
        init();
    }


    public void init() {
        firestoreHelper.getLatestPersonalSensorData(sharedPreferenceHelper.getUid(), new FirestoreCallback() {
            @Override
            public void onResultGet() {
                if (firestoreHelper.getFirestoreData().getBmeDataLatest() != null) {
                    latestBmeData.setValue(firestoreHelper.getFirestoreData().getBmeDataLatest());
                }
            }
        });
    }

    public void attachListener() {
        Log.d(TAG, "Listener attached");
        CollectionReference sensorDataCollection = firestoreHelper.getSensorDataCollection(sharedPreferenceHelper.getUid());
        registration = sensorDataCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (snapshot != null) {
                    int lastIdx = snapshot.getDocumentChanges().size() - 1;

                    if (lastIdx >= 0) {
                        DocumentSnapshot latestDoc = snapshot.getDocumentChanges().get(lastIdx).getDocument();
                        Log.d(TAG, snapshot.getDocumentChanges().get(lastIdx).getDocument().getData().toString());

                        latestBmeData.setValue(new BmeData(latestDoc.getId(), Objects.requireNonNull(latestDoc.getData())));
                        mText.setValue(Objects.requireNonNull(latestBmeData.getValue()).toBetterString());
                    }

                }

            }

            ;
        });
    }

    public void detachListener() {
        registration.remove();
        Log.d(TAG, "Listener detached");

    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<BmeData>> getBmeArrayList() {
        return bmeArraylist;
    }

    public LiveData<BmeData> getLatestBmeData() {
        return latestBmeData;
    }
}

