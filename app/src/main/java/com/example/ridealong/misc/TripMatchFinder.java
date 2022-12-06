package com.example.ridealong.misc;

import static java.lang.System.currentTimeMillis;

import android.util.Log;

import com.example.ridealong.api.models.Ride;
import com.example.ridealong.api.models.Trip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TripMatchFinder {

    static public ArrayList<Trip> MatchFinder(ArrayList<Trip> trips, Trip query, int threshold, int daysThreshold){
        ArrayList<Trip> result = new ArrayList<>();
        for (Trip t : trips){
            if (isMatch(t,query,threshold, daysThreshold)) result.add(t);
        }
        return result;
    }

    static public boolean isMatch(Trip trip, Trip query, double threshold, int daysThreshold){


        double originDistance = distance(Double.parseDouble(trip.getPoints().get(0).getLat()),
                Double.parseDouble(trip.getPoints().get(0).getLon()),
                Double.parseDouble(query.getPoints().get(0).getLat()),
                Double.parseDouble(query.getPoints().get(0).getLon()),
                'K');

        double destinationDistance = distance(Double.parseDouble(trip.getPoints().get(1).getLat()),
                Double.parseDouble(trip.getPoints().get(1).getLon()),
                Double.parseDouble(query.getPoints().get(1).getLat()),
                Double.parseDouble(query.getPoints().get(1).getLon()),
                'K');

//        boolean distanceOk = originDistance < threshold || destinationDistance < threshold; // if matching origin or destination
        boolean distanceOk = destinationDistance < threshold; // if matching destination only
        Log.d("MatchFinder Distance", "Comparing " + originDistance + " and " + destinationDistance + " against threshold " + threshold + " resulting in value of " + distanceOk);

        boolean capcityOk = trip.getCapacity() <= query.getCapacity();
        Log.d("MatchFinder Capacity", "Comparing " + trip.getCapacity() + " and " + query.getCapacity());
        boolean statusOk = trip.getStatus().getId() == 2;

        int daysDifference;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date1 = LocalDate.parse(trip.getTripdate(), dtf);
        LocalDate date2 = LocalDate.parse(query.getTripdate(), dtf);
        long daysBetween = Duration.between(date1.atStartOfDay(), date2.atStartOfDay()).toDays();

        boolean datesOk = Math.abs(daysBetween) < daysThreshold;

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        LocalDate dateToday = LocalDate.parse( df.format(c), dtf);
        long daysForPast = Duration.between(date1.atStartOfDay(), dateToday.atStartOfDay()).toDays();

//        try {
//            if (new SimpleDateFormat("yyyy-MM-dd").parse(trip.getTripdate()).before(new Date())) {
//                Log.d("MatchFinder", trip.getTripdate() + " and " + new Date());
//                notPast = false;
//            }
//            else notPast = true;
//
//        }
//        catch (Exception e){
//            Log.d("Parsing", "Wrong");
//            notPast = true;
//        }

        Log.d("MatchFinder:" , "Difference between Days calculating past: " + daysForPast + " days between " + dateToday.toString() + " and " + date1.toString());
        boolean notPast = daysForPast <= 0;

        return distanceOk && capcityOk && statusOk && datesOk && notPast;

    }


    static public double distance(double lat1, double lon1, double lat2, double lon2, char unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);

    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    static private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    static private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
