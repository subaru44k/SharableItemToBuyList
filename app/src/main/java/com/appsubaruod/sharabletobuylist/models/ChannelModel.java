package com.appsubaruod.sharabletobuylist.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.storage.UserDataStorage;
import com.appsubaruod.sharabletobuylist.storage.userdata.LocalUserDataStorage;
import com.appsubaruod.sharabletobuylist.util.WorkerThread;
import com.appsubaruod.sharabletobuylist.util.messages.ChannelAddedEvent;
import com.appsubaruod.sharabletobuylist.util.messages.MultipleChannelAddedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by s-yamada on 2017/11/20.
 */

public class ChannelModel {
    private static ChannelModel instance;
    private CountDownLatch mCountDownLatch = new CountDownLatch(1);
    private Map<String, String> mChannelMap = new HashMap<>();
    private UserDataStorage mUserDataStorage;

    private static final String LOG_TAG = ChannelModel.class.getName();

    private ChannelModel(Context context) {
        mUserDataStorage = new LocalUserDataStorage(context);
    }

    private void readLocalChannelMapAsync() {
        WorkerThread.getSingleExecutor().submit(() -> {
            mChannelMap = mUserDataStorage.readChannelData();
            // Send multiple channel with sticky event.
            // Since Activity registers its instance to EventBus when onStart() is called,
            // and readLocalChannelMapAsync method is called onCreate(),
            // read local channel could be faster than receiver side's gets ready.
            // So the system should send sticky event here.
            // Also we should note sticky event will be overwritten when another sticky event is sent.
            // Therefore the system should gather some information to one event.
            EventBus.getDefault().postSticky(new MultipleChannelAddedEvent(mChannelMap.keySet()));
            mCountDownLatch.countDown();
        });
    }

    private void writeLocalChannelMapAsync(Map<String, String> channelMap) {
        WorkerThread.getSingleExecutor().submit(() -> mUserDataStorage.writeChannelData(channelMap));
    }

    static synchronized ChannelModel getInstance(Context context) {
        if (instance == null) {
            instance = new ChannelModel(context);
        }
        instance.readLocalChannelMapAsync();
        return instance;
    }

    static synchronized ChannelModel getInstanceIfCreated() {
        if (instance == null) {
            throw new IllegalStateException("ChannelModel is not instantiated");
        }
        return instance;
    }

    void createChannelIfNotExist(@NonNull String channelName) {
        if (mChannelMap.containsKey(channelName)) {
            return;
        }
        try {
            if (mCountDownLatch.await(3, TimeUnit.SECONDS) == false) {
                Log.w(LOG_TAG, "Create channel before reading local channel map since its timeout!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String newChannelId =
                SharableItemListModel.getInstanceIfCreated().createAndGetUniqueChannel();
        addChannelIfNotExist(channelName, newChannelId);
    }

    void addChannelIfNotExist(@NonNull String channelName, String newChannelId) {
        if (mChannelMap.containsKey(channelName)) {
            return;
        }
        mChannelMap.put(channelName, newChannelId);
        EventBus.getDefault().post(new ChannelAddedEvent(channelName));
        writeLocalChannelMapAsync(mChannelMap);
    }

    void changeChannel(@NonNull String channelName) {
        try {
            if (mCountDownLatch.await(3, TimeUnit.SECONDS) == false) {
                Log.w(LOG_TAG, "Change channel before reading local channel map since its timeout!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mChannelMap.containsKey(channelName)) {
            SharableItemListModel.getInstanceIfCreated()
                    .changeRootPath(mChannelMap.get(channelName));
        } else {
            Log.w(LOG_TAG, channelName + " not found. Ignore.");
        }
    }

    void changeToDefaultChannel() {
        SharableItemListModel.getInstanceIfCreated().changeToDefaultPath();
    }

    List<String> getChannelList() {
        return new ArrayList<>(mChannelMap.keySet());
    }

    public String getFirebaseId(@NonNull String channelName) {
        if (mChannelMap != null) {
            return mChannelMap.get(channelName);
        } else {
            return "";
        }
    }
}
