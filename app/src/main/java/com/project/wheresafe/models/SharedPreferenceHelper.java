package com.project.wheresafe.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.firebase.auth.FirebaseUser;
import com.project.wheresafe.R;
import com.project.wheresafe.utils.FirestoreCallback;
import com.project.wheresafe.utils.User;

public class SharedPreferenceHelper {
    private String TAG = "SharedPreferenceHelper";
    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_preference_file_key), Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
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
                editor.putString(context.getResources().getString(R.string.user_language_key), curUser.getLanguageCode());
                editor.commit();

                // update language changes
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(curUser.getLanguageCode());
                AppCompatDelegate.setApplicationLocales(appLocale);
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

    public String getLanguageCode() {
        return sharedPreferences.getString(context.getResources().getString(R.string.user_language_key), "en");
    }

    public void setLanguageCode(String languageCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.user_language_key), languageCode);
        editor.commit();
    }

    public void setConnectionStatus(String status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.connection_status_key), status);
        editor.commit();
    }

    public String getConnectionStatus() {
        return sharedPreferences.getString(context.getResources().getString(R.string.connection_status_key), context.getResources().getString(R.string.status_connect));
    }

    public User getCurrentUser() {
        User currentUser = new User();

        currentUser.setName(getUserName());
        currentUser.setId(getUid());
        currentUser.setTeamCode(getTeamCode());
        currentUser.setMacAddress(getMacAddress());
        currentUser.setLanguageCode(getLanguageCode());


        return currentUser;
    }

    public void setDarkMode(boolean darkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.dark_mode_key), darkMode);
        editor.commit();
    }

    public boolean getDarkMode() {
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.dark_mode_key), false);
    }

    public void setLastTemperatureNotification(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_temperature_notification), time);
        editor.commit();
    }

    public long getLastTemperatureNotification() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_temperature_notification), -1);
    }

    public void setLastHumidityNotification(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_humidity_notification), time);
        editor.commit();
    }

    public long getLastHumidityNotification() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_humidity_notification), -1);
    }

    public void setLastIaqLightNotification(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_iaq_light_notification), time);
        editor.commit();
    }

    public long getLastIaqLightNotification() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_iaq_light_notification), -1);
    }

    public void setLastIaqModerateNotification(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_iaq_moderate_notification), time);
        editor.commit();
    }

    public long getLastIaqModerateNotification() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_iaq_moderate_notification), -1);
    }

    public void setLastIaqHeavyNotification(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_iaq_heavy_notification), time);
        editor.commit();
    }

    public long getLastIaqHeavyNotification() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_iaq_heavy_notification), -1);
    }

    public void setLastIaqSevereNotification(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_iaq_severe_notification), time);
        editor.commit();
    }

    public long getLastIaqSevereNotification() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_iaq_severe_notification), -1);
    }

    public void setLastIaqExtremeNotification(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_iaq_extreme_notification), time);
        editor.commit();
    }

    public long getLastIaqExtremeNotification() {
        return sharedPreferences.getLong(context.getResources().getString(R.string.last_iaq_extreme_notification), -1);
    }

}
