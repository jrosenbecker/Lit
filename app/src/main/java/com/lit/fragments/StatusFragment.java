package com.lit.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.lit.R;
import com.lit.adapters.StatusAdapter;
import com.lit.database.DatabaseUtility;
import com.lit.models.Light;
import com.lit.models.Room;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {


    private OnFragmentInteractionListener fragmentInteractionListener;
    private PHHueSDK phHueSDK;
    private ExpandableListView statusListView;
    private StatusAdapter adapter;
    private List<Room> statusList;
    private boolean displayListItems;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatusFragment.
     */
    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        return fragment;
    }

    /**
     * Inflates the fragment
     *
     * @param inflater           - inflater to be used
     * @param container          - View container
     * @param savedInstanceState - saved Instance
     * @return View after the inflate
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        phHueSDK = PHHueSDK.create();

        displayListItems = !(phHueSDK.getAllBridges().isEmpty());

        // Inflate the layout for this fragment
        if (displayListItems) {
            return inflater.inflate(R.layout.fragment_status, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_empty_status, container, false);
        }
    }

    /**
     * Sets the adapter when the fragment starts
     */
    @Override
    public void onStart() {

        super.onStart();

        if (displayListItems) {

            statusListView = (ExpandableListView) getActivity().findViewById(R.id.status_expandable_list_view);
            statusList = new ArrayList<Room>();
            adapter = new StatusAdapter(getContext(), statusList);
            statusListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * Generated onAttach method
     *
     * @param context - current context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            fragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }


    /**
     * Generated onDetach method
     */
    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

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


    @Override
    public void onResume() {
        super.onResume();
        if (displayListItems) {
            updateList();
        }
    }

    private void updateList() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        statusList.clear();
        if(phHueSDK.getAllBridges().size() > 0) {

            List<Room> rooms = DatabaseUtility.getAllRooms();

            for (Room room : rooms) {
                statusList.add(room);
                for (Light light : room.getLights()) {
                    Log.v("updateList","Room: " + room.getName() + " Light: " + light.getLightName());
                }
            }

            Room unassigned = new Room("Unassigned",DatabaseUtility.getRoomLights(0));
            statusList.add(unassigned);

            //statusList.add(room);
            adapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(getActivity(), R.string.could_not_find_bridge, Toast.LENGTH_LONG);
        }
    }
}
