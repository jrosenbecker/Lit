package com.lit.models;

/**
 * Created by JoeLaptop on 3/29/2016.
 */
public class Effect {
    public static final int NONE = 0;
    public static final int STROBE = 1;
    public static final int BREATHE = 2;
    public static final int COLOR_CYCLE = 3;


    private int effectID;
    private boolean effectOn;

    public Effect(int effectID, boolean b)
    {
        this.effectID = effectID;
        this.effectOn = effectOn;
    }

    public int getEffectId()
    {
        return effectID;
    }


    public boolean isEffectOn()
    {
        return effectOn;
    }

    public String getText()
    {
        switch(effectID)
        {
            case NONE:
                return "None";
            case STROBE:
                return "Strobe";
            case BREATHE:
                return "Breathe";
            case COLOR_CYCLE:
                return "Color Cycle";
            default:
                return "N/A";
        }
    }
}
