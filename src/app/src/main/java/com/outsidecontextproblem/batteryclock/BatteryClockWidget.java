package com.outsidecontextproblem.batteryclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.display.DisplayManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.RemoteViews;

import java.util.Calendar;

public class BatteryClockWidget extends AppWidgetProvider {

    private final Paint _arcPaint;
    private final Paint _circlePaint;
    private final Paint _minutePaint;
    private final Paint _hourPaint;
    private final Paint _dotPaint;
    private final Paint _backgroundPaint;

    private DisplayManager _displayManager;

    private BroadcastReceiver _receiver;

    public BatteryClockWidget() {

        _arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _arcPaint.setARGB(255, 107, 99, 255);
        _arcPaint.setStyle(Paint.Style.STROKE);
        _arcPaint.setStrokeWidth(Constants.BezelIndicator);

        _circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _circlePaint.setARGB(64, 255, 255, 255);
        _circlePaint.setStyle(Paint.Style.STROKE);
        _circlePaint.setStrokeWidth(Constants.BezelOutline);

        _minutePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _minutePaint.setARGB(255, 255, 255, 255);
        _minutePaint.setStyle(Paint.Style.STROKE);
        _minutePaint.setStrokeWidth(Constants.MinuteHandThickness);

        _hourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _hourPaint.setARGB(255, 255, 255, 255);
        _hourPaint.setStyle(Paint.Style.STROKE);
        _hourPaint.setStrokeWidth(Constants.HourHandThickness);

        _dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _dotPaint.setARGB(255, 255, 255, 255);
        _dotPaint.setStyle(Paint.Style.STROKE);
        _dotPaint.setStrokeWidth(Constants.TickThickness);

        _backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _backgroundPaint.setARGB(192, 32, 32, 32);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Log.i(BatteryClockWidget.class.getName(), "updateAppWidget()");

        if (_displayManager == null) {
            _displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_clock_widget);

        draw(views, appWidgetManager, appWidgetId, context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(BatteryClockWidget.class.getName(), "onUpdate()");

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(BatteryClockWidget.class.getName(), "onDelete()");

        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BatteryClockWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.i(BatteryClockWidget.class.getName(), "onEnabled()");

        _receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent incomingIntent) {

                if (incomingIntent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    Intent intent2 = new Intent(context, BatteryClockWidget.class);
                    intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, BatteryClockWidget.class));

                    appWidgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

                    intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

                    context.sendBroadcast(intent2);
                }
            }
        };

        context.getApplicationContext().registerReceiver(_receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(BatteryClockWidget.class.getName(), "onDisabled()");

        if (_receiver != null) {
            context.getApplicationContext().unregisterReceiver(_receiver);
            _receiver = null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BatteryClockWidget.class.getName(), "onReceive()");

        super.onReceive(context, intent);
    }

    public void draw(RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId, Context context) {
        Log.i(BatteryClockWidget.class.getName(), "draw()");

        boolean displayOn = false;

        for (Display display : _displayManager.getDisplays()) {
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

        Bitmap bitmap = Bitmap.createBitmap(Constants.BitmapDimensions, Constants.BitmapDimensions, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawCircle(Constants.BitmapCenter, Constants.BitmapCenter, Constants.BackgroundRadius, _backgroundPaint);

        canvas.drawCircle(Constants.BitmapCenter, Constants.BitmapCenter, Constants.FrameRadius, _circlePaint);

        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        canvas.drawArc(Constants.ArcCenterMin, Constants.ArcCenterMin, Constants.ArcCenterMax, Constants.ArcCenterMax, 270, (int) -(level * 3.6), false, _arcPaint);

        for (int i = 0; i < 12; i++) {
            float dotRadians = (float) ((float) ((i * 30) * (Math.PI / 180)) - Math.PI / 2);

            canvas.drawLine((float) (Constants.BitmapCenter + Math.cos(dotRadians) * Constants.TickStart), (float) (Constants.BitmapCenter + Math.sin(dotRadians) * Constants.TickStart),
                    (float) (Constants.BitmapCenter + Math.cos(dotRadians) * Constants.TickEnd), (float) (Constants.BitmapCenter + Math.sin(dotRadians) * Constants.TickEnd), _dotPaint);
        }

        float minuteRadians = (float) ((float) ((Calendar.getInstance().get(Calendar.MINUTE) * 6) * (Math.PI / 180)) - Math.PI / 2);

        canvas.drawLine(Constants.BitmapCenter, Constants.BitmapCenter, (float) (Constants.BitmapCenter + Math.cos(minuteRadians) * Constants.MinuteHandLength), (float) (Constants.BitmapCenter + Math.sin(minuteRadians) * Constants.MinuteHandLength), _minutePaint);

        float hourRadians = (float) ((float) ((Calendar.getInstance().get(Calendar.HOUR) * 30 + Calendar.getInstance().get(Calendar.MINUTE) / 2) * (Math.PI / 180)) - Math.PI / 2);

        canvas.drawLine(Constants.BitmapCenter, Constants.BitmapCenter, (float) (Constants.BitmapCenter + Math.cos(hourRadians) * Constants.HourHandLength), (float) (Constants.BitmapCenter + Math.sin(hourRadians) * Constants.HourHandLength), _hourPaint);

        views.setImageViewBitmap(R.id.imageView, bitmap);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        bitmap.recycle();
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        Log.i(BatteryClockWidget.class.getName(), "onAppWidgetOptionsChanged()");
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);

        Log.i(BatteryClockWidget.class.getName(), "onRestored()");
    }
}