package com.example.ridealong.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ridealong.R;
import com.example.ridealong.adapters.DriverActiveTripAdapter;
import com.example.ridealong.adapters.DriverOldTripsAdapter;
import com.example.ridealong.adapters.PassengerActiveTripAdapter;
import com.example.ridealong.adapters.PassengerOldTripAdapter;
import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.api.models.User;
import com.example.ridealong.misc.PathViewModel;
import com.example.ridealong.misc.SavedSharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */



public class TripsFragment extends Fragment {


    ArrayList<Trip> activeTrips;
    ArrayList<Trip> oldTrips;
    int userId;
    RecyclerView rv1;
    RecyclerView rv2;
    boolean isDriver;
    TextView oldTripsStatic, activeTripsStatic;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private PathViewModel viewModel;

    public TripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripsFragment newInstance(String param1, String param2) {
        TripsFragment fragment = new TripsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PathViewModel.class);
        if (SavedSharedPreference.getProfile(getContext()).getIsDriver()){
            isDriver = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        activeTripsStatic = view.findViewById(R.id.tvActiveTripStatic);
        oldTripsStatic = view.findViewById(R.id.tvPreviousTripsStatic);



        Call<List<Trip>> call1 = RetroClient.getInstance().getMyApi().getTrips();
        call1.enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                rv1 = view.findViewById(R.id.rvDriverActiveTrips);
                rv2 = view.findViewById(R.id.rvOldTrips);
                oldTrips = new ArrayList<>();
                activeTrips = new ArrayList<>();


                if (response.body() != null) {
                    tripsClassifier((ArrayList<Trip>) response.body());
                    RecyclerView.Adapter adapter;
                    RecyclerView.Adapter adapter2;

                    if (isDriver) {
                        adapter = new DriverActiveTripAdapter(getContext(), activeTrips, viewModel);
                        adapter2 = new DriverOldTripsAdapter(getContext(), oldTrips, viewModel);
                    }
                    else{
                        adapter = new PassengerActiveTripAdapter(getContext(), activeTrips, viewModel);
                        adapter2 = new PassengerOldTripAdapter(getContext(), oldTrips, viewModel);
                    }




                    Log.d("yakh", response.toString());
                    Log.d("yakh", response.message());
                    Log.d("yakh", response.body().toString());

                    rv1.setAdapter(adapter);
                    rv1.setLayoutManager(new LinearLayoutManager(getActivity()));

                    rv2.setAdapter(adapter2);
                    rv2.setLayoutManager(new LinearLayoutManager(getActivity()));

                    if (oldTrips.size() == 0){

                        oldTripsStatic.setText("No Previous Trips Yet");
                    }

                    if (activeTrips.size() == 0){

                        activeTripsStatic.setText("No active trips at the moment");
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                Log.e("yakh",t.getMessage() );
            }
        });


        return view;
    }

    private void tripsClassifier(ArrayList<Trip> all){

        for (Trip t: all){
            if (isMyTrip(t) && (t.getStatus().getId() == 1 || t.getStatus().getId() == 3)){
                oldTrips.add(t);
            }
            else if (isMyTrip(t) && (t.getStatus().getId() == 2)){
                activeTrips.add(t);
            }
        }

    }

    private boolean isMyTrip(Trip t){

        userId = SavedSharedPreference.getProfile(getContext()).getId();
        return t.getDriver().getId() == userId || isPassengerInTheTrip((ArrayList<User>) t.getPassengers());

    }

    private boolean isPassengerInTheTrip(ArrayList<User> passengers){
        for (User u : passengers){
            if (u.getId() == userId){
                return true;
            }
        }
        return false;
    }

}