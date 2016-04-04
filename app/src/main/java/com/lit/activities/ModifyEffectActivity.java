package com.lit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lit.R;

public class ModifyEffectActivity extends AppCompatActivity implements ModifyEffectFragment.OnFragmentInteractionListener {

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

        getSupportFragmentManager().beginTransaction().add(R.id.modify_effect_frame_layout, ModifyEffectFragment.newInstance()).commit();



    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Empty listener
    }
}
