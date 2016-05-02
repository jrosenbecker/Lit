package com.lit.database;

        import android.content.Context;
        import android.content.Intent;
        import android.database.sqlite.SQLiteDatabase;
        import android.util.Log;
        import android.widget.Toast;

        import com.lit.R;
        import com.lit.activities.MainActivity;
        import com.lit.api.PH_ConfigureBridge;
        import com.lit.daogenerator.DaoMaster;
        import com.lit.daogenerator.DaoSession;
        import com.lit.daogenerator.LightTable;
        import com.lit.daogenerator.LightTableDao;
        import com.lit.daogenerator.RoomTable;
        import com.lit.daogenerator.RoomTableDao;
        import com.lit.models.Light;
        import com.lit.models.Room;
        import com.philips.lighting.hue.sdk.PHHueSDK;
        import com.philips.lighting.model.PHBridge;
        import com.philips.lighting.model.PHLight;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Random;

        import de.greenrobot.dao.query.DeleteQuery;
        import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Graham on 4/16/2016.
 */
public class DatabaseUtility {

    private static String myTag = "DatabaseUtility";

    private static final String BREATHE_EFFECT = "BREATHE";
    private static final String CYCLE_EFFECT = "COLOR_CYCLE";
    private static final String EPILEPTIC_EFFECT = "SEIZURE";

    private static DaoMaster.DevOpenHelper dbHelper;
    private static SQLiteDatabase database;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    private static LightTable lights;
    private static LightTableDao lightDao;
    private static List<LightTable> lightTableRows;
    private static List<Light> lightList;

    private static RoomTable rooms;
    private static RoomTableDao roomDao;
    private static List<RoomTable> roomTableRows;
    private static List<Room> roomList;

    private static PHHueSDK phHueSDK;

    //private static List<Light> unassignedLights;

    //TODO: Light HueId =  LIGHT_NAME + "_" + LIGHT_ROOM
    //    Ex: Light1_0

    public static void initDatabase(Context context)
    {

        phHueSDK = PHHueSDK.create();

        dbHelper = new DaoMaster.DevOpenHelper(context, "ORM.sqlite", null);
        database = dbHelper.getWritableDatabase();

        daoMaster = new DaoMaster(database);

        daoMaster.createAllTables(database, true);
        daoSession = daoMaster.newSession();

        lightDao = daoSession.getLightTableDao();
        roomDao = daoSession.getRoomTableDao();

        QueryBuilder<LightTable> lightQueryBuilder = lightDao.queryBuilder();
        QueryBuilder<RoomTable> roomQueryBuilder = roomDao.queryBuilder();

        lightQueryBuilder.where(LightTableDao.Properties.Id.isNotNull());
        lightTableRows = lightQueryBuilder.list();

        roomQueryBuilder.where(RoomTableDao.Properties.Id.isNotNull());
        roomTableRows = roomQueryBuilder.list();

        // Add all of the unassigned lights initially
        if (!addUnassignedLights()) {
            Log.v(myTag, "Error: Couldn't initialize the database.");
            Intent intent = new Intent(context, PH_ConfigureBridge.class);
            context.startActivity(intent);
        }

        if(lightTableRows == null || roomTableRows == null)
        {
            closeReopenDatabase(context);
        }
    }

    public static void closeReopenDatabase(Context context) {
        closeDatabase();
        dbHelper = new DaoMaster.DevOpenHelper(context, "ORM.sqlite", null);
        database = dbHelper.getWritableDatabase();

        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();

        lightDao = daoSession.getLightTableDao();
        roomDao = daoSession.getRoomTableDao();
    }

    public static void closeDatabase() {
        daoSession.clear();
        database.close();
        dbHelper.close();
    }

    public static long saveRoom(Context context, Room room) {

        long roomId = saveRoom(room);

        closeReopenDatabase(context);

        return roomId;
    }

    private static long saveRoom(Room room) {

        Random rand = new Random();

        // This checks that the room was added successfully
        long roomId = -1;

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();
        RoomTable table = qb.where(RoomTableDao.Properties.Name.eq(room.getName())).unique();

        if (table == null) {

            if (!room.getName().equalsIgnoreCase("unassigned")) {
                roomId = rand.nextLong();
            } else {
                roomId = 0;
            }

            RoomTable roomTableRow = new RoomTable(roomId, room.getName());
            roomDao.insert(roomTableRow);
        }

        return roomId;
    }

    public static Room getRoom(Context context, long roomId) {

        Room room = getRoom(roomId);

        closeReopenDatabase(context);

        return room;
    }

    private static Room getRoom(long roomId) {

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();

        RoomTable tableRow = qb.where(RoomTableDao.Properties.Id.eq(roomId)).unique();

        Room room;

        if (tableRow != null) {
            room = new Room(tableRow.getName(), getRoomLights(tableRow.getId()));
            room.setId(tableRow.getId());
        } else {
            room = new Room("Unknown", new ArrayList<Light>());
            room.setId((long) 0);
        }

        return room;
    }

