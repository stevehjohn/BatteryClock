package com.outsidecontextproblem.batteryclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ClockElementConfigurator extends LinearLayout {

    public ClockElementConfigurator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize(context, attrs);
    }

    public ClockElementConfigurator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(context, attrs);
    }

    private void initialize(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.clock_element_configurator_view, this);

        TypedArray properties = context.obtainStyledAttributes(attrs, R.styleable.ClockElementConfigurator);

        TextView textView = findViewById(R.id.textElementTitle);
        textView.setText(properties.getString(R.styleable.ClockElementConfigurator_elementTitle));

        textView = findViewById(R.id.textThickness);
        SeekBar seekBar = findViewById(R.id.seekThickness);

        if (! properties.getBoolean(R.styleable.ClockElementConfigurator_showElementThickness, true)) {
            textView.setVisibility(GONE);
            seekBar.setVisibility(GONE);
        }

        int thickness = properties.getInt(R.styleable.ClockElementConfigurator_elementThickness, 0);
        textView.setText(String.format(getResources().getString(R.string.thickness), thickness));

        seekBar.setProgress(thickness);

        seekBar = findViewById(R.id.seekRed);
        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_redComponent, 22));

        seekBar = findViewById(R.id.seekGreen);
        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_greenComponent, 22));

        seekBar = findViewById(R.id.seekBlue);
        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_blueComponent, 22));

        seekBar = findViewById(R.id.colourOpacity);
        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_colourOpacity, 22));
    }
}
