package com.lit.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.lit.R;
import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.lit.services.BreatheEffectService;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class ModifyEffectActivity extends AppCompatActivity implements ModifyEffectFragment.OnFragmentInteractionListener {

    // Background actions to perform
    private static final String ACTION_BREATHE = "com.lit.services.action.BREATHE";

    // Background service parameters
    private static final String PARAM_LIGHT_NAME = "com.lit.services.PARAM_LIGHT_NAME";
    private static final String PARAM_ROOM_ID = "com.lit.services.PARAM_ROOM_ID";
    private static final String PARAM_HUE_ID = "com.lit.services.PARAM_HUE_ID";
    private static final String PARAM_START_STOP = "com.lit.services.PARAM_START_STOP";

    private static final String BREATHE_EFFECT = "BREATHE";
    private static final String CYCLE_EFFECT = "COLOR_CYCLE";

    private PHHueSDK phHueSDK;

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_effect);

        phHueSDK = PHHueSDK.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.modify_effect_toolbar);
        toolbar.setTitle("Modify Effect");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final String lightName = getIntent().getExtras().getString("PARAM_LIGHT_NAME");
        final long roomId = getIntent().getExtras().getLong("PARAM_ROOM_ID");
        final String hueId = getIntent().getExtras().getString("PARAM_HUE_ID");

        TextView lightNameText = (TextView) findViewById(R.id.modify_effect_light_name);
        lightNameText.setText(lightName);

        TextView lightRoomText = (TextView) findViewById(R.id.modify_effect_light_room);
        lightRoomText.setText("" + DatabaseUtility.getRoom(this, roomId).getName());

        setHueSliders(lightName,roomId,hueId);

        setEffectSwitches(lightName,roomId,hueId);

        getSupportFragmentManager().beginTransaction().add(R.id.modify_effect_frame_layout, ModifyEffectFragment.newInstance()).commit();
    }

    private void setHueSliders(final String name, final long roomId, final String hueId)
    {
        Light light = DatabaseUtility.getLight(name, roomId, hueId);

        final SeekBar brightnessSlider = (SeekBar) findViewById(R.id.modify_effect_brightness_seekBar);
        brightnessSlider.setProgress(light.getBrightness());
        final SeekBar redSlider = (SeekBar) findViewById(R.id.modify_effect_red_seekBar);
        redSlider.setProgress(light.getRed());
        final SeekBar greenSlider = (SeekBar) findViewById(R.id.modify_effect_green_seekBar);
        greenSlider.setProgress(light.getGreen());
        final SeekBar blueSlider = (SeekBar) findViewById(R.id.modify_effect_blue_seekBar);
        blueSlider.setProgress(light.getBlue());

        brightnessSlider.setMax(Light.MAX_BRIGHTNESS);
        redSlider.setMax(Light.MAX_COLOR_VALUE);
        greenSlider.setMax(Light.MAX_COLOR_VALUE);
        blueSlider.setMax(Light.MAX_COLOR_VALUE);

        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Light light = DatabaseUtility.getLight(name,roomId,hueId);
                if (light != null) {
                    light.setColor(redSlider.getProgress(), greenSlider.getProgress(), blueSlider.getProgress());
                    light.setBrightness(brightnessSlider.getProgress());
                } else {
                    Log.v("ModifyEffectActivity","Unable to find light for hue/brightness setting");
                }
            }
        };

        redSlider.setOnSeekBarChangeListener(seekBarListener);
        greenSlider.setOnSeekBarChangeListener(seekBarListener);
        blueSlider.setOnSeekBarChangeListener(seekBarListener);
        brightnessSlider.setOnSeekBarChangeListener(seekBarListener);
    }

    private void setEffectSwitches(final String lightName, final long roomId, final String hueId)
    {
        final Switch colorCycle = (Switch) findViewById(R.id.modify_effect_colorCycle);
        final Switch breathe = (Switch) findViewById(R.id.modify_effect_breathe);

        colorCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colorCycle.isChecked()) {
                    breathe.setChecked(false);
                    colorCycleEffect(lightName, roomId, hueId);
                    breatheEffect(lightName, roomId, hueId, false);
                    if (!DatabaseUtility.updateLightEffect(context,CYCLE_EFFECT,true,hueId)) {
                        Log.v("setColorCycle","Unable to turn on color cycle for light: " + hueId);
                    }
                } else {
                    killEffect(lightName, roomId, hueId);
                    if (!DatabaseUtility.updateLightEffect(context,CYCLE_EFFECT,false,hueId)) {
                        Log.v("setColorCycle","Unable to turn off color cycle for light: " + hueId);
                    }
                }
            }
        });

        breathe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (breathe.isChecked()) {
                    colorCycle.setChecked(false);
                    breatheEffect(lightName, roomId, hueId, true);
                    if (!DatabaseUtility.updateLightEffect(context,BREATHE_EFFECT,true,hueId)) {
                        Log.v("setBreatheEffect","Unable to turn on breathe for light: " + hueId);
                    }
                } else {
                    breatheEffect(lightName, roomId, hueId, false);
                    if (!DatabaseUtility.updateLightEffect(context,BREATHE_EFFECT,false,hueId)) {
                        Log.v("setBreatheEffect","Unable to turn off breathe for light: " + hueId);
                    }
                }
            }
        });

        colorCycle.setChecked(DatabaseUtility.getLightEffect(CYCLE_EFFECT, hueId));
        breathe.setChecked(DatabaseUtility.getLightEffect(BREATHE_EFFECT,hueId));
    }

    private void colorCycleEffect(String name, long roomId, String hueId)
    {
        Light light = DatabaseUtility.getLight(name,roomId,hueId);

        if (light != null) {
            PHLight phLight = light.getPhLight();
            PHLightState state = phLight.getLastKnownLightState();
            state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);
            phHueSDK.getSelectedBridge().updateLightState(phLight, state);
        } else {
            Log.v("colorCycle","Could not find Light(" + name + ", " + roomId + ", " + hueId + ")");
        }
    }

    private void breatheEffect(String name, long roomId, String hueId, boolean start_stop)
    {
        if (BreatheEffectService.on_off) {
            BreatheEffectService.on_off = false;
        } else {
            BreatheEffectService.on_off= true;
        }

        BreatheEffectService.setContext(this);

        Intent intent = new Intent(this, BreatheEffectService.class);
        intent.setAction(ACTION_BREATHE);
        intent.putExtra(PARAM_LIGHT_NAME, name);
        intent.putExtra(PARAM_ROOM_ID, roomId);
        intent.putExtra(PARAM_HUE_ID, hueId);
        //intent.putExtra(PARAM_START_STOP, start_stop);

        if (start_stop) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    private void killEffect(String name, long roomId, String hueId)
    {
        Light light = DatabaseUtility.getLight(name,roomId,hueId);
        PHLight phLight = light.getPhLight();
        PHLightState state = phLight.getLastKnownLightState();
        state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Empty listener
    }
}
