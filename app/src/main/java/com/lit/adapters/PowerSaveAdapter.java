package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lit.R;
import com.lit.models.Light;

import java.util.List;

/**
 * Created by JoeLaptop on 3/29/2016.
 */
public class PowerSaveAdapter extends BaseAdapter {

    private Context context;
    private List<Light> powerSaveList;

    public PowerSaveAdapter(Context context, List<Light> powerSaveList)
    {
        this.context = context;
        this.powerSaveList = powerSaveList;
    }

    @Override
    public int getCount() {
        return powerSaveList.size();
    }

    @Override
    public Object getItem(int position) {
        return powerSaveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return powerSaveList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.power_save_single_line, parent, false);
        // TODO: Set up all of the views in the single line
        TextView lightName = (TextView) convertView.findViewById(R.id.power_save_light_name);
        TextView activeSensor = (TextView) convertView.findViewById(R.id.power_save_active_sensor);
        Light light = powerSaveList.get(position);
        lightName.setText(light.getLightName());
//        activeSensor.setText(light.getActiveSensor());


        return convertView;

    }
}
