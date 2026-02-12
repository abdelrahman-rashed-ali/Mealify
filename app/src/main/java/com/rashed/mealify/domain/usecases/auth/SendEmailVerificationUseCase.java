package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.domain.repository.AuthRepository;
import io.reactivex.rxjava3.core.Completable;

public class SendEmailVerificationUseCase {
    private final AuthRepository repo;

    public SendEmailVerificationUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public Completable execute() {
        return repo.sendEmailVerification();
    }
}