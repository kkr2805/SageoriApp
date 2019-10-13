package com.hgtech.sageoriapp;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.gson.*;

public class SageoriClient {
    static final private String BASE_URL = "http://192.168.1.26:3000/";
    static private SageoriClient instance = new SageoriClient();
            
    static public Retrofit getClient() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDateFormatAdapter());

		Retrofit client = new Retrofit.Builder()
									.baseUrl(BASE_URL)		
									.addConverterFactory(GsonConverterFactory.create(builder.create()))
									.build();

        return client; 
    }

    static public SageoriAPI getAPI() {
        return getClient().create(SageoriAPI.class);
    }
}
