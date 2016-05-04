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

    /*This boolean will control if the effect is on or off */
    public static volatile boolean on_off = false;

    /* The Context of the service */
    private static Context context;

    /* Public constructor for creating our service and phHueSDK object */
    public EpilepticService() {
        super("EpilepticService");
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
                    handleActionBreathe(lightName, roomId, hueId);
                } else {
                    Log.v("onHandleIntent","Invalid action passed through Intent");
                }

            } else {
                Log.v("onHandleIntent","Stopping Epileptic service");
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

            boolean test = true;

            /*  Cause an infinite loop so that the effect continues until
                start_stop is set to false */
            while (on_off) {

                /* Set the transition time for the effect */
                state.setTransitionTime(0);

                try {

                    /* Set the brightness of the PHLight */
                    if (test) {
                        light.setBrightness(Light.MAX_BRIGHTNESS - 10);
                    } else {
                        light.setBrightness(Light.MIN_BRIGHTNESS + 10);
                    }

                    test = !test;

                    /* Put the Thread to sleep as to not overload the PHLight */
                    Thread.sleep(250);

                } catch (InterruptedException e) {
                    /* Must have in order to handle a Thread interruption without crashing */
                    Thread.currentThread().interrupt();
                    Log.v("EpilepticService", "The background service has been terminated");
                    e.printStackTrace();
                }
            }

            Log.v("handleActionBreathe", "Stopping service");
            light.setBrightness(Light.MIN_BRIGHTNESS);
            state.setTransitionTime(10);
            stopSelf();

        } else {
            Log.v("handleActionBreathe","Unable to find Light(" + name + ", " + roomId + ", " + hueId +")");
        }
    }
}
