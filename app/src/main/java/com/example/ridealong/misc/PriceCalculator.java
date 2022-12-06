package com.example.ridealong.misc;

import static com.example.ridealong.BuildConfig.MAPS_API_KEY;

import android.util.Log;

import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.DistanceApiModels.DistanceMatrix;
import com.example.ridealong.api.models.Trip;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceCalculator {

    public static float calculate(Trip trip, int distance){

        float driverKm = Float.parseFloat(trip.getDriver().getKmprice());
        Log.d("PriceCalculator", "Driver KM " + driverKm);
        Log.d("PriceCalculator", "Distance is " + distance);
        return driverKm*distance;

    }

}
