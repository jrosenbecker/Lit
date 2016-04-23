package com.lit.database;

import com.philips.lighting.model.PHBridge;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.DaoGenerator;

/**
 * Created by Graham on 4/12/2016.
 */
public class AppDaoGenerator {
    /**
     * Class used to generate the GreenDAO database files used for persistence
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {

        Schema schema = new Schema(1, "com.lit.daogenerator");

        Entity RoomTable = schema.addEntity("RoomTable");
        RoomTable.addIdProperty();
        RoomTable.addStringProperty("name");

        Entity LightTable = schema.addEntity("LightTable");
        LightTable.addIdProperty();
        LightTable.addStringProperty("name");
        LightTable.addIntProperty("red");
        LightTable.addIntProperty("green");
        LightTable.addIntProperty("blue");
        LightTable.addLongProperty("roomId");
        LightTable.addStringProperty("hueId");
        LightTable.addBooleanProperty("effectOn");

        new DaoGenerator().generateAll(schema, "./app/src/main/java/");
    }
}