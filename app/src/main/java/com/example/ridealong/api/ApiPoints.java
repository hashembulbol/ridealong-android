package com.example.ridealong.api;



import com.example.ridealong.api.models.DistanceApiModels.DistanceMatrix;
import com.example.ridealong.api.models.MapsApiModels.Directions;
import com.example.ridealong.api.models.Review;
import com.example.ridealong.api.models.Trip;
import com.example.ridealong.api.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiPoints {

    //String BASE_IP = "192.168.100.105:8000";
    String BASE_IP = "10.0.2.2:8000";
    String BASE_URL = "http://" + BASE_IP + "/";


    @GET("trip")
    Call<List<Trip>> getTrips();

    @GET
    Call<Directions> getDirections(@Url String URL, @Query("origin") String origin, @Query("destination") String destination, @Query("key") String key, @Query("waypoints") String wp);

    @GET
    Call<DistanceMatrix> getDistance(@Url String URL, @Query("origins") String origins, @Query("destinations") String destinations, @Query("key") String key);

    @GET
    Call<Directions> getDirections(@Url String URL, @Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);



    @GET("login/")
    Call<User> login(@Header("Authorization") String authHeader);


    @GET("postreview/{username}/{author}/{content}/{stars}")
    Call<Object> postReview(@Path("username") String username,@Path("author") String author,@Path("content") String content,@Path("stars") int stars);

    @GET("review/")
    Call<List<Review>> getReviews();

    @PUT("user/{username}/")
    Call<User> updateUser(@Path("username") String username, @Body User user);

    @GET("user/{username}/")
    Call<User> getUser(@Path("username") String username);

    @GET("trip/leave/{username}/{id}/")
    Call<Object> leaveTrip(@Path("username") String username, @Path("id") int id);

    @GET("trip/join/{username}/{id}/{originlat}/{originlon}/{destlat}/{destlon}/{costinput}/{distance}/{seats}/")
    Call<Object> joinTrip(@Path("username") String username, @Path("id") int id, @Path("originlat") float originlat, @Path("originlon") float originlon, @Path("destlat") float destlat, @Path("destlon") float destlon , @Path("costinput") float cost, @Path("distance") float distance, @Path("seats") int seats);


    @GET("trip/create/{driver}/{originlat}/{originlon}/{destlat}/{destlon}/{capacity}/{date}/{time}/")
    Call<Object> createTrip(@Path("driver") String driver, @Path("originlat") float originlat, @Path("originlon") float originlon, @Path("destlat") float destlat, @Path("destlon") float destlon , @Path("capacity") int capacity, @Path("date") String date, @Path("time") String time);

    @GET("trip/edit/{tripid}/{driver}/{originlat}/{originlon}/{destlat}/{destlon}/{capacity}/{date}/{time}/")
    Call<Object> editTrip(@Path("tripid") int tripid, @Path("driver") String driver, @Path("originlat") float originlat, @Path("originlon") float originlon, @Path("destlat") float destlat, @Path("destlon") float destlon , @Path("capacity") int capacity, @Path("date") String date, @Path("time") String time);


    @GET("trip/status/{id}/{setstatus}/")
    Call<Object> setStatus(@Path("id") int id, @Path("setstatus") int setstatus);

    @FormUrlEncoded
    @POST("signup/")
    Call<User> signup(@Field("username") String un, @Field("password") String pwd, @Field("is_driver") boolean isDriver);


}
