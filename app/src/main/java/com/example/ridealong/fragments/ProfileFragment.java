package com.example.ridealong.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ridealong.R;
import com.example.ridealong.SignupActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    boolean isDriver;
    EditText etUn, etFname, etLname, etEmail, etMobile, etCar, etJoined, etPriceKm;
    Button btnUpdate;
    RecyclerView rvReviews;
    TextView tvCar, tvPrice;
    RatingBar ratingBar;

    ArrayList<Review> myRevs;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        isDriver = SavedSharedPreference.getProfile(getContext()).getIsDriver();



        etUn = v.findViewById(R.id.etProfileUn);
        etFname = v.findViewById(R.id.etProfileFname);
        etLname = v.findViewById(R.id.etProfileLname);
        etEmail = v.findViewById(R.id.etProfileEmail);
        etCar = v.findViewById(R.id.etProfileCar);
        etMobile = v.findViewById(R.id.etProfileMobile);
        etJoined = v.findViewById(R.id.etProfileJdate);
        etPriceKm = v.findViewById(R.id.etProfileKM);
        btnUpdate = v.findViewById(R.id.btnUpdateMyprofile);

        tvCar = v.findViewById(R.id.tvCarStatic);
        tvPrice = v.findViewById(R.id.tvPriceStatic);

        if (!isDriver) {

            tvPrice.setVisibility(View.GONE);
            tvCar.setVisibility(View.GONE);
            etCar.setVisibility(View.GONE);
            etPriceKm.setVisibility(View.GONE);

        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(getFieldsValues());
            }
        });

        ratingBar = v.findViewById(R.id.ratingAvgMyprofile);

        rvReviews = v.findViewById(R.id.rvMyReviews);

        populateMyProfile();

        RetroClient.getInstance().getMyApi().getReviews().enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {

                myRevs = new ArrayList<>();
                ArrayList<Review> all = (ArrayList<Review>) response.body();

                for (Review rev: all){
                    if (rev.getUser().getUsername().matches(SavedSharedPreference.getProfile(getContext()).getUsername())){
                        myRevs.add(rev);
                    }
                }

                ReviewsAdapter adapter= new ReviewsAdapter(getContext(),myRevs);
                rvReviews.setAdapter(adapter);
                rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

                ratingBar.setRating(calculateAverageRating(myRevs));

            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {

            }
        });

        return v;
    }


    private void populateMyProfile(){

        User saveduser = SavedSharedPreference.getProfile(getContext());


        etUn.setText(saveduser.getUsername());
        if (saveduser.getFirstName() != null)
            etFname.setText(saveduser.getFirstName());
        if (saveduser.getLastName() != null)
            etLname.setText(saveduser.getLastName());
        etJoined.setText(saveduser.getDateJoined().substring(0,10));
        if (saveduser.getEmail() != null)
            etEmail.setText(saveduser.getEmail());
        if (saveduser.getMobile() != null)
            etMobile.setText(saveduser.getMobile().toString());
        if (saveduser.getCar() != null)
            etCar.setText(saveduser.getCar().toString());
        if (saveduser.getKmprice() != null)
            etPriceKm.setText(saveduser.getKmprice());

    }

    private int calculateAverageRating(ArrayList<Review> input){
        int sum = 0;
        int count = input.size();

        if (count == 0){
            return 0;
        }

        else {
            for (Review rev: input){

                sum += rev.getStars();

            }
            return (int) Math.ceil(sum/count);
        }
    }

    private void updateProfile(User newuser){

        Log.d("ProfileFragment1", newuser.toString());
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading");
        pd.show();
        RetroClient.getInstance().getMyApi().updateUser(SavedSharedPreference.getProfile(getContext()).getUsername(), newuser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                Log.d("ProfileFragment2", response.toString());
                Log.d("ProfileFragment2", response.toString());

                SavedSharedPreference.setProfile(getContext(),response.body());
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("ProfileFragment3", t.getMessage());
                pd.dismiss();
            }
        });
    }

    private User getFieldsValues(){

        User user = new User();

        user.setUsername(etUn.getText().toString());
        user.setPassword(SavedSharedPreference.getPassword(getContext()));
        if (!etEmail.getText().toString().matches(""))
            user.setEmail(etEmail.getText().toString());
        if (!etFname.getText().toString().matches(""))
            user.setFirstName(etFname.getText().toString());
        if (!etLname.getText().toString().matches(""))
            user.setLastName(etLname.getText().toString());
        if (!etCar.getText().toString().matches(""))
            user.setCar(etCar.getText().toString());
        user.setIsDriver(SavedSharedPreference.getProfile(getContext()).getIsDriver());
        if (!etMobile.getText().toString().matches(""))
            user.setMobile(etMobile.getText().toString());
        if (!etPriceKm.getText().toString().matches(""))
            user.setKmprice(etPriceKm.getText().toString());

        return user;
    }

}