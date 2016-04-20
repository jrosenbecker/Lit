package com.lit.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 4/18/2016.
 */
public class Room {
    private long id;
    private String name;
    private List<Light> lights;

    public Room(String name)
    {
        this.name = name;
        lights = new ArrayList<Light>();
    }

    public Room(String name, List<Light> lights)
    {
        this.name = name;
        this.lights = lights;
    }

    public String getName() {
        return name;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }

    public void addLight(Light light)
    {
        lights.add(light);
    }

    public long getId() {
        return id;
    }
}
