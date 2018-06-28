package com.die.cn.dietaryenergy;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.die.cn.dietaryenergy.util.PreferencesUtils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * {此处填写描述信息}
 * Created by huangchaobo on 2018/1/25 14.
 */

public class DietDBManager {
    /**
     * 是否读取过表格数据
     */
    public static final String IS_READED_EXTRA_SOUND_DATA = "is_readed_extra_sound_data";

    private static final String TAG = "ExtraVoiceDBManager";
    private static final String EXCEPTION = "exception";
    private Context mContext;
    private DietEnergyDBHelper mDBHelper = null;
    private static DietDBManager instance = null;
    private Workbook workbook;
    private Sheet sheet;
    private Row row;

    public static DietDBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DietDBManager(context.getApplicationContext());
        }
        return instance;
    }

    private DietDBManager(Context context) {
        this.mContext=context;
        mDBHelper = DietEnergyDBHelper.getInstance(context);
        if (!PreferencesUtils.getBoolean(context, IS_READED_EXTRA_SOUND_DATA, false)) {
            try {
                readExcelContentToDb(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取 Excel 文件内容
     */
    public void readExcelContentToDb(Context context) throws IOException {
        InputStream is;
        DietInfoBean info = null;
        try {
            is = context.getAssets().open("diet.xls");
//            if (postfix.equals(".xls")) {
//                workbook = new HSSFWorkbook(new POIFSFileSystem(is));
//            } else {
            workbook = new XSSFWorkbook(is);
//            }

            sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getLastRowNum();//行数
            //正文从第二行开始，第一行为标题行
            for (int i = 1; i < rowNumber; i++) {
                row = sheet.getRow(i);//获得第 i 行对象

                String name = row.getCell(1).getStringCellValue();
                int calory = (int) row.getCell(2).getNumericCellValue();
                float protein = (float) row.getCell(3).getNumericCellValue();
                float fat = (float) row.getCell(4).getNumericCellValue();
                float carbohydrate = (float) row.getCell(5).getNumericCellValue();
                String meal = row.getCell(6).getStringCellValue();
                String variety = row.getCell(7).getStringCellValue();
                int minimum = (int) row.getCell(8).getNumericCellValue();

                info = new DietInfoBean(name, calory, protein, fat, carbohydrate, meal, variety, minimum);
                saveInfoToDataBase(info);
            }
            workbook.close();
            mContext.sendBroadcast(new Intent("data"));
            Log.d("zsj_excel", "excel读取完成");
            PreferencesUtils.putBoolean(context, IS_READED_EXTRA_SOUND_DATA, true);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, EXCEPTION, e);
            PreferencesUtils.putBoolean(context, IS_READED_EXTRA_SOUND_DATA, false);
        }
    }


    private void saveInfoToDataBase(DietInfoBean info) {
        if (mDBHelper == null) {
            return;
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("name", info.getName());
            values.put("calory", info.getCalory());
            values.put("protein", info.getProtein());
            values.put("fat", info.getFat());
            values.put("carbohydrate", info.getCarbohydrate());
            values.put("meal", info.getMeal());
            values.put("variety", info.getVariety());
            values.put("minimum", info.getMinimum());
            db.insert(DietEnergyDBHelper.TABLE_EXTRA_VOICE_INFO, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, EXCEPTION, e);
        } catch (Exception e) {
            Log.e(TAG, EXCEPTION, e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 根据id获取记录
     */
    public DietInfoBean getInfoById(String id) {
        DietInfoBean info = null;
        if (mDBHelper == null) {
            return info;
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        if (db == null) {
            return info;
        }

        Cursor cursor = db.rawQuery("select * from DietEnergyInfo where id = ?", new String[]{id});

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    info = getDietInfoBean(info, cursor);
                } while (cursor.moveToNext());
            }

        } catch (SQLiteException e) {
            Log.e(TAG, EXCEPTION, e);
        } catch (Exception e) {
            Log.e(TAG, EXCEPTION, e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (db != null) {
                db.close();
            }
        }
        return info;
    }

    /**
     * 查询数据
     * 早餐主食
     */
    public List<DietInfoBean> queryBreakfastFood() {
        DietInfoBean info = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DietEnergyInfo where meal like '%早%' and variety='主食'", null);
        List<DietInfoBean> passwords = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                info = getDietInfoBean(info, cursor);
                passwords.add(info);
            }
            cursor.close();
        }
        Log.d("huang",passwords.toString());
        return passwords;
    }

    /**
     * 查询数据
     * 早餐汤
     * caloryString 卡路里
     */
    public List<DietInfoBean> queryBreakfastSoup() {
        DietInfoBean info = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DietEnergyInfo where meal like '%早%' and variety='饮'" ,null);
        List<DietInfoBean> passwords = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                info = getDietInfoBean(info, cursor);
                passwords.add(info);
            }
            cursor.close();
        }
        Log.d("huang早餐饮品",passwords.toString());
        return passwords;
    }
    /**
     * 查询数据
     * 午餐主食和全/配菜和汤
     */
    public List<DietInfoBean> queryLunchFood2(String variety_str1,String variety_str2) {
        DietInfoBean info = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DietEnergyInfo where meal like '%午%' and variety=? or variety=?", new String[]{variety_str1,variety_str2});
        List<DietInfoBean> passwords = new ArrayList<>();
        passwords.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                info = getDietInfoBean(info, cursor);
                passwords.add(info);
            }
            cursor.close();
        }
        Log.d("huang午餐",passwords.toString());
        return passwords;
    }

    /**
     * 查询数据
     * 晚餐餐主食和全/配菜和汤
     */
    public List<DietInfoBean> queryDinner(String variety_str1,String variety_str2) {
        DietInfoBean info = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DietEnergyInfo where meal like '%晚%' and variety=? or variety=?", new String[]{variety_str1,variety_str2});
        List<DietInfoBean> passwords = new ArrayList<>();
        passwords.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                info = getDietInfoBean(info, cursor);
                passwords.add(info);
            }
            cursor.close();
        }
        Log.d("huang晚餐",passwords.toString());
        return passwords;
    }


    /**
     * 查询数据
     * 午餐主食
     */
    public List<DietInfoBean> queryLunchFood(String variety_str) {
        DietInfoBean info = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DietEnergyInfo where meal like '%午%' and variety=?", new String[]{variety_str});
        List<DietInfoBean> passwords = new ArrayList<>();
        passwords.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                info = getDietInfoBean(info, cursor);
                passwords.add(info);
            }
            cursor.close();
        }
        Log.d("huang",passwords.toString());
        return passwords;
    }

    /**
     * 查询数据
     * 晚餐主食
     */
    public List<DietInfoBean> queryDinnerFood(String variety_str) {
        DietInfoBean info = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DietEnergyInfo where meal like '%晚%' and variety=?", new String[]{variety_str});
        List<DietInfoBean> passwords = new ArrayList<>();
        passwords.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                info = getDietInfoBean(info, cursor);
                passwords.add(info);
            }
            cursor.close();
        }
        Log.d("huang晚餐主食",passwords.toString());
        return passwords;
    }





    /**
     * 查询数据
     * 加餐
     */
    public List<DietInfoBean> queryAddFood() {
        DietInfoBean info = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DietEnergyInfo where meal like '%加%' and variety='主食' or variety='水果'", null);
        List<DietInfoBean> passwords = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                info = getDietInfoBean(info, cursor);
                passwords.add(info);
            }
            cursor.close();
        }
        Log.d("huang",passwords.toString());
        return passwords;
    }

    /**
     * 从游标中取出数据
     */
    @NonNull
    private DietInfoBean getDietInfoBean(DietInfoBean info, Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        int calory = cursor.getInt(cursor.getColumnIndex("calory"));
        float protein = cursor.getFloat(cursor.getColumnIndex("protein"));
        float fat = cursor.getFloat(cursor.getColumnIndex("fat"));
        float carbohydrate = cursor.getFloat(cursor.getColumnIndex("carbohydrate"));
        String meal = cursor.getString(cursor.getColumnIndex("meal"));
        String variety = cursor.getString(cursor.getColumnIndex("variety"));
        int minimum = cursor.getInt(cursor.getColumnIndex("minimum"));
        info = new DietInfoBean(name, calory, protein, fat, carbohydrate, meal, variety, minimum);
        info.setId(id);
        return info;
    }

}