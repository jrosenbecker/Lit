package com.lit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lit.R;
import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.lit.services.BreatheEffectService;
import com.philips.lighting.hue.sdk.PHHueSDK;

public class ModifyEffectActivity extends AppCompatActivity implements ModifyEffectFragment.OnFragmentInteractionListener {

    // Background actions to perform
    private static final String ACTION_BREATHE = "com.lit.services.action.BREATHE";

    // Background service parameters
    private static final String PARAM_LIGHT_NAME = "com.lit.services.PARAM_LIGHT_NAME";
    private static final String PARAM_ROOM_ID = "com.lit.services.PARAM_ROOM_ID";
    private static final String PARAM_HUE_ID = "com.lit.services.PARAM_HUE_ID";
    private static final String PARAM_START_STOP = "com.lit.services.PARAM_START_STOP";

    private boolean breathe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_effect);

        Toolbar toolbar = (Toolbar) findViewById(R.id.modify_effect_toolbar);
        toolbar.setTitle("Modify Effect");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        TextView lightText = (TextView) findViewById(R.id.modify_effect_light_name);
        lightText.setText(getIntent().getExtras().getString("light"));

        Spinner effectSpinner = (Spinner) findViewById(R.id.modify_effect_spinner);
        effectSpinner.setPrompt(getIntent().getExtras().getString("effect"));

        final String name = getIntent().getExtras().getString("PARAM_LIGHT_NAME");
        final long roomId = getIntent().getExtras().getLong("PARAM_ROOM_ID");
        final String hueId = getIntent().getExtras().getString("PARAM_HUE_ID");

        Button effect = (Button) findViewById(R.id.effect);
        effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("breatheEffect",name + ", " + roomId + ", " + hueId);
                breathe = !(breathe);
                breatheEffect(name, roomId, hueId, breathe);
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.modify_effect_frame_layout, ModifyEffectFragment.newInstance()).commit();
    }

    private void breatheEffect(String name, long roomId, String hueId, boolean start_stop)
    {
        Log.v("breatheEffect","breate boolean: " + breathe);

        BreatheEffectService.on_off = start_stop;

        BreatheEffectService.setContext(this);

        Log.v("BreatheEffectService", "on_off: " + start_stop);

        Intent intent = new Intent(this, BreatheEffectService.class);
        intent.setAction(ACTION_BREATHE);
        intent.putExtra(PARAM_LIGHT_NAME, name);
        intent.putExtra(PARAM_ROOM_ID, roomId);
        intent.putExtra(PARAM_HUE_ID, hueId);
        intent.putExtra(PARAM_START_STOP, start_stop);

        if (start_stop) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Empty listener
    }
}
