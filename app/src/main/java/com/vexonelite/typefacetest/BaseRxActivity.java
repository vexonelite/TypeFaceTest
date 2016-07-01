package com.vexonelite.typefacetest;

import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class BaseRxActivity extends AppCompatActivity {

    private static final String BACKGROUND_THREAD_NAME = "_background_thread";

    private Looper backgroundLooper;
    private HandlerThread backgroundThread;
    private CompositeSubscription mCompositeSubscription;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBackgroundThreadIfNeeded();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        unsubscribeSubscription();

        resetBackgroundThreadIfNeeded();
    }

    protected String getLogTag() {
        return this.getClass().getSimpleName();
    }

    private void initBackgroundThreadIfNeeded () {
        if (null == backgroundThread) {
            backgroundThread = new HandlerThread(
                    (getLogTag() + BACKGROUND_THREAD_NAME), THREAD_PRIORITY_BACKGROUND);
            backgroundThread.start();
            backgroundLooper = backgroundThread.getLooper();
        }
    }

    private void resetBackgroundThreadIfNeeded () {
        if (null != backgroundThread) {
            try {
                backgroundThread.quit();
                backgroundThread = null;
            } catch (Exception e) {
                Log.w(getLogTag(), "Exception workerThread quit!");
                e.printStackTrace();
            }
        }
        if (null != backgroundLooper) {
            try {
                backgroundLooper.quit();
                backgroundLooper = null;
            } catch (Exception e) {
                Log.w(getLogTag(), "Exception backgroundLooper.quit!");
                e.printStackTrace();
            }
        }
    }

    protected Looper getBackgroundLooper () {
        return backgroundLooper;
    }

    protected void subscribeSubscription (Subscription subscription) {
        unsubscribeSubscription();
        if (null != subscription) {
            mCompositeSubscription = new CompositeSubscription();
            mCompositeSubscription.add(subscription);
        }
    }

    protected void unsubscribeSubscription () {
        if (null != mCompositeSubscription) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }
}
