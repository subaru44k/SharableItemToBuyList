package com.appsubaruod.sharabletobuylist.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.views.activities.MainActivity;

import java.util.concurrent.ScheduledExecutorService;
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

    public NotificationTaskCoordinator(Context context) {
        mContext = context;
    }

    public boolean requestAddedNotification(String itemName) {
        getScheduledExecutorService().schedule(() -> {
            Notification.Builder builder = getNotificationBuilder().setContentText("Added : " + itemName);

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
            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, builder.build());

        }, 1, TimeUnit.SECONDS);

        return true;
    }

    public boolean requestModifiedNotification(String oldItemName, String newItemName) {

        getScheduledExecutorService().schedule(() -> {
            Notification.Builder builder = getNotificationBuilder().setContentText("Modified : " + oldItemName + " -> " + newItemName);

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
            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, builder.build());

        }, 1, TimeUnit.SECONDS);
        return true;
    }

    public boolean requestDeletedNotification(String itemName) {
        getScheduledExecutorService().schedule(() -> {
            Notification.Builder builder = getNotificationBuilder().setContentText("Deleted : " + itemName);

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
            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, builder.build());

        }, 1, TimeUnit.SECONDS);

        return true;
    }

    public boolean requestCompletedNotification(String itemName) {
        getScheduledExecutorService().schedule(() -> {
            Notification.Builder builder = getNotificationBuilder().setContentText("Completed : " + itemName);

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
            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, builder.build());

        }, 1, TimeUnit.SECONDS);

        return true;
    }

    private Notification.Builder getNotificationBuilder() {
        Notification.Builder mBuilder = new Notification.Builder(mContext)
                .setContentTitle("Sharable item list")
                .setSmallIcon(R.drawable.ic_add_item);
        return mBuilder;
    }

    private ScheduledExecutorService getScheduledExecutorService() {
        return WorkerThread.getNotificationTaskExecutor();
    }
}
