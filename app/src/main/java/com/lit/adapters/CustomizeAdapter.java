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
public class CustomizeAdapter extends BaseAdapter {
    private Context context;
    private List<Light> customizeList;

    public CustomizeAdapter(Context context, List<Light> customizeList)
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
    public long getItemId(int position) {
        return customizeList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.customize_single_line, parent, false);
        TextView lightName = (TextView) convertView.findViewById(R.id.customize_light_name);
        TextView effectText = (TextView) convertView.findViewById(R.id.customize_effect_text);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.effect_check_box);
        Light light = customizeList.get(position);
        lightName.setText(light.getLightName());
        effectText.setText(light.getEffect().getText());
        checkBox.setChecked(light.getEffect().isEffectOn());

        return convertView;

    }
}
