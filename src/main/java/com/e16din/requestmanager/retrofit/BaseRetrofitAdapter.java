package com.e16din.requestmanager.retrofit;

public class BaseRetrofitAdapter {
    private static String cookies;

    public static String getCookies() {
        return cookies;
    }

    public static void setCookies(String cookies) {
        BaseRetrofitAdapter.cookies = cookies;
    }
}
