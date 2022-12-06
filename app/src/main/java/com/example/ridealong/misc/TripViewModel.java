package com.example.ridealong.misc;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ridealong.api.models.Trip;

public class TripViewModel extends ViewModel {

    private MutableLiveData<Trip> trip = new MutableLiveData<>();

    public MutableLiveData<Trip> getTrip() {
        return trip;
    }

    public void setTrip(Trip input) {
        trip.setValue(input);
    }

}
