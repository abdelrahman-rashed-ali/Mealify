package com.rashed.mealify.datasource.repository;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuthDataSource dataSource;

    public AuthRepositoryImpl(FirebaseAuthDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void register(String email, String password, String firstName, String lastName, ResultCallback<AuthUser> cb) {
        dataSource.register(email, password, firstName, lastName, cb);
    }

    @Override
    public void login(String email, String password, ResultCallback<AuthUser> cb) {
        dataSource.login(email, password, cb);
    }

    @Override
    public void loginWithGoogle(String idToken, ResultCallback<AuthUser> cb) {
        dataSource.loginWithGoogle(idToken, cb);
    }

    @Override
    public void logout() {
        dataSource.logout();
    }

    @Override
    public void sendPasswordReset(String email, ResultCallback<Void> cb) {
        dataSource.sendPasswordReset(email, cb);
    }

    @Override
    public void getCurrentUser(ResultCallback<AuthUser> cb) {
        dataSource.getCurrentUser(cb);
    }

    @Override
    public void updateName(String firstName, String lastName, ResultCallback<AuthUser> cb) {
        dataSource.updateName(firstName, lastName, cb);
    }

    @Override
    public void sendEmailVerification(ResultCallback<Void> cb) {
        dataSource.sendEmailVerification(cb);
    }

    @Override
    public void checkEmailVerified(ResultCallback<Boolean> cb) {
        dataSource.checkEmailVerified(cb);
    }

    @Override
    public void loginAnonymously(ResultCallback<AuthUser> cb) {
        dataSource.loginAnonymously(cb);
    }
}
