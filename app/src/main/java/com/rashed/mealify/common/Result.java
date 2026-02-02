package com.rashed.mealify.common;

public abstract class Result<T> {

    private Result() {}

    public static final class Success<T> extends Result<T> {
        public final T data;
        public Success(T data) { this.data = data; }
    }

    public static final class Error<T> extends Result<T> {
        public final String message;
        public final Throwable throwable;
        public Error(String message, Throwable throwable) {
            this.message = message;
            this.throwable = throwable;
        }
    }
}
