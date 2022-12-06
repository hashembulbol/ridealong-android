package com.example.ridealong.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ridealong.R;
import com.example.ridealong.api.models.Point;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.misc.SavedSharedPreference;
import com.example.ridealong.misc.TripViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class PickLocationFragment extends Fragment {

    TripViewModel model;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    boolean markerMoved;
    boolean isOrigin;
    LatLng selectedLocation;

    public static PickLocationFragment newInstance(boolean isOrigin) {
        PickLocationFragment myFragment = new PickLocationFragment();

        Bundle args = new Bundle();
        args.putBoolean("isOrigin", isOrigin);
        myFragment.setArguments(args);

        return myFragment;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED){

                getCurrentLocation();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            }

            map.getUiSettings().setZoomControlsEnabled(true);
            map.setMyLocationEnabled(true);
            map.setMinZoomPreference(9);
            map.setMaxZoomPreference(20);
            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    markerMoved = true;
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    map.setOnMarkerDragListener(this);

                    LatLng latLng = marker.getPosition();
                    selectedLocation = latLng;
                    updateViewModel(selectedLocation);
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        android.location.Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pick_location, container, false);

        markerMoved = false;
        isOrigin = getArguments().getBoolean("isOrigin", false);



        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0) && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();

        }
        else{
            Toast.makeText(getActivity(), "Location Not Given", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null){
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        if (!markerMoved) {
                            map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                            selectedLocation = latLng;
                            updateViewModel(selectedLocation);
                        }


                    }
                    else{
                        com.google.android.gms.location.LocationRequest locationRequest = new com.google.android.gms.location.LocationRequest().setPriority(LocationRequest.QUALITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                if (!markerMoved) {
                                    map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                                    selectedLocation = latLng;
                                    updateViewModel(selectedLocation);
                                }


                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

                    }
                }
            });

        }else {

            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.pickmap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        model = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

    }

    private void updateViewModel(LatLng input){

        Trip current = new Trip();

        if (isOrigin){

            Point p = new Point();
            p.setLat(String.valueOf(input.latitude));
            p.setLon(String.valueOf(input.longitude));
            p.setType(1);
            p.setUser(SavedSharedPreference.getProfile(getContext()).getId());
            ArrayList<Point> points = new ArrayList<Point>();
            points.add(p);
            current.setPoints(points);

            model.setTrip(current);

        }

        else
        {
            current = model.getTrip().getValue();
            Point p = new Point();
            p.setLat(String.valueOf(input.latitude));
            p.setLon(String.valueOf(input.longitude));
            p.setType(2);
            p.setUser(SavedSharedPreference.getProfile(getContext()).getId());
            ArrayList<Point> pts = (ArrayList<Point>) current.getPoints();

            if (pts.size() == 1){
                pts.add(p);
                current.setPoints(pts);
                model.setTrip(current);
            }
            else if (pts.size() == 2){
                pts.set(1, p);
                current.setPoints(pts);
                model.setTrip(current);
            }
            else {
                Log.d("PickLocation", "Mismatch");
                Toast.makeText(getContext(), "Mismatch Creating Point", Toast.LENGTH_SHORT).show();
            }

        }
    }

}