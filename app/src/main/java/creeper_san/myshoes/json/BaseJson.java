package creeper_san.myshoes.json;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import creeper_san.myshoes.Exception.JsonDecodeException;


public abstract class BaseJson {
    private final String TAG = getClass().getSimpleName();

    protected BaseJson() {
    }

    public BaseJson(String jsonStr) throws JSONException, JsonDecodeException {
        this(new JSONObject(jsonStr));
    }
    public BaseJson(JSONObject jsonObject) throws JsonDecodeException{
        analyze(jsonObject);
    }

    protected abstract void analyze(JSONObject jsonObject) throws JsonDecodeException;

    protected void log(String content){
        Log.i(TAG, content);
    }
}
