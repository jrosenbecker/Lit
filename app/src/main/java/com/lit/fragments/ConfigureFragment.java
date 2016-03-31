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
import com.lit.adapters.ConfigureAdapter;
import com.lit.models.Light;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfigureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfigureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigureFragment extends Fragment {
    private OnFragmentInteractionListener fragmentInteractionListener;
    private ListView configureListView;
    private ConfigureAdapter adapter;
    private List<Light> configureList;

    public ConfigureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConfigureFragment.
     */
    public static ConfigureFragment newInstance() {
        ConfigureFragment fragment = new ConfigureFragment();
        return fragment;
    }

    /**
     * Inflates the fragment
     * @param inflater - inflater to be used
     * @param container - View container
     * @param savedInstanceState - saved Instance
     * @return View after the inflate
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configure, container, false);
    }

    /**
     * Sets the adapter when the fragment starts
     */
    @Override
    public void onStart() {
        super.onStart();
        configureListView = (ListView) getActivity().findViewById(R.id.configure_list_view);
        configureList = new ArrayList<Light>();
        adapter = new ConfigureAdapter(getContext(), configureList);
        configureListView.setAdapter(adapter);

        // TODO: Remove these additions, used currently for testing purposes
        configureList.add(new Light(1, "Light 1", false, true));
        configureList.add(new Light(2, "Light 2", false, true));
        configureList.add(new Light(3, "Light 3", false, true));
        configureList.add(new Light(4, "Light 4", false, true));
        configureList.add(new Light(5, "Light 5", false, true));
        configureList.add(new Light(6, "Light 6", false, true));
        configureList.add(new Light(7, "Light 7", false, true));
        configureList.add(new Light(3, "Light 8", false, true));
        configureList.add(new Light(3, "Light 9", false, false));
        configureList.add(new Light(3, "Light 10", false, false));
        adapter.notifyDataSetChanged();
    }


    /**
     * Generated onAttach method
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
