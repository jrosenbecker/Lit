package com.lit.models;

import android.graphics.Color;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.bridge.impl.PHHueResourcesConstants;
import com.philips.lighting.hue.sdk.data.PHHueConstants;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.Random;

/**
 * Created by JoeLaptop on 3/19/2016.
 */
public class Light {
    public static final int MAX_BRIGHTNESS = 254;
    public static final int MIN_BRIGHTNESS = 0;
    public static final int MAX_COLOR_VALUE = 255;
    private int red;
    private int green;
    private int blue;
    private PHLight phLight;
    private PHHueSDK phHueSDK;
    private long id;
    private long roomId;
    private boolean isEffectOn;
    private boolean isPowerSaveOn;


    public Light(/*int id_in, */String name, PHLight phLight, PHHueSDK phHueSDK) {
        this.phLight = phLight;
        this.phHueSDK = phHueSDK;
        setLightName(name);

        // TODO: Integrate this with the database
        id = (new Random()).nextLong();

        //id = id_in;

        isEffectOn = !this.phLight.getLastKnownLightState().getEffectMode().equals(PHLight.PHLightEffectMode.EFFECT_NONE);
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

    public String getHueId() {
        return this.phLight.getUniqueId();
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

    public boolean isEffectOn() {
        return isEffectOn;
    }

    public void setBrightness(int brightness)
    {
        if(brightness > MAX_BRIGHTNESS)
        {
            brightness = MAX_BRIGHTNESS;
        }
        else if (brightness < MIN_BRIGHTNESS)
        {
            brightness = MIN_BRIGHTNESS;
        }
        PHLightState lastState = phLight.getLastKnownLightState();
        lastState.setBrightness(brightness);
        phHueSDK.getSelectedBridge().updateLightState(phLight, lastState);
    }

    public int getBrightness()
    {
        return phLight.getLastKnownLightState().getBrightness();
    }

    public void setLightState(PHLightState state)
    {
        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
    }

    public float getX()
    {
        return phLight.getLastKnownLightState().getX();
    }

    public float getY()
    {
        return phLight.getLastKnownLightState().getY();
    }

    public int getRed()
    {
        float[] xy = {getX(), getY()};
        return Color.red(PHUtilities.colorFromXY(xy, phLight.getModelNumber()));
    }

    public int getGreen()
    {
        float[] xy = {getX(), getY()};
        return Color.green(PHUtilities.colorFromXY(xy, phLight.getModelNumber()));
    }

    public int getBlue()
    {
        float[] xy = {getX(), getY()};
        return Color.blue(PHUtilities.colorFromXY(xy, phLight.getModelNumber()));
    }

    public void setColor(int red, int green, int blue)
    {
        float[] xy = PHUtilities.calculateXYFromRGB(red, green, blue, phLight.getModelNumber());
        PHLightState state = phLight.getLastKnownLightState();
        state.setX(xy[0]);
        state.setY(xy[1]);
        phHueSDK.getSelectedBridge().updateLightState(phLight, state);
    }

    public long getRoomId() {
        return this.roomId;
    }

    public void setRoomId(long room_id) {
        this.roomId = room_id;
    }

    public boolean isPowerSaveOn() {
        return isPowerSaveOn;
    }

    public void setPowerSaveOn(boolean powerSaveOn) {
        isPowerSaveOn = powerSaveOn;
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
