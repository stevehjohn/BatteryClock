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

    private SeekBar.OnSeekBarChangeListener _onSeekBarChangeListener;
    private OnClockElementConfiguratorChangeListener _onClockElementConfiguratorChangeListener;

    public ClockElementConfigurator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize(context, attrs);

        initializeEvents();
    }

    public ClockElementConfigurator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(context, attrs);

        initializeEvents();
    }

    private void initializeEvents() {
        _onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                raiseChange();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        SeekBar seekBar = findViewById(R.id.seekThickness);
        seekBar.setOnSeekBarChangeListener(_onSeekBarChangeListener);

        seekBar = findViewById(R.id.seekRed);
        seekBar.setOnSeekBarChangeListener(_onSeekBarChangeListener);

        seekBar = findViewById(R.id.seekGreen);
        seekBar.setOnSeekBarChangeListener(_onSeekBarChangeListener);

        seekBar = findViewById(R.id.seekBlue);
        seekBar.setOnSeekBarChangeListener(_onSeekBarChangeListener);

        seekBar = findViewById(R.id.colourOpacity);
        seekBar.setOnSeekBarChangeListener(_onSeekBarChangeListener);
    }

    private void raiseChange() {
        if (_onClockElementConfiguratorChangeListener != null) {
            _onClockElementConfiguratorChangeListener.onDataChanged();
        }
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

        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_elementThickness, 0));

        seekBar = findViewById(R.id.seekRed);
        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_redComponent, 22));

        seekBar = findViewById(R.id.seekGreen);
        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_greenComponent, 22));

        seekBar = findViewById(R.id.seekBlue);
        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_blueComponent, 22));

        seekBar = findViewById(R.id.colourOpacity);
        seekBar.setProgress(properties.getInt(R.styleable.ClockElementConfigurator_colourOpacity, 22));
    }

    public void setOnClockElementConfiguratorChangeListener(OnClockElementConfiguratorChangeListener listener) {
        _onClockElementConfiguratorChangeListener = listener;
    }

    public interface OnClockElementConfiguratorChangeListener {
        void onDataChanged();
    }
}
