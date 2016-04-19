package com.lit.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lit.R;
import com.lit.adapters.EffectListAdapter;
import com.lit.models.Light;
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
    private EffectListAdapter listViewAdapter;
    //
    private PHHueSDK phHueSDK;
    //
//    //TODO: private List<Light> lights;
    private List<Light> lights;
    //
    private ListView customizeListView;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customize, container, false);
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
        customizeListView = (ListView) getActivity().findViewById(R.id.customize_list_view);

        List<PHBridge> savedBridges = new ArrayList<PHBridge>();
//
//        //TODO: listViewAdapter = new CustomizeAdapter(getContext(), lights);
        lights = new ArrayList<Light>();
        listViewAdapter = new EffectListAdapter(getContext(), lights);
//
        customizeListView.setAdapter(listViewAdapter);
        if(phHueSDK.getAllBridges().size() > 0) {
            for (PHLight light : phHueSDK.getSelectedBridge().getResourceCache().getAllLights()) {
                lights.add(new Light(light.getName(), light, phHueSDK));
            }
        }

        listViewAdapter.notifyDataSetChanged();
//        for (PHBridge bridge : bridges) {
//            savedBridges.add(bridge);
//        }

//        lights.add(new Light(1, "Kitchen Bulb", false, true, Effect.STROBE));
//        lights.add(new Light(2, "Bathroom bulb", false, true, Effect.BREATHE));
//        lights.add(new Light(3, "Living Room", false, true, Effect.COLOR_CYCLE));
//        lights.add(new Light(4, "Family Room", false, true));
//        listViewAdapter.notifyDataSetChanged();

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
}
