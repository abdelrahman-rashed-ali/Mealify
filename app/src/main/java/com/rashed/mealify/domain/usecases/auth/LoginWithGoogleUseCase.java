package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;

public class LoginWithGoogleUseCase {

    private final AuthRepository repo;

    public LoginWithGoogleUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute(String idToken, ResultCallback<AuthUser> cb) {
        repo.loginWithGoogle(idToken, cb);
    }
}
