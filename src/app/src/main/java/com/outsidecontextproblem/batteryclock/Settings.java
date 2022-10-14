package com.outsidecontextproblem.batteryclock;

import android.content.Context;

public class Settings {

    private static final String BATTERY_INDICATOR = "BatteryIndicator";
    private static final String BEZEL = "Bezel";
    private static final String TICKS = "Ticks";
    private static final String MINUTE = "Minute";
    private static final String MINUTE_ARC = "MinuteArc";
    private static final String HOUR = "Hour";
    private static final String HOUR_ARC = "HourArc";
    private static final String WEEK = "Week";
    private static final String BACKGROUND = "Background";

    private final ElementSettings _batteryLevelIndicatorSettings;
    private final ElementSettings _bezelSettings;
    private final ElementSettings _ticksSettings;
    private final ElementSettings _minuteSettings;
    private final ElementSettings _minuteArcSettings;
    private final ElementSettings _hourSettings;
    private final ElementSettings _hourArcSettings;
    private final ElementSettings _weekSettings;
    private final ElementSettings _backgroundSettings;

    public ElementSettings getBatteryLevelIndicatorSettings() {
        return _batteryLevelIndicatorSettings;
    }

    public ElementSettings getBezelSettings() {
        return _bezelSettings;
    }

    public ElementSettings getTicksSettings() {
        return _ticksSettings;
    }

    public ElementSettings getMinuteSettings() {
        return _minuteSettings;
    }

    public ElementSettings getMinuteArcSettings() {
        return _minuteArcSettings;
    }

    public ElementSettings getHourSettings() {
        return _hourSettings;
    }

    public ElementSettings getHourArcSettings() {
        return _hourArcSettings;
    }

    public ElementSettings getWeekSettings() {
        return _weekSettings;
    }

    public ElementSettings getBackgroundSettings() {
        return _backgroundSettings;
    }

    public Settings(int appWidgetId) {
        _batteryLevelIndicatorSettings = new ElementSettings(appWidgetId, Constants.BezelIndicator, 51, 22, 20, 51);
        _bezelSettings = new ElementSettings(appWidgetId, Constants.BezelOutline,51, 51, 51, 51);
        _ticksSettings = new ElementSettings(appWidgetId, Constants.TickThickness,13, 51, 51, 51);
        _minuteSettings = new ElementSettings(appWidgetId, Constants.MinuteHandThickness, 51, 51, 51, 51);
        _minuteArcSettings = new ElementSettings(appWidgetId, Constants.BezelOutline, 13, 51, 51, 51);
        _hourSettings = new ElementSettings(appWidgetId, Constants.HourHandThickness, 51, 51, 51, 51);
        _hourArcSettings =  new ElementSettings(appWidgetId, Constants.BezelOutline, 13, 51, 51, 51);
        _weekSettings = new ElementSettings(appWidgetId, 0, 13, 51, 51, 51);
        _backgroundSettings = new ElementSettings(appWidgetId, 0,38, 6, 6, 6);
    }

    public void loadSettings(Context context) {
        _batteryLevelIndicatorSettings.loadSettings(context, BATTERY_INDICATOR);
        _bezelSettings.loadSettings(context, BEZEL);
        _ticksSettings.loadSettings(context, TICKS);
        _minuteSettings.loadSettings(context, MINUTE);
        _minuteArcSettings.loadSettings(context, MINUTE_ARC);
        _hourSettings.loadSettings(context, HOUR);
        _hourArcSettings.loadSettings(context, HOUR_ARC);
        _weekSettings.loadSettings(context, WEEK);
        _backgroundSettings.loadSettings(context, BACKGROUND);
    }

    public void saveSettings(Context context) {
        _batteryLevelIndicatorSettings.saveSettings(context, BATTERY_INDICATOR);
        _bezelSettings.saveSettings(context, BEZEL);
        _ticksSettings.saveSettings(context, TICKS);
        _minuteSettings.saveSettings(context, MINUTE);
        _minuteArcSettings.saveSettings(context, MINUTE_ARC);
        _hourSettings.saveSettings(context, HOUR);
        _hourArcSettings.saveSettings(context, HOUR_ARC);
        _weekSettings.saveSettings(context, WEEK);
        _backgroundSettings.saveSettings(context, BACKGROUND);
    }

    public void deleteSettings(Context context) {
        _batteryLevelIndicatorSettings.deleteSettings(context, BATTERY_INDICATOR);
        _bezelSettings.deleteSettings(context, BEZEL);
        _ticksSettings.deleteSettings(context, TICKS);
        _minuteSettings.deleteSettings(context, MINUTE);
        _minuteArcSettings.deleteSettings(context, MINUTE_ARC);
        _hourSettings.deleteSettings(context, HOUR);
        _hourArcSettings.deleteSettings(context, HOUR_ARC);
        _weekSettings.deleteSettings(context, WEEK);
        _backgroundSettings.deleteSettings(context, BACKGROUND);
    }
}
