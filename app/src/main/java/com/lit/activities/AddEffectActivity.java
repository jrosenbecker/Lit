package com.lit.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.lit.R;
import com.lit.adapters.EffectListAdapter;
import com.lit.models.Light;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.util.Log;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;

public class AddEffectActivity extends AppCompatActivity {

    private ListView addEffectListView;
    private EffectListAdapter adapter;
    //TODO: private List<Light> addEffectList;
    private List<Light> addEffectList;

    private PHHueSDK phHueSDK;
    public static final String TAG = "Lit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_effect);

        // Gets an instance of the Hue SDK.
        phHueSDK = PHHueSDK.create();

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_effect_activity_toolbar);
        toolbar.setTitle("Add Effect");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        
        addEffectListView = (ListView) findViewById(R.id.addEffect_list_view);

        //TODO: addEffectList = new ArrayList<Light>();
        // For now, we will be displaying the connected 'bridges' rather than
        // the lights connected to those bridges. When we get another PH light
        // we can then figure out how to discern between the light on a bridge

        addEffectList = new ArrayList<Light>();
        adapter = new EffectListAdapter(this, addEffectList);
        addEffectListView.setAdapter(adapter);


        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            addEffectList.add(new Light(light.getName(), light, phHueSDK));
        }
        // TODO: Remove these additions, used currently for testing purposes
//        addEffectList.add(new Light(1, "Light 1", false, true, 0));
//        addEffectList.add(new Light(2, "Light 2", false, true, 1));
//        addEffectList.add(new Light(3, "Light 3", false, true, 2));
//        addEffectList.add(new Light(4, "Light 4", false, true, 3));
//        addEffectList.add(new Light(5, "Light 5", false, true, 3));
//        addEffectList.add(new Light(6, "Light 6", false, true, 2));
//        addEffectList.add(new Light(7, "Light 7", false, true, 1));
//        addEffectList.add(new Light(3, "Light 8", false, true, 0));
//        addEffectList.add(new Light(3, "Light 9", false, false, 1));
//        addEffectList.add(new Light(3, "Light 10", false, false, 2));
        adapter.notifyDataSetChanged();
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener lightListener = new PHLightListener() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
           Log.w(TAG, "Light has updated");
        }

        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };


}
