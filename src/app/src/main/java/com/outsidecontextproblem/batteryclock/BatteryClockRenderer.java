package com.outsidecontextproblem.batteryclock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.Calendar;

public class BatteryClockRenderer {

    public static Typeface _typeface;

    private final Paint _arcPaint;
    private final Paint _circlePaint;
    private final Paint _minutePaint;
    private final Paint _hourPaint;
    private final Paint _secondsPaint;
    private final Paint _dotPaint;
    private final Paint _backgroundPaint;
    private final Paint _minuteTrailPaint;
    private final Paint _hourTrailPaint;
    private final Paint _dayArcPaint;
    private final Paint _labelPaint;

    public BatteryClockRenderer() {

        _arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _arcPaint.setARGB(255, 110, 100, 255);
        _arcPaint.setStyle(Paint.Style.STROKE);
        _arcPaint.setStrokeWidth(Constants.BezelIndicator);

        _circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _circlePaint.setARGB(65, 255, 255, 255);
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

        _secondsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _secondsPaint.setARGB(255, 255, 255, 255);
        _secondsPaint.setStyle(Paint.Style.STROKE);
        _secondsPaint.setStrokeWidth(Constants.TickThickness);

        _dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _dotPaint.setARGB(255, 255, 255, 255);
        _dotPaint.setStyle(Paint.Style.STROKE);
        _dotPaint.setStrokeWidth(Constants.TickThickness);

        _backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _backgroundPaint.setARGB(190, 30, 30, 30);

        _minuteTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _minuteTrailPaint.setARGB(65, 255, 255, 255);
        _minuteTrailPaint.setStyle(Paint.Style.STROKE);
        _minuteTrailPaint.setStrokeWidth(Constants.BezelOutline);

        _hourTrailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _hourTrailPaint.setARGB(65, 255, 255, 255);
        _hourTrailPaint.setStyle(Paint.Style.STROKE);
        _hourTrailPaint.setStrokeWidth(Constants.BezelOutline);

        _dayArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _dayArcPaint.setARGB(65, 255, 255, 255);

        _labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _labelPaint.setARGB(255, 255, 255, 255);
        _labelPaint.setTextSize(Constants.LabelSizeDefault);
        _labelPaint.setTextAlign(Paint.Align.CENTER);
        if (_typeface != null) {
            _labelPaint.setTypeface(_typeface);
        }
    }

    public void updateFromSettings(Settings settings) {
        updatePaint(_arcPaint, settings.getBatteryLevelIndicatorSettings());
        updatePaint(_circlePaint, settings.getBezelSettings());
        updatePaint(_dotPaint, settings.getTicksSettings());
        updatePaint(_secondsPaint, settings.getSecondsSettings());
        updatePaint(_minutePaint, settings.getMinuteSettings());
        updatePaint(_minuteTrailPaint, settings.getMinuteArcSettings());
        updatePaint(_hourPaint, settings.getHourSettings());
        updatePaint(_hourTrailPaint, settings.getHourArcSettings());
        updatePaint(_dayArcPaint, settings.getWeekSettings());
        updatePaint(_backgroundPaint, settings.getBackgroundSettings());
        updatePaint(_labelPaint, settings.getLabelSettings());

        switch (settings.getLabelSize()) {
            case 0:
                _labelPaint.setTextSize(Constants.LabelSizeSmall);
                break;
            case 1:
                _labelPaint.setTextSize(Constants.LabelSizeDefault);
                break;
            case 2:
                _labelPaint.setTextSize(Constants.LabelSizeMedium);
                break;
            case 3:
                _labelPaint.setTextSize(Constants.LabelSizeLarge);
                break;
        }
    }

    private void updatePaint(Paint paint, ElementSettings settings) {
        paint.setStrokeWidth(settings.getThickness());
        paint.setARGB(settings.getOpacity() * 5, settings.getRed() * 5, settings.getGreen() * 5, settings.getBlue() * 5);
    }

