package com.lit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.lit.R;
import com.lit.adapters.AccessPointListAdapter;
import com.lit.api.HueSharedPreferences;
import com.lit.api.PH_AlertDialog;
import com.lit.api.PH_ConfigureBridge;
import com.lit.api.PH_Pushlink;
import com.lit.constants.TabConstants;
import com.lit.daogenerator.PowerSavePreference;
import com.lit.database.DatabaseUtility;
import com.lit.fragments.CustomizeFragment;
import com.lit.fragments.PowerSaveFragment;
import com.lit.fragments.StatusFragment;
import com.lit.services.PowerSaveService;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements CustomizeFragment.OnFragmentInteractionListener,
        PowerSaveFragment.OnFragmentInteractionListener,
        StatusFragment.OnFragmentInteractionListener {

    /**
     * Philips Hue SDK interface variables
     */
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE = 65535;
    public static final String TAG = "Lit";
    private HueSharedPreferences prefs;
    private boolean lastSearchWasIPScan = false;

    /**
     * Our stuff
     */
    private TabLayout tabLayout;
    private Menu menu;
    private String myTag = "MainAcvitity";


    /**
     * Sets up the activity by initializing the first fragment and setting up the toolbars
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Philips Hue interface handler
        phHueSDK = PHHueSDK.create();

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // Set up the tabbed action bar at the bottom
        tabLayout = (TabLayout) findViewById(R.id.tabMenu);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Status").setTag(TabConstants.STATUS_TAB));
        tabLayout.addTab(tabLayout.newTab().setText("Power Save").setTag(TabConstants.POWER_SAVE_TAB));
        tabLayout.addTab(tabLayout.newTab().setText("Customize").setTag(TabConstants.CUSTOMIZE_TAB));
        tabLayout.setSelectedTabIndicatorHeight(20);
        tabLayout.setTabTextColors(Color.LTGRAY,Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(getApplicationContext().getResources().getColor(R.color.tabIndicatorColor));

        tabLayout.setOnTabSelectedListener(onTabSelected);

        try {
            // If the database has been created, then we know that a previous
            // bridge has been connected to, so initiate access to the bridge
            if (DatabaseUtility.getAllRooms().isEmpty()) {
                DatabaseUtility.initDatabase(this);
            }

        } catch (Exception e) {
            Log.v(myTag, "Error: DatabaseUtility has not be initialized");
            DatabaseUtility.initDatabase(this);
        }

        // Open the status fragment when first created
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, StatusFragment.newInstance()).commit();
        List<Integer> powerSavePrefs = DatabaseUtility.getPowerSavePref();
        if(powerSavePrefs.size() == 2)
        {
            PowerSaveService.setLuxRange(powerSavePrefs.get(0), powerSavePrefs.get(1));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PowerSaveService.on_off = false;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.configure_option:
                intent = new Intent(getApplicationContext(), PH_ConfigureBridge.class);
                startActivity(intent);
                return true;
            case R.id.clean_database:
                DatabaseUtility.clean();
                DatabaseUtility.initDatabase(getApplicationContext());

            default:
                return super.onOptionsItemSelected(item);
        }
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
                break;
            case TabConstants.POWER_SAVE_TAB:
                getSupportActionBar().setTitle("Power Save");
                menu.findItem(R.id.configure_option).setVisible(false);
                break;
            case TabConstants.CUSTOMIZE_TAB:
                getSupportActionBar().setTitle("Customize");
                menu.findItem(R.id.configure_option).setVisible(false);
                break;
            default:
                break;
        }
    }



}
