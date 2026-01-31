package com.rashed.mealify.datasource.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rashed.mealify.datasource.remote.dto.MealsResponse;

@Database(entities = {MealsResponse.class}, version = 1)
public abstract class DatabaseConnection extends RoomDatabase {

    private static final String DATABASE_NAME = "products_db";
    private static volatile DatabaseConnection instance;
    //public abstract ProductDao productDao();

    public static DatabaseConnection getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            DatabaseConnection.class,
                            DATABASE_NAME
                    )
                    .build();
        }
        return instance;
    }
}
