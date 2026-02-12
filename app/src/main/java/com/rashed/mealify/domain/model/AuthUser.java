package com.rashed.mealify.domain.model;

public class AuthUser {
    public final String uid;
    public final String email;
    public final String firstName;
    public final String lastName;

    public AuthUser(String uid, String email, String firstName, String lastName) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
