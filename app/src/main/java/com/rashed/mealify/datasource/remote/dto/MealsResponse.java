package com.rashed.mealify.datasource.remote.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MealsResponse {
    @PrimaryKey
    private int i;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
}
