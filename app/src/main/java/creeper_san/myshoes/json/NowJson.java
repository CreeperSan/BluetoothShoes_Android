package creeper_san.myshoes.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import creeper_san.myshoes.Exception.JsonDecodeException;

import static creeper_san.myshoes.Exception.JsonDecodeException.*;
import static creeper_san.myshoes.json.ErrorCode.*;
import static creeper_san.myshoes.json.JsonKey.*;

public class NowJson extends BaseJson {
    private List<NowSingleJson> nowSingleItemList;

    public NowJson(String jsonStr) throws JSONException, JsonDecodeException {
        super(jsonStr);
        log(jsonStr);
    }
    public NowJson(JSONObject jsonObject) throws JsonDecodeException {
        super(jsonObject);
    }

    @Override
    protected void analyze(JSONObject jsonObject) throws JsonDecodeException {
        if (nowSingleItemList==null){
            nowSingleItemList = new ArrayList<>();
        }else {
            nowSingleItemList.clear();
        }
        JSONArray heWeather5Json = jsonObject.optJSONArray(KEY_HE_WEATHER5);
        if (heWeather5Json!=null){
            for (int i=0;i<heWeather5Json.length();i++){
                NowSingleJson item = new NowSingleJson(heWeather5Json.optJSONObject(i));
                if (item!=null){
                    nowSingleItemList.add(item);
                }
            }
        }else {
            throw new JsonDecodeException(TYPE_MISS,"缺少heweather5键值对",CODE_MISSING);
        }
    }

    public int size(){
        return (nowSingleItemList==null)?0:nowSingleItemList.size();
    }
    public String getCity(int which){
        return nowSingleItemList.get(which).getCity();
    }
    public String getCountry(int which){
        return nowSingleItemList.get(which).getCnty();
    }
    public String getID(int which){
        return nowSingleItemList.get(which).getId();
    }
    public String getLat(int which){
        return nowSingleItemList.get(which).getLat();
    }
    public String getLon(int which){
        return nowSingleItemList.get(which).getLon();
    }
    public String getLoc(int which){
        return nowSingleItemList.get(which).getLoc();
    }
    public String getUtc(int which){
        return nowSingleItemList.get(which).getUtc();
    }
    public String getCode(int which){
        return nowSingleItemList.get(which).getCode();
    }
    public String getTxt(int which){
        return nowSingleItemList.get(which).getTxt();
    }
    public String getFl(int which){
        return nowSingleItemList.get(which).getFl();
    }
    public String getHum(int which){
        return nowSingleItemList.get(which).getHum();
    }
    public String getPcpn(int which){
        return nowSingleItemList.get(which).getPcpn();
    }
    public String getPres(int which){
        return nowSingleItemList.get(which).getPres();
    }
    public String getTmp(int which){
        return nowSingleItemList.get(which).getTmp();
    }
    public String getVis(int which){
        return nowSingleItemList.get(which).getVis();
    }
    public String getDeg(int which){
        return nowSingleItemList.get(which).getDeg();
    }
    public String getDir(int which){
        return nowSingleItemList.get(which).getDir();
    }
    public String getSc(int which){
        return nowSingleItemList.get(which).getSc();
    }
    public String getSpd(int which){
        return nowSingleItemList.get(which).getSpd();
    }




    private class NowSingleJson extends BaseJson {
        private String city;
        private String cnty;
        private String id;
        private String lat;
        private String lon;
        private String loc;
        private String utc;
        private String code;
        private String txt;
        private String fl;
        private String hum;
        private String pcpn;
        private String pres;
        private String tmp;
        private String vis;
        private String deg;
        private String dir;
        private String sc;
        private String spd;

        private NowSingleJson(String jsonStr) throws JSONException, JsonDecodeException {
            super(jsonStr);
        }
        private NowSingleJson(JSONObject jsonObject) throws JsonDecodeException {
            super(jsonObject);
        }

        @Override
        protected void analyze(JSONObject jsonObject) throws JsonDecodeException {
//            log("----------------------------------------");
//            log(jsonObject.toString());
            String status = jsonObject.optString(KEY_STATUS);
            if (!status.equals(CODE_OK)){
                throw new JsonDecodeException(TYPE_DECODE_ERR,"status不成功",status);
            }
            JSONObject basicJson = jsonObject.optJSONObject(KEY_BASIC);
            if (basicJson!=null){
                city = basicJson.optString(KEY_CITY);
                cnty = basicJson.optString(KEY_CNTY);
                id = basicJson.optString(KEY_ID);
                lat = basicJson.optString(KEY_LAT);
                lon = basicJson.optString(KEY_LON);
                JSONObject updateJson = basicJson.optJSONObject(KEY_UPDATE);
                if (updateJson!=null){
                    loc = updateJson.optString(KEY_LOC);
                    utc = updateJson.optString(KEY_UTC);
                }
            }
            JSONObject nowJson = jsonObject.optJSONObject(KEY_NOW);
            if (nowJson!=null){
                JSONObject condJson = nowJson.optJSONObject(KEY_COND);
                if (condJson!=null){
                    code = condJson.optString(KEY_CODE);
                    txt = condJson.optString(KEY_TXT);
                }
                fl = nowJson.optString(KEY_FL);
                hum = nowJson.optString(KEY_HUM);
                pcpn = nowJson.optString(KEY_PCPN);
                pres = nowJson.optString(KEY_PRES);
                tmp = nowJson.optString(KEY_TMP);
                vis = nowJson.optString(KEY_VIS);
                JSONObject windJson = nowJson.optJSONObject(KEY_WIND);
                if (windJson!=null){
                    deg = windJson.optString(KEY_DEG);
                    dir = windJson.optString(KEY_DIR);
                    sc = windJson.optString(KEY_SC);
                    spd =windJson.optString(KEY_SPD);
                }
            }
        }

        private String getCity() {
            return city;
        }

        private String getCnty() {
            return cnty;
        }

        private String getId() {
            return id;
        }

        private String getLat() {
            return lat;
        }

        private String getLon() {
            return lon;
        }

        private String getLoc() {
            return loc;
        }

        private String getUtc() {
            return utc;
        }

        private String getCode() {
            return code;
        }

        private String getTxt() {
            return txt;
        }

        private String getFl() {
            return fl;
        }

        private String getHum() {
            return hum;
        }

        private String getPcpn() {
            return pcpn;
        }

        private String getPres() {
            return pres;
        }

        private String getTmp() {
            return tmp;
        }

        private String getVis() {
            return vis;
        }

        private String getDeg() {
            return deg;
        }

        private String getDir() {
            return dir;
        }

        private String getSc() {
            return sc;
        }

        private String getSpd() {
            return spd;
        }
    }


}
