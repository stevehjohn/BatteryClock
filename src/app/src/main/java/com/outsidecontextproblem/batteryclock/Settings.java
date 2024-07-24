package com.outsidecontextproblem.batteryclock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.TimeZone;

public class Settings {

    public static final String PREFERENCES_NAME = "com.outsidecontextproblem.batteryclock.BatteryClockWidget";

    private static final String BATTERY_INDICATOR = "BatteryIndicator";
    private static final String BEZEL = "Bezel";
    private static final String TICKS = "Ticks";
    private static final String SHOW_SECONDS = "ShowSeconds";
    private static final String SECONDS = "Seconds";
    private static final String MINUTE = "Minute";
    private static final String MINUTE_ARC = "MinuteArc";
    private static final String HOUR = "Hour";
    private static final String HOUR_ARC = "HourArc";
    private static final String WEEK = "Week";
    private static final String BACKGROUND = "Background";
    private static final String LABEL_COLOUR = "LabelColour";

    private static final String TIMEZONE = "Timezone";
    private static final String LABEL = "Label";
    private static final String LABEL_SIZE = "LabelSize";

    private final ElementSettings _batteryLevelIndicatorSettings;
    private final ElementSettings _bezelSettings;
    private final ElementSettings _ticksSettings;
    private final ElementSettings _secondsSettings;
    private final ElementSettings _minuteSettings;
    private final ElementSettings _minuteArcSettings;
    private final ElementSettings _hourSettings;
    private final ElementSettings _hourArcSettings;
    private final ElementSettings _weekSettings;
    private final ElementSettings _backgroundSettings;
    private final ElementSettings _labelSettings;

    public ElementSettings getBatteryLevelIndicatorSettings() {
        return _batteryLevelIndicatorSettings;
    }

    public ElementSettings getBezelSettings() {
        return _bezelSettings;
    }

    public ElementSettings getTicksSettings() {
        return _ticksSettings;
    }

    public ElementSettings getSecondsSettings() {
        return _secondsSettings;
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

    public ElementSettings getLabelSettings() {
        return _labelSettings;
    }

    private String _timeZone;

    public String getTimeZone() {
        return _timeZone;
    }

    public void setTimeZone(String timeZone) {
        _timeZone = timeZone;
    }

    private String _label;

    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label;
    }

    private int _labelSize;

    public int getLabelSize() {
        return _labelSize;
    }

    public void setLabelSize(int labelSize) {
        _labelSize = labelSize;
    }

    private final int _appWidgetId;

    private static boolean _updateSeconds;

    public static boolean getUpdateSeconds() {
        return _updateSeconds;
    }

    public static void setUpdateSeconds(boolean updateSeconds) {
        _updateSeconds = updateSeconds;
    }

    private static boolean _smoothSeconds;

    public static boolean getSmoothSeconds() {
        return _smoothSeconds;
    }

    public static void setSmoothSeconds(boolean smoothSeconds) {
        _smoothSeconds = smoothSeconds;
    }

    public Settings(int appWidgetId) {
        _appWidgetId = appWidgetId;

        _batteryLevelIndicatorSettings = new ElementSettings(appWidgetId, Constants.BezelIndicator, 51, 22, 20, 51);
        _bezelSettings = new ElementSettings(appWidgetId, Constants.BezelOutline,51, 51, 51, 51);
        _ticksSettings = new ElementSettings(appWidgetId, Constants.TickThickness,35, 51, 51, 51);
        _secondsSettings = new ElementSettings(appWidgetId, Constants.SecondsThickness,51, 22, 20, 51);
        _minuteSettings = new ElementSettings(appWidgetId, Constants.MinuteHandThickness, 51, 51, 51, 51);
        _minuteArcSettings = new ElementSettings(appWidgetId, Constants.BezelOutline, 13, 51, 51, 51);
        _hourSettings = new ElementSettings(appWidgetId, Constants.HourHandThickness, 51, 51, 51, 51);
        _hourArcSettings =  new ElementSettings(appWidgetId, Constants.BezelOutline, 13, 51, 51, 51);
        _weekSettings = new ElementSettings(appWidgetId, 0, 13, 51, 51, 51);
        _backgroundSettings = new ElementSettings(appWidgetId, 0,38, 6, 6, 6);
        _labelSettings = new ElementSettings(appWidgetId, 0,51, 51, 51, 51);

        _timeZone = TimeZone.getDefault().getID();
        _label = "";
        _labelSize = 1;
        _updateSeconds = true;
    }

