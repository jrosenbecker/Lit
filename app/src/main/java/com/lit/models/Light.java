package com.lit.models;

/**
 * Created by JoeLaptop on 3/19/2016.
 */
public class Light {
    private boolean lightOn;
    private String lightName;
    private long id;
    private boolean connectionStatus;

    public Light(long id, String name, boolean lightOn, boolean connectionStatus) {
        lightName = name;
        this.lightOn = lightOn;
        this.id = id;
        this.connectionStatus = connectionStatus;
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public String getLightName() {
        return lightName;
    }

    public long getId() {
        return id;
    }

    public boolean getConnectionStatus()
    {
        return connectionStatus;
    }
}
