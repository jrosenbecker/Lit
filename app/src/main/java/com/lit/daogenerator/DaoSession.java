package com.lit.daogenerator;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.lit.daogenerator.RoomTable;
import com.lit.daogenerator.LightTable;
import com.lit.daogenerator.PowerSavePreference;

import com.lit.daogenerator.RoomTableDao;
import com.lit.daogenerator.LightTableDao;
import com.lit.daogenerator.PowerSavePreferenceDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig roomTableDaoConfig;
    private final DaoConfig lightTableDaoConfig;
    private final DaoConfig powerSavePreferenceDaoConfig;

    private final RoomTableDao roomTableDao;
    private final LightTableDao lightTableDao;
    private final PowerSavePreferenceDao powerSavePreferenceDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        roomTableDaoConfig = daoConfigMap.get(RoomTableDao.class).clone();
        roomTableDaoConfig.initIdentityScope(type);

        lightTableDaoConfig = daoConfigMap.get(LightTableDao.class).clone();
        lightTableDaoConfig.initIdentityScope(type);

        powerSavePreferenceDaoConfig = daoConfigMap.get(PowerSavePreferenceDao.class).clone();
        powerSavePreferenceDaoConfig.initIdentityScope(type);

        roomTableDao = new RoomTableDao(roomTableDaoConfig, this);
        lightTableDao = new LightTableDao(lightTableDaoConfig, this);
        powerSavePreferenceDao = new PowerSavePreferenceDao(powerSavePreferenceDaoConfig, this);

        registerDao(RoomTable.class, roomTableDao);
        registerDao(LightTable.class, lightTableDao);
        registerDao(PowerSavePreference.class, powerSavePreferenceDao);
    }
    
    public void clear() {
        roomTableDaoConfig.getIdentityScope().clear();
        lightTableDaoConfig.getIdentityScope().clear();
        powerSavePreferenceDaoConfig.getIdentityScope().clear();
    }

    public RoomTableDao getRoomTableDao() {
        return roomTableDao;
    }

    public LightTableDao getLightTableDao() {
        return lightTableDao;
    }

    public PowerSavePreferenceDao getPowerSavePreferenceDao() {
        return powerSavePreferenceDao;
    }

}
