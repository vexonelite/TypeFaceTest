package com.vexonelite.typefacetest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class okHttpUtils {

    public static final long CONNECTION_TIME = 10000;
    public static final long READ_TIMEOUT = 120000;
    public static final long WRITE_TIMEOUT = 120000;

    public static OkHttpClient getOkHttpClient () {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECTION_TIME, TimeUnit.MILLISECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        return builder.build();
    }





}
