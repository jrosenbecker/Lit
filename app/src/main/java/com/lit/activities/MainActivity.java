package com.lit.activities;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.lit.R;
import com.lit.constants.TabConstants;
import com.lit.fragments.CustomizeFragment;
import com.lit.fragments.PowerSaveFragment;
import com.lit.fragments.StatusFragment;

public class MainActivity extends AppCompatActivity
        implements CustomizeFragment.OnFragmentInteractionListener,
        PowerSaveFragment.OnFragmentInteractionListener,
        StatusFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private Menu menu;


    /**
     * Sets up the activity by initializing the first fragment and setting up the toolbars
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        // Set up the tabbed action bar at the bottom
        tabLayout = (TabLayout) findViewById(R.id.tabMenu);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Status").setTag(TabConstants.STATUS_TAB));
        tabLayout.addTab(tabLayout.newTab().setText("Power Save").setTag(TabConstants.POWER_SAVE_TAB));
        tabLayout.addTab(tabLayout.newTab().setText("Customize").setTag(TabConstants.CUSTOMIZE_TAB));
        tabLayout.setSelectedTabIndicatorHeight(20);
        tabLayout.setOnTabSelectedListener(onTabSelected);

        // Open the status fragment when first created
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, StatusFragment.newInstance()).commit();

    }

    /**
     * Listener for when the tabs change
     */
    private TabLayout.OnTabSelectedListener onTabSelected = new TabLayout.OnTabSelectedListener() {
        // TODO: Figure out how onTabSelected is different than reselected and don't always create a new instance
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

            updateToolbar(tab);
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


    /**
     * Required blank listener
     * @param uri - uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        // Blank listener
    }

    /**
     * Updates the menu when it is first created
     * @param menu - menu element
     * @return boolean - true if completed correctly
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        this.menu = menu;
        updateToolbar(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
        return true;
    }


    /**
     * Updates the options available in the action bar based on which tab is selected
     * @param tab - tab currently selected
     */
    private void updateToolbar(TabLayout.Tab tab) {
        switch((int) tab.getTag()) {
            case TabConstants.STATUS_TAB:
                getSupportActionBar().setTitle("Status");
                menu.findItem(R.id.configure_option).setVisible(true);
                menu.findItem(R.id.add_effect_option).setVisible(false);
                break;
            case TabConstants.POWER_SAVE_TAB:
                getSupportActionBar().setTitle("Power Save");
                menu.findItem(R.id.configure_option).setVisible(false);
                menu.findItem(R.id.add_effect_option).setVisible(false);
                break;
            case TabConstants.CUSTOMIZE_TAB:
                getSupportActionBar().setTitle("Customize");
                menu.findItem(R.id.configure_option).setVisible(false);
                menu.findItem(R.id.add_effect_option).setVisible(true);
                break;
            default:
                break;
        }
    }
}
