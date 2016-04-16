package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lit.R;
import com.lit.models.Light;
import com.philips.lighting.model.PHBridge;

import java.util.List;

/**
 * Created by JoeLaptop on 3/29/2016.
 */
public class CustomizeAdapter extends BaseAdapter {
    private Context context;

    //TODO: private List<Light> customizeList;
    private List<PHBridge> customizeList;

    //TODO: public CustomizeAdapter(Context context, List<Light> customizeList)
    public CustomizeAdapter(Context context, List<PHBridge> customizeList)
    {
        this.context = context;
        this.customizeList = customizeList;
    }

    @Override
    public int getCount() {
        return customizeList.size();
    }

    @Override
    public Object getItem(int position) {
        return customizeList.get(position);
    }

    @Override
    //TODO: public long getItemId(int position) {return customizeList.get(position).getId();}
    public long getItemId(int position) {return 1;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.customize_single_line, parent, false);
        TextView lightName = (TextView) convertView.findViewById(R.id.customize_light_name);
        TextView effectText = (TextView) convertView.findViewById(R.id.customize_effect_text);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.effect_check_box);

//        TODO:
//        Light light = customizeList.get(position);
//        lightName.setText(light.getLightName());
//        effectText.setText(light.getEffect().getText());
//        checkBox.setChecked(light.getEffect().isEffectOn());

        PHBridge bridge = customizeList.get(position);
        lightName.setText(bridge.toString());
        effectText.setText("Not sure what to put here...");
        checkBox.setChecked(false);
        Log.v("CustomizeAdapter","Successfully added bridge.");

        return convertView;

    }
}
