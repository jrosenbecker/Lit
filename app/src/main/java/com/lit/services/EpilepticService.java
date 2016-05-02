package com.lit.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.Random;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EpilepticService extends IntentService {


    /* Background actions to perform */
    private static final String ACTION_SEIZURE = "com.lit.services.action.SEIZURE";

    /* Background service parameters */
    private static final String PARAM_LIGHT_NAME = "com.lit.services.PARAM_LIGHT_NAME";
    private static final String PARAM_ROOM_ID = "com.lit.services.PARAM_ROOM_ID";
    private static final String PARAM_HUE_ID = "com.lit.services.PARAM_HUE_ID";
    private static final String PARAM_START_STOP = "com.lit.services.PARAM_START_STOP";

    //*This boolean will control if the effect is on or off */
    public static volatile boolean on_off = false;
    public static volatile int color = 0;

    /* The Context of the service */
    private static Context context;

    /* The PHHueSDK object for interacting with the light */
    private PHHueSDK phHueSDK;

    /* Public constructor for creating our service and phHueSDK object */
    public EpilepticService() {
        super("EpilepticService");
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

                Log.v("onHandleIntent", "Attempting to start service");

                context.startService(intent);

                final String action = intent.getAction();

                if (ACTION_SEIZURE.equals(action)) {
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
            state.setTransitionTime(1);
            state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_UNKNOWN);

            /*  Cause an infinite loop so that the effect continues until
                start_stop is set to false */
            while (on_off) {

                try {

                    /* Set the brightness of the PHLight */
                    light.setBrightness(Light.MAX_BRIGHTNESS);

                    switch (color) {
                        case 0 : setDistinctColor(light.getPhLight(), Color.RED);
                        case 1 : setDistinctColor(light.getPhLight(), 0xff7a00);
                        case 2 : setDistinctColor(light.getPhLight(), 0xefd000);
                        case 3 : setDistinctColor(light.getPhLight(), 0x00ff1b);
                        case 4 : setDistinctColor(light.getPhLight(), Color.BLUE);
                        case 5 : setDistinctColor(light.getPhLight(), 0x9b00ff);

                    }

                    /* Put the Thread to sleep as to not overload the PHLight */
                    Thread.sleep(500);

                    light.setBrightness(Light.MIN_BRIGHTNESS);

                    /* Put the Thread to sleep as to not overload the PHLight */
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    /* Must have in order to handle a Thread interruption without crashing */
                    Thread.currentThread().interrupt();
                    Log.v("EpilepticService", "The background service has been terminated");
                    e.printStackTrace();
                }
            }

            Log.v("handleActionBreathe", "Stopping service");
            light.setBrightness(Light.MIN_BRIGHTNESS);
            stopSelf();

        } else {
            Log.v("handleActionBreathe","Unable to find Light(" + name + ", " + roomId + ", " + hueId +")");
        }
    }

    private void setDistinctColor(PHLight phLight, int color) {
        float[] xy = PHUtilities.calculateXY(color, phLight.getModelNumber());
        PHLightState state = phLight.getLastKnownLightState();
        state.setX(xy[0]);
        state.setY(xy[1]);
        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
    }
}
