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

import com.example.ridealong.databinding.ActivityDriverBinding;
import com.example.ridealong.fragments.MapFragment;
import com.example.ridealong.fragments.TripsFragment;
import com.example.ridealong.fragments.LogoutFragment;
import com.example.ridealong.fragments.ProfileFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class DriverActivity extends AppCompatActivity {
    ActivityDriverBinding binding;
    FloatingActionButton fabCreate;
    boolean locationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        binding = ActivityDriverBinding.inflate(getLayoutInflater());
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
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        binding.fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FAB", "Clicked");
                Intent i = new Intent(DriverActivity.this, CreateActivity.class);
                startActivity(i);

            }
        });


        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        replaceFragment(new MapFragment());
                        break;

                    case R.id.trips:
                        replaceFragment(new TripsFragment());
                        break;

                    case R.id.driverprofile:
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
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.commit();

    }

}