    public static List<Room> getAllRooms() {

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();
        List<RoomTable> table = qb.where(RoomTableDao.Properties.Id.isNotNull()).list();

        List<Room> rooms = new ArrayList<Room>();

        Room testRoom;

        for (RoomTable room : table) {

            // Don't add unknown rooms
            testRoom = getRoom(room.getId());
            testRoom.setId(room.getId());
            if (!testRoom.getName().equalsIgnoreCase("unknown")) {
                rooms.add(testRoom);
            }
        }

        return rooms;
    }

    public static boolean deleteRoom(Context context, Room room) {

        boolean value = deleteRoom(room);

        closeReopenDatabase(context);

        return value;

    }

    private static boolean deleteRoom(Room room) {

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();

        RoomTable tableRow = qb.where(RoomTableDao.Properties.Name.eq(room.getName())).unique();

        boolean returnValue = false;

        if (tableRow != null) {

            for (RoomTable roomTableRow : qb.list()) {
                roomDao.delete(roomTableRow);
            }
            returnValue = true;
        }

        return returnValue;
    }

    public static boolean saveLight(Context context, Light light) {

        boolean value = saveLight(light);

        closeReopenDatabase(context);

        return value;
    }

    private static boolean saveLight(Light light) {

        Random rand = new Random();

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        List<LightTable> table = qb.where(LightTableDao.Properties.Name.eq(light.getLightName()),
                LightTableDao.Properties.RoomId.eq(light.getRoomId()),
                LightTableDao.Properties.HueId.eq(light.getHueId())).list();

        boolean returnValue = false;

        if (table.isEmpty()) {

            Log.v("saveLight","Name: " + light.getLightName() + " RoomId: " + light.getRoomId() + " HueId: " + light.getHueId());

            LightTable lightTableRow = new LightTable(rand.nextLong(),
                                        light.getLightName(),
                                        light.getRed(),
                                        light.getGreen(),
                                        light.getBlue(),
                                        light.getRoomId(),
                                        light.getHueId(),
                                        false,false,false);
                                        //light.isEffectOn());

            lightDao.insert(lightTableRow);
            returnValue = true;
        }

        return returnValue;
    }

    public static Light getLight(String name, long roomId, String hueId) {

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();

        LightTable tableRow = qb.where(LightTableDao.Properties.Name.eq(name),
                LightTableDao.Properties.RoomId.eq(roomId),
                LightTableDao.Properties.HueId.eq(hueId)).unique();

        Light light = null;
        PHLight theChosenOne = null;

        List<PHLight> allLights = phHueSDK.getSelectedBridge().getResourceCache().getAllLights();

        Log.v("getLight","Finding light (" + name + ", " + roomId + ", " + hueId + ")");

        if (tableRow != null) {
            for (PHLight phLight : allLights) {
                if (phLight.getUniqueId().equals(tableRow.getHueId())) {
                    theChosenOne = phLight;
                    break;
                }
            }
        }

        if (tableRow != null && theChosenOne != null) {
            light = new Light(tableRow.getName(),theChosenOne,phHueSDK);
        }

        return light;

    }

    public static boolean updateLightName(Context context,
                                          /*Update parameter*/ String lightName,
                                          /*Query parameters*/ long roomId, String hueId) {

        boolean value = updateLightName(lightName, roomId, hueId);

        closeReopenDatabase(context);

        return value;
    }

    private static boolean updateLightName(
                                          /*Update parameter*/ String lightName,
                                          /*Query parameters*/ long roomId, String hueId) {

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        LightTable tableRow = qb.where(LightTableDao.Properties.RoomId.eq(roomId),
                LightTableDao.Properties.HueId.eq(hueId)).unique();

        Log.v("updateLightName","RoomId: " + roomId + " HueId: " + hueId);

        boolean returnValue = false;

        if (tableRow != null) {

            // Delete old entry
            lightDao.deleteByKey(tableRow.getId());

            Log.v("updateLightName", "LightName: " + lightName);

            // Add new entry
            tableRow.setName(lightName);
            lightDao.insert(tableRow);

            returnValue = true;
        }

        return returnValue;
    }

    public static boolean updateLightRoom(Context context,
                                          /*Update parameter*/ long roomId,
                                          /*Query parameters*/ String lightName, String hueId) {

        boolean value = updateLightRoom(roomId, lightName, hueId);

        closeReopenDatabase(context);

        return value;

    }

    private static boolean updateLightRoom(
                                          /*Update parameter*/ long roomId,
                                          /*Query parameters*/ String lightName, String hueId) {

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        LightTable tableRow = qb.where(LightTableDao.Properties.Name.eq(lightName),
                LightTableDao.Properties.HueId.eq(hueId)).unique();

        boolean returnValue = false;

        if (tableRow != null) {

            // Delete old entry
            lightDao.deleteByKey(tableRow.getId());

            // Add new entry
            tableRow.setRoomId(roomId);
            lightDao.insert(tableRow);

            returnValue = true;

        }

        return returnValue;
    }

