package com.lit.models;

/**
 * Created by JoeLaptop on 3/19/2016.
 */
public class Light {
    private boolean lightOn;
    private String lightName;
    private long id;

    public Light(long id, String name, boolean status) {
        lightName = name;
        lightOn = status;
        this.id = id;
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
}
