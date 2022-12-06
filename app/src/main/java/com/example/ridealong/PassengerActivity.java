package com.example.ridealong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.ridealong.databinding.ActivityPassengerBinding;
import com.example.ridealong.fragments.MapFragment;
import com.example.ridealong.fragments.TripsFragment;
import com.example.ridealong.fragments.LogoutFragment;
import com.example.ridealong.fragments.ProfileFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class PassengerActivity extends AppCompatActivity {

    ActivityPassengerBinding binding;
    FloatingActionButton fabCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        binding = ActivityPassengerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        if (i.getExtras() != null) {
            if (i.getExtras().getBoolean("firsttime")) // check if user came from signup activity direct him to the profile fragment to fill the details
                replaceFragment(new ProfileFragment());
            else
                replaceFragment(new MapFragment());
        }
        else{
            replaceFragment(new MapFragment());
        }
        binding.bottomNavigationView2.setBackground(null);
        binding.bottomNavigationView2.getMenu().getItem(2).setEnabled(false);
        binding.fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FAB", "Clicked");
                Intent i = new Intent(PassengerActivity.this, SearchActivity.class);
                startActivity(i);

            }
        });


        binding.bottomNavigationView2.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        replaceFragment(new MapFragment());
                        break;

                    case R.id.trips:
                        replaceFragment(new TripsFragment());
                        break;

                    case R.id.passprofile:
                        replaceFragment(new ProfileFragment());
                        break;

                    case R.id.logout:
                        replaceFragment(new LogoutFragment());
                        break;
                }
                return true;
            }
        });

    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragmentPass, fragment);
        fragmentTransaction.commit();

    }
}