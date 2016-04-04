package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lit.R;
import com.lit.activities.MainActivity;
import com.lit.activities.ModifyEffectActivity;
import com.lit.fragments.AddEffectFragment;
import com.lit.models.Light;

import java.util.ArrayList;
import java.util.List;


public class AddEffectAdapter extends BaseAdapter {

    private Context context;
    private List<Light> addEffectList;

    public AddEffectAdapter(Context context, List<Light> addEffectList)
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
    public long getItemId(int position) {
        return addEffectList.get(position).getId();
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
        convertView =  inflater.inflate(R.layout.add_effect_single_line, parent, false);
        TextView lightName = (TextView) convertView.findViewById(R.id.addEffect_light_name);
        Light addEffectLine = addEffectList.get(position);
        lightName.setText(addEffectLine.getLightName());
        Spinner spinner = (Spinner) convertView.findViewById(R.id.addEffect_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.effects_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        Button modifyEffect = (Button) convertView.findViewById(R.id.modify_effect_button);
        modifyEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyEffectActivity.class);
                intent.putExtra("light", ((Light)getItem(positionConstant)).getLightName());
                intent.putExtra("effect", ((Light)getItem(positionConstant)).getEffect().getText());
                context.startActivity(intent);
            };
        });

        // you need to have a list of data that you want the spinner to display


        return convertView;
    }

}
