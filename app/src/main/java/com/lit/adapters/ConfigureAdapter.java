package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lit.R;
import com.lit.models.Light;

import java.util.List;


public class ConfigureAdapter extends BaseAdapter {

    private Context context;
    private List<Light> configureList;

    public ConfigureAdapter(Context context, List<Light> configureList)
    {
        this.context = context;
        this.configureList = configureList;
    }

    /**
     * Getter method, get the number of items in the list
     * @return number of items in the list
     */
    @Override
    public int getCount() {
        return configureList.size();
    }


    /**
     * Getter method, get the item at a specific position
     * @return item at the given position
     */
    @Override
    public Object getItem(int position) {
        return configureList.get(position);
    }


    /**
     * Gets the id of an item at a specific position
     * @param position - index of the item
     * @return id of the item
     */
    @Override
    public long getItemId(int position) {
        return configureList.get(position).getId();
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
        convertView =  inflater.inflate(R.layout.configure_single_line, parent, false);
        //Switch lightSwitch = (Switch) convertView.findViewById(R.id.configure_light_switch);
        TextView lightName = (TextView) convertView.findViewById(R.id.configure_light_name);
        TextView connection = (TextView) convertView.findViewById(R.id.configure_connection_text);
        final Light configureLine = configureList.get(position);
       // lightSwitch.setChecked(configureLine.isLightOn());
        lightName.setText(configureLine.getLightName());
        String connectionConfigureString;

//        if(configureLine.getConnectionStatus())
//            connectionConfigureString = "Connected";
//        else
//            connectionConfigureString = "Disconnected";
//
//        connection.setText("Status: " + connectionConfigureString);
//
//        Button attemptConnection = (Button) convertView.findViewById(R.id.attempt_connection_button);
//
//        if(!configureLine.getConnectionStatus())
//        {
//            connection.setTextColor(Color.RED);
//            attemptConnection.setText("Attempt\nConnection");
//        }
//
//        attemptConnection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(configureLine.getConnectionStatus()) {
//                    Toast.makeText(context, "Reconnected to " + configureLine.getLightName() + " successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "Error: " + configureLine.getLightName() + " is not connected on this Wi-Fi network", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



        return convertView;
    }
}
