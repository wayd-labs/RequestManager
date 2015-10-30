package com.e16din.requestmanager.retrofit;


import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class StaticErrorHandler implements ErrorHandler {

    private static RetrofitError lastRetrofitError;

    public static RetrofitError getLastRetrofitError() {
        return lastRetrofitError;
    }

    public static void setLastRetrofitError(RetrofitError lastRetrofitError) {
        StaticErrorHandler.lastRetrofitError = lastRetrofitError;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        lastRetrofitError = cause;
        return cause;
    }
}