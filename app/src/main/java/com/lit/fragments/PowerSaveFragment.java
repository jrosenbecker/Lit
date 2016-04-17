package com.lit.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lit.R;
import com.lit.adapters.PowerSaveAdapter;
import com.lit.models.Effect;
import com.lit.models.Light;

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
    private PowerSaveAdapter listAdapter;
    private List<Light> lights;
    private ListView powerSaveListView;

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
        return inflater.inflate(R.layout.fragment_power_save, container, false);
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
        powerSaveListView = (ListView) getActivity().findViewById(R.id.power_save_list_view);
        lights = new ArrayList<Light>();
        listAdapter = new PowerSaveAdapter(getContext(), lights);
        powerSaveListView.setAdapter(listAdapter);
//        lights.add(new Light(1, "Kitchen Bulb", false, true, Effect.STROBE));
//        lights.add(new Light(2, "Bathroom bulb", false, true, Effect.BREATHE));
//        lights.add(new Light(3, "Living Room", false, true, Effect.COLOR_CYCLE, "Living Room Sensor"));
//        lights.add(new Light(4, "Family Room", false, true, Effect.NONE,  "Family Room Sensor"));
        listAdapter.notifyDataSetChanged();
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
