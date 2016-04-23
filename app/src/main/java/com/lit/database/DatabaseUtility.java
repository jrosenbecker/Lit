package com.lit.database;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.util.Log;

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
        import java.util.List;
        import java.util.Random;

        import de.greenrobot.dao.query.DeleteQuery;
        import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Graham on 4/16/2016.
 */
public class DatabaseUtility {

    private static String myTag = "DatabaseUtility";

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

    private static List<Light> unassignedLights;

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
        unassignedLights = new ArrayList<Light>();
        if (!addUnassignedLights()) {
            Log.v(myTag, "Error: Couldn't initialize the 'unassignedLights' list.");
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

    public static boolean saveRoom(Context context, Room room) {

        Random rand = new Random();

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();
        qb.where(RoomTableDao.Properties.Name.eq(room.getName()));

        boolean returnValue = false;

        if (qb.list().isEmpty()) {
            RoomTable roomTableRow = new RoomTable(rand.nextLong(), room.getName());
            roomDao.insert(roomTableRow);
            returnValue = true;
        }

        closeReopenDatabase(context);

        return returnValue;
    }

    public static Room getRoom(String name) {

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();
        qb.where(RoomTableDao.Properties.Name.eq(name));
        qb.limit(1); // TODO: Make sure this is right

        RoomTable tableRow = qb.unique();

        Room room = new Room(tableRow.getName(), getRoomLights(tableRow.getId()));

        return room;
    }

    public static List<Room> getAllRooms() {
        // TODO: use getRoom() for this

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();
        qb.where(RoomTableDao.Properties.Id.isNotNull());

        List<RoomTable> roomRows = qb.list();

        List<Room> rooms = new ArrayList<Room>();

        for (RoomTable room : roomRows) {
            rooms.add(getRoom(room.getName()));
        }

        return rooms;
    }

    public static boolean deleteRoom(Room room) {

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();
        qb.where(RoomTableDao.Properties.Name.eq(room.getName()));

        boolean returnValue = false;

        if (!qb.list().isEmpty()) {

            for (RoomTable roomTableRow : qb.list()) {
                roomDao.delete(roomTableRow);
            }
            returnValue = true;
        }

        return returnValue;
    }

//    public static boolean saveLight(Context context, String name,
//                                 int red, int green, int blue,
//                                 long roomId, String hueId,
//                                 boolean effectOn) {


    public static boolean saveLight(Context context, Light light) {
        Random rand = new Random();

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        qb.where(LightTableDao.Properties.Name.eq(light.getLightName()),
                LightTableDao.Properties.RoomId.eq(light.getRoomId()),
                LightTableDao.Properties.HueId.eq(light.getHueId()));

        boolean returnValue = false;

        if (qb.list().isEmpty()) {
            LightTable lightTableRow = new LightTable(rand.nextLong(),
                                        light.getLightName(),
                                        light.getRed(),
                                        light.getGreen(),
                                        light.getBlue(),
                                        light.getRoomId(),
                                        light.getHueId(),
                                        light.isEffectOn());

            lightDao.insert(lightTableRow);
            returnValue = true;
        }

        closeReopenDatabase(context);

        return returnValue;
    }
//
//    public static List<Light> getAllLights() {
//        // TODO: use getRoom() for this
//
//        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
//        qb.where(RoomTableDao.Properties.Id.isNotNull());
//
//        List<LightTable> lightRows = qb.list();
//
//        List<Light> lights = new ArrayList<Light>();
//
//        for (RoomTable room : lightRows) {
//            lights.add((room.getName()));
//        }
//
//        return rooms;
//    }

    public static List<Light> getRoomLights(long roomId) {
        // TODO: use getRoom() for this

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        qb.where(LightTableDao.Properties.RoomId.eq(roomId));

        List<String> roomLights = new ArrayList<String>();

        for (LightTable lightTableRow : qb.list()) {
            roomLights.add(lightTableRow.getHueId());
        }

        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        List<Light> lights = new ArrayList<Light>();

        if (!qb.list().isEmpty()) {

            for (PHLight light : allLights) {

                if (!roomLights.contains(light.getUniqueId())) {
                    Light tempLight = new Light(light.getName(), light, phHueSDK);
                    lights.add(tempLight);
                }
            }
        }
        return lights;
    }

    public static boolean deleteLight(Light light) {

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        qb.where(RoomTableDao.Properties.Name.eq(light.getLightName()));

        boolean returnValue = false;

        if (!qb.list().isEmpty()) {

            for (LightTable lightTableRow : qb.list()) {
                lightDao.delete(lightTableRow);
            }
            returnValue = true;
        }

        return returnValue;
    }

    public static boolean addUnassignedLights() {

        boolean returnValue = false;

        try {

            List<PHLight> lights = phHueSDK.getSelectedBridge().getResourceCache().getAllLights();

            for (PHLight light : lights) {

                Light newLight = new Light(light.getName(),light,phHueSDK);
                newLight.setRoomId(0); // Room ID 0 is the 'unassigned room'
                newLight.setHueId(light.getUniqueId() + "_" + newLight.getRoomId() + "_" + newLight.getLightName());

                unassignedLights.add(newLight);
            }

            returnValue = true;

        } catch (Exception e){
            Log.v(myTag, e.getMessage());
        }
        return returnValue;
    }

    public static List<Light> getUnassignedLights() {
        return unassignedLights;
    }

    private static void assignLight(Light light) {
        unassignedLights.remove(light);
    }

    public static void clean() {
        daoMaster.dropAllTables(database, true);
    }
}
