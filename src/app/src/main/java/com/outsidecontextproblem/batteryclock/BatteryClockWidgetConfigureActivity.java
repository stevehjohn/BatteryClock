package com.outsidecontextproblem.batteryclock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.outsidecontextproblem.batteryclock.databinding.BatteryClockWidgetConfigureBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class BatteryClockWidgetConfigureActivity extends Activity implements Runnable {

    private final BatteryClockRenderer _batteryClockRenderer;

    private final Handler _handler = new Handler();

    int _appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Settings _settings;

    private final View.OnClickListener _addOnClickListener = view -> {
        final Context context = BatteryClockWidgetConfigureActivity.this;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        BatteryClockWidget.updateAppWidget(context, appWidgetManager, _appWidgetId, _settings);

        _settings.saveSettings(context);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    };

    private final View.OnClickListener _cancelOnClickListener = view -> {
        setResult(RESULT_CANCELED);
        finish();
    };

    @SuppressWarnings("FieldCanBeLocal")
    private BatteryClockWidgetConfigureBinding _binding;

    @SuppressWarnings("FieldCanBeLocal")
    private ClockElementConfigurator.OnClockElementConfiguratorChangeListener _elementListener;

    public BatteryClockWidgetConfigureActivity() {
        super();

        _batteryClockRenderer = new BatteryClockRenderer();
    }

    @SuppressLint("CutPasteId")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        _binding = BatteryClockWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());
        _binding.buttonAdd.setOnClickListener(_addOnClickListener);
        _binding.buttonCancel.setOnClickListener(_cancelOnClickListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            _appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (_appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        Context context = getApplicationContext();

        if (BatteryClockRenderer._typeface == null) {
            BatteryClockRenderer._typeface = context.getResources().getFont(R.font.roboto);
        }

        _settings = new Settings(_appWidgetId);

        EditText editText = findViewById(R.id.inputLabel);
        editText.setText(_settings.getLabel());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                _settings.setLabel(editable.toString());

                updatePreview();
            }
        });

        SeekBar seekBar = findViewById(R.id.seekLabelSize);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                _settings.setLabelSize(i);

                _batteryClockRenderer.updateFromSettings(_settings);

                updatePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SwitchMaterial secondsSwitch = findViewById(R.id.switchSeconds);
        secondsSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            ClockElementConfigurator secondConfigurator = findViewById(R.id.configuratorSeconds);
            secondConfigurator.setVisibility(b ? View.VISIBLE : View.GONE);

            TextView warning = findViewById(R.id.textWarning);
            warning.setVisibility(b ? View.VISIBLE : View.GONE);

            Settings.setUpdateSeconds(b);
        });

        context.getAssets();

        configureTimezones(context);

        applySettingsToView(context);

        _elementListener = this::onElementChanged;

        ClockElementConfigurator clockElementConfigurator = findViewById(R.id.configuratorBattery);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorBezel);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorTicks);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorSeconds);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorMinute);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorMinuteArc);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorHour);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorHourArc);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorWeek);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorBackground);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        clockElementConfigurator = findViewById(R.id.configuratorLabel);
        clockElementConfigurator.setOnClockElementConfiguratorChangeListener(_elementListener);

        if (! serviceIsRunning(context)) {
            Log.i(BatteryClockWidget.class.getName(), "onCreate(): Starting service.");

            Intent serviceIntent = new Intent(context, BatteryClockWidgetService.class);
            context.startForegroundService(serviceIntent);
        }

        updatePreview();

        _handler.postDelayed(this, 1_000);
    }

    @Override
    public void run() {
        updatePreview();

        _handler.postDelayed(this, 1_000);
    }

    private void configureTimezones(Context context) {
        Spinner spinner = findViewById(R.id.spinContinent);

        List<String> continents = new ArrayList<>();

        continents.add("Africa");
        continents.add("America");
        continents.add("Antarctica");
        continents.add("Arctic");
        continents.add("Asia");
        continents.add("Atlantic");
        continents.add("Australia");
        continents.add("Brazil");
        continents.add("Canada");
        continents.add("Chile");
        continents.add("Cuba");
        continents.add("Egypt");
        continents.add("Eire");
        continents.add("Europe");
        continents.add("Iceland");
        continents.add("Indian");
        continents.add("Iran");
        continents.add("Israel");
        continents.add("Jamaica");
        continents.add("Japan");
        continents.add("Kwajalein");
        continents.add("Libya");
        continents.add("Mexico");
        continents.add("Navajo");
        continents.add("Pacific");
        continents.add("Poland");
        continents.add("Portugal");
        continents.add("Singapore");
        continents.add("Turkey");
        continents.add("US");
        continents.add("UTC");
        continents.add("Zulu");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, continents);
        spinner.setAdapter(adapter);

        String[] timezone = _settings.getTimeZone().split("/");

        int index = continents.indexOf(timezone[0]);

        if (index > -1) {
            spinner.setSelection(index);
        }

        continentSelected();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                continentSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void continentSelected() {
        Spinner continentSpinner = findViewById(R.id.spinContinent);

        String selection = (String) continentSpinner.getSelectedItem();

        List<String> locations = new ArrayList<>();

        for (String id : TimeZone.getAvailableIDs()) {
            String[] split = id.split("/");

            if (split.length > 1 && split[0].equals(selection)) {
                locations.add(split[1]);
            }
        }

        if (locations.size() == 0) {
            locations.add(" - ");

            _settings.setTimeZone(selection);
        }

        Spinner locationSpinner = findViewById(R.id.spinLocation);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, locations);
        locationSpinner.setAdapter(adapter);

        String[] timezone = _settings.getTimeZone().split("/");

        if (timezone.length > 1) {
            int index = locations.indexOf(timezone[1]);

            if (index > -1) {
                locationSpinner.setSelection(index);
            }
        }

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locationSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void locationSelected() {
        Spinner continentSpinner = findViewById(R.id.spinContinent);

        String continent = (String) continentSpinner.getSelectedItem();

        Spinner locationSpinner = findViewById(R.id.spinLocation);

        String location = (String) locationSpinner.getSelectedItem();

        if (! location.equals(" - ")) {
            _settings.setTimeZone(String.format("%s/%s", continent, location));
        }
    }

    private void onElementChanged() {
        updatePaints();

        updatePreview();
    }

    private void updatePaints() {
        _batteryClockRenderer.updateFromSettings(_settings);

        ClockElementConfigurator configurator = (ClockElementConfigurator) findViewById(R.id.configuratorBattery);
        updateSettings(_settings.getBatteryLevelIndicatorSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorBezel);
        updateSettings(_settings.getBezelSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorTicks);
        updateSettings(_settings.getTicksSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorSeconds);
        updateSettings(_settings.getSecondsSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorMinute);
        updateSettings(_settings.getMinuteSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorMinuteArc);
        updateSettings(_settings.getMinuteArcSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorHour);
        updateSettings(_settings.getHourSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorHourArc);
        updateSettings(_settings.getHourArcSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorWeek);
        updateSettings(_settings.getWeekSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorBackground);
        updateSettings(_settings.getBackgroundSettings(), configurator);

        configurator = (ClockElementConfigurator) findViewById(R.id.configuratorLabel);
        updateSettings(_settings.getLabelSettings(), configurator);
    }

    private void updateSettings(ElementSettings settings, ClockElementConfigurator configurator) {
        settings.setThickness(configurator.getElementThickness());
        settings.setOpacity(configurator.getOpacity());
        settings.setRed(configurator.getRed());
        settings.setGreen(configurator.getGreen());
        settings.setBlue(configurator.getBlue());
    }

    private void applySettingsToView(Context context) {
        _settings.loadSettings(context);

        configureElement(findViewById(R.id.configuratorBattery), _settings.getBatteryLevelIndicatorSettings());
        configureElement(findViewById(R.id.configuratorBezel), _settings.getBezelSettings());
        configureElement(findViewById(R.id.configuratorTicks), _settings.getTicksSettings());
        configureElement(findViewById(R.id.configuratorSeconds), _settings.getSecondsSettings());
        configureElement(findViewById(R.id.configuratorMinute), _settings.getMinuteSettings());
        configureElement(findViewById(R.id.configuratorMinuteArc), _settings.getMinuteArcSettings());
        configureElement(findViewById(R.id.configuratorHour), _settings.getHourSettings());
        configureElement(findViewById(R.id.configuratorHourArc), _settings.getHourArcSettings());
        configureElement(findViewById(R.id.configuratorWeek), _settings.getWeekSettings());
        configureElement(findViewById(R.id.configuratorBackground), _settings.getBackgroundSettings());
        configureElement(findViewById(R.id.configuratorLabel), _settings.getLabelSettings());

        updatePaints();

        updatePreview();

        String[] timezone = _settings.getTimeZone().split("/");

        Spinner continentSpinner = findViewById(R.id.spinContinent);
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> continentAdapter = (ArrayAdapter<String>) continentSpinner.getAdapter();
        int index = continentAdapter.getPosition(timezone[0]);
        continentSpinner.setSelection(index);

        if (timezone.length > 1) {
            Spinner locationSpinner = findViewById(R.id.spinLocation);
            @SuppressWarnings("unchecked")
            ArrayAdapter<String> locationAdapter = (ArrayAdapter<String>) locationSpinner.getAdapter();
            index = locationAdapter.getPosition(timezone[0]);
            locationSpinner.setSelection(index);
        }

        EditText editText = findViewById(R.id.inputLabel);
        editText.setText(_settings.getLabel());

        SeekBar seekBar = findViewById(R.id.seekLabelSize);
        seekBar.setProgress(_settings.getLabelSize());

        SwitchMaterial switchSeconds = findViewById(R.id.switchSeconds);
        switchSeconds.setChecked(Settings.getUpdateSeconds());

        TextView textWarning = findViewById(R.id.textWarning);
        textWarning.setVisibility(Settings.getUpdateSeconds() ? View.VISIBLE : View.GONE);

        ClockElementConfigurator secondsConfigurator = findViewById(R.id.configuratorSeconds);
        secondsConfigurator.setVisibility(Settings.getUpdateSeconds() ? View.VISIBLE : View.GONE);
    }

    private void configureElement(ClockElementConfigurator configurator, ElementSettings settings) {
        configurator.setElementThickness(settings.getThickness());
        configurator.setOpacity(settings.getOpacity());
        configurator.setRed(settings.getRed());
        configurator.setGreen(settings.getGreen());
        configurator.setBlue(settings.getBlue());

        _batteryClockRenderer.updateFromSettings(_settings);
    }

    private void updatePreview() {

        int seconds = -1;
        if (Settings.getUpdateSeconds()) {
            seconds = Calendar.getInstance().get(Calendar.SECOND);
        }

        Bitmap bitmap = _batteryClockRenderer.render(75, 10, 10, seconds, 3, _settings.getLabel());

        ImageView imageView = findViewById(R.id.imageClock);

        imageView.setImageBitmap(bitmap);
    }

    private boolean serviceIsRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // noinspection deprecation - acceptable to use for locating an app's own service
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (BatteryClockWidgetService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}