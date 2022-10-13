package com.outsidecontextproblem.batteryclock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class ElementSettings {

    private static final String PREFERENCES_NAME = "com.outsidecontextproblem.batteryclock.BatteryClockWidget";

    private static final String SETTING_THICKNESS = "thickness";

    private int _appWidgetId;

    private int _thickness;
    private final int _thicknessDefault;

    public ElementSettings(int appWidgetId, int thicknessDefault) {
        _appWidgetId = appWidgetId;

        _thicknessDefault = thicknessDefault;
    }

    public int getThickness() {
        return _thickness;
    }

    public void setThickness(int thickness) {
        _thickness = thickness;
    }

    @SuppressLint("DefaultLocale")
    public void loadSettings(Context context, String elementName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, 0);

        _thickness = prefs.getInt(String.format("%s.%d.%s", elementName, _appWidgetId, SETTING_THICKNESS), _thicknessDefault);
    }

    @SuppressLint("DefaultLocale")
    public void saveSettings(Context context, String elementName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, 0).edit();

        prefs.putInt(String.format("%s.%d.%s", elementName, _appWidgetId, SETTING_THICKNESS), _thickness);

        prefs.apply();
    }
}
