package com.outsidecontextproblem.batteryclock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;

import androidx.annotation.Nullable;

public class BatteryClockWidgetService extends Service implements Runnable {

    private static final String NOTIFICATION_CHANNEL_ID = "com.outsidecontextproblem.batteryclock";

    private static final int NOTIFICATION_ID = 123; //824954302;

    private NotificationManager _notificationManager;

    private Notification.Builder _notificationBuilder;

    private Handler _handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        NotificationChannel notificationChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW
        );

        _notificationManager = getSystemService((NotificationManager.class));

        _notificationManager.createNotificationChannel(notificationChannel);

        _notificationBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentText("Badger")
                .setSmallIcon(R.drawable.ic_launcher_background);

        startForeground(NOTIFICATION_ID, _notificationBuilder.build());

//        if (_handler == null) {
//            _handler = new Handler();
//
//            _handler.removeCallbacks(this);
//            _handler.postDelayed(this, DateUtils.MINUTE_IN_MILLIS - System.currentTimeMillis() % DateUtils.MINUTE_IN_MILLIS);
//        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        Intent intent = new Intent(getApplicationContext(), BatteryClockWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);



        getApplicationContext().sendBroadcast(intent);

        _handler.removeCallbacks(this);
        _handler.postDelayed(this, DateUtils.MINUTE_IN_MILLIS - System.currentTimeMillis() % DateUtils.MINUTE_IN_MILLIS);
    }
}
