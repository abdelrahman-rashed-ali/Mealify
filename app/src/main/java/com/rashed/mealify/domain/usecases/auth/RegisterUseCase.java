package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;
import io.reactivex.rxjava3.core.Single;

public class RegisterUseCase {
    private final AuthRepository repo;

    public RegisterUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public Single<AuthUser> execute(String email, String password, String firstName, String lastName) {
        return repo.register(email, password, firstName, lastName);
    }
}