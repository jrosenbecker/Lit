package com.lit.database;

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

        Schema schema = new Schema(1, "com.daogenerator");

        Entity lightBulbTable = schema.addEntity("lightBulbTable");
        lightBulbTable.addIdProperty();
        lightBulbTable.addStringProperty("name");
        lightBulbTable.addLongProperty("effectId");

        Entity effectTable = schema.addEntity("effectTable");
        effectTable.addIdProperty();
        effectTable.addStringProperty("name");
        effectTable.addLongProperty("intensity");
        effectTable.addLongProperty("dutyCycle");
        effectTable.addLongProperty("color1");
        effectTable.addLongProperty("color2");
        effectTable.addLongProperty("color3");

        Entity sensorTable = schema.addEntity("sensorTable");
        sensorTable.addIdProperty();
        sensorTable.addLongProperty("lightBulbId");

        Entity roomTable = schema.addEntity("roomTable");
        roomTable.addIdProperty();
        roomTable.addLongProperty("lightBulbId");

        new DaoGenerator().generateAll(schema, "./app/src/main/java/");
    }
}