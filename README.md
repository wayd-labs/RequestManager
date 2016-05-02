# RequestManager

## Callback
```java
public interface Callback<T> {

    void onSuccess(T result, int statusCode); // start on success result

    void onErrorFromServer(Result result); // start when Result.isSuccess() == false

    void onExceptionError(Throwable e, String responseString); // start on any exception 

    void onHttpError(int code, String message, String body);// start on http error

    void afterResult(boolean withError); // start after all (if needCancel() == false)

    boolean needCancel();//set true when you need to cancel other methods

    void onCancel();//start when needCancel() == true
}
```

## Download (Gradle)

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

buildscript {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

dependencies {
	compile 'com.github.e16din:RequestManager:1.2.5'
}
```