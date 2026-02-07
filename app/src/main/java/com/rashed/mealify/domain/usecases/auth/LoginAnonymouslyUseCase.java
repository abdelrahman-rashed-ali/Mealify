package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;

public class LoginAnonymouslyUseCase {
    private final AuthRepository repo;

    public LoginAnonymouslyUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute(ResultCallback<AuthUser> cb) {
        repo.loginAnonymously(cb);
    }
}