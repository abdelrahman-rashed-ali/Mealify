package com.rashed.mealify.datasource.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.rashed.mealify.datasource.meals.local.MealsLocalDataSource;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.remote.MealsRemoteDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class SyncRepository {

    private final MealsLocalDataSource localDataSource;
    private final MealsRemoteDataSource remoteDataSource;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public SyncRepository(MealsLocalDataSource localDataSource, MealsRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public Completable syncData() {
        if (auth.getCurrentUser() == null) return Completable.error(new Exception("User not logged in"));
        String uid = auth.getCurrentUser().getUid();

        Completable uploadFavorites = localDataSource.getFavorites(uid)
                .flatMapCompletable(meals -> pushFavoritesToFirebase(uid, meals));


        Completable downloadFavorites = pullFavoritesFromFirebase(uid);

        return Completable.mergeArray(uploadFavorites, downloadFavorites);
    }

    private Completable pushFavoritesToFirebase(String uid, List<MealEntity> meals) {
        return Completable.create(emitter -> {
            WriteBatch batch = db.batch();
            CollectionReference ref = db.collection("users").document(uid).collection("favorites");

            for (MealEntity meal : meals) {
                Map<String, Object> data = new HashMap<>();
                data.put("mealId", meal.idMeal);
                data.put("mealName", meal.strMeal);
                data.put("mealThumb", meal.strMealThumb);
                data.put("savedAt", System.currentTimeMillis());

                batch.set(ref.document(meal.idMeal), data, SetOptions.merge());
            }

            batch.commit()
                    .addOnSuccessListener(unused -> {
                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    });
        });
    }

    private Completable pullFavoritesFromFirebase(String uid) {
        return Single.<List<DocumentSnapshot>>create(emitter -> {
            db.collection("users").document(uid).collection("favorites").get()
                    .addOnSuccessListener(querySnap -> {
                        if (!emitter.isDisposed()) {
                            emitter.onSuccess(querySnap.getDocuments());
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    });
        }).flatMapCompletable(documents -> {
            return Observable.fromIterable(documents)
                    .flatMapCompletable(doc -> {
                        String mealId = doc.getString("mealId");
                        return localDataSource.isFavorite(uid, mealId)
                                .flatMapCompletable(exists -> {

                                    return ensureMealDetailsExist(mealId)
                                            .andThen(localDataSource.addFavorite(uid, new MealEntity(mealId, doc.getString("mealName"), "", "", "", doc.getString("mealThumb"), "", "", "", "", "", "", "", "", 123 )));

                                });
                    });
        });
    }


    private Completable ensureMealDetailsExist(String mealId) {
        return remoteDataSource.lookupMealById(mealId)
                .flatMapCompletable(response -> {
                    if (response.getMeals() != null && !response.getMeals().isEmpty()) {

                        return Completable.complete();
                    }
                    return Completable.complete();
                })
                .onErrorComplete();
    }
}