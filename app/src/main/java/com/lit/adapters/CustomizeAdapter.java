package com.lit.adapters;

import android.app.Activity;
import android.app.Service;
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
import com.lit.constants.TabConstants;
import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.lit.models.Room;
import com.lit.services.BreatheEffectService;
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

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_BREATHE = "com.lit.services.action.BREATHE";
    private static final String ACTION_BAZ = "com.lit.services.action.BAZ";

    // TODO: Rename parameters
    private static final String PARAM_LIGHT_NAME = "PARAM_LIGHT_NAME";
    private static final String PARAM_ROOM_ID = "PARAM_ROOM_ID";
    private static final String PARAM_HUE_ID = "PARAM_HUE_ID";
    private static final String PARAM_START_STOP = "PARAM_START_STOP";

    /**
     * Global variable from the Philips Hue API
     */
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "Lit";

    private Context context;
    //TODO: private List<Light> roomList;
    private List<Room> roomList;

    private BreatheEffectService breatheEffect;

    private boolean breathe = false;

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

    // TODO: Use a service to kick off this effect to prevent an infinite loop
    private void breatheEffect(final int roomIndex, final int lightIndex)
    {
        if (!breathe) {
            Light tempLight = (Light) getChild(roomIndex, lightIndex);
            //startService(new Intent(context, BreatheEffectService.class));
            //BreatheEffectService.actionBreathe(context, tempLight.getLightName(), roomIndex, tempLight.getHueId(), breathe);
        } else {
            breatheEffect.stopSelf();
            breatheEffect.onDestroy();
        }
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
    public View getGroupView(int roomIndex, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(R.layout.room_layout, null);

        TextView roomText = (TextView) view.findViewById(R.id.room_name);
        roomText.setText(((Room) getGroup(roomIndex)).getName());
        if(roomIndex%2 == 1)
        {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else
        {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        return view;
    }

    @Override
    public View getChildView(final int roomIndex, final int lightIndex, boolean b, View view, ViewGroup viewGroup) {

        phHueSDK = PHHueSDK.create();

        final int constRoomIndex = roomIndex;
        final int constLightIndex = lightIndex;

        Log.v("getViewChild","roomIndex: " + roomIndex + " lightIndex: " + lightIndex);

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


        final Light light = (Light) getChild(roomIndex, lightIndex);

        lightName.setText(light.getLightName());

        Button modifyEffect = (Button) view.findViewById(R.id.modify_effect_button);

        modifyEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyEffectActivity.class);
                intent.setAction(ACTION_BREATHE);
                intent.putExtra(PARAM_LIGHT_NAME, ((Light)getChild(constRoomIndex, constLightIndex)).getLightName());
                intent.putExtra(PARAM_ROOM_ID, light.getRoomId());
                intent.putExtra(PARAM_HUE_ID, ((Light)getChild(constRoomIndex, constLightIndex)).getHueId());
                intent.putExtra(PARAM_START_STOP, true);

                ((Activity) context).startActivity(intent);
            }
        });

        if (lightIndex%2 == 1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void updateAdapter()
    {
        roomList = DatabaseUtility.getAllRooms();
        List<Light> unassignedLights = DatabaseUtility.getRoomLights((long) 0);

        if(unassignedLights.size() > 0)
        {
            roomList.add(new Room("Unassigned", unassignedLights));
        }
        this.notifyDataSetChanged();
    }
}
