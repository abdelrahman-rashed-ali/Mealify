package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;

public class LoginUseCase {

    private final AuthRepository repo;

    public LoginUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute(String email, String password, ResultCallback<AuthUser> cb) {
        repo.login(email, password, cb);
    }
}
