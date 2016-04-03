package com.lit.activities;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.lit.R;
import com.lit.adapters.AddEffectAdapter;
import com.lit.models.Light;

import java.util.ArrayList;
import java.util.List;

public class AddEffectActivity extends AppCompatActivity {
    private ListView addEffectListView;
    private AddEffectAdapter adapter;
    private List<Light> addEffectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_effect);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_effect_activity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        addEffectListView = (ListView) findViewById(R.id.addEffect_list_view);
        addEffectList = new ArrayList<Light>();
        adapter = new AddEffectAdapter(this, addEffectList);
        addEffectListView.setAdapter(adapter);

        // TODO: Remove these additions, used currently for testing purposes
        addEffectList.add(new Light(1, "Light 1", false, true, 0));
        addEffectList.add(new Light(2, "Light 2", false, true, 1));
        addEffectList.add(new Light(3, "Light 3", false, true, 2));
        addEffectList.add(new Light(4, "Light 4", false, true, 3));
        addEffectList.add(new Light(5, "Light 5", false, true, 3));
        addEffectList.add(new Light(6, "Light 6", false, true, 2));
        addEffectList.add(new Light(7, "Light 7", false, true, 1));
        addEffectList.add(new Light(3, "Light 8", false, true, 0));
        addEffectList.add(new Light(3, "Light 9", false, false, 1));
        addEffectList.add(new Light(3, "Light 10", false, false, 2));
        adapter.notifyDataSetChanged();
    }

}