    @SuppressLint("DefaultLocale")
    public void loadSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, 0);
        _timeZone = prefs.getString(String.format("%s.%d", TIMEZONE, _appWidgetId), TimeZone.getDefault().getID());
        _label = prefs.getString(String.format("%s.%d", LABEL, _appWidgetId), "");
        _labelSize = prefs.getInt(String.format("%s.%d", LABEL_SIZE, _appWidgetId), 1);
        _updateSeconds = prefs.getBoolean(String.format("%s", SHOW_SECONDS), true);

        _batteryLevelIndicatorSettings.loadSettings(context, BATTERY_INDICATOR);
        _bezelSettings.loadSettings(context, BEZEL);
        _ticksSettings.loadSettings(context, TICKS);
        _secondsSettings.loadSettings(context, SECONDS);
        _minuteSettings.loadSettings(context, MINUTE);
        _minuteArcSettings.loadSettings(context, MINUTE_ARC);
        _hourSettings.loadSettings(context, HOUR);
        _hourArcSettings.loadSettings(context, HOUR_ARC);
        _weekSettings.loadSettings(context, WEEK);
        _backgroundSettings.loadSettings(context, BACKGROUND);
        _labelSettings.loadSettings(context, LABEL_COLOUR);
    }

    @SuppressLint("DefaultLocale")
    public void saveSettings(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        prefs.putString(String.format("%s.%d", TIMEZONE, _appWidgetId), _timeZone);
        prefs.putString(String.format("%s.%d", LABEL, _appWidgetId), _label);
        prefs.putInt(String.format("%s.%d", LABEL_SIZE, _appWidgetId), _labelSize);
        prefs.putBoolean(String.format("%s", SHOW_SECONDS), _updateSeconds);
        prefs.apply();

        _batteryLevelIndicatorSettings.saveSettings(context, BATTERY_INDICATOR);
        _bezelSettings.saveSettings(context, BEZEL);
        _ticksSettings.saveSettings(context, TICKS);
        _secondsSettings.saveSettings(context, SECONDS);
        _minuteSettings.saveSettings(context, MINUTE);
        _minuteArcSettings.saveSettings(context, MINUTE_ARC);
        _hourSettings.saveSettings(context, HOUR);
        _hourArcSettings.saveSettings(context, HOUR_ARC);
        _weekSettings.saveSettings(context, WEEK);
        _backgroundSettings.saveSettings(context, BACKGROUND);
        _labelSettings.saveSettings(context, LABEL_COLOUR);
    }

    @SuppressLint("DefaultLocale")
    public void deleteSettings(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        prefs.remove(String.format("%s.%d", TIMEZONE, _appWidgetId));
        prefs.remove(String.format("%s.%d", LABEL, _appWidgetId));
        prefs.remove(String.format("%s.%d", LABEL_SIZE, _appWidgetId));
        prefs.remove(String.format("%s", SHOW_SECONDS));
        prefs.apply();

        _batteryLevelIndicatorSettings.deleteSettings(context, BATTERY_INDICATOR);
        _bezelSettings.deleteSettings(context, BEZEL);
        _ticksSettings.deleteSettings(context, TICKS);
        _secondsSettings.deleteSettings(context, SECONDS);
        _minuteSettings.deleteSettings(context, MINUTE);
        _minuteArcSettings.deleteSettings(context, MINUTE_ARC);
        _hourSettings.deleteSettings(context, HOUR);
        _hourArcSettings.deleteSettings(context, HOUR_ARC);
        _weekSettings.deleteSettings(context, WEEK);
        _backgroundSettings.deleteSettings(context, BACKGROUND);
        _labelSettings.deleteSettings(context, LABEL_COLOUR);
    }
}
