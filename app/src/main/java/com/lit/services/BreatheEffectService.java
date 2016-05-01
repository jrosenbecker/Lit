package com.lit.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * The BreatheEffectService is an IntentService subclass for handling asynchronous
 * updates tp the PHLight for gradually brightening and dimming the light's intensity.
 *
 * Created by Graham Nygard on 4/29/2016
 *
 */
public class BreatheEffectService extends IntentService {

    /* Background actions to perform */
    private static final String ACTION_BREATHE = "com.lit.services.action.BREATHE";

    /* Background service parameters */
    private static final String PARAM_LIGHT_NAME = "com.lit.services.PARAM_LIGHT_NAME";
    private static final String PARAM_ROOM_ID = "com.lit.services.PARAM_ROOM_ID";
    private static final String PARAM_HUE_ID = "com.lit.services.PARAM_HUE_ID";
    private static final String PARAM_START_STOP = "com.lit.services.PARAM_START_STOP";

    //*This boolean will control if the effect is on or off */
    public static volatile boolean on_off = false;

    /* The Context of the service */
    private static Context context;

    /* The PHHueSDK object for interacting with the light */
    private PHHueSDK phHueSDK;

    /* Public constructor for creating our service and phHueSDK object */
    public BreatheEffectService() {
        super("BreatheEffectService");
        phHueSDK = PHHueSDK.create();
    }

    /* Sets the context of the service, crucial for starting the service */
    public static void setContext(Context incomingContext) {
        context = incomingContext;
    }

    /**
     * Starts this service to perform Breathe effect with the given parameters.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            if (on_off) {

                Log.v("onHandleIntent","Attempting to start service");

                context.startService(intent);

                final String action = intent.getAction();

                if (ACTION_BREATHE.equals(action)) {
                    final String lightName = intent.getStringExtra(PARAM_LIGHT_NAME);
                    final long roomId = intent.getLongExtra(PARAM_ROOM_ID, (long) 0);
                    final String hueId = intent.getStringExtra(PARAM_HUE_ID);
                    //final boolean start_stop = intent.getBooleanExtra(PARAM_START_STOP, false);
                    handleActionBreathe(lightName, roomId, hueId);
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

    /**
     * Handle Breathe effect in the background thread with the provided parameters.
     */
    private void handleActionBreathe(String name, long roomId, String hueId) {

        Light light = DatabaseUtility.getLight(name, roomId, hueId);

        /* Perform the action only if a light has been found */
        if (light != null) {

            PHLight phLight = light.getPhLight();
            PHLightState state = phLight.getLastKnownLightState();
            state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_UNKNOWN);

            /*  These variables will be responsible for monitoring the brightness of the
                bulb and how dramatically that intensity changes */
            //int increment = (int) Math.floor((Light.MAX_BRIGHTNESS - Light.MIN_BRIGHTNESS) / 10);
            int increment = (int) Math.floor((Light.MAX_BRIGHTNESS - Light.MIN_BRIGHTNESS) / 25);
            int intensity = Light.MIN_BRIGHTNESS;

            /* Initiate the light to minimum brightness for the effect */
            light.setBrightness(Light.MIN_BRIGHTNESS);

            /*  Cause an infinite loop so that the effect continues until
                start_stop is set to false */
            while (on_off) {

                /*  Make sure that the intensity is within the MIN-MAX
                    values of the PHLight brightness spectrum */
                if (intensity >= Light.MAX_BRIGHTNESS) {
                    intensity = Light.MAX_BRIGHTNESS;
                    increment = -increment;
                } else if (intensity <= Light.MIN_BRIGHTNESS) {
                    intensity = Light.MIN_BRIGHTNESS;
                    increment = -increment;
                }

                try {

                    /* Set the brightness of the PHLight */
                    light.setBrightness(intensity);

                    /* Put the Thread to sleep as to not overload the PHLight */
                    Thread.sleep(150);

                    /* Alter the brightness by the increment to cause the gradient effect */
                    intensity += increment;

                } catch (InterruptedException e) {
                    /* Must have in order to handle a Thread interruption without crashing */
                    Thread.currentThread().interrupt();
                    Log.v("BreatheEffectService", "The background service has been terminated");
                    e.printStackTrace();
                }
            }

            Log.v("handleActionBreathe","Stopping service");
            light.setBrightness(Light.MIN_BRIGHTNESS);
            stopSelf();

        } else {
            Log.v("handleActionBreathe","Unable to find Light(" + name + ", " + roomId + ", " + hueId +")");
        }
    }
}
