package com.example.ridealong.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridealong.ProfileActivity;
import com.example.ridealong.R;
import com.example.ridealong.api.models.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    Context context;
    List<Review> items;

    public ReviewsAdapter(Context context, List<Review> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_myreview_item, parent, false);

        return new ReviewsAdapter.ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ReviewsViewHolder holder, int position) {

        Review currentReview = items.get(holder.getAdapterPosition());

        holder.tvContent.setText(currentReview.getContent());
        holder.tvAuthor.setText(currentReview.getAuthor().getUsername() + " said: ");
        holder.tvDate.setText(currentReview.getCreatedAt().substring(0,10));

        holder.ratingBar.setRating(currentReview.getStars());
        holder.tvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("username", currentReview.getAuthor().getUsername());
                context.startActivity(i);


            }
        });

    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ReviewsViewHolder extends RecyclerView.ViewHolder{

        TextView tvContent, tvAuthor, tvDate;
        RatingBar ratingBar;

        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContent = itemView.findViewById(R.id.tvContentReview);
            tvAuthor = itemView.findViewById(R.id.tvUnReview);
            tvDate = itemView.findViewById(R.id.tvDateReview);
            ratingBar = itemView.findViewById(R.id.ratingReview);


        }
    }

}

