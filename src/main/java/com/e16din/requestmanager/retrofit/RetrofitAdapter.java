package com.e16din.requestmanager.retrofit;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by e16din on 20.08.15.
 */
public class RetrofitAdapter extends BaseRetrofitAdapter {

    public static Object getService(Class requestManagerInterface, String endpoint, Map<String, String> headers) {
        return getService(requestManagerInterface, endpoint, headers, new GsonBuilder().create());
    }

    public static Object getService(Class requestManagerInterface, String endpoint, Map<String, String> headers, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setErrorHandler(new StaticErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(getRequestInterceptor(headers))
                .setConverter(new GsonConverter(gson))
                .build()
                .create(requestManagerInterface);
    }

    @NonNull
    private static RequestInterceptor getRequestInterceptor(final Map<String, String> headers) {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                for (String key : headers.keySet()) {
                    request.addHeader(key, headers.get(key));
                }

                if (!TextUtils.isEmpty(getCookies()))
                    request.addHeader("Cookie", getCookies());
            }
        };
    }
}
