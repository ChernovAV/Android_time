package com.chernov.android.android_time.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chernov.android.android_time.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class TimeDatabase extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "baza.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<Simple, Integer> studentDao;

    public TimeDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i("myLog", "onCreate");
            // создаем БД по шаблону Simple; connectionSource - подключение к БД
            TableUtils.createTable(connectionSource, Simple.class);
        } catch (SQLException e) {
            Log.e("myLog", "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Вызывается, когда ваше приложение обновляется и имеет больший номер версии. Это позволяет
     * регулировать различные данные в соответствии с новый номер версии.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i("myLog", "onUpgrade");
            TableUtils.dropTable(connectionSource, Simple.class, true);
            // после того как мы отбросили старую базы данных, мы создаем новую; connectionSource - подключение к БД
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e("myLog", "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public RuntimeExceptionDao<Simple, Integer> getSimpleDao() {
        if (studentDao == null) {
            studentDao = getRuntimeExceptionDao(Simple.class);
        }
        return studentDao;
    }

    /**
     * Закрываем подключение к БД и обнуляем simpleDao и simpleRuntimeDao
     */
    @Override
    public void close() {
        super.close();
        // simpleDao = null;
        studentDao = null;
    }
}