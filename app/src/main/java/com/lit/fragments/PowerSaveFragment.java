package com.lit.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lit.R;
import com.lit.adapters.PowerSaveAdapter;
import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.lit.models.Room;
import com.philips.lighting.hue.sdk.PHHueSDK;

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
    private boolean displayListItems;

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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.power_save_settings_dialog, null);

            TextView luxOutput = (TextView) view.findViewById(R.id.lux_output_text_view);

            builder.setTitle("Power Save Settings");
            builder.setView(view);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.show();
        }
    };
}
