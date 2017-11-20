package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.util.Constant;
import com.appsubaruod.sharabletobuylist.util.WorkerThread;
import com.appsubaruod.sharabletobuylist.util.messages.ChannelCreatedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by s-yamada on 2017/11/20.
 */

public class ChannelModel {
    private static ChannelModel instance;
    private SharableItemListModel mSharableItemListModel;
    private CountDownLatch mCountDownLatch = new CountDownLatch(1);
    private Map<String, String> mChannelMap = new HashMap<>();

    private static final String LOG_TAG = ChannelModel.class.getName();

    ChannelModel() {
        mSharableItemListModel = SharableItemListModel.getInstanceIfCreated();
        readLocalChannelMapAsync();
    }

    private void readLocalChannelMapAsync() {
        WorkerThread.getSingleExecutor().submit(() -> {
            //TODO readLocalChannelMap();
            mCountDownLatch.countDown();
        });
    }

    public static synchronized ChannelModel getInstance() {
        if (instance == null) {
            instance = new ChannelModel();
        }
        return instance;
    }

    void createChannel(@NonNull String channelName) {
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String newChannelId = mSharableItemListModel.createAndGetUniqueChannel();
        mChannelMap.put(channelName, newChannelId);
        EventBus.getDefault().post(new ChannelCreatedEvent(channelName));
        //TODO storeLocalChannelMap(channelName, newChannelId);
    }

    void changeChannel(@NonNull String channelName) {
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mChannelMap.containsKey(channelName)) {
            mSharableItemListModel.changeRootPath(mChannelMap.get(channelName));
        } else {
            Log.w(LOG_TAG, channelName + " not found. Ignore.");
        }
    }

    void changeToDefaultChannel() {
        mSharableItemListModel.changeToDefaultPath();
    }
}
