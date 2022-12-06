package com.example.ridealong.adapters;


import static com.example.ridealong.BuildConfig.MAPS_API_KEY;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ridealong.PassengerActivity;
import com.example.ridealong.ProfileActivity;
import com.example.ridealong.R;
import com.example.ridealong.RidersActivity;
import com.example.ridealong.SearchActivity;
import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.DistanceApiModels.DistanceMatrix;
import com.example.ridealong.api.models.Ride;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.misc.PriceCalculator;
import com.example.ridealong.misc.SavedSharedPreference;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.TripsViewHolder> {

    Context context;
    List<Trip> items;
    Trip query;
    public SearchAdapter(Context context, List<Trip> items, Trip query){
        this.context = context;
        this.items = items;
        this.query = query;
    }

    @NonNull
    @Override
    public SearchAdapter.TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_searchresult_item, parent, false);

        return new SearchAdapter.TripsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.TripsViewHolder holder, int position) {

        Trip currentTrip = items.get(holder.getAdapterPosition());

        RetroClient.getInstance().getMyApi().getDistance("https://maps.googleapis.com/maps/api/distancematrix/json"
                , query.getPoints().get(0).getLat() + "," + query.getPoints().get(0).getLon()
                , query.getPoints().get(1).getLat() + "," + query.getPoints().get(1).getLon()
                , MAPS_API_KEY).enqueue(new Callback<DistanceMatrix>() {
            @Override
            public void onResponse(Call<DistanceMatrix> call, Response<DistanceMatrix> response) {
                if (response.body().getStatus().matches("OK")){

                    int distance = response.body().getRows().get(0).getElements().get(0).getDistance().getValue()/1000;
                    float cost = PriceCalculator.calculate(currentTrip, distance);



                    holder.tvCost.setText( "Per seat: " + cost + " AED");
                    holder.tvDistance.setText(" for " + distance + " KMs");

                    holder.btnReserve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            boolean alreadyRider = false;

                            for (Ride r:currentTrip.getRides()){
                                if (r.getUser() == SavedSharedPreference.getProfile(context).getId()){
                                    alreadyRider = true;
                                    break;
                                }
                            }

                            if (alreadyRider)
                                Toast.makeText(context, "You're already a passenger on this trip! If you want to change the number of seats leave the trip and join again", Toast.LENGTH_LONG).show();
                            else {
                            RetroClient.getInstance().getMyApi().joinTrip(SavedSharedPreference.getProfile(context).getUsername(),
                                    currentTrip.getId(),
                                    Float.parseFloat(query.getPoints().get(0).getLat()),
                                    Float.parseFloat(query.getPoints().get(0).getLon()),
                                    Float.parseFloat(query.getPoints().get(1).getLat()),
                                    Float.parseFloat(query.getPoints().get(1).getLon()),
                                    cost*Integer.parseInt(holder.BtnSeats.getNumber()),
                                    distance, Integer.parseInt(holder.BtnSeats.getNumber())).enqueue(new Callback<Object>() {
                                @Override
                                public void onResponse(Call<Object> call, Response<Object> response) {

                                    context.startActivity(new Intent(context, PassengerActivity.class));

                                }

                                @Override
                                public void onFailure(Call<Object> call, Throwable t) {
                                    Log.d("SearchAdapter", t.getMessage());
                                    Toast.makeText(context, "Reservation Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                            }
                        }
                    });


                }
            }

            @Override
            public void onFailure(Call<DistanceMatrix> call, Throwable t) {

            }
        });

        holder.tvName.setText(currentTrip.getDriver().getUsername());
        holder.tvDateTime.setText(currentTrip.getTripdate() + " at " + currentTrip.getTriptime().substring(0,5));
        holder.tvCapacity.setText(currentTrip.getReserved() + "/" + currentTrip.getCapacity().toString());
        holder.BtnSeats.setRange(1,currentTrip.getCapacity()-currentTrip.getReserved());


        holder.tvCapacity.setOnClickListener(new View.OnClickListener() {
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

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("username", currentTrip.getDriver().getUsername());
                context.startActivity(i);
            }
        });



    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TripsViewHolder extends RecyclerView.ViewHolder{

        ElegantNumberButton BtnSeats;
        Button btnReserve;
        TextView tvName, tvDateTime, tvCapacity, tvCost, tvDistance;


        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);
            BtnSeats = itemView.findViewById(R.id.btnSeatsIncrementer);
            btnReserve = itemView.findViewById(R.id.btnSearchitmReserve);

            tvDistance = itemView.findViewById(R.id.tvSearchDistance);


            tvCost = itemView.findViewById(R.id.tvSearchitemSeatprice);
            tvName = itemView.findViewById(R.id.tvDrivernameSearchitem);
            tvDateTime = itemView.findViewById(R.id.tvDateSearchitem);
            tvCapacity = itemView.findViewById(R.id.tvRidersSearchitem);

        }
    }

}
