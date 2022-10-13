package com.outsidecontextproblem.batteryclock;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryClockBootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (! intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }

        if (serviceIsRunning(context)) {
            return;
        }

        Intent serviceIntent = new Intent(context, BatteryClockWidgetService.class);
        context.startForegroundService(serviceIntent);
    }

    private boolean serviceIsRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // noinspection deprecation - acceptable to use for locating an app's own service
        for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (BatteryClockWidgetService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
