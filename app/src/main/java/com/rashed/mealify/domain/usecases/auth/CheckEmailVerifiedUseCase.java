package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.repository.AuthRepository;

public class CheckEmailVerifiedUseCase {
    private final AuthRepository repo;

    public CheckEmailVerifiedUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute(ResultCallback<Boolean> cb) {
        repo.checkEmailVerified(cb);
    }
}
