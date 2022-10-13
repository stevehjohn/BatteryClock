package com.outsidecontextproblem.batteryclock;

import android.content.Context;

public class Settings {

    private static final String BATTERY_INDICATOR = "BatteryIndicator";

    private ElementSettings _batteryLevelIndicatorSettings;

    private int _appWidgetId;

    public ElementSettings getBatteryLevelIndicatorSettings() {
        return _batteryLevelIndicatorSettings;
    }

    public Settings(int appWidgetId) {
        _appWidgetId = appWidgetId;

        _batteryLevelIndicatorSettings = new ElementSettings(_appWidgetId, Constants.BezelIndicator, 255, 110, 100, 255);
    }

    public void loadSettings(Context context) {
        _batteryLevelIndicatorSettings.loadSettings(context, BATTERY_INDICATOR);
    }

    public void saveSettings(Context context) {
        _batteryLevelIndicatorSettings.saveSettings(context, BATTERY_INDICATOR);
    }
}
