package com.lit.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lit.R;
import com.lit.adapters.PowerSaveAdapter;
import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.lit.models.Room;
import com.lit.services.PowerSaveService;
import com.philips.lighting.hue.sdk.PHHueSDK;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PowerSaveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PowerSaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PowerSaveFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private PHHueSDK phHueSDK;
    private PowerSaveAdapter listAdapter;
    private List<Room> powerSaveList;
    private ExpandableListView powerSaveListView;
    private Button powerSaveSettingsButton;
    private TextView luxOutput;
    private boolean displayListItems;

    private static final String POWER_SAVE = "POWER_SAVE";

    public PowerSaveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment PowerSaveFragment.
     */
    public static PowerSaveFragment newInstance() {
        PowerSaveFragment fragment = new PowerSaveFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        phHueSDK = PHHueSDK.create();

        displayListItems = !(phHueSDK.getAllBridges().isEmpty());

        // Inflate the layout for this fragment
        if (displayListItems) {
            return inflater.inflate(R.layout.fragment_power_save, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_empty_status, container, false);
        }
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {

        if(displayListItems) {
            powerSaveListView = (ExpandableListView) getActivity().findViewById(R.id.power_save_list_view);
            powerSaveSettingsButton = (Button) getActivity().findViewById(R.id.power_save_settings_button);
            powerSaveSettingsButton.setOnClickListener(buttonClickListener);
            powerSaveList = DatabaseUtility.getAllRooms();
            listAdapter = new PowerSaveAdapter(getContext(), powerSaveList);
            powerSaveListView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }

        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v("onResume","displayListItems: " + displayListItems);

        if (displayListItems) {
            listAdapter.updateAdapter();
        }
    }

//    private void updateList() {
//
//        powerSaveList.clear();
//        List<Room> rooms = DatabaseUtility.getAllRooms();
//        if (phHueSDK.getAllBridges().size() > 0) {
//            for (Room room : rooms) {
//                powerSaveList.add(room);
//                for (Light light : room.getLights()) {
//                    Log.v("updateList", "Room: " + room.getName() + " Light: " + light.getLightName());
//                }
//            }
//
//
//            Room unassigned = new Room("Unassigned", DatabaseUtility.getRoomLights(0));
//            powerSaveList.add(unassigned);
//            listAdapter.notifyDataSetChanged();
//        }
//        else
//        {
//            Toast.makeText(getActivity(), R.string.could_not_find_bridge, Toast.LENGTH_LONG).show();
//        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            PowerSaveDialog builder = new PowerSaveDialog(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.power_save_settings_dialog, null);

            luxOutput = (TextView) view.findViewById(R.id.lux_output_text_view);
            final List<Integer> userPrefs = DatabaseUtility.getPowerSavePref();

            final EditText minLux = (EditText) view.findViewById(R.id.min_lux_edit_text);

            final EditText maxLux = (EditText) view.findViewById(R.id.max_lux_edit_text);

            if (!userPrefs.isEmpty()) {
                minLux.setText("" + userPrefs.get(0));
                maxLux.setText("" + userPrefs.get(1));
            }

            builder.setTitle("Power Save Settings");
            builder.setView(view);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    try {
                        int min = Integer.parseInt(minLux.getText().toString());
                        int max = Integer.parseInt(maxLux.getText().toString());
                        if(max < min)
                        {
                            Toast.makeText(getContext(), "Minimum value must be smaller than the maximum", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (userPrefs.isEmpty()) {
                                DatabaseUtility.savePowerSavePref(getContext(), min, max);
                                PowerSaveService.setLuxRange(min, max);
                            } else {
                                DatabaseUtility.updatePowerSavePref(getContext(),
                                        min, max,
                                        userPrefs.get(0), userPrefs.get(1));
                                PowerSaveService.setLuxRange(min, max);
                            }
                        }

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Please input integer values and try again.", Toast.LENGTH_LONG).show();
                    }


                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Do nothing
                }
            });

            builder.show();



        }
    };

    private class PowerSaveDialog extends AlertDialog.Builder implements SensorEventListener {


        SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        protected PowerSaveDialog(Context context) {
            super(context);
            mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }

        @Override
        public final void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do something here if sensor accuracy changes.
        }

        @Override
        public final void onSensorChanged(SensorEvent event) {

            // Do something with this sensor data.
            luxOutput.setText(event.values[0] + " Luxes");
        }
    }


}
