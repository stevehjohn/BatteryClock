package com.outsidecontextproblem.batteryclock;

import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.outsidecontextproblem.batteryclock.databinding.BatteryClockWidgetConfigureBinding;

public class BatteryClockWidgetConfigureActivity extends Activity {

    private final BatteryClockRenderer _batteryClockRenderer;

    int _appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Settings _settings;

    private final View.OnClickListener _addOnClickListener = view -> {
        final Context context = BatteryClockWidgetConfigureActivity.this;

        savePreferences(context);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        BatteryClockWidget.updateAppWidget(context, appWidgetManager, _appWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    };

    private final View.OnClickListener _cancelOnClickListener = view -> {
        setResult(RESULT_CANCELED);
        finish();
    };

    private BatteryClockWidgetConfigureBinding _binding;

    public BatteryClockWidgetConfigureActivity() {
        super();

        _batteryClockRenderer = new BatteryClockRenderer();
    }

    private void savePreferences(Context context) {
        _settings.saveSettings(context);
    }

    static void deleteWidgetPreferences(Context context, int appWidgetId) {
        // TODO
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        _binding = BatteryClockWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());
        _binding.buttonAdd.setOnClickListener(_addOnClickListener);
        _binding.buttonCancel.setOnClickListener(_cancelOnClickListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            _appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (_appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        Context context = getApplicationContext();

        _settings = new Settings(_appWidgetId);

        applySettings(context);

        if (! serviceIsRunning(context)) {
            Log.i(BatteryClockWidget.class.getName(), "onCreate(): Starting service.");

            Intent serviceIntent = new Intent(context, BatteryClockWidgetService.class);
            context.startForegroundService(serviceIntent);
        }

        Bitmap bitmap = _batteryClockRenderer.render(75, 10, 10, 3);

        ImageView imageView = findViewById(R.id.imageClock);

        imageView.setImageBitmap(bitmap);
    }

    private void applySettings(Context context) {
        _settings.loadSettings(context);
    }

    private boolean serviceIsRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // noinspection deprecation - acceptable to use for locating an app's own service
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (BatteryClockWidgetService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}