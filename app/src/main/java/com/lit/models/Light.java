package com.lit.models;

/**
 * Created by JoeLaptop on 3/19/2016.
 */
public class Light {
    private boolean lightOn;
    private String lightName;
    private String activeSensor;
    private Effect effect;
    private long id;
    private boolean connectionStatus;

    public Light(long id, String name, boolean lightOn, boolean connectionStatus) {
        this(id, name, lightOn, connectionStatus, Effect.NONE, "Built In Light Sensor");
    }

    public Light(long id, String name, boolean lightOn, boolean connectionStatus, int effectID) {
        this(id, name, lightOn, connectionStatus, effectID, "Built In Light Sensor");
    }


        public Light(long id, String name, boolean lightOn, boolean connectionStatus, int effectID, String activeSensor) {
        lightName = name;
        this.lightOn = lightOn;
        this.id = id;
        this.connectionStatus = connectionStatus;
        effect = new Effect(effectID, false);
        this.activeSensor = activeSensor;
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

    public Effect getEffect()
    {
        return effect;
    }

    public String getActiveSensor()
    {
        return activeSensor;
    }
}
