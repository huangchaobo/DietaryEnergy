package com.die.cn.dietaryenergy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * {数据库操作类}
 * Created by huangchaobo on 2018/1/25 14.
 * 邮箱：huangchaobo@miao.cn
 */

public class DietEnergyDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_EXTRA_VOICE_INFO = "DietEnergyInfo";
    public static String DATA_BASE_NAME = "DietEnergy.db";
    private static final int VERSION = 1;
    private static DietEnergyDBHelper instance;

    public static DietEnergyDBHelper getInstance( Context context ) {
        if (instance == null) {
            instance = new DietEnergyDBHelper( context );
        }
        return instance;
    }

    public DietEnergyDBHelper(Context context){
        super(context, DATA_BASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableUser(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateTableUser(db, oldVersion, newVersion);
    }

    /**
     * 创建用户表
     *
     * */
    public void createTableUser(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EXTRA_VOICE_INFO + " (id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ",name TEXT, calory INTEGER ,protein FLOAT, fat FLOAT , carbohydrate FLOAT ,meal TEXT NOT NULL, variety TEXT NOT NULL,minimum INTEGER)" );
    }

    /**
     * 更新用户表
     *
     * */
    public void updateTableUser(SQLiteDatabase db, int oldVersion, int newVersion){
        if ( oldVersion != newVersion ){
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_EXTRA_VOICE_INFO );
            createTableUser(db);
        }
    }
}