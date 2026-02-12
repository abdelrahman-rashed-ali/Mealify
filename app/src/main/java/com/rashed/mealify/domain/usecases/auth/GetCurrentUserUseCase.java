package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;
import io.reactivex.rxjava3.core.Single;

public class GetCurrentUserUseCase {
    private final AuthRepository repo;

    public GetCurrentUserUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public Single<AuthUser> execute() {
        return repo.getCurrentUser();
    }
}