package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lit.R;
import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.lit.models.Room;
import com.lit.services.PowerSaveService;

import java.util.List;

/**
 * Created by JoeLaptop on 3/29/2016.
 */
public class PowerSaveAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Room> roomList;

    private static final String POWER_SAVE = "POWER_SAVE";
    private static final String ACTION_POWERSAVE = "com.lit.services.action.POWERSAVE";

    public PowerSaveAdapter(Context context, List<Room> roomList)
    {
        this.context = context;
        this.roomList = roomList;
    }

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
    public Object getChild(int roomIndex, int childIndex) {
        return roomList.get(roomIndex).getLights().get(childIndex);
    }

    @Override
    public long getGroupId(int roomIndex) {
        return roomList.get(roomIndex).getId();
    }

    @Override
    public long getChildId(int roomIndex, int childIndex) {
        return roomList.get(roomIndex).getLights().get(childIndex).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int roomIndex, boolean b, View view, ViewGroup viewGroup) {
        Room room = (Room) getGroup(roomIndex);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.room_layout, null);
        TextView roomName = (TextView) view.findViewById(R.id.room_name);
        roomName.setText(room.getName());
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
    public View getChildView(final int roomIndex, final int childIndex, boolean b, View view, ViewGroup viewGroup) {
        final int constRoomIndex = roomIndex;
        final int constChildIndex = childIndex;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.power_save_single_line, viewGroup, false);
        }

        Light light = (Light) getChild(roomIndex, childIndex);

        Switch enabledSwitch = (Switch) view.findViewById(R.id.power_save_enabled_switch);
        TextView lightName = (TextView) view.findViewById(R.id.power_save_light_name);
        lightName.setText(light.getLightName());
        if (childIndex%2 == 1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        enabledSwitch.setChecked(DatabaseUtility.getLightEffect(POWER_SAVE,light.getHueId()));

        enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Light light = (Light) getChild(roomIndex, childIndex);
                light.setPowerSaveOn(b);
               // DatabaseUtility.updatePowerSaveOn(context, light.getHueId(), b);
                DatabaseUtility.updateLightEffect(context,POWER_SAVE,b,light.getHueId());
                if(b)
                {
                    if(DatabaseUtility.getAllPowerSaveEnabledLights().size() == 1) {
                        Intent intent = new Intent(context, PowerSaveService.class);
                        intent.setAction(ACTION_POWERSAVE);
                        PowerSaveService.on_off = true;
                        PowerSaveService.setContext(context);
                        context.startService(intent);
                    }
                } else {
                    if(DatabaseUtility.getAllPowerSaveEnabledLights().size() == 0)
                    {
                        Intent intent = new Intent(context, PowerSaveService.class);
                        PowerSaveService.on_off = false;
                        PowerSaveService.setContext(context);
                        context.stopService(intent);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
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
