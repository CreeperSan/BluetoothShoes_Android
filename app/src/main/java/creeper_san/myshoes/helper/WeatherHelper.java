package creeper_san.myshoes.helper;

import org.json.JSONException;

import creeper_san.myshoes.Exception.JsonDecodeException;
import creeper_san.myshoes.json.NowJson;

public class WeatherHelper {
    private static WeatherHelper mInstance;
    private long timeStamp = 0;
    private NowJson nowJson;

    private WeatherHelper(){}

    public static WeatherHelper getInstance(){
        if (mInstance==null){
            mInstance = new WeatherHelper();
        }
        return mInstance;
    }

    public static NowJson getJson(){
        return getInstance().nowJson;
    }

    public static long getTimeStamp(){
        return getInstance().timeStamp;
    }

    public static void setNowJson(String jsonStr){
        getInstance().timeStamp = System.currentTimeMillis();
        try {
            getInstance().nowJson = new NowJson(jsonStr);
        } catch (JSONException | JsonDecodeException e) {
            e.printStackTrace();
            getInstance().nowJson = null;
        }
    }

    public static void setTimeStamp(){
        getInstance().timeStamp = System.currentTimeMillis();
    }

}
