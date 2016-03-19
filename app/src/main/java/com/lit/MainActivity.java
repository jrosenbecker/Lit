package com.lit;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lit.Constants.TabConstants;
import com.lit.fragments.CustomizeFragment;
import com.lit.fragments.PowerSaveFragment;
import com.lit.fragments.StatusFragment;

public class MainActivity extends AppCompatActivity
        implements CustomizeFragment.OnFragmentInteractionListener,
        PowerSaveFragment.OnFragmentInteractionListener,
        StatusFragment.OnFragmentInteractionListener {

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tabMenu);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Status").setTag(TabConstants.STATUS_TAB));
        tabLayout.addTab(tabLayout.newTab().setText("Power Save").setTag(TabConstants.POWER_SAVE_TAB));
        tabLayout.addTab(tabLayout.newTab().setText("Customize").setTag(TabConstants.CUSTOMIZE_TAB));
        tabLayout.setOnTabSelectedListener(onTabSelected);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, StatusFragment.newInstance()).commit();

    }

    private TabLayout.OnTabSelectedListener onTabSelected = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch((int) tab.getTag())
            {
                case TabConstants.STATUS_TAB:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, StatusFragment.newInstance()).commit();
                    break;
                case TabConstants.POWER_SAVE_TAB:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, PowerSaveFragment.newInstance()).commit();
                    break;
                case TabConstants.CUSTOMIZE_TAB:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, CustomizeFragment.newInstance()).commit();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            switch((int) tab.getTag())
            {
                case TabConstants.STATUS_TAB:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, StatusFragment.newInstance()).commit();
                    break;
                case TabConstants.POWER_SAVE_TAB:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, PowerSaveFragment.newInstance()).commit();
                    break;
                case TabConstants.CUSTOMIZE_TAB:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, CustomizeFragment.newInstance()).commit();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Blank listener
    }
}
