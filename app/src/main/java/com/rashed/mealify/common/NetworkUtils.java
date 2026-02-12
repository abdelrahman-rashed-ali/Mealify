package com.rashed.mealify.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import io.reactivex.rxjava3.core.Single;

public class NetworkUtils {
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        android.net.Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    public static <T> Single<T> checkConnection(Context context, Single<T> remoteSource) {
        return Single.defer(() -> {
            if (!isInternetAvailable(context)) {
                return Single.error(new Exception("No internet connection available."));
            }
            return remoteSource;
        });
    }
}