    public Bitmap render(int level, int hour, int minute, int second, int dayOfWeek, String label) {

        Bitmap bitmap = Bitmap.createBitmap(Constants.BitmapDimensions, Constants.BitmapDimensions, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawCircle(Constants.BitmapCenter, Constants.BitmapCenter, Constants.BackgroundRadius, _backgroundPaint);

        canvas.drawCircle(Constants.BitmapCenter, Constants.BitmapCenter, Constants.FrameRadius, _circlePaint);

        int batteryArcOffset = Constants.BitmapCenter - Constants.BatteryIndicatorRadius;

        canvas.drawArc(batteryArcOffset, batteryArcOffset, Constants.BitmapDimensions - batteryArcOffset, Constants.BitmapDimensions - batteryArcOffset, 270, (int) -(level * 3.6), false, _arcPaint);

        float secondsRadians = (float) ((float) ((Calendar.getInstance().get(Calendar.SECOND) * 6) * (Math.PI / 180)) - Math.PI / 2);

        for (int i = 0; i < 12; i++) {
            float dotRadians = (float) ((float) ((i * 30) * (Math.PI / 180)) - Math.PI / 2);

            canvas.drawLine((float) (Constants.BitmapCenter + Math.cos(dotRadians) * Constants.TickStart), (float) (Constants.BitmapCenter + Math.sin(dotRadians) * Constants.TickStart),
                    (float) (Constants.BitmapCenter + Math.cos(dotRadians) * Constants.TickEnd), (float) (Constants.BitmapCenter + Math.sin(dotRadians) * Constants.TickEnd), _dotPaint);
        }
        canvas.drawLine((float) (Constants.BitmapCenter + Math.cos(secondsRadians) * Constants.TickStart), (float) (Constants.BitmapCenter + Math.sin(secondsRadians) * Constants.TickStart),
                (float) (Constants.BitmapCenter + Math.cos(secondsRadians) * Constants.TickEnd), (float) (Constants.BitmapCenter + Math.sin(secondsRadians) * Constants.TickEnd), _secondsPaint);

        if (label != null && label.length() > 0) {
            canvas.drawText(label, Constants.BitmapCenter, Constants.LabelY + _labelPaint.getTextSize() / 2, _labelPaint);
        }

        int dayArcOffset = Constants.BitmapCenter - Constants.DayArcRadius;

        float degreesForDay = 360F / 7F;

        float dayDegrees = dayOfWeek * degreesForDay;

        dayDegrees += (degreesForDay / 24) * hour;

        canvas.drawArc(dayArcOffset, dayArcOffset, Constants.BitmapDimensions - dayArcOffset, Constants.BitmapDimensions - dayArcOffset, 270, dayDegrees, true, _dayArcPaint);

        float minuteDegrees = minute * 6;

        int minuteArcOffset = Constants.BitmapCenter - Constants.MinuteHandLength;

        canvas.drawArc(minuteArcOffset, minuteArcOffset, Constants.BitmapDimensions - minuteArcOffset, Constants.BitmapDimensions - minuteArcOffset, 270, minuteDegrees, false, _minuteTrailPaint);

        float minuteRadians = (float) ((float) ((minute * 6) * (Math.PI / 180)) - Math.PI / 2);

        float hourDegrees = (hour % 12) * 30 + (minute / 2F);

        int hourArcOffset = Constants.BitmapCenter - Constants.HourHandLength;

        canvas.drawArc(hourArcOffset, hourArcOffset, Constants.BitmapDimensions - hourArcOffset, Constants.BitmapDimensions - hourArcOffset, 270, hourDegrees, false, _hourTrailPaint);

        canvas.drawLine(Constants.BitmapCenter, Constants.BitmapCenter, (float) (Constants.BitmapCenter + Math.cos(minuteRadians) * Constants.MinuteHandLength), (float) (Constants.BitmapCenter + Math.sin(minuteRadians) * Constants.MinuteHandLength), _minutePaint);

        float hourRadians = (float) ((float) ((hour * 30 + minute / 2) * (Math.PI / 180)) - Math.PI / 2);

        canvas.drawLine(Constants.BitmapCenter, Constants.BitmapCenter, (float) (Constants.BitmapCenter + Math.cos(hourRadians) * Constants.HourHandLength), (float) (Constants.BitmapCenter + Math.sin(hourRadians) * Constants.HourHandLength), _hourPaint);

        return bitmap;
    }
}