    public static boolean getLightEffect(String effectType,
                                            /*Query parameters*/ String hueId) {

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        LightTable tableRow = qb.where(LightTableDao.Properties.HueId.eq(hueId)).unique();

        boolean returnValue = false;

        if (effectType.equals(CYCLE_EFFECT)) {
            returnValue = tableRow.getCycleEffect();
        } else if (effectType.equals(BREATHE_EFFECT)) {
            returnValue = tableRow.getBreatheEffect();
        } else {
            Log.v("getLightEffect","Invalid effect query");
        }

        return returnValue;
    }

    public static boolean updateLightEffect(Context context, String effectType,
                                            /*Update parameter*/ boolean effectOn,
                                            /*Query parameters*/ String hueId) {

        boolean value = updateLightEffect(effectType, hueId, effectOn);

        closeReopenDatabase(context);

        return value;
    }

    private static boolean updateLightEffect(String effectType, String hueId, boolean effectOn)
    {
        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        LightTable tableRow = qb.where(LightTableDao.Properties.HueId.eq(hueId)).unique();

        boolean returnValue = false;

        if (tableRow != null) {

            // Delete old entry
            lightDao.deleteByKey(tableRow.getId());

            // Add new entry
            if (effectType.equals(BREATHE_EFFECT)) {
                tableRow.setBreatheEffect(effectOn);

                /* If the other effect is turned on the other must be forced off */
                if (effectOn) {
                    tableRow.setCycleEffect(false);
                    tableRow.setEpilepticEffect(false);
                }
            } else if (effectType.equals(CYCLE_EFFECT)) {
                tableRow.setCycleEffect(effectOn);

                /* If the other effect is turned on the other must be forced off */
                if (effectOn) {
                    tableRow.setBreatheEffect(false);
                    tableRow.setEpilepticEffect(false);
                }
            } else if (effectType.equals(EPILEPTIC_EFFECT)) {
                tableRow.setEpilepticEffect(effectOn);

                /* If the other effect is turned on the other must be forced off */
                if (effectOn) {
                    tableRow.setBreatheEffect(false);
                    tableRow.setCycleEffect(false);
                }
            } else {
                Log.v("updateLightEffect","Invalid effect assignment");
            }
            lightDao.insert(tableRow);

            returnValue = true;
        }

        return returnValue;
    }

    public static List<Light> getRoomLights(long roomId)
    {
        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        List<LightTable> table = qb.where(LightTableDao.Properties.RoomId.eq(roomId)).list();

        List<String> roomLights = new ArrayList<String>();

        Map<String, String> uniqueIdToName = new HashMap<String, String>();

        for (LightTable lightTableRow : table) {
            roomLights.add(lightTableRow.getHueId());
            uniqueIdToName.put(lightTableRow.getHueId(),lightTableRow.getName());
            Log.v("getRoomLight", "Found light " + lightTableRow.getName() + " in room " + roomId);
        }

        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        List<Light> lights = new ArrayList<Light>();

        if (!table.isEmpty()) {

            for (PHLight light : allLights) {

                Log.v("getRoomLights", "PHLight: " + light.getName() + " Unique id: " + light.getUniqueId());

                if (roomLights.contains(light.getUniqueId())) {

                    Light tempLight = new Light(light.getName(), light, phHueSDK);
                    tempLight.setLightName(uniqueIdToName.get(light.getUniqueId()));
                    tempLight.setRoomId(roomId);

                    Log.v("getRoomLight", "Found: " + light.getName());
                    lights.add(tempLight);
                }

            }
        }
        return lights;
    }

    public static boolean deleteLight(Context context, Light light)
    {
        boolean value = deleteLight(light);

        closeReopenDatabase(context);

        return value;
    }

    private static boolean deleteLight(Light light)
    {
        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        LightTable tableRow = qb.where(LightTableDao.Properties.Name.eq(light.getLightName()),
                LightTableDao.Properties.RoomId.eq(light.getRoomId())).unique();

        Log.v("deleteLight", "Name: " + light.getLightName());
        Log.v("deleteLight", "RoomId: " + light.getRoomId());

        boolean returnValue = false;

        if (tableRow != null) {

            lightDao.deleteByKey(tableRow.getId());

            LightTable test = qb.where(LightTableDao.Properties.Name.eq(light.getLightName()),
                    LightTableDao.Properties.RoomId.eq(light.getRoomId())).unique();

            Log.v("deleteLight", "Deleting: " + tableRow.getName());
            Log.v("deleteLight", "Deleting: " + (test == null));

            returnValue = true;
        }

        return returnValue;
    }

    public static boolean addUnassignedLights()
    {
        boolean returnValue = false;

        try {

            List<PHLight> lights = phHueSDK.getSelectedBridge().getResourceCache().getAllLights();

            for (PHLight light : lights) {

                Light newLight = new Light(light.getName(),light,phHueSDK);
                newLight.setLightName(light.getName());
                newLight.setRoomId(0); // Room ID 0 is the 'Unassigned' room

                saveLight(newLight);
            }

            returnValue = true;

        } catch (Exception e){
            Log.v(myTag, e.getMessage());
        }
        return returnValue;
    }

    public static void clean() {
        daoMaster.dropAllTables(database, true);
    }
}
