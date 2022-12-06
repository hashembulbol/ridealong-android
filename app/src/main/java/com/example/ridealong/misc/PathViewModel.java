package com.example.ridealong.misc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLngBounds;

public class PathViewModel extends ViewModel {
    private MutableLiveData<String> path = new MutableLiveData<>();
    private MutableLiveData<LatLngBounds> bounds = new MutableLiveData<>();


    public void setPath(String input) {
        path.setValue(input);
    }

    public LiveData<String> getPath(){
        return path;
    }

    public void setBounds(LatLngBounds input) {
        bounds.setValue(input);
    }

    public LiveData<LatLngBounds> getBounds(){
        return bounds;
    }

}
