package com.rashed.mealify.domain.usecases.auth;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;

public class RegisterUseCase {

    private final AuthRepository repo;

    public RegisterUseCase(AuthRepository repo) {
        this.repo = repo;
    }

    public void execute(String email, String password, String firstName, String lastName,
                        ResultCallback<AuthUser> cb) {
        repo.register(email, password, firstName, lastName, cb);
    }
}
