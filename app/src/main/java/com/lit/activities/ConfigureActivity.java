package com.lit.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.lit.R;
import com.lit.adapters.ConfigureAdapter;
import com.lit.models.Light;

import java.util.ArrayList;
import java.util.List;

public class ConfigureActivity extends AppCompatActivity {
//    private OnFragmentInteractionListener fragmentInteractionListener;
    private ListView configureListView;
    private ConfigureAdapter adapter;
    private List<Light> configureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.configure_activity_toolbar);
        toolbar.setTitle("Configure");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        configureListView = (ListView) findViewById(R.id.configure_list_view);
        configureList = new ArrayList<Light>();
        adapter = new ConfigureAdapter(this, configureList);
        configureListView.setAdapter(adapter);

        // TODO: Remove these additions, used currently for testing purposes
//        configureList.add(new Light(1, "Light 1", false, true));
//        configureList.add(new Light(2, "Light 2", false, true));
//        configureList.add(new Light(3, "Light 3", false, true));
//        configureList.add(new Light(4, "Light 4", false, true));
//        configureList.add(new Light(5, "Light 5", false, true));
//        configureList.add(new Light(6, "Light 6", false, true));
//        configureList.add(new Light(7, "Light 7", false, true));
//        configureList.add(new Light(3, "Light 8", false, true));
//        configureList.add(new Light(3, "Light 9", false, false));
//        configureList.add(new Light(3, "Light 10", false, false));
        adapter.notifyDataSetChanged();
    }


}
