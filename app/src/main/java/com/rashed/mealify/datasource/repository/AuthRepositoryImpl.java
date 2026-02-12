package com.rashed.mealify.datasource.repository;

import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuthDataSource dataSource;

    public AuthRepositoryImpl(FirebaseAuthDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Single<AuthUser> register(String email, String password, String firstName, String lastName) {
        return dataSource.register(email, password, firstName, lastName);
    }

    @Override
    public Single<AuthUser> login(String email, String password) {
        return dataSource.login(email, password);
    }

    @Override
    public Single<AuthUser> loginWithGoogle(String idToken) {
        return dataSource.loginWithGoogle(idToken);
    }

    @Override
    public void logout() {
        dataSource.logout();
    }

    @Override
    public Completable sendPasswordReset(String email) {
        return dataSource.sendPasswordReset(email);
    }

    @Override
    public Single<AuthUser> getCurrentUser() {
        return dataSource.getCurrentUser();
    }

    @Override
    public Single<AuthUser> updateName(String firstName, String lastName) {
        return dataSource.updateName(firstName, lastName);
    }

    @Override
    public Completable sendEmailVerification() {
        return dataSource.sendEmailVerification();
    }

    @Override
    public Single<Boolean> checkEmailVerified() {
        return dataSource.checkEmailVerified();
    }

    @Override
    public Single<AuthUser> loginAnonymously() {
        return dataSource.loginAnonymously();
    }
}