package creeper_san.myshoes.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper {
    private static final String DATABASE_NAME = "Database.db";



    SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        database = context.openOrCreateDatabase(DATABASE_NAME,Context.MODE_PRIVATE,null);
    }

    public void close(){
        database.close();
    }
}
