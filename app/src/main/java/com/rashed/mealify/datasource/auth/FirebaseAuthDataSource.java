package com.rashed.mealify.datasource.auth;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.common.ResultCallback;
import com.rashed.mealify.domain.model.AuthUser;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthDataSource {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseAuthDataSource() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public void register(String email, String password, String firstName, String lastName,
                         ResultCallback<AuthUser> cb) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        cb.onResult(new Result.Error<>("User is null after register", null));
                        return;
                    }

                    String uid = user.getUid();

                    Map<String, Object> userDoc = new HashMap<>();
                    userDoc.put("uid", uid);
                    userDoc.put("email", email);
                    userDoc.put("firstName", firstName);
                    userDoc.put("lastName", lastName);
                    userDoc.put("createdAt", System.currentTimeMillis());

                    db.collection("users")
                            .document(uid)
                            .set(userDoc, SetOptions.merge())
                            .addOnSuccessListener(unused -> {
                                user.sendEmailVerification()
                                        .addOnSuccessListener(v -> {
                                            cb.onResult(new Result.Success<>(
                                                    new AuthUser(uid, email, firstName, lastName)
                                            ));
                                        })
                                        .addOnFailureListener(e -> {
                                            cb.onResult(new Result.Success<>(
                                                    new AuthUser(uid, email, firstName, lastName)
                                            ));
                                        });
                            })
                            .addOnFailureListener(e ->
                                    cb.onResult(new Result.Error<>("Failed to save user profile", e))
                            );
                })
                .addOnFailureListener(e ->
                        cb.onResult(new Result.Error<>("Register failed", e))
                );
    }

    public void login(String email, String password, ResultCallback<AuthUser> cb) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        cb.onResult(new Result.Error<>("User is null after login", null));
                        return;
                    }

                    user.reload().addOnSuccessListener(unused -> {
                        if (!user.isEmailVerified()) {
                            cb.onResult(new Result.Error<>("Email not verified", new Exception("UNVERIFIED")));
                            return;
                        }
                        fetchProfile(user.getUid(), user.getEmail(), cb);
                    }).addOnFailureListener(e ->
                            cb.onResult(new Result.Error<>("Failed to reload user status", e))
                    );
                })
                .addOnFailureListener(e ->
                        cb.onResult(new Result.Error<>("Login failed", e))
                );
    }

    public void loginWithGoogle(String idToken, ResultCallback<AuthUser> cb) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        cb.onResult(new Result.Error<>("User is null after Google login", null));
                        return;
                    }

                    String uid = user.getUid();
                    String email = user.getEmail() == null ? "" : user.getEmail();

                    String displayName = user.getDisplayName() == null ? "" : user.getDisplayName();
                    String[] parts = displayName.trim().split("\\s+", 2);
                    String firstName = parts.length > 0 ? parts[0] : "";
                    String lastName = parts.length > 1 ? parts[1] : "";

                    Map<String, Object> userDoc = new HashMap<>();
                    userDoc.put("uid", uid);
                    userDoc.put("email", email);

                    userDoc.put("firstName", firstName);
                    userDoc.put("lastName", lastName);


                    db.collection("users")
                            .document(uid)
                            .set(userDoc, SetOptions.merge())
                            .addOnSuccessListener(unused ->
                                    cb.onResult(new Result.Success<>(
                                            new AuthUser(uid, email, firstName, lastName)
                                    ))
                            )
                            .addOnFailureListener(e -> {
                                // FIX: If Firestore fails, still allow login with Google info
                                cb.onResult(new Result.Success<>(
                                        new AuthUser(uid, email, firstName, lastName)
                                ));
                            });

                })
                .addOnFailureListener(e ->
                        cb.onResult(new Result.Error<>("Google login failed", e))
                );
    }

    public void sendEmailVerification(ResultCallback<Void> cb) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            cb.onResult(new Result.Error<>("No logged-in user", null));
            return;
        }

        user.sendEmailVerification()
                .addOnSuccessListener(unused -> cb.onResult(new Result.Success<>(null)))
                .addOnFailureListener(e -> cb.onResult(new Result.Error<>("Failed to send verification email", e)));
    }

    public void checkEmailVerified(ResultCallback<Boolean> cb) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            cb.onResult(new Result.Error<>("No logged-in user", null));
            return;
        }

        user.reload()
                .addOnSuccessListener(unused -> cb.onResult(new Result.Success<>(user.isEmailVerified())))
                .addOnFailureListener(e -> cb.onResult(new Result.Error<>("Failed to reload user", e)));
    }

    public void logout() {
        auth.signOut();
    }

    public void sendPasswordReset(String email, ResultCallback<Void> cb) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> cb.onResult(new Result.Success<>(null)))
                .addOnFailureListener(e -> cb.onResult(new Result.Error<>("Reset password failed", e)));
    }

    public void getCurrentUser(ResultCallback<AuthUser> cb) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            cb.onResult(new Result.Error<>("No logged-in user", null));
            return;
        }
        user.reload().addOnSuccessListener(aVoid -> {
            if(user.isEmailVerified()) {
                fetchProfile(user.getUid(), user.getEmail(), cb);
            } else {
                cb.onResult(new Result.Error<>("User not verified", new Exception("UNVERIFIED")));
            }
        }).addOnFailureListener(e -> cb.onResult(new Result.Error<>("Failed to reload", e)));
    }

    public void updateName(String firstName, String lastName, ResultCallback<AuthUser> cb) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) { cb.onResult(new Result.Error<>("No user", null)); return; }

        Map<String, Object> update = new HashMap<>();
        update.put("firstName", firstName);
        update.put("lastName", lastName);

        db.collection("users").document(user.getUid())
                .set(update, SetOptions.merge())
                .addOnSuccessListener(u -> cb.onResult(new Result.Success<>(new AuthUser(user.getUid(), user.getEmail(), firstName, lastName))))
                .addOnFailureListener(e -> cb.onResult(new Result.Error<>("Failed", e)));
    }

    private void fetchProfile(String uid, String email, ResultCallback<AuthUser> cb) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String firstName = snapshot.getString("firstName");
                        String lastName = snapshot.getString("lastName");
                        cb.onResult(new Result.Success<>(
                                new AuthUser(uid, email == null ? "" : email,
                                        firstName == null ? "" : firstName,
                                        lastName == null ? "" : lastName)
                        ));
                    } else {
                        cb.onResult(new Result.Success<>(new AuthUser(uid, email, "", "")));
                    }
                })
                .addOnFailureListener(e -> {
                    cb.onResult(new Result.Success<>(new AuthUser(uid, email, "", "")));
                });
    }
}