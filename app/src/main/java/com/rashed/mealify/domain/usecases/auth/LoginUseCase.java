package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;

import io.reactivex.rxjava3.core.Single;

public class LoginUseCase {
    private final AuthRepository repo;
    public LoginUseCase(AuthRepository repo) { this.repo = repo; }
    public Single<AuthUser> execute(String email, String password) {
        return repo.login(email, password);
    }
}
