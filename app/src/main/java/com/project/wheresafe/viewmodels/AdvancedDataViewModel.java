package com.project.wheresafe.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdvancedDataViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdvancedDataViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Advanced stuff");
    }

    public LiveData<String> getText() {
        return mText;
    }
}