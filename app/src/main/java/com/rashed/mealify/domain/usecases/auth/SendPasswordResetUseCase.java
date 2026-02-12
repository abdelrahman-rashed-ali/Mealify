package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.repository.AuthRepository;

public class SendPasswordResetUseCase {

    private final AuthRepository repo;

    public SendPasswordResetUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute(String email, ResultCallback<Void> cb) {
        repo.sendPasswordReset(email, cb);
    }
}
