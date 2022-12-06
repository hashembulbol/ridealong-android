package com.example.ridealong.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    private static RetroClient instance = null;
    private ApiPoints myApi;

    private RetroClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiPoints.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(ApiPoints.class);
    }

    public static synchronized RetroClient getInstance() {
        if (instance == null) {
            instance = new RetroClient();
        }
        return instance;
    }

    public ApiPoints getMyApi() {
        return myApi;
    }
}