package com.appsubaruod.sharabletobuylist.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by s-yamada on 2017/07/18.
 */

public class WorkerThread {
    private static ExecutorService mSingleExecutor;
    private static ScheduledExecutorService mNotificationTaskExecutor;

    public static synchronized ExecutorService getSingleExecutor() {
        if (mSingleExecutor == null) {
            mSingleExecutor = Executors.newSingleThreadExecutor();
        }
        return mSingleExecutor;
    }

    public static synchronized ScheduledExecutorService getNotificationTaskExecutor() {
        if (mNotificationTaskExecutor == null) {
            mNotificationTaskExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        return mNotificationTaskExecutor;
    }
}
