package com.outsidecontextproblem.batteryclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.display.DisplayManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.widget.RemoteViews;

import java.util.Calendar;

public class BatteryClockWidget extends AppWidgetProvider implements Runnable, DisplayManager.DisplayListener {

    private final Paint _arcPaint;
    private final Paint _circlePaint;
    private final Paint _minutePaint;
    private final Paint _hourPaint;
    private final Paint _dotPaint;
    private final Paint _backgroundPaint;

    private DisplayManager _displayManager;
    private Handler _handler;
    private AppWidgetManager _appWidgetManager;
    private Context _context;
    private int _appWidgetId;

    public BatteryClockWidget() {

        _arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _arcPaint.setARGB(255, 107, 99, 255);
        _arcPaint.setStyle(Paint.Style.STROKE);
        _arcPaint.setStrokeWidth(20);

        _circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _circlePaint.setARGB(64, 255, 255, 255);
        _circlePaint.setStyle(Paint.Style.STROKE);
        _circlePaint.setStrokeWidth(4);

        _minutePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _minutePaint.setARGB(255, 255, 255, 255);
        _minutePaint.setStyle(Paint.Style.STROKE);
        _minutePaint.setStrokeWidth(4);

        _hourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _hourPaint.setARGB(255, 255, 255, 255);
        _hourPaint.setStyle(Paint.Style.STROKE);
        _hourPaint.setStrokeWidth(4);

        _dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _dotPaint.setARGB(255, 255, 255, 255);
        _dotPaint.setStyle(Paint.Style.STROKE);
        _dotPaint.setStrokeWidth(4);

        _backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _backgroundPaint.setARGB(192, 32, 32, 32);
        _backgroundPaint.setStrokeWidth(4);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        _context = context;
        _appWidgetManager = appWidgetManager;
        _appWidgetId = appWidgetId;

        if (_displayManager == null) {
            _displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);

            _displayManager.registerDisplayListener(this, null);
        }

        if (_handler == null) {
            _handler = new Handler();

            _handler.postDelayed(this, DateUtils.MINUTE_IN_MILLIS - System.currentTimeMillis() % DateUtils.MINUTE_IN_MILLIS);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_clock_widget);

        draw(views, appWidgetManager, appWidgetId, context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BatteryClockWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void run() {
        Log.i(BatteryClockWidget.class.getName(), "run()");

        RemoteViews views = new RemoteViews(_context.getPackageName(), R.layout.battery_clock_widget);

        draw(views, _appWidgetManager, _appWidgetId, _context);

        _handler.postDelayed(this, DateUtils.MINUTE_IN_MILLIS - System.currentTimeMillis() % DateUtils.MINUTE_IN_MILLIS);
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

        if (! displayOn) {
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
    public void onDisplayAdded(int i) {
    }

    @Override
    public void onDisplayRemoved(int i) {
    }

    @Override
    public void onDisplayChanged(int i) {
        Log.i(BatteryClockWidget.class.getName(), "onDisplayChanged()");

        RemoteViews views = new RemoteViews(_context.getPackageName(), R.layout.battery_clock_widget);

        draw(views, _appWidgetManager, _appWidgetId, _context);
    }
}