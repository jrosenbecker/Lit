package com.lit.models;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.Random;

/**
 * Created by JoeLaptop on 3/19/2016.
 */
public class Light {
    private PHLight phLight;
    private PHHueSDK phHueSDK;
    private long id;

    public Light(String name, PHLight phLight, PHHueSDK phHueSDK) {
        this.phLight = phLight;
        this.phHueSDK = phHueSDK;
        // TODO: Integrate this with the database
        id = (new Random()).nextLong();
    }

    public boolean isLightOn() {
        return phLight.getLastKnownLightState().isOn();
    }

    public String getLightName() {
        return phLight.getName();
    }

    public void setLightName(String name)
    {
        phLight.setName(name);
    }

    public long getId() {
        return id;
    }

    public void setLightOn(Boolean lightOn)
    {
        PHLightState lightState = phLight.getLastKnownLightState();
        lightState.setOn(lightOn);
        phHueSDK.getSelectedBridge().updateLightState(phLight, lightState);
    }

    public boolean isConnectedToBridge()
    {
//        phHueSDK.getSelectedBridge().getResourceCache().
        return phLight.getLastKnownLightState().isReachable();
    }

    public PHLightState getLightState()
    {
        return phLight.getLastKnownLightState();
    }


    public PHLight getPhLight()
    {
        return phLight;
    }
//
//    public boolean getConnectionStatus() {
//        return connectionStatus;
//    }
//
//    public Effect getEffect() {
//        return effect;
//    }
//
//    public String getActiveSensor() {
//        return activeSensor;
//    }
}
