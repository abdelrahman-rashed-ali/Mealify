package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.domain.repository.AuthRepository;
import io.reactivex.rxjava3.core.Single;

public class CheckEmailVerifiedUseCase {
    private final AuthRepository repo;

    public CheckEmailVerifiedUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public Single<Boolean> execute() {
        return repo.checkEmailVerified();
    }
}