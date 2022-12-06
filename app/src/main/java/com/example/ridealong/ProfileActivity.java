package com.example.ridealong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridealong.adapters.ReviewsAdapter;
import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.Review;
import com.example.ridealong.api.models.User;
import com.example.ridealong.misc.SavedSharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    String username;
    ArrayList<Review> myRevs;
    RatingBar ratingBar;
    RecyclerView rvReviews;
    EditText etUn, etFn, etLn, etCar, etEmail, etMobile, etJdate, etReview, etPrice;
    Button btnSubmit;
    ImageButton btnWhatsapp, btnCall;
    TextView tvCarStatic, tvKMStatic;
    boolean isDriver;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent myIntent = getIntent(); // gets the previously created intent
        username = myIntent.getStringExtra("username");

        rvReviews = findViewById(R.id.rvReviewProfAct);

        etUn = findViewById(R.id.etUnProfAct);
        etFn = findViewById(R.id.etFnameProfAct);
        etLn = findViewById(R.id.etLnameProfAct);
        etEmail = findViewById(R.id.etEmailProfAct);
        etMobile = findViewById(R.id.etMobileProfAct);
        etJdate = findViewById(R.id.etJoinedProfAct);
        etCar = findViewById(R.id.etCarProfAct);
        etReview = findViewById(R.id.etReviewProfAct);
        btnSubmit = findViewById(R.id.btnSubmitProfAct);
        ratingBar = findViewById(R.id.ratingProfAct);
        etPrice = findViewById(R.id.etPriceProfAct);

        btnCall = findViewById(R.id.btnCallProfAct);
        btnWhatsapp = findViewById(R.id.btnWhatsapProfAct);

        tvCarStatic = findViewById(R.id.tvCarStaticProf);
        tvKMStatic = findViewById(R.id.tvProfPriceActStatic);



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etReview.getText().toString().isEmpty() || ratingBar.getNumStars() == 0 )
                    Toast.makeText(ProfileActivity.this, "Please write the review and select rating", Toast.LENGTH_SHORT).show();
                else{
                    RetroClient.getInstance().getMyApi().postReview(username, SavedSharedPreference.getProfile(ProfileActivity.this).getUsername(), etReview.getText().toString(), (int)ratingBar.getRating()).enqueue(
                            new Callback<Object>() {
                                @Override
                                public void onResponse(Call<Object> call, Response<Object> response) {
                                    Log.d("ProfileActivity", response.toString());
                                    onBackPressed();
                                }

                                @Override
                                public void onFailure(Call<Object> call, Throwable t) {
                                    Log.d("ProfileActivity", t.getMessage());
                                    onBackPressed();
                                }
                            }
                    );
                }
            }
        });

        RetroClient.getInstance().getMyApi().getUser(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    user = response.body();
                    populateFields(user);

                    isDriver = user.getIsDriver();

                    if (!isDriver){

                        tvCarStatic.setVisibility(View.GONE);
                        tvKMStatic.setVisibility(View.GONE);
                        etCar.setVisibility(View.GONE);
                        etPrice.setVisibility(View.GONE);

                    }

                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (user.getMobile() == null)

                                Toast.makeText(ProfileActivity.this, "The user didn't add their mobile number", Toast.LENGTH_SHORT).show();

                            else {

                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.getMobile()));
                                startActivity(intent);
                            }
                        }
                    });

                    btnWhatsapp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String url = "https://api.whatsapp.com/send?phone="+user.getMobile();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);

                        }
                    });

                    RetroClient.getInstance().getMyApi().getReviews().enqueue(new Callback<List<Review>>() {
                        @Override
                        public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {

                            myRevs = new ArrayList<>();
                            ArrayList<Review> all = (ArrayList<Review>) response.body();
                            for (Review rev: all){
                                if (rev.getUser().getUsername().matches(username)){
                                    myRevs.add(rev);
                                }
                            }

                            ReviewsAdapter adapter= new ReviewsAdapter(ProfileActivity.this,myRevs);
                            rvReviews.setAdapter(adapter);
                            rvReviews.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                        }
                        @Override
                        public void onFailure(Call<List<Review>> call, Throwable t) {

                        }
                    });
                }
                else
                    Toast.makeText(ProfileActivity.this, "Error Retriving Profile", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }
    private void populateFields(User user){

        etUn.setText(user.getUsername());
        if (user.getFirstName() != null)
            etFn.setText(user.getFirstName());
        if (user.getLastName() != null)
            etLn.setText(user.getLastName());
        if (user.getMobile() != null)
            etMobile.setText(user.getMobile().toString());
        if (user.getEmail() != null)
            etEmail.setText(user.getEmail());
        etJdate.setText(user.getDateJoined().substring(0,10));
        if (user.getCar() != null)
            etCar.setText(user.getCar().toString());
        if (user.getKmprice() != null)
            etPrice.setText(user.getKmprice().toString());

    }
    private int calculateAverageRating(ArrayList<Review> input){

        int sum = 0;
        int count = input.size();
        for (Review rev: input){
            sum += rev.getStars();
        }
        return (int) Math.ceil(sum/count);
    }

    private boolean appInstalledOrNot(String url){
        PackageManager packageManager =getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }catch (PackageManager.NameNotFoundException e){
            app_installed = false;
        }
        return app_installed;
    }
}