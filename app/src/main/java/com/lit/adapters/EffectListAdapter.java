package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.lit.R;
import com.lit.activities.ModifyEffectActivity;
import com.lit.models.Light;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class EffectListAdapter extends BaseAdapter {

    /**
     * Global variable from the Philips Hue API
     */
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "Lit";

    private Context context;
    //TODO: private List<Light> addEffectList;
    private List<Light> addEffectList;

    //TODO: public EffectListAdapter(Context context, List<Light> addEffectList)
    public EffectListAdapter(Context context, List<Light> addEffectList)
    {
        this.context = context;
        this.addEffectList = addEffectList;
    }

    /**
     * Getter method, get the number of items in the list
     * @return number of items in the list
     */
    @Override
    public int getCount() {
        return addEffectList.size();
    }


    /**
     * Getter method, get the item at a specific position
     * @return item at the given position
     */
    @Override
    public Object getItem(int position) {
        return addEffectList.get(position);
    }


    /**
     * Gets the id of an item at a specific position
     * @param position - index of the item
     * @return id of the item
     */
    @Override
    //TODO: public long getItemId(int position) {return addEffectList.get(position).getId();}
    public long getItemId(int position) {return addEffectList.get(position).getId();}

    /**
     * Gets the view. Used to initialize the view variables
     * @param position - Position of the item
     * @param convertView - convert view
     * @param parent - parent group
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int positionConstant = position;

        phHueSDK = PHHueSDK.create();

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView =  inflater.inflate(R.layout.customize_single_line, parent, false);
        TextView lightName = (TextView) convertView.findViewById(R.id.addEffect_light_name);
        Switch effectEnabledSwitch = (Switch) convertView.findViewById(R.id.customize_effect_switch);
        effectEnabledSwitch.setOnCheckedChangeListener(null);
        effectEnabledSwitch.setChecked(addEffectList.get(position).isEffectOn());

        effectEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    colorCycle(positionConstant);
                else
                    killEffect(positionConstant);
            }
        });


        //TODO: Light addEffectLine = addEffectList.get(position);
        Light addEffectLine = addEffectList.get(position);

        //TODO: lightName.setText(addEffectLine.getLightName());
        lightName.setText(addEffectLine.getLightName());

//        Spinner spinner = (Spinner) convertView.findViewById(R.id.addEffect_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context,
//                R.array.effects_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
//        spinner.setAdapter(spinnerAdapter);

        Button modifyEffect = (Button) convertView.findViewById(R.id.modify_effect_button);

//        TODO: Add a proper effect listener
        modifyEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyEffectActivity.class);
                intent.putExtra("light", ((Light)getItem(positionConstant)).getLightName());
                context.startActivity(intent);
            }
        });
//        modifyEffect.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                randomLights();
//            }
//
//        });

        // you need to have a list of data that you want the spinner to display


        return convertView;
    }

    /**
     * All code found below has been supplied by Philips.
     * All code originates from sample projects found at:
     * TODO: http://www.developers.meethue.com
     */
    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Random rand = new Random();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));
            // To validate your lightstate is valid (before sending to the bridge) you can use:
            // String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener);
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
    }

    private void colorCycle(int position)
    {
        Light tempLight = (Light) getItem(position);
        PHLight phLight = tempLight.getPhLight();
        PHLightState state = phLight.getLastKnownLightState();
        state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);
        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
    }


    private void killEffect(int position)
    {
        Light tempLight = (Light) getItem(position);
        PHLight phLight = tempLight.getPhLight();
        PHLightState state = phLight.getLastKnownLightState();
        state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {

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
