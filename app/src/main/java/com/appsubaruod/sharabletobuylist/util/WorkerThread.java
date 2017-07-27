package com.appsubaruod.sharabletobuylist.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by s-yamada on 2017/07/18.
 */

public class WorkerThread {
    private static ExecutorService mSingleExecutor;

    public static ExecutorService getSingleExecutor() {
        if (mSingleExecutor == null) {
            mSingleExecutor = Executors.newSingleThreadExecutor();
        }
        return mSingleExecutor;
    }
}
