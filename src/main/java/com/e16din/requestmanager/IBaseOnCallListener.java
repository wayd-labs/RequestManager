package com.e16din.requestmanager;

public interface IBaseOnCallListener<T> {

    void beforeResult();

    void onResultNull();

    void onSuccess(T result);

    void onErrorFromServer(T result);

    void onExceptionError(Throwable e, String responseString);

    void onHttpError(int code);

    void afterResult();
}
