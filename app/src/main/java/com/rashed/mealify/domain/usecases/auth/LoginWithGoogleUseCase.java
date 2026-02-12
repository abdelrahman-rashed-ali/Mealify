package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;
import io.reactivex.rxjava3.core.Single;

public class LoginWithGoogleUseCase {
    private final AuthRepository repo;

    public LoginWithGoogleUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public Single<AuthUser> execute(String idToken) {
        return repo.loginWithGoogle(idToken);
    }
}