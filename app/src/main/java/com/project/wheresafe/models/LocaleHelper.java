//package com.project.wheresafe.models;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.content.res.Resources;
//
//import com.project.wheresafe.controllers.MainActivity;
//
//import java.util.Locale;
//
//public class LocaleHelper {
//    private Activity mActivity;
//
//    public LocaleHelper(Activity activity) {
//        mActivity = activity;
//    }
//
//    public void setLocale(String languageCode) {
//        Locale locale = new Locale(languageCode);
//        Locale.setDefault(locale);
//
//        Resources resources = mActivity.getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//
////        Intent intent = new Intent(mActivity, MainActivity.class);
////        mActivity.startActivity(intent);
////        mActivity.finish();
//
//    }
//
//    public String getLanguageCode() {
//        return Locale.getDefault().getLanguage();
//    }
//}
