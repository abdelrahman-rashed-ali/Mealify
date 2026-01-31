package com.rashed.mealify.datasource.local;

import android.content.Context;

public class DatabaseDatasource {
    private final DatabaseConnection databaseConnection;

    public DatabaseDatasource(Context context) {
        this.databaseConnection = DatabaseConnection.getInstance(context);
    }


}
