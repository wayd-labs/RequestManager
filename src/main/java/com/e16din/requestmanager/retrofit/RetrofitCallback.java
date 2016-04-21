package com.e16din.requestmanager.retrofit;

import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.e16din.requestmanager.BaseOnCallListener;
import com.e16din.requestmanager.IBaseResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public abstract class RetrofitCallback<T extends IBaseResult> extends BaseOnCallListener<T>
        implements Callback<T> {

    public static final String LOG_TAG = "RequestManager";

    public static final int HTTP_ERROR_BAD_REQUEST = 400;

    private boolean withError = false;

    @Override
    public void success(T result, Response response) {
        final String cookies = getCookieString(response);
        if (!TextUtils.isEmpty(cookies))
            BaseRetrofitAdapter.setCookies(cookies);

        if (needCancel()) {
            Log.w(LOG_TAG, "success: operation canceled!");
            onCancel();
            return;
        }

        withError = false;
        beforeResult();

        if (StaticErrorHandler.getLastRetrofitError() != null) {
            withError = true;

            final Response handledResponse = StaticErrorHandler.getLastRetrofitError().getResponse();
            if (handledResponse != null
                    && handledResponse.getStatus() != 0) {
                onHttpError(handledResponse.getStatus(),
                        StaticErrorHandler.getLastRetrofitError().getMessage(),
                        new String(((TypedByteArray) StaticErrorHandler.getLastRetrofitError().getResponse().getBody()).getBytes()));
            } else {
                onExceptionError(StaticErrorHandler.getLastRetrofitError().getCause(),
                        StaticErrorHandler.getLastRetrofitError().getMessage());
            }

            StaticErrorHandler.setLastRetrofitError(null);
            afterResult(withError);
            return;
        }

        try {
            if (result == null) {
                onSuccess(null, response.getStatus());
            } else if ((result + "").startsWith("[") && (result + "").length() <= 3) {
                onSuccess(null, response.getStatus());
            } else if (result.isSuccess()) {
                onSuccess(result, response.getStatus());
            } else {
                withError = true;
                onErrorFromServer(result);//rename to onErrorCode
            }
        } catch (NullPointerException | WindowManager.BadTokenException | IllegalStateException e) {
            logExceptionOnSuccess(e);
        }

        afterResult(withError);
        StaticErrorHandler.setLastRetrofitError(null);
    }

    private void logExceptionOnSuccess(Exception e) {
        Log.e(LOG_TAG, "error caused when activity or fragment " +
                "becomes inactive before call the methods: " + e.getMessage() + " or trivial NPE");
    }

    @Override
    public void failure(RetrofitError error) {
        if (needCancel()) {
            onCancel();
            return;
        }

        withError = true;
        beforeResult();

        if (StaticErrorHandler.getLastRetrofitError() != null) {
            if (StaticErrorHandler.getLastRetrofitError().getResponse() != null
                    && StaticErrorHandler.getLastRetrofitError().getResponse().getStatus() != 0) {
                onHttpError(StaticErrorHandler.getLastRetrofitError().getResponse().getStatus(),
                        StaticErrorHandler.getLastRetrofitError().getMessage(),
                        new String(((TypedByteArray) StaticErrorHandler.getLastRetrofitError().getResponse().getBody()).getBytes()));
            } else {
                onExceptionError(StaticErrorHandler.getLastRetrofitError().getCause(),
                        StaticErrorHandler.getLastRetrofitError().getMessage());
            }

            StaticErrorHandler.setLastRetrofitError(null);
            afterResult(withError);
            return;
        }

        Log.e(LOG_TAG, "error: " + error.getMessage());

        if (error.getMessage() != null && (error.getMessage().contains("java.io.EOFException")
                || error.getMessage().contains("400 Bad Request"))) {
            onHttpError(HTTP_ERROR_BAD_REQUEST, error.getMessage(), null);
            afterResult(withError);
            return;
        }

        onExceptionError(error.getCause(), error.getMessage());
        StaticErrorHandler.setLastRetrofitError(null);
        afterResult(withError);
    }

    private String getCookieString(Response response) {
        for (Header header : response.getHeaders()) {
            if (header.getName() != null && header.getName().equals("Set-Cookie")) {
                Log.d(LOG_TAG, "Set-Cookie: " + header.getValue());
                return header.getValue();
            }
        }
        return null;
    }
}
