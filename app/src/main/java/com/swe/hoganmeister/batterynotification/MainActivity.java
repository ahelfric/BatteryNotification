package com.swe.hoganmeister.batterynotification;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences savedTargetValue;

    private TextView batteryLevelTextView;
    private SeekBar batteryBar;
    private EditText batteryTarget;
    private static final String SAVED_TARGET_VALUE_NAME = "TargetValueInfo";
    private Button startServiceButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findviewbyid
        batteryLevelTextView = (TextView)findViewById(R.id.batteryLevelTextView);
        batteryBar = findViewById(R.id.seekBar);
        batteryTarget = findViewById(R.id.targetBatteryLevelInput);
        startServiceButton = findViewById(R.id.startServiceButton);
        startServiceButton = findViewById(R.id.startServiceButton);

        //create intent filter, register filter
        IntentFilter bfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, bfilter);

        //determine and display current battery level
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        double batteryPercent = level / (double)scale;
        String batteryPercentText = batteryPercent + "";
        batteryLevelTextView.setText(batteryPercentText);

        //initialize the shared preferences
        savedTargetValue = getSharedPreferences(SAVED_TARGET_VALUE_NAME, MODE_PRIVATE);

        //start listener methods
        chargeBar();
        chargeTargetListener();
        //startServiceListener(findViewById(android.R.id.content));
        //chargeTarget(); //unused; replaced with chargeTargetListener()


        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, BatteryMonitorService.class);
                serviceIntent.setAction(BatteryMonitorService.ACTION_START_FOREGROUND_SERVICE);
                startService(serviceIntent);
            }
        });

    }
    /*
    private void startServiceListener(View v) {

        switch (v.getId()) {
            case R.id.startServiceButton:
                //start service
                Intent serviceIntent = new Intent(this, BatteryMonitorService.class);
                serviceIntent.setAction(BatteryMonitorService.ACTION_START_FOREGROUND_SERVICE);
                startService(serviceIntent);
        }
    }
    */

    public void chargeBar() {
        final int batteryBarMin = 5;
        final int batteryBarMax = 95; //should be 100 //todo

        batteryBar.setMax(batteryBarMax);
        batteryTarget.setText("" + batteryBarMax, TextView.BufferType.EDITABLE);

        batteryBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue;
            @Override
            public void onProgressChanged(SeekBar batteryBar, int progress, boolean b) {
                progressValue = progress + batteryBarMin;
                batteryTarget.setText("" + progressValue, TextView.BufferType.EDITABLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //unused
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //unused
            }
        });
    }

    public void chargeTargetListener() {
        batteryTarget.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        //indicates the user is done typing
                        /***************/
                        if (!(batteryTarget.getText().toString().equals(null) || batteryTarget.getText().toString().equals(""))) { //should have used android.text.TextUtils.isEmpty
                            int i = Integer.parseInt(batteryTarget.getText().toString());
                            //if ()
                            if (i >= 5 && i <= 100) {
                                batteryBar.setProgress(i - 5);
                            } else {
                                Toast.makeText(getApplicationContext(), "please enter a number between 5 and 100", Toast.LENGTH_LONG).show();
                                if (i > 100) {
                                    batteryTarget.setText("" + 100);
                                    batteryBar.setProgress(95);
                                } else {
                                    batteryTarget.setText("" + 5);
                                    batteryBar.setProgress(0);
                                }

                            }
                        }
                        /***************/
                        return true;
                    }
                }
                return false; //pass to other listeners
            }
        });
    }

    /*
    public void chargeTarget() {
        batteryTarget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //unused
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //unused
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(batteryTarget.getText().toString().equals(null) || batteryTarget.getText().toString().equals(""))) { //should have used android.text.TextUtils.isEmpty
                    int i = Integer.parseInt(editable.toString());
                    //if ()
                    if (i >= 5 && i <= 100) {
                        batteryBar.setProgress(i - 5);
                    } else {
                        Toast.makeText(getApplicationContext(), "please enter a number between 5 and 100", Toast.LENGTH_LONG).show();
                        batteryTarget.setText("" + 5);
                        batteryBar.setProgress(0);
                    }
                }
            }
        });
    }
    */
}
