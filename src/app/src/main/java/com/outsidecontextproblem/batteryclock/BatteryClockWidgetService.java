package com.outsidecontextproblem.batteryclock;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;

public class BatteryClockWidgetService extends Service implements Runnable, DisplayManager.DisplayListener {

    private static final String NOTIFICATION_CHANNEL_ID = "com.outsidecontextproblem.batteryclock";

    private static final int NOTIFICATION_ID = 824954302;

    private NotificationManager _notificationManager;

    private Notification.Builder _notificationBuilder;

    private Handler _handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(BatteryClockWidgetService.class.getName(), "onStartCommand()");

        NotificationChannel notificationChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW
        );

        _notificationManager = getSystemService((NotificationManager.class));

        _notificationManager.createNotificationChannel(notificationChannel);

        _notificationBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentText("WANK!")
                .setSmallIcon(R.drawable.ic_launcher_background);

        startForeground(NOTIFICATION_ID, _notificationBuilder.build());

        if (_handler == null) {
            _handler = new Handler();

            _handler.removeCallbacks(this);
            _handler.postDelayed(this, DateUtils.MINUTE_IN_MILLIS - System.currentTimeMillis() % DateUtils.MINUTE_IN_MILLIS);
        }

        DisplayManager displayManager = (DisplayManager) getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(this, null);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        Log.i(BatteryClockWidgetService.class.getName(), "run()");

        Context context = getApplicationContext();

        Intent intent = new Intent(context, BatteryClockWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, BatteryClockWidget.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        context.sendBroadcast(intent);

        _handler.removeCallbacks(this);
        _handler.postDelayed(this, DateUtils.MINUTE_IN_MILLIS - System.currentTimeMillis() % DateUtils.MINUTE_IN_MILLIS);
    }

    @Override
    public void onDisplayAdded(int i) {
    }

    @Override
    public void onDisplayRemoved(int i) {
    }

    @Override
    public void onDisplayChanged(int i) {
        Log.i(BatteryClockWidgetService.class.getName(), "onDisplayChanged()");

        run();
    }
}
