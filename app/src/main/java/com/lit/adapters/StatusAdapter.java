package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lit.R;
import com.lit.models.Light;
import com.lit.models.Room;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.hue.sdk.utilities.impl.PHUtilitiesHelper;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by JoeLaptop on 3/19/2016.
 */
public class StatusAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Room> rooms;

    public StatusAdapter(Context context, List<Room> roomList) {
        this.context = context;
        rooms = roomList;
    }

//    /**
//     * Getter method, get the number of items in the list
//     * @return number of items in the list
//     */
//    @Override
//    public int getCount() {
//        return lightList.size();
//    }
//

//    /**
//     * Getter method, get the item at a specific position
//     * @return item at the given position
//     */
//    @Override
//    public Object getItem(int position) {
//        return lightList.get(position);
//    }
//
//
//    /**
//     * Gets the id of an item at a specific position
//     * @param position - index of the item
//     * @return id of the item
//     */
//    @Override
//    public long getItemId(int position) {
//        return lightList.get(position).getId();
//    }
//
//
//    /**
//     * Gets the view. Used to initialize the view variables
//     * @param position - Position of the item
//     * @param convertView - convert view
//     * @param parent - parent group
//     * @return View
//     */
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final int positionConstant = position;
//        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//        convertView =  inflater.inflate(R.layout.status_single_line, parent, false);
//        Switch lightSwitch = (Switch) convertView.findViewById(R.id.status_light_switch);
//        TextView lightName = (TextView) convertView.findViewById(R.id.status_light_name);
//        TextView connection = (TextView) convertView.findViewById(R.id.status_connection_text);
//        Light statusLine = lightList.get(position);
//        lightSwitch.setChecked(statusLine.isLightOn());
//        lightName.setText(statusLine.getLightName());
//        lightName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Rename " + lightList.get(positionConstant).getLightName());
//                final EditText newNameInput = new EditText(context);
//                int padding = 20;
//                newNameInput.setPadding(padding, padding, padding, padding);
//                newNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(newNameInput);
//
//                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        lightList.get(positionConstant).setLightName(newNameInput.getText().toString());
//                    }
//                });
//
//
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                });
//
//                builder.show();
//            }
//        });
//
//        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                lightList.get(positionConstant).setLightOn(b);
//            }
//        });
//        String connectionStatusString;
//
//
//        if(statusLine.isConnectedToBridge())
//            connectionStatusString = "Connected";
//        else
//            connectionStatusString = "Light is not reachable";
//
//        connection.setText("Status: " + connectionStatusString);
//
//        if(!statusLine.isConnectedToBridge())
//            connection.setTextColor(Color.RED);
//
//
//        return convertView;
//    }

    @Override
    public int getGroupCount() {
        return rooms.size();
    }

    @Override
    public int getChildrenCount(int roomIndex) {
        return rooms.get(roomIndex).getLights().size();
    }

    @Override
    public Object getGroup(int roomIndex) {
        return rooms.get(roomIndex);
    }

    @Override
    public Object getChild(int roomIndex, int lightIndex) {
        return rooms.get(roomIndex).getLights().get(lightIndex);
    }

    @Override
    public long getGroupId(int roomIndex) {
        return rooms.get(roomIndex).getId();
    }

    @Override
    public long getChildId(int roomIndex, int childIndex) {
        return rooms.get(roomIndex).getLights().get(childIndex).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int roomIndex, boolean isExpanded, View view, ViewGroup viewGroup) {
        Room room = (Room) getGroup(roomIndex);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.room_layout, null);
        TextView roomName = (TextView) view.findViewById(R.id.room_name);
        roomName.setText(room.getName());
        return view;
    }

    @Override
    public View getChildView(int roomIndex, int childIndex, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        final int constRoomIndex = roomIndex;
        final int constChildIndex = childIndex;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.status_single_line, viewGroup, false);
        }
        Light light = (Light) getChild(roomIndex, childIndex);

        Switch lightSwitch = (Switch) view.findViewById(R.id.status_light_switch);
        TextView lightName = (TextView) view.findViewById(R.id.status_light_name);
        TextView connection = (TextView) view.findViewById(R.id.status_connection_text);
        ImageButton menuButton = (ImageButton) view.findViewById(R.id.status_options_button);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.status_context_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId())
                        {
                            case R.id.rename_light_option:
                                renameLightDialog(constRoomIndex, constChildIndex);
                                return true;
                            case R.id.change_color:
                                changeColorDialog(constRoomIndex, constChildIndex);
                                return true;
                            case R.id.brightness_option:
                                changeBrightnessDialog(constRoomIndex, constChildIndex);
                                return true;
                            default:
                                return false;
                        }

                    }
                });
                popup.show();

            }
        });



        // Need to set listener to null, otherwise setChecked will fire off the listener created below
        lightSwitch.setOnCheckedChangeListener(null);
        lightSwitch.setChecked(light.isLightOn());
        lightName.setText(light.getLightName());

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ((Light) getChild(constRoomIndex, constChildIndex)).setLightOn(b);
            }
        });
        String connectionStatusString;


        if (light.isConnectedToBridge()) {
            connectionStatusString = "Connected";
            connection.setTextColor(Color.GRAY);
        } else {
            connectionStatusString = "Light is not reachable";
            connection.setTextColor(Color.RED);
        }

        connection.setText("Status: " + connectionStatusString);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private void renameLightDialog(final int roomIndex, final int childIndex)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rename " + ((Light) getChild(roomIndex, childIndex)).getLightName());
        final EditText newNameInput = new EditText(context);
        int padding = 20;
        newNameInput.setPadding(padding, padding, padding, padding);
        newNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(newNameInput);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((Light) getChild(roomIndex, childIndex)).setLightName(newNameInput.getText().toString());
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

    public void changeBrightnessDialog(final int roomIndex, final int lightIndex)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final Light light = (Light) getChild(roomIndex, lightIndex);
        final PHLightState initialState = light.getLightState();
        builder.setTitle("Change brightness of " + ((Light) getChild(roomIndex, lightIndex)).getLightName());
        SeekBar slider = new SeekBar(context);
        slider.setMax(Light.MAX_BRIGHTNESS);
        slider.setProgress(light.getBrightness());
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((Light) getChild(roomIndex, lightIndex)).setBrightness(seekBar.getProgress());
                Toast.makeText(context, "" + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
            }
        });
        int padding = 20;

        builder.setView(slider);

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing, brightness changes during the slider movement
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Go back to the original brightness
                ((Light) getChild(roomIndex, lightIndex)).setLightState(initialState);
            }
        });

        builder.show();
    }

    public void changeColorDialog(final int roomIndex, final int lightIndex)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.rgb_slider_layout, null);

        final Light light = (Light) getChild(roomIndex, lightIndex);
        final PHLightState initialState = light.getLightState();
        builder.setTitle("Change color of " + ((Light) getChild(roomIndex, lightIndex)).getLightName());

        final SeekBar redSlider = (SeekBar) view.findViewById(R.id.red_seekbar);
        final SeekBar greenSlider = (SeekBar) view.findViewById(R.id.green_seekbar);
        final SeekBar blueSlider = (SeekBar) view.findViewById(R.id.blue_seekbar);



        redSlider.setMax(Light.MAX_COLOR_VALUE);
        greenSlider.setMax(Light.MAX_COLOR_VALUE);
        blueSlider.setMax(Light.MAX_COLOR_VALUE);

        redSlider.setProgress(light.getRed());
        greenSlider.setProgress(light.getGreen());
        blueSlider.setProgress(light.getBlue());

        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Light light = ((Light) getChild(roomIndex, lightIndex));
                light.setColor(redSlider.getProgress(), greenSlider.getProgress(), blueSlider.getProgress());
            }
        };

        redSlider.setOnSeekBarChangeListener(seekBarListener);
        greenSlider.setOnSeekBarChangeListener(seekBarListener);
        blueSlider.setOnSeekBarChangeListener(seekBarListener);

        builder.setView(view);

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing, brightness changes during the slider movement
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Go back to the original brightness
                ((Light) getChild(roomIndex, lightIndex)).setLightState(initialState);
            }
        });

        builder.show();
    }
}
