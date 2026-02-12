package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.domain.repository.AuthRepository;
import io.reactivex.rxjava3.core.Completable;

public class SendPasswordResetUseCase {
    private final AuthRepository repo;

    public SendPasswordResetUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public Completable execute(String email) {
        return repo.sendPasswordReset(email);
    }
}