package com.example.ridealong.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridealong.ProfileActivity;
import com.example.ridealong.R;
import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.api.models.User;
import com.example.ridealong.misc.SavedSharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidersAdapter extends RecyclerView.Adapter<RidersAdapter.RidersViewHolder> {

    Context context;
    List<User> items;
    int tripId;
    boolean isTripDone;

    public RidersAdapter(Context context, List<User> items, int tripId, boolean isTripDone){
        this.context = context;
        this.items = items;
        this.tripId = tripId;
        this.isTripDone = isTripDone;
    }

    @NonNull
    @Override
    public RidersAdapter.RidersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_rider_item, parent, false);

        return new RidersAdapter.RidersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RidersAdapter.RidersViewHolder holder, int position) {

        User currentRider = items.get(holder.getAdapterPosition());

        holder.tvName.setText(currentRider.getUsername());

        if (isTripDone || !SavedSharedPreference.getProfile(context).getIsDriver()){holder.btnKick.setVisibility(View.GONE);}
        else {

            holder.btnKick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    RetroClient.getInstance().getMyApi().leaveTrip(currentRider.getUsername(),tripId).enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.isSuccessful()){

                                Log.d("kicked", "kicked");
                                Toast.makeText(context, "Rider has been kicked out of the trip", Toast.LENGTH_SHORT).show();
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
        }

        if (!currentRider.getUsername().matches(SavedSharedPreference.getProfile(context).getUsername()))
            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("username", currentRider.getUsername());
                    context.startActivity(i);


                }
            });

    }

    public void updateItem(int position){

        items.remove(position);
        this.notifyItemRemoved(position);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RidersViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        ImageButton btnKick;

        public RidersViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvRiderName);
            btnKick = itemView.findViewById(R.id.btnRderKick);



        }
    }

}

