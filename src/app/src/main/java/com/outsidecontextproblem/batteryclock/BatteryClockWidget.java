package com.outsidecontextproblem.batteryclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.os.BatteryManager;
import android.util.Log;
import android.view.Display;
import android.widget.RemoteViews;

import java.util.Calendar;

public class BatteryClockWidget extends AppWidgetProvider {

    private static final BatteryClockRenderer _batteryClockRenderer = new BatteryClockRenderer();

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Settings settings) {

        Log.i(BatteryClockWidget.class.getName(), "updateAppWidget()");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_clock_widget);

        if (settings != null) {
            _batteryClockRenderer.updateFromSettings(settings);
        }

        draw(views, appWidgetManager, appWidgetId, context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(BatteryClockWidget.class.getName(), "onUpdate()");

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, null);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(BatteryClockWidget.class.getName(), "onDelete()");

        for (int appWidgetId : appWidgetIds) {
            BatteryClockWidgetConfigureActivity.deleteWidgetPreferences(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.i(BatteryClockWidget.class.getName(), "onEnabled()");
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(BatteryClockWidget.class.getName(), "onDisabled()");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BatteryClockWidget.class.getName(), "onReceive()");

        super.onReceive(context, intent);
    }

    public static void draw(RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId, Context context) {
        Log.i(BatteryClockWidget.class.getName(), "draw()");

        boolean displayOn = false;

        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);

        for (Display display : displayManager.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                displayOn = true;
                break;
            }
        }

        if (!displayOn) {
            Log.i(BatteryClockWidget.class.getName(), "Display is off, skipping update.");

            return;
        }

        if (views == null) {
            Log.i(BatteryClockWidget.class.getName(), "Views is null.");

            return;
        }

        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        Bitmap bitmap = _batteryClockRenderer.render(level, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), (((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2) + 7) % 7));

        views.setImageViewBitmap(R.id.imageView, bitmap);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        bitmap.recycle();
    }
}