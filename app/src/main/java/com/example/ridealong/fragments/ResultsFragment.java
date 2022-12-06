package com.example.ridealong.fragments;

import static com.example.ridealong.misc.TripMatchFinder.distance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridealong.R;
import com.example.ridealong.adapters.SearchAdapter;
import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.misc.TripMatchFinder;
import com.example.ridealong.misc.TripViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView rv;
    TripViewModel model;
    TextView tvStatic;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultsFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(String param1, String param2) {
        ResultsFragment fragment = new ResultsFragment();
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
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        rv = view.findViewById(R.id.rvSearchResults);
        tvStatic = view.findViewById(R.id.tvResultsStatic);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        model = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        Trip query = model.getTrip().getValue();

        double destinationDistance = distance(Double.parseDouble(query.getPoints().get(0).getLat()),
                Double.parseDouble(query.getPoints().get(0).getLon()),
                Double.parseDouble(query.getPoints().get(1).getLat()),
                Double.parseDouble(query.getPoints().get(1).getLon()),
                'K');

        boolean pointsFarFromOther = destinationDistance < 3;

        if ( query.getPoints() == null || query.getPoints().size() <= 1 || pointsFarFromOther ){

            Toast.makeText(getContext(), "Please select origin and destination first", Toast.LENGTH_SHORT).show();
            tvStatic.setText("Please select origin and destination first");
            rv.setVisibility(View.GONE);

        }

        else{

            if (query != null && query.getCapacity() != null){

                RetroClient.getInstance().getMyApi().getTrips().enqueue(new Callback<List<Trip>>() {
                    @Override
                    public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                        if (response.body() != null){
                            rv.setVisibility(View.VISIBLE);

                            ArrayList<Trip> filtered = TripMatchFinder.MatchFinder((ArrayList<Trip>) response.body(), query,200, 20);

                            if (filtered.size() == 0)
                                tvStatic.setText("No results found, try increasing capacity or changing the origin/destination");
                            else
                                tvStatic.setText("These trips might match:");
                            SearchAdapter searchAdapter = new SearchAdapter(getContext(), filtered, query);

                            rv.setAdapter(searchAdapter);
                            rv.setLayoutManager(new LinearLayoutManager(getActivity()));


                        }

                    }

                    @Override
                    public void onFailure(Call<List<Trip>> call, Throwable t) {
                        Log.d("Results", t.getMessage());
                    }
                });
            }
            else {
                query.setCapacity(3);
                query.setTripdate(new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
                query.setTriptime("00:00");
                RetroClient.getInstance().getMyApi().getTrips().enqueue(new Callback<List<Trip>>() {
                    @Override
                    public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                        if (response.body() != null){
                            rv.setVisibility(View.VISIBLE);

                            ArrayList<Trip> filtered = TripMatchFinder.MatchFinder((ArrayList<Trip>) response.body(), query,200, 20);

                            if (filtered.size() == 0)
                                tvStatic.setText("No results found, try increasing capacity or changing the origin/destination");
                            else
                                tvStatic.setText("These trips might match:");
                            SearchAdapter searchAdapter = new SearchAdapter(getContext(), filtered, query);
                            rv.setAdapter(searchAdapter);
                            rv.setLayoutManager(new LinearLayoutManager(getActivity()));


                        }

                    }

                    @Override
                    public void onFailure(Call<List<Trip>> call, Throwable t) {
                        Log.d("Results", t.getMessage());
                    }
                });
            }
        }
    }
}