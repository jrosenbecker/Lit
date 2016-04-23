package com.lit.database;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;

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

    private static DaoMaster.DevOpenHelper dbHelper;
    private static SQLiteDatabase database;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

//    private static BridgeInfoDao bridgeDao;
//    private static List<BridgeInfo> bridgeList;

    private static LightTable lights;
    private static LightTableDao lightDao;
    private static List<LightTable> lightTableRows;
    private static List<Light> lightList;

    private static RoomTable rooms;
    private static RoomTableDao roomDao;
    private static List<RoomTable> roomTableRows;
    private static List<Room> roomList;

    private static PHHueSDK phHueSDK;

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

    public static void saveRoom(Context context, String name) {
        Random rand = new Random();
        RoomTable roomTableRow = new RoomTable(rand.nextLong(), name);
        roomDao.insert(roomTableRow);
        closeReopenDatabase(context);
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

    public static void deleteRoom(Room room) {

        QueryBuilder<RoomTable> qb = roomDao.queryBuilder();
        qb.where(RoomTableDao.Properties.Name.eq(room.getName()));

        if (!qb.list().isEmpty()) {

            for (RoomTable roomTableRow : qb.list()) {
                roomDao.delete(roomTableRow);
            }
        }
    }

    public static void saveLight(Context context, String name,
                                 int red, int green, int blue,
                                 long roomId, String hueId,
                                 boolean effectOn) {
        Random rand = new Random();

        LightTable lightTableRow = new LightTable(rand.nextLong(), name,
                                                red, green, blue,
                                                roomId, hueId,
                                                effectOn);
        lightDao.insert(lightTableRow);
        closeReopenDatabase(context);
    }

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

    public static void deleteLight(Light light) {

        QueryBuilder<LightTable> qb = lightDao.queryBuilder();
        qb.where(RoomTableDao.Properties.Name.eq(light.getLightName()));

        if (!qb.list().isEmpty()) {

            for (LightTable lightTableRow : qb.list()) {
                lightDao.delete(lightTableRow);
            }

        }
    }

    public static void clean() {
        daoMaster.dropAllTables(database, true);
    }
}
