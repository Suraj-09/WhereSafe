package com.project.wheresafe.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.firebase.auth.FirebaseUser;
import com.project.wheresafe.R;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.utils.User;

public class SharedPreferenceHelper {
    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_preference_file_key), Context.MODE_PRIVATE);
    }


    public void saveUser(FirebaseUser user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.user_name_key), user.getDisplayName());
        editor.putString(context.getResources().getString(R.string.user_id_key), user.getUid());

        editor.commit();

        saveUserInfo(user);
    }

    public void saveUserInfo(FirebaseUser user) {
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.getUser(user.getUid(), new FirestoreCallback() {
            @Override
            public void onResultGet() {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                User curUser = firestoreHelper.getFirestoreData().getUser();

                editor.putString(context.getResources().getString(R.string.user_team_code_key), curUser.getTeamCode());
                editor.putString(context.getResources().getString(R.string.user_mac_key), curUser.getMacAddress());
                editor.apply();
            }
        });
    }

    public String getUserName() {
        return sharedPreferences.getString(context.getResources().getString(R.string.user_name_key), "");
    }

    public String getUid() {
        return sharedPreferences.getString(context.getResources().getString(R.string.user_id_key), null);
    }


    public String getMacAddress() {
        return sharedPreferences.getString(context.getResources().getString(R.string.user_mac_key), null);
    }

    public String getTeamCode() {
        return sharedPreferences.getString(context.getResources().getString(R.string.user_team_code_key), null);
    }


//
//    public void saveSettings(String cntName1, String cntName2, String cntName3, int maxCount) {
//        Gson gson = new Gson();
//        Settings settingsObj = new Settings(cntName1, cntName2, cntName3, maxCount);
//        String settingsJson = gson.toJson(settingsObj);
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("settingsObj", settingsJson);
//        editor.commit();
//    }
//
//    public Settings getSettings() {
//        Gson gson = new Gson();
//        String settingsJson = sharedPreferences.getString("settingsObj", null);
//        Settings settingsObj = gson.fromJson(settingsJson, Settings.class);
//
//        return settingsObj;
//    }
//
//    public String getCntName1() {
//        return sharedPreferences.getString("cntName1",null);
//    }
//
//    public String getCntName2() {
//        return sharedPreferences.getString("cntName2",null);
//    }
//
//    public String getCntName3() {
//        return sharedPreferences.getString("cntName3",null);
//    }
//
//    public int getMaxCount() {
//        return sharedPreferences.getInt("maxCount",0);
//    }

}
