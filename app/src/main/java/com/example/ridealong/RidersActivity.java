package com.example.ridealong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.ridealong.adapters.RidersAdapter;
import com.example.ridealong.api.models.User;
import com.example.ridealong.misc.SavedSharedPreference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RidersActivity extends AppCompatActivity {

    boolean isDriver;
    ArrayList<User> riders;
    int tripId;
    RecyclerView rvRiders;
    boolean isTripDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riders);

        isDriver = SavedSharedPreference.getProfile(this).getIsDriver();

        tripId = (int) getIntent().getExtras().get("tripId");
        isTripDone = (boolean) getIntent().getExtras().get("tripDone");



        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<User>>(){}.getType();

        riders = gson.fromJson(getIntent().getExtras().getString("riders"),listType);

        RidersAdapter ridersAdapter = new RidersAdapter(this, riders, tripId, isTripDone);
        rvRiders = findViewById(R.id.rvRiders);

        rvRiders.setAdapter(ridersAdapter);
        rvRiders.setLayoutManager(new LinearLayoutManager(this));




    }
}