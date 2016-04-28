package com.e16din.requestmanager;

public interface Callback<T> {

    void onSuccess(T result, int statusCode);

    void onErrorFromServer(Result result);

    void onExceptionError(Throwable e, String responseString);

    void onHttpError(int code, String message, String body);

    void afterResult(boolean withError);

    boolean needCancel();

    void onCancel();
}
