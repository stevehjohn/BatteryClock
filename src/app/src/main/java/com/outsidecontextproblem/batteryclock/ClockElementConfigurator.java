package com.outsidecontextproblem.batteryclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
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

        int thickness = properties.getInt(R.styleable.ClockElementConfigurator_elementThickness, 0);
        textView = findViewById(R.id.textThickness);
        textView.setText(String.format(getResources().getString(R.string.thickness), thickness));
    }
}
