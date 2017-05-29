package creeper_san.myshoes.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDatabaseHelper {
    private final static String NAME_DATABASE = "Step.db";
    private final static String NAME_TABLE_STEP = "Step";
    private final static String NAME_WEIGHT_STEP = "Weight";
    private final static String COLUMN_YEAR = "Year";
    private final static String COLUMN_MONTH = "Month";
    private final static String COLUMN_DAY = "Day";
    private final static String COLUMN_STEP = "Step";
    private final static String COLUM_WEIGHT = "Weight";

    private SQLiteDatabase stepDatabase;

    public UserDatabaseHelper(Context context) {
        stepDatabase = context.openOrCreateDatabase(NAME_DATABASE,Context.MODE_PRIVATE,null);
        stepDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_STEP+"("
                + COLUMN_YEAR + " INTEGER NOT NULL ,"
                + COLUMN_MONTH + " INTEGER NOT NULL ,"
                + COLUMN_DAY + " INTEGER NOT NULL ,"
                + COLUMN_STEP + " INTEGER NOT NULL "
                +")");
        stepDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_WEIGHT_STEP+"("
                + COLUMN_YEAR + " INTEGER NOT NULL ,"
                + COLUMN_MONTH + " INTEGER NOT NULL ,"
                + COLUMN_DAY + " INTEGER NOT NULL ,"
                + COLUM_WEIGHT + " INTEGER NOT NULL "
                +")");
    }

    public int getDataSteps(int year,int month,int day){
        Cursor cursor = stepDatabase.query(NAME_TABLE_STEP,null,
                COLUMN_YEAR+" = ? and "+COLUMN_MONTH+" = ? and "+COLUMN_DAY+" = ?",
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)},null,null,null);
        if (cursor!=null){
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                int result = cursor.getInt(cursor.getColumnIndex(COLUMN_STEP));
                cursor.close();
                return result;
            }
            cursor.close();
        }
        return 0;
    }

    public int getDataWeight(int year,int month,int day){
        Cursor cursor = stepDatabase.query(NAME_WEIGHT_STEP,null,
                COLUMN_YEAR+" = ? and "+COLUMN_MONTH+" = ? and "+COLUMN_DAY+" = ?",
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)},null,null,null);
        if (cursor!=null){
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                int result = cursor.getInt(cursor.getColumnIndex(COLUM_WEIGHT));
                cursor.close();
                return result;
            }
            cursor.close();
        }
        return 0;
    }

    public void insertDataSteps(int year,int month,int day,int steps){
        //先查询是否存在
        Cursor cursor = stepDatabase.query(NAME_TABLE_STEP,null,
                COLUMN_YEAR+" = ? and "+COLUMN_MONTH+" = ? and "+COLUMN_DAY+" = ?",
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)},null,null,null);
        if (cursor!=null){
            if (cursor.getCount()>0){//假如已经存在
                ContentValues values = new ContentValues();
                values.put(COLUMN_STEP,steps);
                stepDatabase.update(NAME_TABLE_STEP,values,
                        COLUMN_YEAR+" = ? and "+COLUMN_MONTH+" = ? and "+COLUMN_DAY+" = ?",
                        new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});
            }else {//假如没有存在
                ContentValues values = new ContentValues();
                values.put(COLUMN_YEAR,year);
                values.put(COLUMN_MONTH,month);
                values.put(COLUMN_DAY,day);
                values.put(COLUMN_STEP,steps);
                stepDatabase.insert(NAME_TABLE_STEP,null,values);
            }
            cursor.close();
        }
    }

    public void insertDataWeight(int year,int month,int day,int weight){
        //先查询是否存在
        Cursor cursor = stepDatabase.query(NAME_WEIGHT_STEP,null,
                COLUMN_YEAR+" = ? and "+COLUMN_MONTH+" = ? and "+COLUMN_DAY+" = ?",
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)},null,null,null);
//        log("查询是否存在");
        if (cursor!=null){
//            log("游标不为空");
            if (cursor.getCount()>0){//假如已经存在
//                log("已有数据");
                ContentValues values = new ContentValues();
                values.put(COLUM_WEIGHT,weight);
                stepDatabase.update(NAME_WEIGHT_STEP,values,
                        COLUMN_YEAR+" = ? and "+COLUMN_MONTH+" = ? and "+COLUMN_DAY+" = ?",
                        new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});
            }else {//假如没有存在
//                log("还没有数据");
                ContentValues values = new ContentValues();
                values.put(COLUMN_YEAR,year);
                values.put(COLUMN_MONTH,month);
                values.put(COLUMN_DAY,day);
                values.put(COLUM_WEIGHT,weight);
                stepDatabase.insert(NAME_WEIGHT_STEP,null,values);
            }
//            log("关闭游标");
            cursor.close();
        }
//        log("结束");
    }

    public void close(){
        stepDatabase.close();
    }


    public void log(String content){
        Log.i("数据库",content);
    }
}
