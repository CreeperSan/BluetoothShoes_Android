package creeper_san.myshoes.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import creeper_san.myshoes.bean.ChatBean;
import creeper_san.myshoes.bean.ChatUserBean;

public class ChatDatabaseHelper {
    private final static String TAG = "ChatDataBaseHelper";

    private final static String NAME_DATABASE  = "Chat.db";
    private final static String NAME_TABLE_INDEX = "_index";

    private final static String COLUMN_INDEX_ID = "_ID";
    private final static String COLUMN_INDEX_USERNAME = "UserName";
    private final static String COLUMN_INDEX_NICKNAME = "NickName";
    private final static String COLUMN_INDEX_NEED_READ = "IsRead";
    private final static String COLUMN_INDEX_CONTENT = "Content";
    private final static String COLUMN_INDEX_TIME_STAMP = "TimeStamp";

    private final static String COLUMN_USER_TIME_STAMP = "TimeStamp";
    private final static String COLUMN_USER_CONTENT = "Content";
    private final static String COLUMN_USER_IS_FROM_SELF = "IsFromSelf";

    private SQLiteDatabase database;


    public ChatDatabaseHelper(Context context,String userName) {
        database = context.openOrCreateDatabase(userName+NAME_DATABASE,Context.MODE_PRIVATE,null);
        //初始化Index表
        database.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_INDEX+"("
                +COLUMN_INDEX_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , "
                +COLUMN_INDEX_USERNAME + " TEXT NOT NULL , "
                +COLUMN_INDEX_NICKNAME + " TEXT NOT NULL , "
                +COLUMN_INDEX_TIME_STAMP + " TEXT NOT NULL , "
                +COLUMN_INDEX_CONTENT + " TEXT NOT NULL , "
                + COLUMN_INDEX_NEED_READ + " TEXT NOT NULL "
                +")");
    }

    private void initUserTable(String userName){
        database.execSQL("CREATE TABLE IF NOT EXISTS "+userName+"(" +
                COLUMN_USER_TIME_STAMP +" TEXT NOT NULL ," +
                COLUMN_USER_CONTENT +" TEXT NOT NULL ," +
                COLUMN_USER_IS_FROM_SELF +" TEXT NOT NULL " +
                ")");
    }


    public void writeNewMessage(String fromUserName,String fromNickName,String timeStamp,String content,boolean isFormSelf){
        //先把数据写入聊天记录表
        initUserTable(fromUserName);//创建表
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_CONTENT,content);
        values.put(COLUMN_USER_TIME_STAMP,timeStamp);
        values.put(COLUMN_USER_IS_FROM_SELF,isFormSelf?"1":"0");
        database.insert(fromUserName,null,values);
        if (isFormSelf){
            return;
        }
        //提醒有新消息收到
        Cursor cursor = database.query(NAME_TABLE_INDEX,null,COLUMN_INDEX_USERNAME+"=?",new String[]{fromUserName},null,null,null);
        if (cursor!=null){
            if (cursor.getCount()>0){//如果存在这个用户
                ContentValues valuesIndex = new ContentValues();
                valuesIndex.put(COLUMN_INDEX_NICKNAME,fromNickName);
                valuesIndex.put(COLUMN_INDEX_CONTENT,content);
                valuesIndex.put(COLUMN_INDEX_TIME_STAMP,timeStamp);
                valuesIndex.put(COLUMN_INDEX_NEED_READ,"1");
                database.update(NAME_TABLE_INDEX,valuesIndex,COLUMN_INDEX_USERNAME+"=?",new String[]{fromUserName});
                cursor.close();
                return;
            }
        }
        //如果不存在这个用户
        ContentValues valuesInsert = new ContentValues();
        valuesInsert.put(COLUMN_INDEX_NICKNAME,fromNickName);
        valuesInsert.put(COLUMN_INDEX_CONTENT,content);
        valuesInsert.put(COLUMN_INDEX_TIME_STAMP,timeStamp);
        valuesInsert.put(COLUMN_INDEX_NEED_READ,"1");
        valuesInsert.put(COLUMN_INDEX_USERNAME,fromUserName);
        database.insert(NAME_TABLE_INDEX,null,valuesInsert);
    }
    public void readMessage(String userName){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INDEX_NEED_READ,"0");
        database.update(NAME_TABLE_INDEX,contentValues,COLUMN_INDEX_USERNAME+"=?",new String[]{userName});
    }
    public void sendMessage(String userName,String timeStamp,String content){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_TIME_STAMP,timeStamp);
        values.put(COLUMN_USER_CONTENT,content);
        values.put(COLUMN_USER_IS_FROM_SELF,"1");
        database.insert(userName,null,values);
        //更新索引
        Cursor cursor = database.query(NAME_TABLE_INDEX,null,COLUMN_INDEX_USERNAME+"=?",new String[]{userName},null,null,null);
        if (cursor!=null){
            if (cursor.getCount()>0){//如果存在这个用户
                ContentValues valuesIndex = new ContentValues();
                valuesIndex.put(COLUMN_INDEX_CONTENT,content);
                valuesIndex.put(COLUMN_INDEX_TIME_STAMP,timeStamp);
                valuesIndex.put(COLUMN_INDEX_NEED_READ,"0");
                database.update(NAME_TABLE_INDEX,valuesIndex,COLUMN_INDEX_USERNAME+"=?",new String[]{userName});
                cursor.close();
                return;
            }
        }
        //如果不存在这个用户
        ContentValues valuesInsert = new ContentValues();
        valuesInsert.put(COLUMN_INDEX_CONTENT,content);
        valuesInsert.put(COLUMN_INDEX_TIME_STAMP,timeStamp);
        valuesInsert.put(COLUMN_INDEX_NEED_READ,"0");
        valuesInsert.put(COLUMN_INDEX_USERNAME,userName);
        database.insert(NAME_TABLE_INDEX,null,valuesInsert);
    }

    public List<ChatUserBean> getCharUserBeanList(){
        List<ChatUserBean> chatUserBeanList = new ArrayList<>();
        Cursor cursor = database.query(NAME_TABLE_INDEX,null,"_ID>?",new String[]{"0"},null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                String userName = cursor.getString(cursor.getColumnIndex(COLUMN_INDEX_USERNAME));
                String nickName = cursor.getString(cursor.getColumnIndex(COLUMN_INDEX_NICKNAME));
                String timeStamp = cursor.getString(cursor.getColumnIndex(COLUMN_INDEX_TIME_STAMP));
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_INDEX_CONTENT));
                String needRead = cursor.getString(cursor.getColumnIndex(COLUMN_INDEX_NEED_READ));
                ChatUserBean bean = new ChatUserBean(userName,nickName,timeStamp,needRead,content);
                chatUserBeanList.add(bean);
            }
            cursor.close();
        }
        return chatUserBeanList;
    }
    public List<ChatBean> getChatBeanList(String userName){
        List<ChatBean> chatBeanList = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM "+userName+" ORDER BY TimeStamp",null);
            if (cursor!=null){
                while (cursor.moveToNext()){
                    String timeStamp = cursor.getString(cursor.getColumnIndex(COLUMN_USER_TIME_STAMP));
                    String content = cursor.getString(cursor.getColumnIndex(COLUMN_USER_CONTENT));
                    String isFromSelf = cursor.getString(cursor.getColumnIndex(COLUMN_USER_IS_FROM_SELF));
                    ChatBean bean = new ChatBean(isFromSelf,content,timeStamp);
                    chatBeanList.add(bean);
                }
                cursor.close();
            }
        } catch (Exception e) {
            log("没有该表");
        }
        return chatBeanList;
    }

    public void close(){
        database.close();
    }

    public void log(String content){
        Log.i(TAG,content);
    }
}
