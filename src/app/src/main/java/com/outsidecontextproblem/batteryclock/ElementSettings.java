package com.outsidecontextproblem.batteryclock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class ElementSettings {

    private static final String PREFERENCES_NAME = "com.outsidecontextproblem.batteryclock.BatteryClockWidget";

    private static final String SETTING_THICKNESS = "thickness";
    private static final String SETTING_OPACITY = "opacity";

    private int _appWidgetId;

    private int _thickness;
    private final int _thicknessDefault;

    private int _opacity;
    private final int _opacityDefault;

    private int _red;
    private final int _redDefault;

    private int _green;
    private final int _greenDefault;

    private int _blue;
    private final int _blueDefault;

    public ElementSettings(int appWidgetId, int thicknessDefault, int opacityDefault, int redDefault, int greenDefault, int blueDefault) {
        _appWidgetId = appWidgetId;

        _thicknessDefault = thicknessDefault;
        _opacityDefault = opacityDefault;
        _redDefault = redDefault;
        _greenDefault = greenDefault;
        _blueDefault = blueDefault;
    }

    public int getThickness() {
        return _thickness;
    }
    public void setThickness(int thickness) {
        _thickness = thickness;
    }

    public int getOpacity() {
        return _opacity;
    }
    public void setOpacity(int opacity) {
        _opacity = opacity;
    }

    public int getRed() {
        return _red;
    }
    public void setRed(int red) {
        _red = red;
    }

    public int getGreen() {
        return _green;
    }
    public void setGreen(int green) { _green = green; }

    public int getBlue() {
        return _blue;
    }
    public void setBlue(int blue) { _blue = blue; }

    @SuppressLint("DefaultLocale")
    public void loadSettings(Context context, String elementName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, 0);

        _thickness = prefs.getInt(String.format("%s.%d.%s", elementName, _appWidgetId, SETTING_THICKNESS), _thicknessDefault);
        _opacity = prefs.getInt(String.format("%s.%d.%s", elementName, _appWidgetId, SETTING_OPACITY), _opacityDefault);
    }

    @SuppressLint("DefaultLocale")
    public void saveSettings(Context context, String elementName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, 0).edit();

        prefs.putInt(String.format("%s.%d.%s", elementName, _appWidgetId, SETTING_THICKNESS), _thickness);
        prefs.putInt(String.format("%s.%d.%s", elementName, _appWidgetId, SETTING_OPACITY), _opacity);

        prefs.apply();
    }
}
