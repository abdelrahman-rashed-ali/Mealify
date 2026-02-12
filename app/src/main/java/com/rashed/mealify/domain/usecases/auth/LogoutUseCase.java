package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.domain.repository.AuthRepository;

public class LogoutUseCase {
    private final AuthRepository repo;

    public LogoutUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute() {
        repo.logout();
    }
}