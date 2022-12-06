package com.example.ridealong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.fragments.DetailsFragment;
import com.example.ridealong.fragments.PickLocationFragment;
import com.example.ridealong.misc.SavedSharedPreference;
import com.example.ridealong.misc.TripViewModel;
import com.example.ridealong.misc.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.sql.Driver;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripEditActivity extends AppCompatActivity implements TabLayoutMediator.TabConfigurationStrategy{


    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ArrayList<String> titles;
    FloatingActionButton fabNav;
    TripViewModel model;
    Trip current;
    Intent i;
    int tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_edit);

        model = new ViewModelProvider(this).get(TripViewModel.class);

        tabLayout = findViewById(R.id.tabLayoutUpdate);
        fabNav = findViewById(R.id.fabnavUpdate);

        i = getIntent();

        if (i.getExtras() != null){

            tripId = (int) getIntent().getExtras().get("tripId");

            Log.d("TripEdit", String.valueOf(tripId));
            fabNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = tabLayout.getSelectedTabPosition();

                    if (pos == 0){
                        TabLayout.Tab tab = tabLayout.getTabAt(1);
                        tab.select();
                    }
                    else if (pos == 1){
                        TabLayout.Tab tab = tabLayout.getTabAt(2);
                        tab.select();
                    }

                    else if (pos == 2){
                        current = model.getTrip().getValue();
                        RetroClient.getInstance().getMyApi().editTrip(tripId,SavedSharedPreference.getProfile(TripEditActivity.this).getUsername(),
                                Float.parseFloat(current.getPoints().get(0).getLat()),
                                Float.parseFloat(current.getPoints().get(0).getLon()),
                                Float.parseFloat(current.getPoints().get(1).getLat()),
                                Float.parseFloat(current.getPoints().get(1).getLon()),
                                current.getCapacity(),
                                current.getTripdate(),
                                current.getTriptime()
                        ).enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Call<Object> call, Response<Object> response) {
                                Log.d("EditActivity", response.toString());
                                Intent i = new Intent(TripEditActivity.this, DriverActivity.class);
                                startActivity(i);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable t) {
                                Log.d("EditActivity", t.getMessage());

                            }
                        });

                        Log.d("EditActivity", current.toString());
                    }
                }
            });


            viewPager2 = findViewById(R.id.viewpagerUpdate);
            viewPager2.setUserInputEnabled(false);
            titles = new ArrayList<String>();
            titles.add("ORIGIN");
            titles.add("DESTINATION");
            titles.add("DETAILS");

            setViewPagerAdapter();

            new TabLayoutMediator(tabLayout, viewPager2, this).attach();
        }






    }

    public void setViewPagerAdapter(){

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(PickLocationFragment.newInstance(true));
        fragmentList.add(PickLocationFragment.newInstance(false));
        fragmentList.add(new DetailsFragment());

        viewPagerAdapter.setFragments(fragmentList);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                if (position == 0) fabNav.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                if (position == 1) fabNav.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                if (position == 2) fabNav.setImageResource(R.drawable.ic_baseline_check_24);

                super.onPageSelected(position);
            }
        });

        viewPager2.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        tab.setText(titles.get(position));

    }
}