package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lit.R;
import com.lit.activities.ModifyEffectActivity;
import com.lit.models.Light;
import com.lit.models.Room;
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


public class CustomizeAdapter extends BaseExpandableListAdapter {

    /**
     * Global variable from the Philips Hue API
     */
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "Lit";

    private Context context;
    //TODO: private List<Light> roomList;
    private List<Room> roomList;

    //TODO: public CustomizeAdapter(Context context, List<Light> roomList)
    public CustomizeAdapter(Context context, List<Room> roomList)
    {
        this.context = context;
        this.roomList = roomList;
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

    private void colorCycle(final int roomIndex, final int lightIndex)
    {
        Light tempLight = (Light) getChild(roomIndex, lightIndex);
        PHLight phLight = tempLight.getPhLight();
        PHLightState state = phLight.getLastKnownLightState();
        state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);
        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
    }


    private void killEffect(final int roomIndex, final int lightIndex)
    {
        Light tempLight = (Light) getChild(roomIndex, lightIndex);
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

    @Override
    public int getGroupCount() {
        return roomList.size();
    }

    @Override
    public int getChildrenCount(int roomIndex) {
        return roomList.get(roomIndex).getLights().size();
    }

    @Override
    public Object getGroup(int roomIndex) {
        return roomList.get(roomIndex);
    }

    @Override
    public Object getChild(int roomIndex, int lightIndex) {
        return roomList.get(roomIndex).getLights().get(lightIndex);
    }

    @Override
    public long getGroupId(int roomIndex) {
        return roomList.get(roomIndex).getId();
    }

    @Override
    public long getChildId(int roomIndex, int lightIndex) {
        return roomList.get(roomIndex).getLights().get(lightIndex).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(R.layout.room_layout, null);

        TextView roomText = (TextView) view.findViewById(R.id.room_name);
        roomText.setText(((Room) getGroup(i)).getName());

        return view;
    }

    @Override
    public View getChildView(int roomIndex, int lightIndex, boolean b, View view, ViewGroup viewGroup) {

        phHueSDK = PHHueSDK.create();

        final int constRoomIndex = roomIndex;
        final int constLightIndex = lightIndex;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view =  inflater.inflate(R.layout.customize_single_line, viewGroup, false);
        TextView lightName = (TextView) view.findViewById(R.id.addEffect_light_name);
        Switch effectEnabledSwitch = (Switch) view.findViewById(R.id.customize_effect_switch);
        effectEnabledSwitch.setOnCheckedChangeListener(null);
        effectEnabledSwitch.setChecked(((Light) getChild(roomIndex, lightIndex)).isEffectOn());

        effectEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    colorCycle(constRoomIndex, constLightIndex);
                else
                    killEffect(constRoomIndex, constLightIndex);
            }
        });


        Light light = (Light) getChild(roomIndex, lightIndex);


        lightName.setText(light.getLightName());

        Button modifyEffect = (Button) view.findViewById(R.id.modify_effect_button);


        modifyEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyEffectActivity.class);
                intent.putExtra("light", ((Light)getChild(constRoomIndex, constLightIndex)).getLightName());
                context.startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
