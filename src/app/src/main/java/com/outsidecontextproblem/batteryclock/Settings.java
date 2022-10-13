package com.outsidecontextproblem.batteryclock;

import android.content.Context;

public class Settings {

    private static final String BATTERY_INDICATOR = "BatteryIndicator";
    private static final String BEZEL = "Bezel";
    private static final String TICKS = "Ticks";
    private static final String BACKGROUND = "Background";

    private final ElementSettings _batteryLevelIndicatorSettings;
    private final ElementSettings _bezelSettings;
    private final ElementSettings _ticksSettings;
    private ElementSettings _minuteSettings;
    private ElementSettings _minuteArcIndicatorSettings;
    private ElementSettings _hourIndicatorSettings;
    private ElementSettings _hourArcIndicatorSettings;
    private ElementSettings _weekSettings;
    private ElementSettings _backgroundSettings;

    private int _appWidgetId;

    public ElementSettings getBatteryLevelIndicatorSettings() {
        return _batteryLevelIndicatorSettings;
    }

    public ElementSettings getBezelSettings() {
        return _bezelSettings;
    }

    public ElementSettings getTicksSettings() {
        return _ticksSettings;
    }

    public ElementSettings getBackgroundSettings() {
        return _backgroundSettings;
    }

    public Settings(int appWidgetId) {
        _appWidgetId = appWidgetId;

        _batteryLevelIndicatorSettings = new ElementSettings(_appWidgetId, Constants.BezelIndicator, 51, 22, 20, 51);
        _bezelSettings = new ElementSettings(_appWidgetId, Constants.BezelOutline,51, 51, 51, 51);
        _ticksSettings = new ElementSettings(_appWidgetId, Constants.TickThickness,13, 51, 51, 51);
        _backgroundSettings = new ElementSettings(_appWidgetId, 0,38, 6, 6, 6);
    }

    public void loadSettings(Context context) {
        _batteryLevelIndicatorSettings.loadSettings(context, BATTERY_INDICATOR);
        _bezelSettings.loadSettings(context, BEZEL);
        _ticksSettings.loadSettings(context, TICKS);
        _backgroundSettings.loadSettings(context, BACKGROUND);
    }

    public void saveSettings(Context context) {
        _batteryLevelIndicatorSettings.saveSettings(context, BATTERY_INDICATOR);
        _bezelSettings.saveSettings(context, BEZEL);
        _ticksSettings.saveSettings(context, TICKS);
        _backgroundSettings.saveSettings(context, BACKGROUND);
    }
}
