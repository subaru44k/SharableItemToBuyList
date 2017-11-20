package com.appsubaruod.sharabletobuylist.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.views.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by s-yamada on 2017/11/18.
 * This class coordinates notification of Android Device.
 *
 * Continuous add/delete/complete events annoy application user.
 * This class waits for some time and coordinates multiple notification request.
 * Also, it modifies old notification when that item is operated again.
 * E.g. Add "ItemA" and later delete "ItemA" then user cannot observe "ItemA".
 * In this case notification of "ItemA is added" should be deleted.
 */
public class NotificationTaskCoordinator {
    private Context mContext;
    private NotificationManager mNotificationManager;
    private int mNotificationId = 1;
    private Map<String, List<OperationType>> mNotifyOperation = new HashMap<>();
    private ScheduledFuture mFuture;

    public NotificationTaskCoordinator(Context context) {
        mContext = context;
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void cancelNotification() {
        mNotificationManager.cancelAll();
        changeNotificationId();
        clearOperations();
    }

    private void clearOperations() {
        mNotifyOperation = new HashMap<>();
    }

    private void changeNotificationId() {
        mNotificationId++;
    }

    private int getNotificationId() {
        return mNotificationId;
    }

    private void putNotificationDetail(OperationType type, String itemName) {
        if (!mNotifyOperation.containsKey(itemName)) {
            mNotifyOperation.put(itemName, new ArrayList<>());
        }
        List<OperationType> operations = mNotifyOperation.get(itemName);
        operations.add(type);
    }

    public String getSequenceOfModification() {
        if (mNotifyOperation == null) {
            return null;
        }
        StringBuilder modification = new StringBuilder();
        mNotifyOperation.entrySet().forEach(entry -> {
            modification.append(entry.getKey() + " " + interpretOperations(entry.getValue()));
            modification.append(".");
        });

        // eliminate final .
        if (modification.length() != 0) {
            modification.deleteCharAt(modification.length() - 1);
        }
        return modification.toString();
    }

    private String interpretOperations(List<OperationType> operations) {
        StringBuilder builder = new StringBuilder();
        operations.forEach(operation -> {
            switch (operation) {
                case ITEM_ADDED:
                    builder.append("is added");
                    break;
                case ITEM_DELETED:
                    builder.append("is deleted");
                    break;
                case ITEM_COMPLETED:
                    builder.append("is completed");
                    break;
                default:
                    builder.append("unknown operation");
            }
        });
        return builder.toString();
    }

    public int getSmallIcon() {
        return R.drawable.ic_launcher_background;
    }

    private void createNotificationAsync() {
        if (mFuture != null && !mFuture.isDone()) {
            mFuture.cancel(true);
        }

        mFuture = getScheduledExecutorService().schedule(() -> {
            String contentText = getSequenceOfModification();
            if (contentText == "") {
                return;
            }
            Notification.Builder builder = getNotificationBuilder().setContentText(contentText);

            Intent resultIntent = new Intent(mContext, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            builder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(getNotificationId(), builder.build());

        }, 1, TimeUnit.SECONDS);
    }

    public void requestAddedNotification(String itemName) {
        putNotificationDetail(OperationType.ITEM_ADDED, itemName);
        createNotificationAsync();
    }

    public void requestModifiedNotification(String oldItemName, String newItemName) {
        putNotificationDetail(OperationType.ITEM_DELETED, oldItemName);
        putNotificationDetail(OperationType.ITEM_ADDED, newItemName);
        createNotificationAsync();
    }

    public void requestDeletedNotification(String itemName) {
        putNotificationDetail(OperationType.ITEM_DELETED, itemName);
        createNotificationAsync();
    }

    public void requestCompletedNotification(String itemName) {
        putNotificationDetail(OperationType.ITEM_COMPLETED, itemName);
        createNotificationAsync();
    }

    private Notification.Builder getNotificationBuilder() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromNotification", true);
        Notification.Builder mBuilder = new Notification.Builder(mContext)
                .setAutoCancel(true)
                .setContentTitle("Sharable item list")
                .setSmallIcon(getSmallIcon())
                .setStyle(new Notification.BigTextStyle());
        return mBuilder;
    }

    private ScheduledExecutorService getScheduledExecutorService() {
        return WorkerThread.getNotificationTaskExecutor();
    }

    enum OperationType {
        ITEM_ADDED,
        ITEM_DELETED,
        ITEM_COMPLETED
    }
}
