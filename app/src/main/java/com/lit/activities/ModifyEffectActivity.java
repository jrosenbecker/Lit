package com.lit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.lit.R;
import com.lit.constants.TabConstants;
import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.lit.services.BreatheEffectService;
import com.lit.services.EpilepticService;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class ModifyEffectActivity extends AppCompatActivity {

    // Background actions to perform
    private static final String ACTION_BREATHE = "com.lit.services.action.BREATHE";
    private static final String ACTION_SEIZURE = "com.lit.services.action.SEIZURE";

    // Background service parameters
    private static final String PARAM_LIGHT_NAME = "com.lit.services.PARAM_LIGHT_NAME";
    private static final String PARAM_ROOM_ID = "com.lit.services.PARAM_ROOM_ID";
    private static final String PARAM_HUE_ID = "com.lit.services.PARAM_HUE_ID";
    private static final String PARAM_START_STOP = "com.lit.services.PARAM_START_STOP";

    private static final String BREATHE_EFFECT = "BREATHE";
    private static final String CYCLE_EFFECT = "COLOR_CYCLE";
    private static final String EPILEPTIC_EFFECT = "SEIZURE";

    private PHHueSDK phHueSDK;

    private final Context context = this;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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

        Light test = DatabaseUtility.getLight(lightName,roomId,hueId);
        Log.v("light colors", "Red: " + test.getRed() + " Green: " + test.getGreen() + " Blue: " + test.getBlue());

        TextView lightNameText = (TextView) findViewById(R.id.modify_effect_light_name);
        lightNameText.setText(lightName);

        try {
            TextView lightRoomText = (TextView) findViewById(R.id.modify_effect_light_room);
            lightRoomText.setText("" + DatabaseUtility.getRoom(this, roomId).getName());
        } catch (Exception e) {
            Log.v("onCreate","Rerouting back to MainActivity");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        setEffectSwitches(lightName, roomId, hueId);

        setColorButtons(lightName, roomId, hueId);

        setHueSliders(lightName, roomId, hueId);

    }

    private void setEffectSwitches(final String lightName, final long roomId, final String hueId)
    {
        final Switch colorCycle = (Switch) findViewById(R.id.modify_effect_colorCycle);
        final Switch breathe = (Switch) findViewById(R.id.modify_effect_breathe);
        final Switch seizure = (Switch) findViewById(R.id.modify_effect_seizure);

        colorCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (colorCycle.isChecked()) {

                    breathe.setChecked(false);
                    seizure.setChecked(false);

                    BreatheEffectService.on_off = false;
                    EpilepticService.on_off = false;

                    colorCycleEffect(lightName, roomId, hueId);

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
                    seizure.setChecked(false);

                    EpilepticService.on_off = false;

                    breatheEffect(lightName, roomId, hueId, true);

                    if (!DatabaseUtility.updateLightEffect(context, BREATHE_EFFECT, true, hueId)) {
                        Log.v("setBreatheEffect", "Unable to turn on breathe for light: " + hueId);
                    }
                } else {
                    breatheEffect(lightName, roomId, hueId, false);
                    if (!DatabaseUtility.updateLightEffect(context, BREATHE_EFFECT, false, hueId)) {
                        Log.v("setBreatheEffect", "Unable to turn off breathe for light: " + hueId);
                    }
                }
            }
        });

        seizure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (seizure.isChecked()) {

                    colorCycle.setChecked(false);
                    breathe.setChecked(false);

                    BreatheEffectService.on_off = false;

                    epilepticEffect(lightName, roomId, hueId, true);

                    if (!DatabaseUtility.updateLightEffect(context, EPILEPTIC_EFFECT, true, hueId)) {
                        Log.v("setBreatheEffect", "Unable to turn on breathe for light: " + hueId);
                    }
                } else {
                    epilepticEffect(lightName, roomId, hueId, false);
                    if (!DatabaseUtility.updateLightEffect(context, BREATHE_EFFECT, false, hueId)) {
                        Log.v("setBreatheEffect", "Unable to turn off breathe for light: " + hueId);
                    }
                }
            }
        });

        colorCycle.setChecked(DatabaseUtility.getLightEffect(CYCLE_EFFECT, hueId));
        breathe.setChecked(DatabaseUtility.getLightEffect(BREATHE_EFFECT, hueId));
        seizure.setChecked(DatabaseUtility.getLightEffect(EPILEPTIC_EFFECT, hueId));
    }

    private void setColorButtons(final String name, final long roomId, final String hueId)
    {
        final Light light = DatabaseUtility.getLight(name, roomId, hueId);

        final Button redButton = (Button) findViewById(R.id.setRed);
        final Button orangeButton = (Button) findViewById(R.id.setOrange);
        final Button yellowButton = (Button) findViewById(R.id.setYellow);
        final Button greenButton = (Button) findViewById(R.id.setGreen);
        final Button blueButton = (Button) findViewById(R.id.setBlue);
        final Button purpleButton = (Button) findViewById(R.id.setPurple);
        final Button whiteButton = (Button) findViewById(R.id.resetLight);

        View.OnClickListener redSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killEffect(name, roomId, hueId);
                setDistinctColor(light.getPhLight(), Color.RED);
                setSliderValues(0xff, 0x00, 0x00);
            }
        };

        View.OnClickListener orangeSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killEffect(name, roomId, hueId);
                setDistinctColor(light.getPhLight(), 0xff7a00);
                setSliderValues(0xff, 0x7a, 0x00);
            }
        };

        View.OnClickListener yellowSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killEffect(name, roomId, hueId);
                setDistinctColor(light.getPhLight(), 0xefd000);
                setSliderValues(0xef, 0xd0, 0x00);
            }
        };

        View.OnClickListener greenSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killEffect(name, roomId, hueId);
                setDistinctColor(light.getPhLight(), 0x00ff1b);
                setSliderValues(0x00, 0xff, 0x1b);
            }
        };

        View.OnClickListener blueSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killEffect(name, roomId, hueId);
                setDistinctColor(light.getPhLight(), Color.BLUE);
                setSliderValues(0x00, 0x00, 0xff);
            }
        };

        View.OnClickListener purpleSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killEffect(name, roomId, hueId);
                setDistinctColor(light.getPhLight(), 0x9b00ff);
                setSliderValues(0x9b, 0x00, 0xff);
            }
        };

        View.OnClickListener whiteSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killEffect(name, roomId, hueId);
                resetEffect(hueId);
                setDistinctColor(light.getPhLight(), 0xfecd75);
                setSliderValues(0xfe, 0xcd, 0x75);
            }
        };

        redButton.setOnClickListener(redSelector);
        orangeButton.setOnClickListener(orangeSelector);
        yellowButton.setOnClickListener(yellowSelector);
        greenButton.setOnClickListener(greenSelector);
        blueButton.setOnClickListener(blueSelector);
        purpleButton.setOnClickListener(purpleSelector);
        whiteButton.setOnClickListener(whiteSelector);

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
                } else {
                    Log.v("ModifyEffectActivity","Unable to find light for hue/brightness setting");
                }
            }
        };

        SeekBar.OnSeekBarChangeListener brightnessListener = new SeekBar.OnSeekBarChangeListener() {
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
                    light.setBrightness(brightnessSlider.getProgress());
                } else {
                    Log.v("ModifyEffectActivity","Unable to find light for hue/brightness setting");
                }
            }
        };

        redSlider.setOnSeekBarChangeListener(seekBarListener);
        greenSlider.setOnSeekBarChangeListener(seekBarListener);
        blueSlider.setOnSeekBarChangeListener(seekBarListener);
        brightnessSlider.setOnSeekBarChangeListener(brightnessListener);
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
            Log.v("colorCycle", "Could not find Light(" + name + ", " + roomId + ", " + hueId + ")");
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

        if (start_stop) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    private void epilepticEffect(String name, long roomId, String hueId, boolean start_stop)
    {
        if (EpilepticService.on_off) {
            EpilepticService.on_off = false;
        } else {
            EpilepticService.on_off= true;
        }

        EpilepticService.setContext(this);

        Intent intent = new Intent(this, EpilepticService.class);
        intent.setAction(ACTION_SEIZURE);
        intent.putExtra(PARAM_LIGHT_NAME, name);
        intent.putExtra(PARAM_ROOM_ID, roomId);
        intent.putExtra(PARAM_HUE_ID, hueId);

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

    private void resetEffect(String hueId) {

        final Switch colorCycle = (Switch) findViewById(R.id.modify_effect_colorCycle);
        final Switch breathe = (Switch) findViewById(R.id.modify_effect_breathe);
        final Switch seizure = (Switch) findViewById(R.id.modify_effect_seizure);

        colorCycle.setChecked(false);
        breathe.setChecked(false);
        seizure.setChecked(false);

        BreatheEffectService.on_off = false;
        EpilepticService.on_off = false;

        if (!DatabaseUtility.updateLightEffect(context,CYCLE_EFFECT,false,hueId)) {
            Log.v("setColorCycle","Unable to turn off color cycle for light: " + hueId);
        }
        if (!DatabaseUtility.updateLightEffect(context,BREATHE_EFFECT,false,hueId)) {
            Log.v("setColorCycle","Unable to turn off breathe for light: " + hueId);
        }
        if (!DatabaseUtility.updateLightEffect(context,EPILEPTIC_EFFECT,false,hueId)) {
            Log.v("setColorCycle","Unable to turn off party for light: " + hueId);
        }
    }

    private void setDistinctColor(PHLight phLight, int color) {
        float[] xy = PHUtilities.calculateXY(color, phLight.getModelNumber());
        PHLightState state = phLight.getLastKnownLightState();
        state.setX(xy[0]);
        state.setY(xy[1]);
        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
    }

    private void setSliderValues(int red, int green, int blue) {
        final SeekBar redSlider = (SeekBar) findViewById(R.id.modify_effect_red_seekBar);
        redSlider.setProgress(red);
        final SeekBar greenSlider = (SeekBar) findViewById(R.id.modify_effect_green_seekBar);
        greenSlider.setProgress(green);
        final SeekBar blueSlider = (SeekBar) findViewById(R.id.modify_effect_blue_seekBar);
        blueSlider.setProgress(blue);
    }
}
