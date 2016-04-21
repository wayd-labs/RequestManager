package com.e16din.requestmanager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

public abstract class BaseOnCallListener<T> implements IBaseOnCallListener<T> {

    public static final String LOG_TAG = "RequestManager";

    public static final String KEY_NO_INTERNET_CONNECTION = "no_internet_connection";

    public abstract Context getContext();

    @Override
    public void onExceptionError(Throwable exception, String responseString) {
        String message;
        if (exception != null)
            Log.e(LOG_TAG, "onExceptionError: " + exception.getMessage());

        if (exception instanceof JsonSyntaxException) {
            message = "Получены данные неверного формата";
        } else if (exception instanceof SSLException) {
            message = KEY_NO_INTERNET_CONNECTION;
        } else if (exception instanceof SocketTimeoutException || exception instanceof ConnectTimeoutException) {
            message = KEY_NO_INTERNET_CONNECTION;
        } else if (exception instanceof UnknownHostException) {
            message = KEY_NO_INTERNET_CONNECTION;
        } else if (exception instanceof ConnectException) {
            message = KEY_NO_INTERNET_CONNECTION;
        } else
            message = String.format("Неизвестное исключение: %1$s", exception != null ? exception.getMessage() : null);

        showErrorAlert(message);
    }

    @Override
    public void afterResult(boolean withError) {
    }

    @Override
    public void beforeResult() {
    }

    protected void showErrorAlert(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        // AlertManager.manager(getContext()).showErrorAlert(message);
    }
}
