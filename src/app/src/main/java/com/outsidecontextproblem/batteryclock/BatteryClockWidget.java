package com.outsidecontextproblem.batteryclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
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
    private Bitmap _bitmap;

    public BatteryClockWidget() {

        _arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _arcPaint.setARGB(255, 107, 99, 255);
        _arcPaint.setStyle(Paint.Style.STROKE);
        _arcPaint.setStrokeWidth(20);

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

        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        canvas.drawArc(10, 10, 490, 490, 270, -_temp, false, _arcPaint);

        views.setImageViewBitmap(R.id.imageView, _bitmap);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        _temp += 10;

        if (_temp > 359) {
            _temp = 0;
        }
    }
}