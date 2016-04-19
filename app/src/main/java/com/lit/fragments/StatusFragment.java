package com.lit.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.lit.R;
import com.lit.adapters.StatusAdapter;
import com.lit.models.Light;
import com.lit.models.Room;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    /**
     * Sets the adapter when the fragment starts
     */
    @Override
    public void onStart() {
        super.onStart();
        statusListView = (ExpandableListView) getActivity().findViewById(R.id.status_expandable_list_view);
        statusList = new ArrayList<Room>();
        adapter = new StatusAdapter(getContext(), statusList);
        statusListView.setAdapter(adapter);

        phHueSDK = PHHueSDK.create();
        adapter.notifyDataSetChanged();
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
        updateList();
    }

    private void updateList() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if(phHueSDK.getAllBridges().size() > 0) {
            List<PHLight> allLights = bridge.getResourceCache().getAllLights();
            List<Light> lights = new ArrayList<Light>();

            for (int i = 0; i < allLights.size(); i++) {
                Light tempLight = new Light("Light " + allLights.get(i), allLights.get(i), phHueSDK);
                lights.add(tempLight);
            }

            Room room = new Room("Bedroom", lights);
            statusList.add(room);
            adapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(getActivity(), R.string.could_not_find_bridge, Toast.LENGTH_LONG);
        }
    }

}
