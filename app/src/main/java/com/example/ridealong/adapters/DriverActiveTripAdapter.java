package com.example.ridealong.adapters;


import static com.example.ridealong.BuildConfig.MAPS_API_KEY;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridealong.R;
import com.example.ridealong.RidersActivity;
import com.example.ridealong.TripEditActivity;
import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.MapsApiModels.Bounds;
import com.example.ridealong.api.models.MapsApiModels.Directions;
import com.example.ridealong.api.models.Point;

import com.example.ridealong.api.models.Trip;
import com.example.ridealong.fragments.MapFragment;
import com.example.ridealong.misc.PathViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverActiveTripAdapter extends RecyclerView.Adapter<DriverActiveTripAdapter.TripsViewHolder> {

    PathViewModel viewmodel;
    Context context;
    List<Trip> items;
    String waypoints;

    public DriverActiveTripAdapter(Context context, List<Trip> items, PathViewModel model){

        this.viewmodel = model;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public DriverActiveTripAdapter.TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_driveractivetrip_item, parent, false);

        return new DriverActiveTripAdapter.TripsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverActiveTripAdapter.TripsViewHolder holder, int position) {

        Trip currentTrip = items.get(holder.getAdapterPosition());



        holder.tvActiveDrvRiders.setText(currentTrip.getReserved() + "/" + currentTrip.getCapacity() + " Seats");
        holder.tvIncome.setText(currentTrip.getRevenue());
        holder.tvDate.setText(currentTrip.getTripdate() + " at " + currentTrip.getTriptime().substring(0,5));
        holder.tvActiveDrvRiders.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (currentTrip.getReserved() == 0)
                    Toast.makeText(context, "Trip is still empty", Toast.LENGTH_SHORT).show();
                else {
                    Intent i = new Intent(context, RidersActivity.class);
                    i.putExtra("tripId", currentTrip.getId());

                    Gson gson = new Gson();
                    String s = gson.toJson(currentTrip.getPassengers());
                    i.putExtra("riders", s);

                    i.putExtra("tripDone", currentTrip.getStatus().getId() == 1 || currentTrip.getStatus().getId() == 3);

                    context.startActivity(i);
                }

            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentTrip.getPassengers().size() == 0)
                {
                Intent i = new Intent(context, TripEditActivity.class);
                Log.d("DriverAdapter", currentTrip.getId().toString());
                i.putExtra("tripId", currentTrip.getId());
                context.startActivity(i);
                }
                else Toast.makeText(context, "Trip is not empty, please remove the passengers first and notify them", Toast.LENGTH_LONG).show();
            }
        });

        holder.btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentTrip.getRides().size() == 0 || currentTrip.getPassengers().size() == 0 || currentTrip.getReserved() == 0){

                    Toast.makeText(context, "The trip is still empty, you can cancel it but cannot mark it as completed", Toast.LENGTH_LONG).show();
                }
                else {

                // status 1 is completed, 2 is in progress (waiting passengers), 3 is cancelled
                RetroClient.getInstance().getMyApi().setStatus(currentTrip.getId(), 1).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Log.d("Completed", response.toString());
                        updateItem(holder.getAdapterPosition());


                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d("Completed", t.getMessage());

                    }
                });

                }
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetroClient.getInstance().getMyApi().setStatus(currentTrip.getId(), 3).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Log.d("Completed", response.toString());
                        updateItem(holder.getAdapterPosition());


                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d("Completed", t.getMessage());

                    }
                });
            }
        });

        holder.btnActiveDrvTripShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (currentTrip.getPassengers().size() != 0) {
                    String origin =  getDriverOrigin(currentTrip);
                    String destination =  getDriverDest(currentTrip);
                    waypoints = getWaypoints(currentTrip);
                    boolean optimize = true; //make it false to rurn off optimization
                    if (optimize)
                        waypoints = "optimize:true|" + waypoints;

                    Call<Directions> directionsCallWithPassengers = RetroClient.getInstance().getMyApi().getDirections("https://maps.googleapis.com/maps/api/directions/json", origin, destination, MAPS_API_KEY.toString(),waypoints);

                    directionsCallWithPassengers.enqueue(new Callback<Directions>() {

                        @Override
                        public void onResponse(Call<Directions> call, Response<Directions> response) {

                            if (!response.body().getStatus().equals("OK")){

                                Toast.makeText(context, "The path is invalid, please edit the trip", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {

                            sendPath(response.body().getRoutes().get(0).getOverviewPolyline().getPoints().toString());
                            Bounds responseBounds = response.body().getRoutes().get(0).getBounds();
                            LatLngBounds bounds = new LatLngBounds(new LatLng(responseBounds.getSouthwest().getLat(), responseBounds.getSouthwest().getLng())
                                    ,new LatLng(responseBounds.getNortheast().getLat(), responseBounds.getNortheast().getLng()));

                            sendBounds(bounds);
                            Log.d("DirectionsRequest Origin", origin );
                            Log.d("DirectionsRequest Dest", destination );
                            Log.d("DirectionsRequest wp", waypoints);
                            Log.d("DirectionsRequest", response.raw().request().url().toString());
                            Log.d("DirectionsResult", response.body().getRoutes().get(0).getWaypointOrder().toString());
                            Log.d("DirectionsResult", response.body().getRoutes().get(0).getOverviewPolyline().getPoints());
                            FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flFragment, new MapFragment());
                            fragmentTransaction.commit();

                            }

                        }

                        @Override
                        public void onFailure(Call<Directions> call, Throwable t) {
                            Log.d("TripsAdapter", t.getMessage());
                        }
                    });
                }

                else{
                    Call<Directions> directionsCall = RetroClient.getInstance().getMyApi().getDirections("https://maps.googleapis.com/maps/api/directions/json", getDriverOrigin(currentTrip), getDriverDest(currentTrip), MAPS_API_KEY.toString());
                    directionsCall.enqueue(new Callback<Directions>() {
                        @Override
                        public void onResponse(Call<Directions> call, Response<Directions> response) {

                            Log.d( "TripsAdapter",response.toString());

                            if (!response.body().getStatus().equals("OK")){
                                Log.d( "TripsAdapter",response.body().toString());
                                Toast.makeText(context, "The path is invalid, please edit the trip", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                            Bounds responseBounds = response.body().getRoutes().get(0).getBounds();
                            LatLngBounds bounds = new LatLngBounds(new LatLng(responseBounds.getSouthwest().getLat(), responseBounds.getSouthwest().getLng())
                                    ,new LatLng(responseBounds.getNortheast().getLat(), responseBounds.getNortheast().getLng()));

                            sendBounds(bounds);
                            sendPath(response.body().getRoutes().get(0).getOverviewPolyline().getPoints().toString());
                            FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flFragment, new MapFragment());
                            fragmentTransaction.commit();
                            }
                        }

                        @Override
                        public void onFailure(Call<Directions> call, Throwable t) {
                            Log.d("TripsAdapter", t.getMessage());
                        }
                    });
                }

            }
        });

    }

    public String getDriverOrigin(Trip trip){

        return trip.getPoints().get(0).getLat().toString() + "," + trip.getPoints().get(0).getLon();

    }
    public void sendBounds(LatLngBounds bounds){

        Log.d("Sending ", bounds.toString());
        viewmodel.setBounds(bounds);

    }

    public String getDriverDest(Trip trip){

        return trip.getPoints().get(1).getLat().toString() + "," + trip.getPoints().get(1).getLon();

    }

    public String getWaypoints(Trip trip){
        int index =0;
        String result = "";

        for(Point point: trip.getPoints()){
            if(index <= 1){
                index++;
            }
            else{
                result += point.getLat() + "," + point.getLon() + "|";
                index++;
            }

        }
        return result;

    }

    public void updateItem(int position){

        items.remove(position);
        this.notifyItemRemoved(position);

    }


    public void sendPath(String path){

        viewmodel.setPath(path);

    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TripsViewHolder extends RecyclerView.ViewHolder{

        ImageButton btnActiveDrvTripShow, btnCompleted, btnCancel, btnUpdate;
        TextView tvActiveDrvRiders, tvDate, tvIncome;

        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);
            btnActiveDrvTripShow = itemView.findViewById(R.id.btnActiveDrvTripShow);
            tvActiveDrvRiders = itemView.findViewById(R.id.tcActiveDrvTripRiders);
            btnCompleted = itemView.findViewById(R.id.btnDriverActiveCompleted);
            btnCancel = itemView.findViewById(R.id.btnActiveDrvTripCancel);
            tvDate = itemView.findViewById(R.id.tvActiveDrvTripDate);
            tvIncome = itemView.findViewById(R.id.tvActiveDrvTripAmount);
            btnUpdate = itemView.findViewById(R.id.btnActiveDrvTripUpdate);
        }
    }

}

