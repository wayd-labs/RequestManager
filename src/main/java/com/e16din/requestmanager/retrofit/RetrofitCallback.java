package com.e16din.requestmanager.retrofit;

import android.text.TextUtils;
import android.view.WindowManager;

import com.e16din.lightutils.utils.U;
import com.e16din.requestmanager.BaseOnCallListener;
import com.e16din.requestmanager.IBaseResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public abstract class RetrofitCallback<T extends IBaseResult> extends BaseOnCallListener<T>
        implements Callback<T> {

    public static final int HTTP_ERROR_BAD_REQUEST = 400;

    @Override
    public void success(T result, Response response) {
        final String cookies = getCookieString(response);
        if (!TextUtils.isEmpty(cookies))
            BaseRetrofitAdapter.setCookies(cookies);

        if (needCancel()) {
            onCancel();
            return;
        }

        beforeResult();

        if (StaticErrorHandler.getLastRetrofitError() != null) {
            onHttpError(StaticErrorHandler.getLastRetrofitError().getResponse().getStatus());
            StaticErrorHandler.setLastRetrofitError(null);
            afterResult();
            return;
        }

        if (StaticErrorHandler.getLastRetrofitError() != null) {
            onExceptionError(StaticErrorHandler.getLastRetrofitError().getCause(),
                    StaticErrorHandler.getLastRetrofitError().getMessage());
            StaticErrorHandler.setLastRetrofitError(null);
            afterResult();
            return;
        }

        try {
            if (result == null) {
                onSuccess(null);
            } else if ((result + "").startsWith("[") && (result + "").length() <= 3) {
                onSuccess(null);
            } else if (result.isSuccess()) {
                onSuccess(result);
            } else {
                onErrorFromServer(result);//rename to onErrorCode
            }
        } catch (NullPointerException | WindowManager.BadTokenException | IllegalStateException e) {
            logExceptionOnSuccess(e);
        }

        afterResult();
        StaticErrorHandler.setLastRetrofitError(null);
    }

    private void logExceptionOnSuccess(Exception e) {
        U.e(getClass(), "error caused when activity or fragment " +
                "becomes inactive before call the methods: " + e.getMessage());
    }

    @Override
    public void failure(RetrofitError error) {
        if (needCancel()) {
            onCancel();
            return;
        }
        
        beforeResult();

        U.e(getClass(), "error: " + error.getMessage());

        if (error.getMessage() != null && (error.getMessage().contains("java.io.EOFException")
                || error.getMessage().contains("400 Bad Request"))) {
            onHttpError(HTTP_ERROR_BAD_REQUEST);
            afterResult();
            return;
        }

        onExceptionError(error.getCause(), error.getMessage());
        StaticErrorHandler.setLastRetrofitError(null);
        afterResult();
    }

    private String getCookieString(Response response) {
        for (Header header : response.getHeaders()) {
            if (header.getName() != null && header.getName().equals("Set-Cookie")) {
                U.d(getClass(), "Set-Cookie: " + header.getValue());
                return header.getValue();
            }
        }
        return null;
    }
}
