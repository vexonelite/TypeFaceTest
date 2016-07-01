package com.vexonelite.typefacetest;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

public class MainActivity extends BaseRxActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)  findViewById(R.id.helloWorld);

        View view = findViewById(R.id.builtInTypeface);
        if (null != view) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView.setTypeface(Typeface.DEFAULT);
                }
            });
        }

        view = findViewById(R.id.customTypeFace);
        if (null != view) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator;
                    try {
                        Typeface typeface = Typeface.createFromFile(new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                "Ubuntu_R.ttf"));
                        textView.setTypeface(typeface);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        view = findViewById(R.id.downloadFile);
        if (null != view) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    download();
                }
            });
        }
    }

    private void download () {
        ApiRequestObservable observable = new ApiRequestObservable.Builder()
                .setApiUri("https://dl.dropboxusercontent.com/u/77217798/UbuntuFonts/Ubuntu-R.ttf")
                .build();
        Subscription subscription = Observable.create(observable)
                .map(new OkHttpResponseToJsonString())
                // Run on a background thread
                .subscribeOn(AndroidSchedulers.from(getBackgroundLooper()))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Typeface>() {
                    @Override
                    public void onCompleted() {
                        Log.i(getLogTag(), "onCompleted - Id: " + Thread.currentThread().getId());
                        unsubscribeSubscription();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(getLogTag(), "onError - Id: " + Thread.currentThread().getId());
                        unsubscribeSubscription();
                    }

                    @Override
                    public void onNext(Typeface typeface) {
                        Log.i(getLogTag(), "onNext - Id: " + Thread.currentThread().getId());
                        textView.setTypeface(typeface);
                    }
                });
        subscribeSubscription(subscription);
    }

    public static class ApiRequestObservable implements Observable.OnSubscribe<Response> {

        private String mApiUrl;

        public static class Builder {
            private String bApiUrl;

            public Builder setApiUri (String apiUri) {
                bApiUrl = apiUri;
                return this;
            }

            public ApiRequestObservable build() {
                return new ApiRequestObservable (this);
            }
        }

        private ApiRequestObservable (Builder builder) {
            mApiUrl = builder.bApiUrl;
        }

        @Override
        public void call(Subscriber<? super Response> subscriber) {
            try {
                subscriber.onNext(issueApiRequest () );
                subscriber.onCompleted();
            }
            catch (Throwable e) {
                throw Exceptions.propagate(e);
            }
        }

        private Response issueApiRequest () throws Exception {
            try {
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(mApiUrl)
                        .build();

                OkHttpClient okHttpClient = okHttpUtils.getOkHttpClient();
                okhttp3.Call call = okHttpClient.newCall(request);
                return call.execute();
            }
            catch (Exception e) {
                throw e;
            }
        }
    }

    public static class OkHttpResponseToJsonString implements Func1<Response, Typeface> {

        @Override
        public Typeface call (okhttp3.Response response) {
            try {
                return okHttpResponseToJsonString(response);
            }
            catch (Exception e) {
                throw Exceptions.propagate(e);
            }
        }

        private Typeface okHttpResponseToJsonString (final okhttp3.Response response) throws Exception {

            try {
                File downloadedFile = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "abcdefg.ttf");
                BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                sink.writeAll(response.body().source());
                sink.close();

                return Typeface.createFromFile(new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "abcdefg.ttf"));
            }
            catch (Exception e) {
                throw e;
            }
        }
    }

}
