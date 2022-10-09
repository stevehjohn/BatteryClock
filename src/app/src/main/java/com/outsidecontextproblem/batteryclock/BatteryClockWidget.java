package com.outsidecontextproblem.batteryclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

public class BatteryClockWidget extends AppWidgetProvider {

    private final Paint _arcPaint;
    private final Paint _circlePaint;
    private final Paint _minutePaint;
    private final Paint _hourPaint;
    private Bitmap _bitmap;

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
        _hourPaint.setStrokeWidth(8);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_clock_widget);

        new CountDownTimer(Long.MAX_VALUE, 60_000) {

            @Override
            public void onTick(long l) {
                draw(views, appWidgetManager, appWidgetId);
            }

            @Override
            public void onFinish() {

            }
        }.start();

        draw(views, appWidgetManager, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        Log.e("BALLS", "updated");
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

    private static int _temp = 30;

    public void draw(RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.e("BALLS", "DRAW!");

        if (views == null) {
            Log.e("BALLS", "Views is null.");

            return;
        }

        _bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        _bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(_bitmap);

        canvas.drawCircle(250, 250, 240, _circlePaint);
        canvas.drawArc(10, 10, 490, 490, 270, -_temp, false, _arcPaint);

        float minuteRadians = (float) ((float) ((Calendar.getInstance().get(Calendar.MINUTE) * 6) * (Math.PI / 180)) - Math.PI / 2);

        canvas.drawLine(250F, 250F, (float) (250 + Math.cos(minuteRadians) * 190), (float) (250 + Math.sin(minuteRadians) * 190), _minutePaint);

        float hourRadians = (float) ((float) ((Calendar.getInstance().get(Calendar.HOUR) * 30) * (Math.PI / 180)) - Math.PI / 2);

        canvas.drawLine(250F, 250F, (float) (250 + Math.cos(hourRadians) * 120), (float) (250 + Math.sin(hourRadians) * 120), _hourPaint);

        views.setImageViewBitmap(R.id.imageView, _bitmap);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        _temp += 10;

        if (_temp > 359) {
            _temp = 0;
        }
    }
}