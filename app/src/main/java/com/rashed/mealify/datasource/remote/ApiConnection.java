package com.rashed.mealify.datasource.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConnection {
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static ApiDao service;

    public static ApiDao getService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(ApiDao.class);
        }
        return service;
    }
}
