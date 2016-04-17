package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
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
        final int positionConstant = position;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView =  inflater.inflate(R.layout.status_single_line, parent, false);
        Switch lightSwitch = (Switch) convertView.findViewById(R.id.status_light_switch);
        TextView lightName = (TextView) convertView.findViewById(R.id.status_light_name);
        TextView connection = (TextView) convertView.findViewById(R.id.status_connection_text);
        Light statusLine = statusList.get(position);
        lightSwitch.setChecked(statusLine.isLightOn());
        lightName.setText(statusLine.getLightName());
        lightName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Rename " + statusList.get(positionConstant).getLightName());
                final EditText newNameInput = new EditText(context);
                int padding = 20;
                newNameInput.setPadding(padding, padding, padding, padding);
                newNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(newNameInput);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        statusList.get(positionConstant).setLightName(newNameInput.getText().toString());
                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                statusList.get(positionConstant).setLightOn(b);
            }
        });
        String connectionStatusString;


        if(statusLine.isConnectedToBridge())
            connectionStatusString = "Connected";
        else
            connectionStatusString = "Light is not reachable";

        connection.setText("Status: " + connectionStatusString);

        if(!statusLine.isConnectedToBridge())
            connection.setTextColor(Color.RED);

        
        return convertView;
    }
}
