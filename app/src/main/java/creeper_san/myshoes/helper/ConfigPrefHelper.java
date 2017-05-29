package creeper_san.myshoes.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ConfigPrefHelper {
    private static final String PREF_NAME = "Config ";
    private static final String PREF_INFO_NAME = "Info";

    private static final String CONFIG_IP_ADDRESS = "IpAddress";
    private static final String CONFIG_STEP_TARGET = "StepTarget";
    private static final String CONFIG_USER_HEIGHT = "UserHeight";
    private static final String CONFIG_USER_WEIGHT = "UserWeight";
    private static final String CONFIG_USER_KEEP_TEMPERATURE = "UserTemperature";
    private static final String CONFIG_IS_AUTO_WARM = "AutoWarm";
    private static final String CONFIG_IS_AUTO_LOGIN = "AutoLogin";
    private static final String CONFIG_REMEMBER_ACCOUNT = "RememberAccount";
    private static final String CONFIG_REMEMBER_PASSWORD = "RememberPassword";

    private static final String INFO_STEP_NUMBER = "StepNumber";
    private static final String INFO_STEP_YEAR = "StepYear";
    private static final String INFO_STEP_MONTH = "StepMonth";
    private static final String INFO_STEP_DAY = "StepDay";
    private static final String INFO_TEMP_SWITCH = "TempSwitch";
    private static final String INFO_TEMP = "Temp";
    private static final String INFO_TEMP_OFFSET = "TempOffset";

    private SharedPreferences config;
    private SharedPreferences infoPref;

    public ConfigPrefHelper(Context context) {
        config = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        infoPref = context.getSharedPreferences(PREF_INFO_NAME,Context.MODE_PRIVATE);
    }

    /**
     *      应用程序设置
     */

    public String getServerAddress(){
        return config.getString(CONFIG_IP_ADDRESS,"192.168.1.1");
    }
    public void setServerIpAddress(String ipAddress){
        config.edit().putString(CONFIG_IP_ADDRESS,ipAddress).commit();
    }

    /**
     *      个人目标设置
     */
    public int getStepTarget(){
        return config.getInt(CONFIG_STEP_TARGET,10000);
    }
    public void setStepTarget(int target){
        config.edit().putInt(CONFIG_STEP_TARGET,target).commit();
    }

    /**
     *      个人信息设置
     */
    public int getUserHeight(){
        return config.getInt(CONFIG_USER_HEIGHT,165);
    }
    public void setUserHeight(int height){
        config.edit().putInt(CONFIG_USER_HEIGHT,height).commit();
    }
    public int getUserWeight(){
        return config.getInt(CONFIG_USER_WEIGHT,60);
    }
    public void setUserWeight(int weight){
        config.edit().putInt(CONFIG_USER_WEIGHT,weight).commit();
    }
    public int getUserTemperature(){
        return config.getInt(CONFIG_USER_KEEP_TEMPERATURE,15);
    }
    public void setUserTemperature(int temperature){
        config.edit().putInt(CONFIG_USER_KEEP_TEMPERATURE,temperature).commit();
    }
    public boolean getIsAutoWarm(){
        return config.getBoolean(CONFIG_IS_AUTO_WARM,false);
    }
    public void setAutoWarm(boolean state){
        config.edit().putBoolean(CONFIG_IS_AUTO_WARM,state).commit();
    }
    public boolean getAutoLogin(){
        return config.getBoolean(CONFIG_IS_AUTO_LOGIN,false);
    }
    public void setAutoLogin(boolean autoLogin){
        config.edit().putBoolean(CONFIG_IS_AUTO_LOGIN,autoLogin).commit();
    }
    public String getRememberAccount(){
        return config.getString(CONFIG_REMEMBER_ACCOUNT,"");
    }
    public void setRememberAccount(String account){
        config.edit().putString(CONFIG_REMEMBER_ACCOUNT,account).commit();
    }
    public String getRememberPassword(){
        return config.getString(CONFIG_REMEMBER_PASSWORD,"");
    }
    public void setRememberPassword(String password){
        config.edit().putString(CONFIG_REMEMBER_PASSWORD,password).commit();
    }

    /**
     *      infoPref
     */
    public int getStepNumber(){
        return infoPref.getInt(INFO_STEP_NUMBER,0);
    }
    public int getStepYear(){
        return infoPref.getInt(INFO_STEP_YEAR,-1);
    }
    public int getStepMonth(){
        return infoPref.getInt(INFO_STEP_MONTH,-1);
    }
    public int getStepDay(){
        return infoPref.getInt(INFO_STEP_DAY,-1);
    }
    public int getTemp(){
        return infoPref.getInt(INFO_TEMP,25);
    }
    public int getTempOffset(){
        return infoPref.getInt(INFO_TEMP_OFFSET,1);
    }
    public boolean getTempSwitch(){
        return infoPref.getBoolean(INFO_TEMP_SWITCH,false);
    }
    public void setStepNumber(int value){
        infoPref.edit().putInt(INFO_STEP_NUMBER,value).commit();
    }
    public void setStepYear(int value){
        infoPref.edit().putInt(INFO_STEP_YEAR,value).commit();
    }
    public void setStepMonth(int value){
        infoPref.edit().putInt(INFO_STEP_MONTH,value).commit();
    }
    public void setStepDay(int value){
        infoPref.edit().putInt(INFO_STEP_DAY,value).commit();
    }
    public void setTemp(int value){
        infoPref.edit().putInt(INFO_TEMP,value).commit();
    }
    public void setTempOffset(int value){
        infoPref.edit().putInt(INFO_TEMP_OFFSET,value).commit();
    }
    public void setTempSwitch(boolean value){
        infoPref.edit().putBoolean(INFO_TEMP_SWITCH,value).commit();
    }
}
