package com.outsidecontextproblem.batteryclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.display.DisplayManager;
import android.os.BatteryManager;
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
    private final Paint _minuteTrailPaint;
    private final Paint _hourTrailPaint;
    private final Paint _dayArcPaint;

    private DisplayManager _displayManager;

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

        _minuteTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _minuteTrailPaint.setARGB(64, 255, 255, 255);
        _minuteTrailPaint.setStyle(Paint.Style.STROKE);
        _minuteTrailPaint.setStrokeWidth(Constants.BezelOutline);

        _hourTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _hourTrailPaint.setARGB(64, 255, 255, 255);
        _hourTrailPaint.setStyle(Paint.Style.STROKE);
        _hourTrailPaint.setStrokeWidth(Constants.BezelOutline);

        _dayArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _dayArcPaint.setARGB(64, 255, 255, 255);
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

        int batteryArcOffset = Constants.BitmapCenter - Constants.BatteryIndicatorRadius;

        canvas.drawArc(batteryArcOffset, batteryArcOffset, Constants.BitmapDimensions - batteryArcOffset, Constants.BitmapDimensions - batteryArcOffset, 270, (int) -(level * 3.6), false, _arcPaint);

        for (int i = 0; i < 12; i++) {
            float dotRadians = (float) ((float) ((i * 30) * (Math.PI / 180)) - Math.PI / 2);

            canvas.drawLine((float) (Constants.BitmapCenter + Math.cos(dotRadians) * Constants.TickStart), (float) (Constants.BitmapCenter + Math.sin(dotRadians) * Constants.TickStart),
                    (float) (Constants.BitmapCenter + Math.cos(dotRadians) * Constants.TickEnd), (float) (Constants.BitmapCenter + Math.sin(dotRadians) * Constants.TickEnd), _dotPaint);
        }

        int dayArcOffset = Constants.BitmapCenter - Constants.DayArcRadius;

        float degreesForDay = 360F / 7F;

        float dayDegrees = (((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2) + 7) % 7) * degreesForDay;

        dayDegrees += (degreesForDay / 24) * Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        canvas.drawArc(dayArcOffset, dayArcOffset, Constants.BitmapDimensions - dayArcOffset, Constants.BitmapDimensions - dayArcOffset, 270, dayDegrees, true, _dayArcPaint);

        float minuteDegrees = Calendar.getInstance().get(Calendar.MINUTE) * 6;

        int minuteArcOffset = Constants.BitmapCenter - Constants.MinuteHandLength;

        canvas.drawArc(minuteArcOffset, minuteArcOffset, Constants.BitmapDimensions - minuteArcOffset, Constants.BitmapDimensions - minuteArcOffset, 270, minuteDegrees, false, _minuteTrailPaint);

        float minuteRadians = (float) ((float) ((Calendar.getInstance().get(Calendar.MINUTE) * 6) * (Math.PI / 180)) - Math.PI / 2);

        float hourDegrees = Calendar.getInstance().get(Calendar.HOUR) * 30 + (Calendar.getInstance().get(Calendar.MINUTE) / 2F);

        int hourArcOffset = Constants.BitmapCenter - Constants.HourHandLength;

        canvas.drawArc(hourArcOffset, hourArcOffset, Constants.BitmapDimensions - hourArcOffset, Constants.BitmapDimensions - hourArcOffset, 270, hourDegrees, false, _hourTrailPaint);

        canvas.drawLine(Constants.BitmapCenter, Constants.BitmapCenter, (float) (Constants.BitmapCenter + Math.cos(minuteRadians) * Constants.MinuteHandLength), (float) (Constants.BitmapCenter + Math.sin(minuteRadians) * Constants.MinuteHandLength), _minutePaint);

        float hourRadians = (float) ((float) ((Calendar.getInstance().get(Calendar.HOUR) * 30 + Calendar.getInstance().get(Calendar.MINUTE) / 2) * (Math.PI / 180)) - Math.PI / 2);

        canvas.drawLine(Constants.BitmapCenter, Constants.BitmapCenter, (float) (Constants.BitmapCenter + Math.cos(hourRadians) * Constants.HourHandLength), (float) (Constants.BitmapCenter + Math.sin(hourRadians) * Constants.HourHandLength), _hourPaint);

        views.setImageViewBitmap(R.id.imageView, bitmap);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        bitmap.recycle();
    }
}