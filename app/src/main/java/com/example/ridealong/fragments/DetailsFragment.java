package com.example.ridealong.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ridealong.R;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.misc.TripViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    TripViewModel model;
    TextInputEditText dateField, timeField;
    EditText etCapacity;
    Trip current;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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

        View view = inflater.inflate(R.layout.fragment_details, container, false);
        model = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT TRIP DATE");

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        final MaterialTimePicker materialTimePicker = materialTimeBuilder.build();

        dateField = view.findViewById(R.id.dateField);
        timeField = view.findViewById(R.id.timeField);
        etCapacity = (EditText) view.findViewById(R.id.etCapacityDetails);


        etCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){
                    if (Integer.parseInt(charSequence.toString()) == 0 || charSequence.toString().isEmpty() ||charSequence.toString().matches("")) {
                        Toast.makeText(getContext(), "Maximum Capacity Cannot Be 0", Toast.LENGTH_SHORT).show();
                        etCapacity.setText("1");
                        updateViewModel();
                    }

                    else if (Integer.parseInt(charSequence.toString()) <0 || Integer.parseInt(charSequence.toString())>7 ){

                        Toast.makeText(getContext(), "Invalid capacity", Toast.LENGTH_SHORT).show();
                        etCapacity.setText("1");
                        updateViewModel();
                    }

                    else
                        updateViewModel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                if ( (Long) selection < System.currentTimeMillis()){

                    Toast.makeText(getContext(), "Trip date cannot be in the past", Toast.LENGTH_SHORT).show();
                    dateField.setText(formatter.format(new Date(System.currentTimeMillis())));
                    updateViewModel();

                }
                else {
                    String dateString = formatter.format(new Date((Long) selection));
                    dateField.setText(dateString);
                    updateViewModel();
                }

            }
        });

        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TimePicker", String.valueOf(materialTimePicker.getHour()));
                Time tme = new Time(materialTimePicker.getHour(),materialTimePicker.getMinute(),0);//seconds by default set to zero
                Format formatter;
                formatter = new SimpleDateFormat("HH:mm");
                timeField.setText(formatter.format(tme));
                updateViewModel();

            }
        });


        view.findViewById(R.id.dateField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            }
        });

        view.findViewById(R.id.timeField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialTimePicker.show(getActivity().getSupportFragmentManager(), "TIME_PICKER");

            }
        });

        setDefaultValues();

        return view;
    }

    private void updateViewModel(){

        current = model.getTrip().getValue();

        Log.d("Before updating the ViewModel ", current.toString());
        current.setCapacity(Integer.parseInt(etCapacity.getText().toString()));
        current.setTripdate(dateField.getText().toString());
        current.setTriptime(timeField.getText().toString());
        Log.d("After updating the ViewModel ", current.toString());
        model.setTrip(current);

    }

    private boolean checkFields(){

        return true;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        model = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        updateViewModel();
    }

    @Override
    public void onPause() {
        updateViewModel();
        super.onPause();
    }

    private void setDefaultValues(){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(new Date((Long) System.currentTimeMillis()));
        dateField.setText(todayString);

        SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
        String nowString = formatter2.format(new Date((Long) System.currentTimeMillis()));
        timeField.setText(nowString);

    }

}