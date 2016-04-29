package com.lit.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lit.R;
import com.lit.models.Light;
import com.lit.services.BreatheEffectService;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ModifyEffectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ModifyEffectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyEffectFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private boolean breathe;

    public ModifyEffectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ModifyEffectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ModifyEffectFragment newInstance() {
        ModifyEffectFragment fragment = new ModifyEffectFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify_effect, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    private void colorCycle(final int roomIndex, final int lightIndex)
//    {
//        Light tempLight = (Light) getChild(roomIndex, lightIndex);
//        PHLight phLight = tempLight.getPhLight();
//        PHLightState state = phLight.getLastKnownLightState();
//        state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);
//        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
//    }
//
//    // TODO: Use a service to kick off this effect to prevent an infinite loop
//    private void breatheEffect(final int roomIndex, final int lightIndex)
//    {
//        if (!breathe) {
//            Light tempLight = (Light) getChild(roomIndex, lightIndex);
//            getActivity().startService(new Intent(getContext(), BreatheEffectService.class));
//            BreatheEffectService.startActionBreathe(getContext(), tempLight.getLightName(), roomIndex, tempLight.getHueId(), breathe);
//        } else {
//            breatheEffect.stopSelf();
//            breatheEffect.onDestroy();
//        }
//    }
//
//    private void killEffect(final int roomIndex, final int lightIndex)
//    {
//        Light tempLight = (Light) getChild(roomIndex, lightIndex);
//        PHLight phLight = tempLight.getPhLight();
//        PHLightState state = phLight.getLastKnownLightState();
//        state.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
//        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
//    }

}
