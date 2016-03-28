package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.lit.R;
import com.lit.models.Light;

import java.util.List;

/**
 * Created by JoeLaptop on 3/19/2016.
 */
public class StatusAdapter extends BaseAdapter {

    private Context context;
    private List<Light> statusList;

    public StatusAdapter(Context context, List<Light> statusList)
    {
        this.context = context;
        this.statusList = statusList;
    }

    /**
     * Getter method, get the number of items in the list
     * @return number of items in the list
     */
    @Override
    public int getCount() {
        return statusList.size();
    }


    /**
     * Getter method, get the item at a specific position
     * @return item at the given position
     */
    @Override
    public Object getItem(int position) {
        return statusList.get(position);
    }


    /**
     * Gets the id of an item at a specific position
     * @param position - index of the item
     * @return id of the item
     */
    @Override
    public long getItemId(int position) {
        return statusList.get(position).getId();
    }


    /**
     * Gets the view. Used to initialize the view variables
     * @param position - Position of the item
     * @param convertView - convert view
     * @param parent - parent group
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView =  inflater.inflate(R.layout.status_single_line, parent, false);
        Switch lightSwitch = (Switch) convertView.findViewById(R.id.status_light_switch);
        TextView lightName = (TextView) convertView.findViewById(R.id.status_light_name);

        Light statusLine = statusList.get(position);
        lightSwitch.setChecked(statusLine.isLightOn());
        lightName.setText(statusLine.getLightName());
        return convertView;
    }
}
