package com.rashed.mealify.domain.repository;

import com.rashed.mealify.domain.model.AuthUser;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface AuthRepository {
    Single<AuthUser> register(String email, String password, String firstName, String lastName);
    Single<AuthUser> login(String email, String password);
    Single<AuthUser> loginWithGoogle(String idToken);
    void logout();
    Completable sendPasswordReset(String email);
    Single<AuthUser> getCurrentUser();
    Single<AuthUser> updateName(String firstName, String lastName);
    Completable sendEmailVerification();
    Single<Boolean> checkEmailVerified();
    Single<AuthUser> loginAnonymously();
}