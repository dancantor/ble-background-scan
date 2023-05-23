package com.ubb.bachelor.blebackgroundscan.domain.service;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ubb.bachelor.blebackgroundscan.R;
import com.ubb.bachelor.blebackgroundscan.data.repository.ScanResultRepository;
import com.ubb.bachelor.blebackgroundscan.domain.exception.DeviceScannerNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.exception.NotificationServiceNotInstantiated;
import com.ubb.bachelor.blebackgroundscan.domain.exception.ThreatServiceNotInstantiated;

public class NotificationService {
    private static NotificationService instance;
    private NotificationManager notificationManager;
    private Context context;

    private NotificationService(Context context) {
        this.context = context;
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
    }

    public static NotificationService getInstance(Context context) {
        if (NotificationService.instance == null) {
            NotificationService.instance = new NotificationService(context);
        }
        return NotificationService.instance;
    }

    public static NotificationService getInstanceIfAvailable() throws NotificationServiceNotInstantiated {
        if (NotificationService.instance == null) {
            throw new NotificationServiceNotInstantiated("ThreatService was not instantiated");
        }
        return NotificationService.instance;
    }

    @NonNull
    public Notification createNotification(
            String id, String title, boolean onGoing, String channelId, int icon
    ) {
        // Build a notification using bytesRead and contentLength
        Notification notification;
        Intent intent = null;
        try {
            intent = new Intent(context, Class.forName("com.ubb.bletracker.MainActivity"));
        } catch (ClassNotFoundException e) {
            Log.e("NotificationService", "Main activity not found");
        }

        notification = new NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setTicker(title)
                .setOngoing(onGoing)
                .setChannelId(channelId)
                .setSmallIcon(icon)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE))
                .build();

        return notification;
    }

    public void createChannel(CharSequence name, String description, int importance, String id) {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(int id, Notification notification) {
        notificationManager.notify(id, notification);
    }

    public void removeNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

}
