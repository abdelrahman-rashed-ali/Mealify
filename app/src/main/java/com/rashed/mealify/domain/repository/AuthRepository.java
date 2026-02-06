package com.rashed.mealify.domain.repository;

import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.model.AuthUser;

public interface AuthRepository {
    void register(String email, String password, String firstName, String lastName, ResultCallback<AuthUser> cb);
    void login(String email, String password, ResultCallback<AuthUser> cb);
    void loginWithGoogle(String idToken, ResultCallback<AuthUser> cb);
    void logout();
    void sendPasswordReset(String email, ResultCallback<Void> cb);
    void getCurrentUser(ResultCallback<AuthUser> cb);
    void updateName(String firstName, String lastName, ResultCallback<AuthUser> cb);
    void sendEmailVerification(ResultCallback<Void> cb);
    void checkEmailVerified(ResultCallback<Boolean> cb);

}
