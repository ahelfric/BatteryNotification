package com.swe.hoganmeister.batterynotification;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class BatteryMonitorService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICEE = "ACTION_STOP_FOREGROUND_SERVICE";

    private boolean runGenerator;
    private int charge;


    @Override
    public IBinder onBind(Intent intent) {
        //we are not using a bound service, as we want this to run indefinitely
        throw new UnsupportedOperationException(("Not yet implemented"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");
       // onStartCommand();//todo
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent .getAction();

            switch(action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICEE:
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is stopped", Toast.LENGTH_LONG).show();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService() {
        Log.i(TAG_FOREGROUND_SERVICE, "Start foreground service.");
        runGenerator = true;
        Intent intent = new Intent();

        IntentFilter bfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, bfilter);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            while(runGenerator) {
                try {
                    Thread.sleep(1000);
                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    double batteryPercent = level / (double)scale;
                    String batteryPercentText = batteryPercent + "";
                    charge = Log.i(TAG_FOREGROUND_SERVICE, "Thread ID is " + Thread.currentThread().getId() + ", Charge is: " + batteryPercentText);

                } catch (InterruptedException e) {
                    Log.i(TAG_FOREGROUND_SERVICE, "Thread Interrupted.");
                }
            }


        startForegroundService(intent);
    }

    private void stopForegroundService() {
        Log.i(TAG_FOREGROUND_SERVICE, "Stop foreground service.");
        runGenerator = false;
        stopForeground(true);
        stopSelf();
    }
}
