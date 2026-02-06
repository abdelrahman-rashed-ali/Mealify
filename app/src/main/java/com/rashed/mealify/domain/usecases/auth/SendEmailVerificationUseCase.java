package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.repository.AuthRepository;

public class SendEmailVerificationUseCase {
    private final AuthRepository repo;

    public SendEmailVerificationUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute(ResultCallback<Void> cb) {
        repo.sendEmailVerification(cb);
    }
}
