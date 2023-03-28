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
import java.util.HashMap;
import java.util.TimeZone;

public class BatteryClockWidget extends AppWidgetProvider {

    private static final HashMap<Integer, BatteryClockRenderer> _batteryClockRenderers = new HashMap<>();
    private static final HashMap<Integer, Settings> _settings = new HashMap<>();

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Settings settings) {

        Log.i(BatteryClockWidget.class.getName(), "updateAppWidget()");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_clock_widget);

        if (settings != null) {
            Log.i(BatteryClockWidget.class.getName(), String.format("Applying settings to widget %d.", appWidgetId));

            BatteryClockRenderer renderer = new BatteryClockRenderer();
            renderer.updateFromSettings(settings);
            _batteryClockRenderers.put(appWidgetId, renderer);

            _settings.put(appWidgetId, settings);
        } else {
            if (! _batteryClockRenderers.containsKey(appWidgetId)) {
                Log.i(BatteryClockWidget.class.getName(), String.format("Attempting load of settings for widget %d.", appWidgetId));

                settings = new Settings(appWidgetId);
                settings.loadSettings(context);

                BatteryClockRenderer renderer = new BatteryClockRenderer();
                renderer.updateFromSettings(settings);
                _batteryClockRenderers.put(appWidgetId, renderer);

                _settings.put(appWidgetId, settings);
            }
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
            Log.i(BatteryClockWidget.class.getName(), String.format("Deleting settings for widget %d.", appWidgetId));

            Settings settings = new Settings(appWidgetId);
            settings.deleteSettings(context);
        }
    }

    @Override
    public void onEnabled(Context context) {
        if (BatteryClockRenderer._typeface == null) {
            BatteryClockRenderer._typeface = context.getResources().getFont(R.font.roboto);
        }

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

        BatteryClockRenderer renderer = _batteryClockRenderers.get(appWidgetId);

        if (renderer == null) {
            Log.w(BatteryClockWidget.class.getName(), String.format("Renderer not found in HashMap for widget %d, using a default.", appWidgetId));

            renderer = new BatteryClockRenderer();
        }

        Settings settings = _settings.get(appWidgetId);
        String label = "";
        TimeZone timeZone;
        boolean showSeconds = false;
        if (settings != null) {
            timeZone = TimeZone.getTimeZone(settings.getTimeZone());
            label = settings.getLabel();
            showSeconds = Settings.getUpdateSeconds();
        } else {
            Log.w(BatteryClockWidget.class.getName(), String.format("Settings not found in HashMap for widget %d, using default.", appWidgetId));

            timeZone = TimeZone.getDefault();
        }
        Calendar calendar = Calendar.getInstance(timeZone);

        Bitmap bitmap = renderer.render(level, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), showSeconds ? calendar.get(Calendar.SECOND) : -1, (((calendar.get(Calendar.DAY_OF_WEEK) - 2) + 7) % 7), label, calendar);

        views.setImageViewBitmap(R.id.imageView, bitmap);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        bitmap.recycle();
    }
}