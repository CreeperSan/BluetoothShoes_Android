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


public class WeatherJson extends BaseJson {
    private List<WeatherSingleJson> singleItemList;
    private JSONObject jsonObject;

    public WeatherJson(String jsonStr) throws JSONException, JsonDecodeException {
        super(jsonStr);
    }
    public WeatherJson(JSONObject jsonObject) throws JsonDecodeException {
        super(jsonObject);
    }

    @Override
    protected void analyze(JSONObject jsonObject) throws JsonDecodeException {
        this.jsonObject = jsonObject;
        if (singleItemList==null){
            singleItemList = new ArrayList<>();
        }else {
            singleItemList.clear();
        }
        if (jsonObject.has(KEY_HE_WEATHER5)){
            JSONArray heWeather5Json = jsonObject.optJSONArray(KEY_HE_WEATHER5);
            if (heWeather5Json == null){
                throw new JsonDecodeException(TYPE_MISS,"缺少HeWeather5值",CODE_MISSING);
            }
            for (int i=0;i<heWeather5Json.length();i++){
                WeatherSingleJson item = new WeatherSingleJson(heWeather5Json.optJSONObject(i));
                if (item!=null){
                    singleItemList.add(item);
                }
            }
        }else {
            throw new JsonDecodeException(TYPE_MISS,"缺少HeWeather5键",CODE_MISSING);
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public int size(){
        return singleItemList==null?0:singleItemList.size();
    }
    public int dailySize(int which){
        if (singleItemList==null){
            return 0;
        }
        if (which>=singleItemList.size()){
            return 0;
        }
        if (singleItemList.get(which).dailyItemList == null){
            return 0;
        }
        return singleItemList.get(which).dailyItemList.size();
    }
    public int hourlySize(int which){
        if (singleItemList==null){
            return 0;
        }
        if (which>=singleItemList.size()){
            return 0;
        }
        if (singleItemList.get(which).hourlyItemList == null){
            return 0;
        }
        return singleItemList.get(which).hourlyItemList.size();
    }

    public String getAqi(int which){
        return singleItemList.get(which).getAqi();
    }
    public String getCo(int which){
        return singleItemList.get(which).getCo();
    }
    public String getNo2(int which){
        return singleItemList.get(which).getNo2();
    }
    public String getO3(int which){
        return singleItemList.get(which).getO3();
    }
    public String getPm10(int which){
        return singleItemList.get(which).getPm10();
    }
    public String getPm25(int which){
        return singleItemList.get(which).getPm25();
    }
    public String getQlty(int which){
        return singleItemList.get(which).getQlty();
    }
    public String getSo2(int which){
        return singleItemList.get(which).getSo2();
    }
    public String getCity(int which){
        return singleItemList.get(which).getCity();
    }
    public String getCountry(int which){
        return singleItemList.get(which).getCnty();
    }
    public String getId(int which){
        return singleItemList.get(which).getId();
    }
    public String getLat(int which){
        return singleItemList.get(which).getLat();
    }
    public String getLon(int which){
        return singleItemList.get(which).getLon();
    }
    public String getLoc(int which){
        return singleItemList.get(which).getLoc();
    }
    public String getUtc(int which){
        return singleItemList.get(which).getUtc();
    }
    public String getCode(int which){
        return singleItemList.get(which).getCode();
    }
    public String getTxt(int which){
        return singleItemList.get(which).getTxt();
    }
    public String getFl(int which){
        return singleItemList.get(which).getFl();
    }
    public String getHum(int which){
        return singleItemList.get(which).getHum();
    }
    public String getPcpn(int which){
        return singleItemList.get(which).getPcpn();
    }
    public String getPres(int which){
        return singleItemList.get(which).getPres();
    }
    public String getTmp(int which){
        return singleItemList.get(which).getTmp();
    }
    public String getVis(int which){
        return singleItemList.get(which).getVis();
    }
    public String getDeg(int which){
        return singleItemList.get(which).getDeg();
    }
    public String getDir(int which){
        return singleItemList.get(which).getDir();
    }
    public String getSc(int which){
        return singleItemList.get(which).getSc();
    }
    public String getSpd(int which){
        return singleItemList.get(which).getSpd();
    }
    public String getBrfAir(int which){
        return singleItemList.get(which).getBrfAir();
    }
    public String getTxtAir(int which){
        return singleItemList.get(which).getTxtAir();
    }
    public String getBrfComf(int which){
        return singleItemList.get(which).getBrfComf();
    }
    public String getTxtComf(int which){
        return singleItemList.get(which).getTxtComf();
    }
    public String getBrfCw(int which){
        return singleItemList.get(which).getBrfCw();
    }
    public String getTxtCw(int which){
        return singleItemList.get(which).getTxtCw();
    }
    public String getBrfDrsg(int which){
        return singleItemList.get(which).getBrfDrsg();
    }
    public String getTxtDrsg(int which){
        return singleItemList.get(which).getTxtDrsg();
    }
    public String getBrfFlu(int which){
        return singleItemList.get(which).getBrfFlu();
    }
    public String getTxtFlu(int which){
        return singleItemList.get(which).getTxtFlu();
    }
    public String getBrfSport(int which){
        return singleItemList.get(which).getBrfSport();
    }
    public String getTxtSport(int which){
        return singleItemList.get(which).getTxtSport();
    }
    public String getBrfTrav(int which){
        return singleItemList.get(which).getBrfTrav();
    }
    public String getTxtTrav(int which){
        return singleItemList.get(which).getTxtTrav();
    }
    public String getBrfUv(int which){
        return singleItemList.get(which).getBrfUv();
    }
    public String getTxtUv(int which){
        return singleItemList.get(which).getTxtUv();
    }

    public String getHourlyCode(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getCode();
    }
    public String getHourlyTxt(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getTxt();
    }
    public String getHourlyDate(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getDate();
    }
    public String getHourlyHum(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getHum();
    }
    public String getHourlyPop(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getPop();
    }
    public String getHourlyPres(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getPres();
    }
    public String getHourlyTmp(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getTmp();
    }
    public String getHourlyDeg(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getDeg();
    }
    public String getHourlyDir(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getDir();
    }
    public String getHourlySc(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getSc();
    }
    public String getHourlySpd(int which,int when){
        return singleItemList.get(which).hourlyItemList.get(when).getSpd();
    }

    public String getDailyMr(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getMr();
    }
    public String getDailyMs(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getMs();
    }
    public String getDailySr(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getSr();
    }
    public String getDailySs(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getSs();
    }
    public String getDailyCodeD(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getCodeD();
    }
    public String getDailyCodeN(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getCodeN();
    }
    public String getDailyTxtD(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getTxtD();
    }
    public String getDailyTxtN(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getTxtN();
    }
    public String getDailyDate(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getDate();
    }
    public String getDailyHum(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getHum();
    }
    public String getDailyPcpn(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getPcpn();
    }
    public String getDailyPop(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getPop();
    }
    public String getDailyPres(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getPres();
    }
    public String getDailyTmpMax(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getTmpMax();
    }
    public String getDailyTmpMin(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getTmpMin();
    }
    public String getDailyUv(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getUv();
    }
    public String getDailyVis(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getVis();
    }
    public String getDailyDeg(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getDeg();
    }
    public String getDailyDir(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getDir();
    }
    public String getDailySc(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getSc();
    }
    public String getDailySpd(int which,int when){
        return singleItemList.get(which).dailyItemList.get(when).getSpd();
    }




    private class WeatherSingleJson extends BaseJson {
        private List<DailyJson> dailyItemList;
        private List<HourlyJson> hourlyItemList;
        private String aqi;
        private String co;
        private String no2;
        private String o3;
        private String pm10;
        private String pm25;
        private String qlty;
        private String so2;
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
        private String brfAir;
        private String txtAir;
        private String brfComf;
        private String txtComf;
        private String brfCw;
        private String txtCw;
        private String brfDrsg;
        private String txtDrsg;
        private String brfFlu;
        private String txtFlu;
        private String brfSport;
        private String txtSport;
        private String brfTrav;
        private String txtTrav;
        private String brfUv;
        private String txtUv;

        private WeatherSingleJson(String jsonStr) throws JSONException, JsonDecodeException {
            super(jsonStr);
        }
        private WeatherSingleJson(JSONObject jsonObject) throws JsonDecodeException {
            super(jsonObject);
        }

        @Override
        protected void analyze(JSONObject jsonObject) throws JsonDecodeException {
            String status = jsonObject.optString(KEY_STATUS,CODE_MISSING);
            if (!status.equals(CODE_OK)){
                throw new JsonDecodeException(TYPE_DECODE_ERR,"status值不正确",status);
            }
            //解析basic
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
            //解析Now
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
                    spd = windJson.optString(KEY_SPD);
                }
            }
            //解析aqi
            JSONObject aqiJson = jsonObject.optJSONObject(KEY_AQI);
            if (aqiJson!=null){
                JSONObject aqiCityJson = aqiJson.optJSONObject(KEY_CITY);
                if (aqiCityJson!=null){
                    aqi = aqiCityJson.optString(KEY_AQI);
                    co = aqiCityJson.optString(KEY_CO);
                    no2 = aqiCityJson.optString(KEY_NO2);
                    o3 = aqiCityJson.optString(KEY_O3);
                    pm10 = aqiCityJson.optString(KEY_PM10);
                    pm25 = aqiCityJson.optString(KEY_PM25);
                    qlty = aqiCityJson.optString(KEY_QLTY);
                    so2 = aqiCityJson.optString(KEY_SO2);
                }
            }
            //解析Suggestion
//            log("解析到Suggestion");
            JSONObject suggestionJson = jsonObject.optJSONObject(KEY_SUGGESTION);
            if (suggestionJson!=null){
//                log("Suggestion不为空");
                JSONObject airJson = suggestionJson.optJSONObject(KEY_AIR);
                if (airJson!=null){
//                    log("KEY_AIR不为空");
                    brfAir = airJson.optString(KEY_BRF);
                    txtAir = airJson.optString(KEY_TXT);
                }
                JSONObject comfJson = suggestionJson.optJSONObject(KEY_COMF);
                if (comfJson!=null){
//                    log("KEY_COMF 不为空");
                    brfComf = comfJson.optString(KEY_BRF);
                    txtComf = comfJson.optString(KEY_TXT);
                }
                JSONObject cwJson = suggestionJson.optJSONObject(KEY_CW);
                if (cwJson!=null){
//                    log("KEY_CW 不为空");
                    brfCw = cwJson.optString(KEY_BRF);
                    txtCw = cwJson.optString(KEY_TXT);
                }
                JSONObject drsgJson = suggestionJson.optJSONObject(KEY_DRSG);
                if (drsgJson!=null){
//                    log("KEY_DRSG 不为空");
                    brfDrsg = drsgJson.optString(KEY_BRF);
                    txtDrsg = drsgJson.optString(KEY_TXT);
                }
                JSONObject fluJson = suggestionJson.optJSONObject(KEY_FLU);
                if (fluJson!=null){
//                    log("KEY_FLU 不为空");
                    brfFlu = fluJson.optString(KEY_BRF);
                    txtFlu = fluJson.optString(KEY_TXT);
                }
                JSONObject sportJson = suggestionJson.optJSONObject(KEY_SPORT);
                if (sportJson!=null){
//                    log("KEY_SPORT 不为空");
                    brfSport = sportJson.optString(KEY_BRF);
                    txtSport = sportJson.optString(KEY_TXT);
                }
                JSONObject travJson = suggestionJson.optJSONObject(KEY_TRAV);
                if (travJson!=null){
//                    log("KEY_TRAV 不为空");
                    brfTrav = travJson.optString(KEY_BRF);
                    txtTrav = travJson.optString(KEY_TXT);
                }
                JSONObject uvJson = suggestionJson.optJSONObject(KEY_UV);
                if (uvJson!=null){
//                    log("KEY_UV 不为空");
                    brfUv = uvJson.optString(KEY_BRF);
                    txtUv = uvJson.optString(KEY_TXT);
                }
            }
            //解析hourly
            if (hourlyItemList==null){
                hourlyItemList = new ArrayList<>();
            }else {
                hourlyItemList.clear();
            }
            JSONArray hourlyJsonArray = jsonObject.optJSONArray(KEY_HOURLY_FORECAST);
            if (hourlyJsonArray!=null){
                for (int i=0;i<hourlyJsonArray.length();i++){
                    HourlyJson item = new HourlyJson(hourlyJsonArray.optJSONObject(i));
                    if (item!=null){
                        hourlyItemList.add(item);
                    }
                }
            }
            //解析daily
            if (dailyItemList==null){
                dailyItemList = new ArrayList<>();
            }else {
                dailyItemList.clear();
            }
            JSONArray dailyJsonArray = jsonObject.optJSONArray(KEY_DAILY_FORECAST);
            if (dailyJsonArray!=null){
                for (int i=0;i<dailyJsonArray.length();i++){
                    DailyJson item = new DailyJson(dailyJsonArray.optJSONObject(i));
                    if (item!=null){
                        dailyItemList.add(item);
                    }
                }
            }
        }



        private String getAqi() {
            return aqi;
        }

        private String getCo() {
            return co;
        }

        private String getNo2() {
            return no2;
        }

        private String getO3() {
            return o3;
        }

        private String getPm10() {
            return pm10;
        }

        private String getPm25() {
            return pm25;
        }

        private String getQlty() {
            return qlty;
        }

        private String getSo2() {
            return so2;
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

        private String getBrfAir() {
            return brfAir;
        }

        private String getTxtAir() {
            return txtAir;
        }

        private String getBrfComf() {
            return brfComf;
        }

        private String getTxtComf() {
            return txtComf;
        }

        private String getBrfCw() {
            return brfCw;
        }

        private String getTxtCw() {
            return txtCw;
        }

        private String getBrfDrsg() {
            return brfDrsg;
        }

        private String getTxtDrsg() {
            return txtDrsg;
        }

        private String getBrfFlu() {
            return brfFlu;
        }

        private String getTxtFlu() {
            return txtFlu;
        }

        private String getBrfSport() {
            return brfSport;
        }

        private String getTxtSport() {
            return txtSport;
        }

        private String getBrfTrav() {
            return brfTrav;
        }

        private String getTxtTrav() {
            return txtTrav;
        }

        private String getBrfUv() {
            return brfUv;
        }

        private String getTxtUv() {
            return txtUv;
        }
    }

    private class DailyJson extends BaseJson {
        private String mr;
        private String ms;
        private String sr;
        private String ss;
        private String codeD;
        private String codeN;
        private String txtD;
        private String txtN;
        private String date;
        private String hum;
        private String pcpn;
        private String pop;
        private String pres;
        private String tmpMax;
        private String tmpMin;
        private String uv;
        private String vis;
        private String deg;
        private String dir;
        private String sc;
        private String spd;

        private DailyJson(String jsonStr) throws JSONException, JsonDecodeException {
            super(jsonStr);
        }
        private DailyJson(JSONObject jsonObject) throws JsonDecodeException {
            super(jsonObject);
        }

        @Override
        protected void analyze(JSONObject jsonObject) throws JsonDecodeException {
            JSONObject astroJson = jsonObject.optJSONObject(KEY_ASTRO);
            if (astroJson!=null){
                mr = astroJson.optString(KEY_MR);
                ms = astroJson.optString(KEY_MS);
                sr = astroJson.optString(KEY_SR);
                ss = astroJson.optString(KEY_SS);
            }
            JSONObject condJson = jsonObject.optJSONObject(KEY_COND);
            if (condJson!=null){
                codeD = condJson.optString(KEY_CODE_D);
                codeN = condJson.optString(KEY_CODE_N);
                txtD = condJson.optString(KEY_TXT_D);
                txtN = condJson.optString(KEY_TXT_D);
            }
            date = jsonObject.optString(KEY_DATE);
            hum = jsonObject.optString(KEY_HUM);
            pcpn = jsonObject.optString(KEY_PCPN);
            pop = jsonObject.optString(KEY_POP);
            pres = jsonObject.optString(KEY_PRES);
            JSONObject tmpJson = jsonObject.optJSONObject(KEY_TMP);
            if (tmpJson!=null){
                tmpMax = tmpJson.optString(KEY_MAX);
                tmpMin = tmpJson.optString(KEY_MIN);
            }
            uv = jsonObject.optString(KEY_UV);
            vis = jsonObject.optString(KEY_VIS);
            JSONObject windJson = jsonObject.optJSONObject(KEY_WIND);
            if(windJson!=null){
                deg = windJson.optString(KEY_DEG);
                dir = windJson.optString(KEY_DIR);
                sr = windJson.optString(KEY_SR);
                spd = windJson.optString(KEY_SPD);
            }
        }

        private String getMr() {
            return mr;
        }

        private String getMs() {
            return ms;
        }

        private String getSr() {
            return sr;
        }

        private String getSs() {
            return ss;
        }

        private String getCodeD() {
            return codeD;
        }

        private String getCodeN() {
            return codeN;
        }

        private String getTxtD() {
            return txtD;
        }

        private String getTxtN() {
            return txtN;
        }

        private String getDate() {
            return date;
        }

        private String getHum() {
            return hum;
        }

        private String getPcpn() {
            return pcpn;
        }

        private String getPop() {
            return pop;
        }

        private String getPres() {
            return pres;
        }

        private String getTmpMax() {
            return tmpMax;
        }

        private String getTmpMin() {
            return tmpMin;
        }

        private String getUv() {
            return uv;
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

    private class HourlyJson extends BaseJson {
        private String code;
        private String txt;
        private String date;
        private String hum;
        private String pop;
        private String pres;
        private String tmp;
        private String deg;
        private String dir;
        private String sc;
        private String spd;

        private HourlyJson(String jsonStr) throws JSONException, JsonDecodeException {
            super(jsonStr);
        }
        private HourlyJson(JSONObject jsonObject) throws JsonDecodeException {
            super(jsonObject);
        }

        @Override
        protected void analyze(JSONObject jsonObject) throws JsonDecodeException {
            JSONObject condJson = jsonObject.optJSONObject(KEY_COND);
            if (condJson!=null){
                code = condJson.optString(KEY_CODE);
                txt = condJson.optString(KEY_TXT);
            }
            date = jsonObject.optString(KEY_DATE);
            hum = jsonObject.optString(KEY_HUM);
            pop = jsonObject.optString(KEY_POP);
            pres = jsonObject.optString(KEY_PRES);
            tmp = jsonObject.optString(KEY_TMP);
            JSONObject windJson = jsonObject.optJSONObject(KEY_WIND);
            if (windJson!=null){
                deg = windJson.optString(KEY_DEG);
                dir = windJson.optString(KEY_DIR);
                sc = windJson.optString(KEY_SC);
                spd = windJson.optString(KEY_SPD);
            }
        }

        private String getCode() {
            return code;
        }

        private String getTxt() {
            return txt;
        }

        private String getDate() {
            return date;
        }

        private String getHum() {
            return hum;
        }

        private String getPop() {
            return pop;
        }

        private String getPres() {
            return pres;
        }

        private String getTmp() {
            return tmp;
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
