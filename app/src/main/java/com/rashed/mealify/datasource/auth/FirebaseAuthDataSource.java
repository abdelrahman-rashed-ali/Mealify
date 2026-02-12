package com.rashed.mealify.datasource.auth;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rashed.mealify.domain.model.AuthUser;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class FirebaseAuthDataSource {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseAuthDataSource() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public Single<AuthUser> register(String email, String password, String firstName, String lastName) {
        return Single.create(emitter -> {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(result -> {
                        FirebaseUser user = result.getUser();
                        if (user == null) {
                            emitter.onError(new Exception("User is null after register"));
                            return;
                        }

                        String uid = user.getUid();
                        Map<String, Object> userDoc = new HashMap<>();
                        userDoc.put("uid", uid);
                        userDoc.put("email", email);
                        userDoc.put("firstName", firstName);
                        userDoc.put("lastName", lastName);
                        userDoc.put("createdAt", System.currentTimeMillis());

                        db.collection("users").document(uid).set(userDoc, SetOptions.merge())
                                .addOnSuccessListener(unused -> {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(task ->
                                                    emitter.onSuccess(new AuthUser(uid, email, firstName, lastName)));
                                })
                                .addOnFailureListener(emitter::onError);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<AuthUser> login(String email, String password) {
        return Single.create(emitter -> {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(result -> {
                        FirebaseUser user = result.getUser();
                        if (user == null) {
                            emitter.onError(new Exception("User is null after login"));
                            return;
                        }

                        user.reload().addOnSuccessListener(unused -> {
                            if (!user.isEmailVerified()) {
                                emitter.onError(new Exception("UNVERIFIED"));
                                return;
                            }
                            fetchProfile(user.getUid(), user.getEmail()).subscribe(emitter::onSuccess, emitter::onError);
                        }).addOnFailureListener(emitter::onError);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<AuthUser> loginWithGoogle(String idToken) {
        return Single.create(emitter -> {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(credential)
                    .addOnSuccessListener(result -> {
                        FirebaseUser user = result.getUser();
                        if (user == null) {
                            emitter.onError(new Exception("User is null after Google login"));
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

                        db.collection("users").document(uid).set(userDoc, SetOptions.merge())
                                .addOnCompleteListener(task ->
                                        emitter.onSuccess(new AuthUser(uid, email, firstName, lastName)));
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable sendEmailVerification() {
        return Completable.create(emitter -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                emitter.onError(new Exception("No logged-in user"));
                return;
            }
            user.sendEmailVerification()
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<Boolean> checkEmailVerified() {
        return Single.create(emitter -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                emitter.onError(new Exception("No logged-in user"));
                return;
            }
            user.reload()
                    .addOnSuccessListener(unused -> emitter.onSuccess(user.isEmailVerified()))
                    .addOnFailureListener(emitter::onError);
        });
    }

    public void logout() {
        auth.signOut();
    }

    public Completable sendPasswordReset(String email) {
        return Completable.create(emitter -> {
            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<AuthUser> getCurrentUser() {
        return Single.create(emitter -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                emitter.onError(new Exception("No logged-in user"));
                return;
            }
            user.reload().addOnSuccessListener(aVoid -> {
                if (user.isEmailVerified()) {
                    fetchProfile(user.getUid(), user.getEmail()).subscribe(emitter::onSuccess, emitter::onError);
                } else {
                    emitter.onError(new Exception("UNVERIFIED"));
                }
            }).addOnFailureListener(emitter::onError);
        });
    }

    public Single<AuthUser> updateName(String firstName, String lastName) {
        return Single.create(emitter -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                emitter.onError(new Exception("No user"));
                return;
            }
            Map<String, Object> update = new HashMap<>();
            update.put("firstName", firstName);
            update.put("lastName", lastName);

            db.collection("users").document(user.getUid())
                    .set(update, SetOptions.merge())
                    .addOnSuccessListener(u -> emitter.onSuccess(new AuthUser(user.getUid(), user.getEmail(), firstName, lastName)))
                    .addOnFailureListener(emitter::onError);
        });
    }

    private Single<AuthUser> fetchProfile(String uid, String email) {
        return Single.create(emitter -> {
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            emitter.onSuccess(new AuthUser(uid,
                                    email == null ? "" : email,
                                    snapshot.getString("firstName") == null ? "" : snapshot.getString("firstName"),
                                    snapshot.getString("lastName") == null ? "" : snapshot.getString("lastName")));
                        } else {
                            emitter.onSuccess(new AuthUser(uid, email, "", ""));
                        }
                    })
                    .addOnFailureListener(e -> emitter.onSuccess(new AuthUser(uid, email, "", "")));
        });
    }

    public Single<AuthUser> loginAnonymously() {
        return Single.create(emitter -> {
            auth.signInAnonymously()
                    .addOnSuccessListener(result -> {
                        FirebaseUser user = result.getUser();
                        if (user == null) {
                            emitter.onError(new Exception("User is null after anonymous login"));
                            return;
                        }
                        String uid = user.getUid();
                        Map<String, Object> userDoc = new HashMap<>();
                        userDoc.put("uid", uid);
                        userDoc.put("firstName", "Guest");
                        userDoc.put("lastName", "User");
                        userDoc.put("email", "");
                        userDoc.put("isAnonymous", true);
                        userDoc.put("createdAt", System.currentTimeMillis());

                        db.collection("users").document(uid).set(userDoc, SetOptions.merge())
                                .addOnCompleteListener(task ->
                                        emitter.onSuccess(new AuthUser(uid, "", "Guest", "User")));
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
}