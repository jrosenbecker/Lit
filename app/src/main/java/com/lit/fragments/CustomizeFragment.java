package com.lit.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.lit.R;
import com.lit.activities.MainActivity;
import com.lit.adapters.CustomizeAdapter;
import com.lit.database.DatabaseUtility;
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
 * {@link CustomizeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomizeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomizeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private CustomizeAdapter listViewAdapter;

    boolean displayListItems;

    private PHHueSDK phHueSDK;

    //TODO: private List<Light> lights;
    private List<Room> customizeList;

    private ExpandableListView customizeListView;

    public CustomizeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CustomizeFragment.
     */
    public static CustomizeFragment newInstance() {
        CustomizeFragment fragment = new CustomizeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phHueSDK = PHHueSDK.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        phHueSDK = PHHueSDK.create();

        displayListItems = !(phHueSDK.getAllBridges().isEmpty());

        // Inflate the layout for this fragment
        if (displayListItems) {
            return inflater.inflate(R.layout.fragment_customize, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_empty_customize, container, false);
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
        super.onStart();

        if (displayListItems) {
            customizeListView = (ExpandableListView) getActivity().findViewById(R.id.customize_list_view);

            List<PHBridge> savedBridges = new ArrayList<PHBridge>();

            customizeList = new ArrayList<Room>();
            listViewAdapter = new CustomizeAdapter(getActivity(), customizeList);

            customizeListView.setAdapter(listViewAdapter);


            listViewAdapter.updateAdapter();

        } else {
            Toast.makeText(getContext(),"Press 'CONFIGURE' on the Status screen to connect to your bridge...",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v("onResume","displayListItems: " + displayListItems);

        if (displayListItems) {
            listViewAdapter.updateAdapter();
        }
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


//    public void updateList()
//    {
//        PHBridge bridge = phHueSDK.getSelectedBridge();
//        customizeList.clear();
//        if(phHueSDK.getAllBridges().size() > 0) {
//
//            List<Room> rooms = DatabaseUtility.getAllRooms();
//
//            for (Room room : rooms) {
//                customizeList.add(room);
//            }
//
//            Room unassigned = new Room("Unassigned",DatabaseUtility.getRoomLights(0));
//            customizeList.add(unassigned);
//
//            //statusList.add(room);
//            listViewAdapter.notifyDataSetChanged();
//        }
//        else
//        {
//            Toast.makeText(getActivity(), R.string.could_not_find_bridge, Toast.LENGTH_LONG).show();
//        }
//    }
}
