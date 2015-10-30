package com.e16din.requestmanager.retrofit;

import com.e16din.lightutils.utils.U;

public class BaseRetrofitAdapter {
    private static String cookies;

    public static String getCookies() {
        return cookies;
    }

    public static void setCookies(String cookies) {
        U.d(BaseRetrofitAdapter.class, "setCookies: " + cookies);
        BaseRetrofitAdapter.cookies = cookies;
    }
}
