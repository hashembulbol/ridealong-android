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

import com.example.ridealong.ProfileActivity;
import com.example.ridealong.R;
import com.example.ridealong.RidersActivity;
import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.MapsApiModels.Bounds;
import com.example.ridealong.api.models.MapsApiModels.Directions;
import com.example.ridealong.api.models.Point;

import com.example.ridealong.api.models.Ride;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.fragments.MapFragment;
import com.example.ridealong.misc.PathViewModel;
import com.example.ridealong.misc.SavedSharedPreference;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerActiveTripAdapter extends RecyclerView.Adapter<PassengerActiveTripAdapter.TripsViewHolder> {

    PathViewModel viewmodel;
    Context context;
    List<Trip> items;
    String waypoints;

    public PassengerActiveTripAdapter(Context context, List<Trip> items, PathViewModel model){

        this.viewmodel = model;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public PassengerActiveTripAdapter.TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_activetrip_item, parent, false);

        return new PassengerActiveTripAdapter.TripsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerActiveTripAdapter.TripsViewHolder holder, int position) {

        Trip currentTrip = items.get(holder.getAdapterPosition());



        holder.tvRiders.setText(currentTrip.getReserved() + "/" + currentTrip.getCapacity() + " Seats");

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("username", currentTrip.getDriver().getUsername());
                context.startActivity(i);
            }
        });

        holder.tvName.setText(currentTrip.getDriver().getUsername());

        holder.tvDate.setText(currentTrip.getTripdate() + " at " + currentTrip.getTriptime().substring(0,5));


        for (Ride r: currentTrip.getRides()){
            if (r.getUser() == SavedSharedPreference.getProfile(context).getId())
                holder.tvCost.setText(r.getCost());
        }

        holder.tvRiders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentTrip.getReserved() == 0)
                    Toast.makeText(context, "Trip is still empty", Toast.LENGTH_SHORT).show();
                else{
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



        holder.btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RetroClient.getInstance().getMyApi().leaveTrip(SavedSharedPreference.getProfile(context).getUsername(),currentTrip.getId()).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()){

                            Log.d("kicked", "kicked");
                            Toast.makeText(context, "You left the trip successfully", Toast.LENGTH_SHORT).show();
                            updateItem(holder.getAdapterPosition());

                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d("kicked", t.getMessage());

                    }
                });

            }
        });

        holder.btnShow.setOnClickListener(new View.OnClickListener() {
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

                                Log.d( "TripsAdapter",response.body().toString());
                                Toast.makeText(context, "The path is invalid, please edit the trip", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                sendPath(response.body().getRoutes().get(0).getOverviewPolyline().getPoints().toString());
                                Bounds responseBounds = response.body().getRoutes().get(0).getBounds();
                                LatLngBounds bounds = new LatLngBounds(new LatLng(responseBounds.getSouthwest().getLat(), responseBounds.getSouthwest().getLng())
                                        , new LatLng(responseBounds.getNortheast().getLat(), responseBounds.getNortheast().getLng()));

                                sendBounds(bounds);
                                Log.d("DirectionsRequest Origin", origin);
                                Log.d("DirectionsRequest Dest", destination);
                                Log.d("DirectionsRequest wp", waypoints);
                                Log.d("DirectionsRequest", response.raw().request().url().toString());
                                Log.d("DirectionsResult", response.body().getRoutes().get(0).getWaypointOrder().toString());
                                Log.d("DirectionsResult", response.body().getRoutes().get(0).getOverviewPolyline().getPoints());
                                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.flFragmentPass, new MapFragment());
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

                            if (!response.body().getStatus().equals("OK")){
                                Log.d( "TripsAdapter",response.body().toString());
                                Toast.makeText(context, "The path is invalid, please edit the trip", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                Bounds responseBounds = response.body().getRoutes().get(0).getBounds();
                                LatLngBounds bounds = new LatLngBounds(new LatLng(responseBounds.getSouthwest().getLat(), responseBounds.getSouthwest().getLng())
                                        , new LatLng(responseBounds.getNortheast().getLat(), responseBounds.getNortheast().getLng()));

                                sendBounds(bounds);
                                sendPath(response.body().getRoutes().get(0).getOverviewPolyline().getPoints().toString());
                                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.flFragmentPass, new MapFragment());
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


    public void sendPath(String path){

        viewmodel.setPath(path);

    }

    public void updateItem(int position){

        items.remove(position);
        this.notifyItemRemoved(position);

    }

    public void sendBounds(LatLngBounds bounds){

        Log.d("Sending ", bounds.toString());
        viewmodel.setBounds(bounds);

    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TripsViewHolder extends RecyclerView.ViewHolder{

        ImageButton btnShow, btnLeave;
        TextView tvRiders, tvName, tvDate, tvCost;

        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);

            btnShow = itemView.findViewById(R.id.btnActivePassTripShow);
            tvRiders = itemView.findViewById(R.id.tvActivePassTripRiders);
            tvName = itemView.findViewById(R.id.tvActivePassDrvname);
            tvDate = itemView.findViewById(R.id.tvActivePassDate);
            btnLeave = itemView.findViewById(R.id.btnActivePassTripLeave);
            tvCost = itemView.findViewById(R.id.tvActivePassTripPrice);


        }
    }

}

