package creeper_san.myshoes.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingHelper {
    private static SettingHelper mInstance;
    private static SharedPreferences sharePref;
    public static final String NAME = "SettingPref";

    private static final String KEY_AUTO_LIGHT = "isNeedAutoLight";

    private SettingHelper(Context context){
        sharePref = context.getApplicationContext().getSharedPreferences(NAME,Context.MODE_PRIVATE);
    };

    public static SettingHelper getInstance(Context context){
        if (mInstance == null){
            mInstance = new SettingHelper(context);
        }
        return mInstance;
    }

    public boolean isNeedAutoLight(){
        return sharePref.getBoolean(KEY_AUTO_LIGHT,false);
    }

}
