package com.lit.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;
//both off 24 windows closed
//
//place on coffee table in middle of room
//ideal light sensor value is 35-40

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class PowerSaveService extends IntentService implements SensorEventListener {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_POWERSAVE = "com.lit.services.action.POWERSAVE";
    /* Background service parameters */
//    private static final String PARAM_LIGHT_NAME = "com.lit.services.PARAM_LIGHT_NAME";
//    private static final String PARAM_ROOM_ID = "com.lit.services.PARAM_ROOM_ID";
//    private static final String PARAM_HUE_ID = "com.lit.services.PARAM_HUE_ID";
    private static final String PARAM_START_STOP = "com.lit.services.PARAM_START_STOP";
    private static double changeCounter = 0;
    private static double lightAverage = 0;
    private static double lightSum = 0;
    private static int minLux = 35;
    private static int maxLux = 50;
    public static float currentLuxReadings;
    private Light light;

    private static ArrayList<Double> lightValues = new ArrayList<>();

    //*This boolean will control if the effect is on or off */
    public static volatile boolean on_off = false;

    /* The Context of the service */
    private static Context context;

    /* The PHHueSDK object for interacting with the light */
    private PHHueSDK phHueSDK;

    private SensorManager mSensorManager;
    private Sensor lightSensor;
    // private TextView textLIGHT_reading = (TextView)findViewById(R.id.LIGHT_reading);;


    public PowerSaveService() {
        super("PowerSaveService");
        phHueSDK = PHHueSDK.create();
    }



    /* Sets the context of the service, crucial for starting the service */
    public static void setContext(Context incomingContext) {
        context = incomingContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(context, "Starting power save service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        stopSelf();
        Toast.makeText(context, "Destroying power save service", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            if (on_off) {

                Log.v("onHandleIntent","Attempting to start service");

                context.startService(intent);

                final String action = intent.getAction();

                if (ACTION_POWERSAVE.equals(action)) {
//                    final String lightName = intent.getStringExtra(PARAM_LIGHT_NAME);
//                    final long roomId = intent.getLongExtra(PARAM_ROOM_ID, (long) 0);
//                    final String hueId = intent.getStringExtra(PARAM_HUE_ID);
                    //final boolean start_stop = intent.getBooleanExtra(PARAM_START_STOP, false);
//                    light = DatabaseUtility.getLight(lightName, roomId, hueId);
                } else {
                    Log.v("onHandleIntent","Invalid action passed through Intent");
                }

            } else {
                Log.v("onHandleIntent","Stopping service");
                context.stopService(intent);
                stopSelf();
            }

        } else {
            Log.v("onHandleIntent","Invalid intent");
        }
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Do something with this sensor data.
        //textLIGHT_reading.setText("LIGHT: " + event.values[0]);
        currentLuxReadings = event.values[0];
        if(on_off) {
//            Log.w("myApp", "Light: " + event.values[0]);

            changeCounter++;
            lightSum += event.values[0];
            lightAverage = lightSum / changeCounter;
            if (changeCounter == 25) {

                changeBrightness();
            }
        }

    }

    public static void setLuxRange(int min, int max)
    {
        minLux = min;
        maxLux = max;
    }

    private void changeBrightness() {

        for(Light light : DatabaseUtility.getAllPowerSaveEnabledLights()) {
            if(lightAverage < minLux)
            {
                Log.w("myApp", "Increasing brightness, Lux = " + lightAverage);
                light.setBrightness(light.getBrightness() + 10);
            } else if(lightAverage > maxLux)
            {
                Log.w("myApp", "Decreasing brightness, Lux = " + lightAverage);
                light.setBrightness(light.getBrightness() - 10);
            }
        }
        changeCounter = 0;
        lightSum = 0;
        lightAverage = 0;



    }
   /*private class SensorEventLoggerTask extends
            AsyncTask<SensorEvent, Void, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
            SensorEvent event = events[0];
            // log the value
            return null;
        }
    }*/

}
