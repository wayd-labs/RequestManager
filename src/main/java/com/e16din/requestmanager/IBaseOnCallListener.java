package com.e16din.requestmanager;

public interface IBaseOnCallListener<T> {

    void beforeResult();

    void onSuccess(T result, int statusCode);

    void onErrorFromServer(IBaseResult result);

    void onExceptionError(Throwable e, String responseString);

    void onHttpError(int code, String message, String body);

    void afterResult(boolean withError);

    boolean needCancel();

    void onCancel();
}
