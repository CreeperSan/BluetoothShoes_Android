package creeper_san.myshoes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import creeper_san.myshoes.bean.ChatBean;
import creeper_san.myshoes.bean.ChatUserBean;
import creeper_san.myshoes.event.BuzzerEvent;
import creeper_san.myshoes.event.BuzzerResultEvent;
import creeper_san.myshoes.event.HumidityEvent;
import creeper_san.myshoes.event.LedEvent;
import creeper_san.myshoes.event.LedResultEvent;
import creeper_san.myshoes.event.MissingEvent;
import creeper_san.myshoes.event.MissingResultEvent;
import creeper_san.myshoes.event.NetworkEvent;
import creeper_san.myshoes.event.ShoesEvent;
import creeper_san.myshoes.event.TempConfigEvent;
import creeper_san.myshoes.event.TempConfigResultEvent;
import creeper_san.myshoes.event.TemperatureEvent;
import creeper_san.myshoes.event.VibrateEvent;
import creeper_san.myshoes.event.VibrateResultEvent;
import creeper_san.myshoes.event.WarmEvent;
import creeper_san.myshoes.event.WarmResultEvent;
import creeper_san.myshoes.event.WeatherEvent;
import creeper_san.myshoes.event.WeatherResultEvent;
import creeper_san.myshoes.event.WeightDataEvent;
import creeper_san.myshoes.event.WeightDataResultEvent;
import creeper_san.myshoes.event.WeightEvent;
import creeper_san.myshoes.flag.BroadcastKey;
import creeper_san.myshoes.flag.ServerKey;
import creeper_san.myshoes.helper.ChatDatabaseHelper;
import creeper_san.myshoes.helper.ConfigPrefHelper;
import creeper_san.myshoes.helper.ConnectionHelper;
import creeper_san.myshoes.helper.NetworkHelper;
import creeper_san.myshoes.helper.UrlHelper;
import creeper_san.myshoes.helper.UserDatabaseHelper;
import creeper_san.myshoes.helper.WeatherHelper;

public class ShoesService extends Service implements ConnectionHelper.MessageListener,ConnectionHelper.OnConnectionChangeListener{
    public String TAG = getClass().getSimpleName();
    private final int NOTIFICATION_ID = 1;
    private final int NOTIFICATION_MESSAGE_ID = 2;

    private Context context = this;
    private ConnectionHelper connectionHelper;
    private ConfigPrefHelper configHelper;
    private RequestQueue requestQueue;
    private UserDatabaseHelper userDatabaseHelper;
    private ChatDatabaseHelper chatDatabaseHelper;
    private GetMessageThread getMessageThread;
    private MessageHandler messageHandler;
    private CallReceiver callRec;

    private StringBuffer recBuffer = new StringBuffer("");

    private int vibrateLevel = 0;
    private int mStep = 0;
    private int mTemperature = 0;
    private int mHumidity = 0;
    private float mWeight = 0f;
    private boolean isNeedFresh = false;//是否需要刷新数据
//    private boolean isAutoWarm = false; //是否自动保温
//    private int userTemperature = 25;   //自动调节温度下限
    private boolean isLogin = false;    //是否已登录
    private String userName = "";   //用户名
    private String nickName = "";   //昵称
    private boolean isStepOverWeight = false;

    private boolean isChatting = false;
    private String chattingUserName;
    private String getNewMessageUserName;
    private String getNewMessageContent;
    private boolean isMessageActivityNeedFresh = false;














    //新的
    private boolean warmStatus = false;//是否需要保温
    private boolean isWarming = false;//是否正在加热
    private int warmValue = 25;//温度阈值
    private boolean isMissing = false;
    private int ledStatus = LedEvent.Companion.getSTATUS_OFF();
    private boolean buzzerStatus = false;
    private GetShoesInfoThread shoesInfoThread;
    private boolean isBluetoothConnect = false;
    private boolean isWeightOverValue = false;

    /**
     *      收到消息
     */
    @Override
    public void onMessageReceive(String msg) {
        recBuffer.append(msg);
        String tempStr = recBuffer.toString();
        if (tempStr.contains("<") && tempStr.contains(">")){
            int start = tempStr.lastIndexOf("<")+1;
            int end = tempStr.lastIndexOf(">");
            if (end<recBuffer.length() && end>start){
                String receive = tempStr.substring(start,end);
                recBuffer = new StringBuffer(recBuffer.substring(end+1));
                String humidityStr = receive.substring(0,3);
                String temperatureStr = receive.substring(4,7);
                String weightStr = receive.substring(8,13);
                handleTemperatureData(temperatureStr,humidityStr);
                handleWeightData(weightStr);
//                log("收到的消息为 "+receive+" 湿度是"+humidityStr+" 温度是"+mTemperature+"  体重是"+weightStr);

                //处理体重60800 -  65520
                mWeight = handleWeight(weightStr);
                //发送消息
                postEvent(new HumidityEvent(mHumidity));
                postEvent(new TemperatureEvent(mTemperature));
                postEvent(new WeightEvent(mWeight));
            }
        }
    }
    private int handleWeight(String str){
        //处理体重60800 -  65520
        float weightTemp = Float.valueOf(str);
        float weight60 = 65520 - 60800;
        weightTemp -= 60800;
        if (weightTemp<0){
            weightTemp = 0;
        }
        float unit = 60/weight60;
        int weight = (int) (unit*weightTemp);
        if (weight>30){
            isWeightOverValue = true;
        }else {
            if (isWeightOverValue){
                isWeightOverValue = false;
                int[] tempDate = getThisDate();
                if (tempDate[0] == configHelper.getStepYear() &&
                        tempDate[1] == configHelper.getStepMonth() &&
                        tempDate[2] == configHelper.getStepDay()){
                    mStep++;
                }else {
                    //把昨天数据存储到数据库
                    userDatabaseHelper.insertDataSteps(tempDate[0],tempDate[1],tempDate[2],configHelper.getStepNumber());
                    //设置今天数据
                    configHelper.setStepYear(tempDate[0]);
                    configHelper.setStepMonth(tempDate[1]);
                    configHelper.setStepDay(tempDate[2]);
                    mStep = 1;
                }
                configHelper.setStepNumber(mStep);
                notifyStepReceiver(mStep);
            }
        }
        return weight;
    }
    /**
     *      EventBus事件类
     */
    public <T> void postEvent(T event){
        EventBus.getDefault().post(event);
    }
    @Subscribe()
    public void onVibrateEvent(final VibrateEvent event){
        Runnable onFailRunnable = new Runnable() {
            @Override
            public void run() {
                postEvent(new VibrateResultEvent(false,event.getOrigin()));
            }
        };
        if (isConnected()){
            switch (event.getLevel()){
                case 0:connectionHelper.send("f",onFailRunnable);break;
                case 1:connectionHelper.send("a",onFailRunnable);break;
                case 2:connectionHelper.send("b",onFailRunnable);break;
                case 3:connectionHelper.send("c",onFailRunnable);break;
                case 4:connectionHelper.send("d",onFailRunnable);break;
//                case 5:connectionHelper.send("e",onFailRunnable);break;
            }
            postEvent(new VibrateResultEvent(true));
        }else {
            postEvent(new VibrateResultEvent(false,event.getOrigin()));
        }
    }
    @Subscribe()
    public void onLedEvent(LedEvent event){
        Runnable runnable = new Runnable() {
            private  boolean tempIsMissing = isMissing;
            private int tempLedStatus = ledStatus;
            @Override
            public void run() {
                isMissing = tempIsMissing;
                ledStatus = tempLedStatus;
                postEvent(new LedResultEvent(false,ledStatus));
            }
        };//临时保存状态并且运行就恢复状态
        if (isConnected()){
            int recStatus = event.getStatus();
            ledStatus = recStatus;
            if (recStatus == LedEvent.Companion.getSTATUS_ON()){//加入是打开
                connectionHelper.send("1",runnable);
            }else if (recStatus == LedEvent.Companion.getSTATUS_TWINKLE()){//假如是闪烁
                connectionHelper.send("3",runnable);
            }else{//否则是关闭
                connectionHelper.send("2",runnable);
            }
        }else {
            postEvent(new LedResultEvent(false,ledStatus));
        }
    }
    @Subscribe()
    public void onBuzzerEvent(BuzzerEvent event){
        //临时保存
        Runnable runnable = new Runnable() {
            private boolean tempBuzzerStatus = buzzerStatus;
            @Override
            public void run() {
                buzzerStatus = tempBuzzerStatus;
                postEvent(new BuzzerResultEvent(false,buzzerStatus));
            }
        };
        //发送
        if (isConnected()){
            buzzerStatus = event.getStatus();
            if (event.getStatus()){//打开蜂鸣器
                connectionHelper.send("7",runnable);
            }else {//关闭蜂鸣器
                connectionHelper.send("8",runnable);
            }
        }else {
            postEvent(new BuzzerResultEvent(false,buzzerStatus));
        }
    }
    @Subscribe()
    public void onWarmEvent(WarmEvent event){
        configHelper.setAutoWarm(event.getStatus());
        if (event.getTemperature()>0){
            configHelper.setTemp(event.getTemperature());
        }
    }
    @Subscribe()
    public void onMissingEvent(MissingEvent event){
        if (isConnected()){
            if (event.isCommand()){
                isMissing = event.newStatus();
                if (isMissing){
                    postEvent(new LedEvent(LedEvent.Companion.getSTATUS_TWINKLE()));
                    postEvent(new BuzzerEvent(true));
                }else {
                    postEvent(new LedEvent(LedEvent.Companion.getSTATUS_OFF()));
                    postEvent(new BuzzerEvent(false));
                }
            }
            postEvent(new MissingResultEvent(isMissing,true));
        }else {
            postEvent(new MissingResultEvent(isMissing,false));
        }
    }
    @Subscribe()
    public void onShoesEvent(ShoesEvent event){
        if (isConnected()){
            connectionHelper.send("0");
        }
    }
    @Subscribe(sticky = true)
    public void onNetworkEvent(NetworkEvent event){
        loge("收到网络请求");
        NetworkHelper.INSTANCE.request(requestQueue,event.getUrl(),event.getHandler());
        //删除粘滞事件
        EventBus.getDefault().removeStickyEvent(NetworkEvent.class);
    }
    @Subscribe()
    public void onWeatherEvent(WeatherEvent event){
        if (event.isRequest()){
//            log("收到请求");
            long currentTimeStamp = System.currentTimeMillis();
            if ((currentTimeStamp - WeatherHelper.getTimeStamp())<(1000*60*60)){//如果距离上一次请求还不到1小时，则无视
                return;
            }
            NetworkHelper.INSTANCE.request(requestQueue, UrlHelper.generateNowUrl("shenzhen"), new NetworkHelper.NetworkHandler() {
                @Override
                public void onResponse(boolean isSuccess, @NotNull String response) {
                    if (isSuccess){
                        postEvent(new WeatherResultEvent(false));
                    }else {
                        WeatherHelper.setNowJson(response);
                        postEvent(new WeatherResultEvent(true));
                    }
                }
            });
        }else {
            if (WeatherHelper.getTimeStamp()!=0 && WeatherHelper.getJson()!=null){
                postEvent(new WeatherResultEvent(true));
            }
        }
    }
    @Subscribe(sticky = true)
    public void onTempConfigEvent(TempConfigEvent event){
        postEvent(new TempConfigResultEvent(configHelper.getIsAutoWarm(),configHelper.getTemp()));
        //删除粘滞事件
        EventBus.getDefault().removeStickyEvent(TempConfigEvent.class);
    }
    @Subscribe(sticky = true)
    public void onWeightDataEvent(WeightDataEvent event){
        postEvent(new WeightDataResultEvent(userDatabaseHelper.getWeightList(20)));
        EventBus.getDefault().removeStickyEvent(event.getClass());
    }

    /**
     *      状态获取
     */
    public boolean isMissing() {
        return isMissing;
    }
    public int getLedStatus() {
        return ledStatus;
    }
    public boolean getBuzzerStatus() {
        return buzzerStatus;
    }
    public boolean getWarmStatus(){
        return warmStatus;
    }


    /**
     *      蓝牙连接状态监听
     */
    @Override
    public void onConnect() {
        isBluetoothConnect = true;
        shoesInfoThread = new GetShoesInfoThread();
        shoesInfoThread.start();
    }
    @Override
    public void onDisconnect() {
        isBluetoothConnect = false;
        if(shoesInfoThread!=null){
            shoesInfoThread.interrupt();
        }
        shoesInfoThread = null;
    }

    /**
     *      内容处理
     */
    private void handleWeightData(String dataStr){
        try {
            mWeight = Float.parseFloat(dataStr);
            //以下为步数处理
            if (mWeight > 70){
                isStepOverWeight = true;
            }else if (mWeight < 68){
                if (isStepOverWeight){
                    isStepOverWeight = false;

                    int[] tempDate = getThisDate();
                    if (tempDate[0] == configHelper.getStepYear() &&
                            tempDate[1] == configHelper.getStepMonth() &&
                            tempDate[2] == configHelper.getStepDay()){
                        mStep++;
                    }else {
                        //把昨天数据存储到数据库
                        userDatabaseHelper.insertDataSteps(tempDate[0],tempDate[1],tempDate[2],configHelper.getStepNumber());
                        //设置今天数据
                        configHelper.setStepYear(tempDate[0]);
                        configHelper.setStepMonth(tempDate[1]);
                        configHelper.setStepDay(tempDate[2]);
                        mStep = 1;
                    }
                    configHelper.setStepNumber(mStep);
                    notifyStepReceiver(mStep);
                }
            }

            //以下为重量处理
            if (mWeight<66){
                mWeight = 0;
            }else{
                mWeight = (mWeight-66)*(55/9);
            }
//            notifyWeightReceiver((int) mWeight);
        } catch (NumberFormatException e) {
            log("重量数据解析错误 "+dataStr);
        }
    }
    private void handleTemperatureData(String dataStr,String humidityStr){
        try {
            int temperature = Integer.parseInt(dataStr);
            //offset 温度数据
            if (configHelper.getTempSwitch()){
                Random random = new Random();
                temperature = (int) (configHelper.getTemp()+
                                        ((random.nextBoolean())?(configHelper.getTempOffset()*(random.nextInt(8)-4))*0.125f:0));
            }
            mTemperature = temperature;
            checkIsNeedWarming();
            //湿度
            mHumidity = Integer.valueOf(humidityStr);
        } catch (NumberFormatException e) {
            log("温度数据格式错误 "+dataStr);
        }
    }




















    @Override
    public void onCreate() {
        super.onCreate();
        init();
        startAsForeGroundServer();
        initAutoLogin();//处理自动登录
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userDatabaseHelper.close();
        EventBus.getDefault().unregister(this);
    }



    private void initAutoLogin() {

    }// 自动登录 未处理
    private void init() {
        connectionHelper = new ConnectionHelper(this,this);
        connectionHelper.setMessageListener(this);
        configHelper = new ConfigPrefHelper(this);
        warmStatus = configHelper.getIsAutoWarm();
        warmValue = configHelper.getUserTemperature();
        userDatabaseHelper = new UserDatabaseHelper(context);
        requestQueue = Volley.newRequestQueue(this);
        getMessageThread = new GetMessageThread();
        messageHandler = new MessageHandler();

        //初始化步数
        int[] tempDate = getThisDate();
        if (configHelper.getStepYear() == tempDate[0] &&
                configHelper.getStepMonth() == tempDate[1] &&
                configHelper.getStepDay() == tempDate[2]){
            mStep = configHelper.getStepNumber();
        }else {
            userDatabaseHelper.insertDataSteps(configHelper.getStepYear(),configHelper.getStepMonth()
                    ,configHelper.getStepDay(),configHelper.getStepNumber());
            configHelper.setStepYear(tempDate[0]);
            configHelper.setStepMonth(tempDate[1]);
            configHelper.setStepDay(tempDate[2]);
            configHelper.setStepNumber(0);
            mStep = 0;
        }

        //初始化电话监听器
        callRec = new CallReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        registerReceiver(callRec,filter);
    }

    /**
     *      开启为前台服务
     */
    private void startAsForeGroundServer() {
//        remoteViews = new RemoteViews(getPackageName(),R.layout.notification_server);
        Notification.Builder builder = new Notification.Builder(context);
        startForeground(NOTIFICATION_ID,builder.build());
    }

    /**
     *      自身操作
     */
    private void updateSelfUserProfile(){
        if (!userName.equals("")){
            String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ipAddress, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        nickName = jsonObject.getString(ServerKey.NICKNAME);
                        notifySelfProfileUpdate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    nickName = userName;
                    notifySelfProfileUpdate();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<>();
                    map.put(ServerKey.TYPE,ServerKey.TYPE_GET_PROFILE);
                    map.put(ServerKey.USERNAME , userName);
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    /**
     *      网络通信管理
     */
    public void getSocial(final OnInterServerResultListener listener){
        String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
        StringRequest request = new StringRequest(Request.Method.POST, ipAddress
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > map = new HashMap<>();
                map.put(ServerKey.TYPE,ServerKey.TYPE_GET_SOCIAL);
                map.put(ServerKey.GET_NUMS , "20");
                map.put(ServerKey.PAGE , "0");
                return map;
            }
        };
        requestQueue.add(request);
    }
    public void login(final String account, final String password, final boolean isRemember, final OnInterServerResultListener listener){
        String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
        StringRequest request = new StringRequest(Request.Method.POST, ipAddress
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("Status").equals("Success")){
                        listener.onResult("Success");
                        setLogin(true);
                        setUserName(account);
                        setRememberAccount(account);
                        setRememberPassword(password);
                        setIsAutoLogin(isRemember);
                        updateSelfUserProfile();
                        chatDatabaseHelper = new ChatDatabaseHelper(ShoesService.this,account);
                        return;
                    }else {
                        setLogin(false);
                        setUserName("");
                        setRememberAccount("");
                        setRememberPassword("");
                        setIsAutoLogin(false);
                        updateSelfUserProfile();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                log("请求成功 \n"+response);
                listener.onFail("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > map = new HashMap<>();
                map.put(ServerKey.TYPE,ServerKey.TYPE_LOGIN);
                map.put(ServerKey.USERNAME , account);
                map.put(ServerKey.PASSWORD , password);
                return map;
            }
        };
        requestQueue.add(request);
    }
    public void register(final String account, final String password, final boolean isRemember, final OnInterServerResultListener listener){
        String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
        StringRequest request = new StringRequest(Request.Method.POST, ipAddress
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("Status").equals("Success")){
                        listener.onResult("Success");
                        setLogin(true);
                        setUserName(account);
                        setRememberAccount(account);
                        setRememberPassword(password);
                        setIsAutoLogin(isRemember);
                        updateSelfUserProfile();
                        chatDatabaseHelper = new ChatDatabaseHelper(ShoesService.this,account);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setLogin(false);
                setUserName("");
                setRememberAccount("");
                setRememberPassword("");
                setIsAutoLogin(false);
                listener.onFail("用户名被注册啦，客官换一个吧");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("服务器连不上哇，请检查网络是否通畅");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > map = new HashMap<>();
                map.put(ServerKey.TYPE,ServerKey.TYPE_REGISTER);
                map.put(ServerKey.USERNAME , account);
                map.put(ServerKey.PASSWORD , password);
                return map;
            }
        };
        requestQueue.add(request);
    }
    public void getUserProfile(final String userName,final OnInterServerResultListener listener){
        String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
        StringRequest request = new StringRequest(Request.Method.POST, ipAddress
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > map = new HashMap<>();
                map.put(ServerKey.TYPE,ServerKey.TYPE_GET_PROFILE);
                map.put(ServerKey.USERNAME , userName);
                return map;
            }
        };
        requestQueue.add(request);
    }
    public void postSocial(final String content, final String pic, final OnInterServerResultListener listener){
        String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
        StringRequest request = new StringRequest(Request.Method.POST, ipAddress
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResult("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > map = new HashMap<>();
                map.put(ServerKey.TYPE,ServerKey.TYPE_SEND_SOCIAL);
                map.put(ServerKey.CONTENT, content);
                map.put(ServerKey.PIC,pic);
                map.put(ServerKey.USERNAME , userName);
                return map;
            }
        };
        requestQueue.add(request);
    }
    public void updateUserProfile(final String nickName, final String bornDate, final int sex, final int location, final String sign, final OnInterServerResultListener listener){
        String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
        StringRequest request = new StringRequest(Request.Method.POST, ipAddress
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setSelfNickName(nickName);
                listener.onResult("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > map = new HashMap<>();
                map.put(ServerKey.TYPE,ServerKey.TYPE_UPDATE_PROFILE);
                map.put(ServerKey.USERNAME , userName);
                map.put(ServerKey.NICKNAME , nickName);
                map.put(ServerKey.BORN_DATE , bornDate);
                map.put(ServerKey.SEX , String.valueOf(sex));
                map.put(ServerKey.LOCATION , String.valueOf(location));
                map.put(ServerKey.SIGN , sign);
                return map;
            }
        };
        requestQueue.add(request);
    }
    public void getUnreadMessage(final String userName, final OnInterServerResultListener listener){
        String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
        StringRequest request = new StringRequest(Request.Method.POST, ipAddress
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("Status").equals("Success")){
                        listener.onResult("Success");
                    }else{
                        listener.onResult("NotExist");
                    }
                } catch (JSONException e) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject tempJsonObj = jsonArray.getJSONObject(i);
                            String content = tempJsonObj.getString("Content");
                            String userName = tempJsonObj.getString("UserName");
                            String timeStamp = tempJsonObj.getString("TimeStamp");
                            String nickName = tempJsonObj.getString("NickName");
                            getNewMessageUserName = userName;
                            getNewMessageContent = content;
                            chatDatabaseHelper.writeNewMessage(userName,nickName,timeStamp,content,false);
                        }
                        listener.onResult("New");
                        isMessageActivityNeedFresh = true;
                    } catch (JSONException e1) {
                        listener.onResult("Fail");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > map = new HashMap<>();
                map.put(ServerKey.TYPE,ServerKey.TYPE_GET_MESSAGE);
                map.put(ServerKey.USERNAME , userName);
                return map;
            }
        };
        requestQueue.add(request);
    }
    public void sendMessage(final String toUserName, final String content, final OnInterServerResultListener listener){
        String ipAddress = "http://"+configHelper.getServerAddress()+":8080";
        StringRequest request = new StringRequest(Request.Method.POST, ipAddress
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResult("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > map = new HashMap<>();
                map.put(ServerKey.TYPE,ServerKey.TYPE_SEND_MESSAGE);
                map.put(ServerKey.CONTENT, content);
                map.put(ServerKey.USER_NAME_TO , toUserName);
                map.put(ServerKey.USER_NAME_FROM , userName);
                return map;
            }
        };
        requestQueue.add(request);
    }

    /**
     *      配置获取类
     */
    public String getServerIpAddress(){
        return configHelper.getServerAddress();
    }
    public void setServerIpAddress(String ipAddress){
        configHelper.setServerIpAddress(ipAddress);
    }

    /**
     *      获取用户信息
     */
    public int getStepTarget(){
        return configHelper.getStepTarget();
    }
    public void setStepTarget(int stepTarget){
        configHelper.setStepTarget(stepTarget);
    }
    public int getUserHeight(){
        return configHelper.getUserHeight();
    }
    public void setUserHeight(int height){
        configHelper.setUserHeight(height);
    }
    public int getUserWeight(){
        return configHelper.getUserWeight();
    }
    public void setUserWeight(int weight){
        configHelper.setUserWeight(weight);
    }
    public int getUserTemperature(){
        return configHelper.getUserTemperature();
    }
//    public void setUserTemperature(int temperature){
//        userTemperature = temperature;
//        configHelper.setUserTemperature(temperature);
//    }
    public boolean isAutoWarm(){
        return configHelper.getIsAutoWarm();
    }
//    public void setAutoWarm(boolean state){
//        isAutoWarm = state;
//        if (!state){
//            stopWarm();
//        }
//        configHelper.setAutoWarm(state);
//    }
    public boolean getIsAutoLogin(){
        return configHelper.getAutoLogin();
    }
    public void setIsAutoLogin(boolean state){
        configHelper.setAutoLogin(true);
    }
    public String getRememberAccount(){
        return configHelper.getRememberAccount();
    }
    public void setRememberAccount(String account){
        configHelper.setRememberAccount(account);
    }
    public String getRememberPassword(){
        return configHelper.getRememberPassword();
    }
    public void setRememberPassword(String password){
        configHelper.setRememberPassword(password);
    }
    public void readMessage(String userName){
        chatDatabaseHelper.readMessage(userName);
    }
    public List<ChatUserBean> getChatUserBeanList(){
        if (chatDatabaseHelper!=null){
            return chatDatabaseHelper.getCharUserBeanList();
        }
        return new ArrayList<>();
    }
    public void makeMessageRecNotification(){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent intent = new Intent(this,MessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        builder.setSmallIcon(R.drawable.ic_textsms_black_24dp)
                .setContentTitle("你收到了消息")
                .setContentText("点击查看详情")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(NOTIFICATION_MESSAGE_ID,notification);
    }

    public void setTempSwitch(boolean value){
        configHelper.setTempSwitch(value);
    }
    public void setTemp(int value){
        configHelper.setTemp(value);
    }
    public void setTempOffset(int value){
        configHelper.setTempOffset(value);
    }

    /**
     *      取得数据库相关内容
     */
    public int getDataStepNums(int year,int month,int day){
        return userDatabaseHelper.getDataSteps(year, month, day);
    }
    public int getDataWeightNums(int year,int month,int day){
        return userDatabaseHelper.getDataWeight(year, month, day);
    }
    public void insertDataStepNum(int year,int month,int day,int steps){
        userDatabaseHelper.insertDataSteps(year, month, day, steps);
    }
    public void insertDataWeightNum(int year,int month,int day,int weight){
        userDatabaseHelper.insertDataWeight(year, month, day, weight);
    }
    public void insertDataStepNum(int steps){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        insertDataStepNum(year,month,day,steps);
    }
    public void insertDataWeightNum(int weight){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        insertDataWeightNum(year,month,day,weight);
    }
    public LineData getDaysStepData(int days){
        List<Entry> entryList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-days+1);
        for (int i = 0;i<days;i++){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            entryList.add(new Entry(i,getDataStepNums(year,month,day)));
        }
        List<ILineDataSet> setList = new ArrayList<>();
        setList.add(new LineDataSet(entryList,"步数"));
        return new LineData(setList);
    }
    public LineData getDaysWeightData(int days){
        List<Entry> entryList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-days+1);
        for (int i = 0;i<days;i++){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            entryList.add(new Entry(i,getDataWeightNums(year,month,day)));
        }
        List<ILineDataSet> setList = new ArrayList<>();
        setList.add(new LineDataSet(entryList,"体重"));
        return new LineData(setList);
    }
    public List<ChatBean> getUserChatList(String userName){
        return chatDatabaseHelper.getChatBeanList(userName);
    }
    public void insertMessageToUserTable(String userName,String timeStamp,String content){
        chatDatabaseHelper.sendMessage(userName,timeStamp,content);
    }



    /**
     *      蓝牙通信管理类
     */
    public void setMassageLevel(int level){
        String sendData;
        switch (level){
            case 1:sendData = "a";break;
            case 2:sendData = "b";break;
            case 3:sendData = "c";break;
            case 4:sendData = "d";break;
            case 5:sendData = "e";break;
            default:sendData = "f";break;
        }
        vibrateLevel = level;
        connectionHelper.send(sendData);
    }
    public void setCallFromContract(){
        connectionHelper.send("6");
    }
    public void setLED(){
        connectionHelper.send("1");
    }
    public void stopLED(){
        connectionHelper.send("2");
    }
    public void setLEDNotification(){
        connectionHelper.send("3");
    }
    public void setWarm(){
        connectionHelper.send("4");
    }
    public void stopWarm(){
        connectionHelper.send("5");
    }
    public void setBeep(){
        connectionHelper.send("7");
    }
    public void stopBeep(){
        connectionHelper.send("8");
    }

    public void connectToDevice(BluetoothDevice device){
        connectionHelper.connect(device);
    }
    public void disconnectDevice(){
        if (connectionHelper.isConnected()){
            connectionHelper.disConnect();
        }
    }


    private void checkIsNeedWarming() {
        if (warmStatus){
            if (mTemperature>warmValue+15){//不需要加热
                if (isWarming){
                    stopWarm();
                    isWarming = false;
                }
            }else {//需要加热
                if (!isWarming){
                    setWarm();
                    isWarming = true;
                }
            }
        }
    }

    /**
     *      广播相关
     */
    private void notifyStepReceiver(int step){
        Intent intent = new Intent(BroadcastKey.ACTION_STEP_DATA);
        intent.putExtra(BroadcastKey.KEY_DATA,step);
        sendBroadcast(intent);
    }
    private void notifyTemperatureReceiver(int temperature){
        Intent intent = new Intent(BroadcastKey.ACTION_TEMPERATURE_DATA);
        intent.putExtra(BroadcastKey.KEY_DATA,temperature);
        sendBroadcast(intent);
    }
    private void notifyWeightReceiver(int weight){
        Intent intent = new Intent(BroadcastKey.ACTION_WEIGHT_DATA);
        intent.putExtra(BroadcastKey.KEY_DATA,weight);
        sendBroadcast(intent);
    }
    private void notifySelfProfileUpdate(){
        Intent intent = new Intent(BroadcastKey.ACTION_SELF_PROFILE_UPDATE);
        sendBroadcast(intent);
    }
    private void notifyNewChatMessage(String content){
        Intent intent= new Intent(BroadcastKey.ACTION_NEW_CHAT_MESSAGE);
        intent.putExtra(BroadcastKey.KEY_CONTENT,content);
        sendBroadcast(intent);
    }

    /**
     *      Getter & Setter
     */
    public boolean isConnected() {
        return connectionHelper.isConnected();
    }
    public boolean isFinding(){
        return connectionHelper.isFinding();
    }
    public void setVibrateLevel(int vibrateLevel) {
        this.vibrateLevel = vibrateLevel;
    }
    public int getVibrateLevel() {
        return vibrateLevel;
    }
    public boolean isNeedFresh() {
        return isNeedFresh;
    }
    public void setNeedFresh(boolean needFresh) {
        isNeedFresh = needFresh;
    }
    public boolean isLogin() {
        return isLogin;
    }
    public void setLogin(boolean login) {
        isLogin = login;
        if (login){
            getMessageThread.start();
        }else {
            getMessageThread.close();
            getMessageThread = new GetMessageThread();
        }
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getNickName() {
        return nickName;
    }
    private void setSelfNickName(String newNickName){
        this.nickName = newNickName;
    }
    public void setChatting(boolean chatting) {
        isChatting = chatting;
    }
    public void setChattingUserName(String chattingUserName) {
        this.chattingUserName = chattingUserName;
    }
    public boolean isMessageActivityNeedFresh() {
        boolean temp = isMessageActivityNeedFresh;
        isMessageActivityNeedFresh = false;
        return temp;
    }
    public int getCurrentStepNumber(){
        return mStep;
    }
    public float getServerSaveWeight(){
        return mWeight;
    }

    /**
     *      Log相关
     */
    protected void log(String content){
        Log.i(TAG,content);
    }
    protected void logv(String content){
        Log.v(TAG,content);
    }
    protected void logd(String content){
        Log.d(TAG,content);
    }
    protected void logw(String content){
        Log.w(TAG,content);
    }
    protected void loge(String content){
        Log.e(TAG,content);
    }

    /**
     *      其他
     */
    private int[] getThisDate(){
        Calendar calender = Calendar.getInstance();
        return new int[]{calender.get(Calendar.YEAR),calender.get(Calendar.MONTH)+1,calender.get(Calendar.DAY_OF_MONTH)};
    }

    /**
     *      一些接口
     */
    interface OnInterServerResultListener{
        public void onResult(String resultStr);
        public void onFail(String reason);
    }

    /**
     *      信息交换
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ShoesServerBinder();
    }

    class ShoesServerBinder extends Binder{
        public ShoesService getService(){
            return  ShoesService.this;
        }
    }
    class GetMessageThread extends Thread{
        @Override
        public void run() {
            super.run();
            while(isLogin()){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getNewMessage();
            }
        }

        public void getNewMessage(){
            getUnreadMessage(getUserName(), new OnInterServerResultListener() {
                @Override
                public void onResult(String resultStr) {
                    if (resultStr.equals("New") || resultStr.equals("Success") || resultStr.equals("Fail")){
                        if (resultStr.equals("New")){
                            if (isChatting){
                                if (chattingUserName.equals(getNewMessageUserName)){
                                    notifyNewChatMessage(getNewMessageContent);
                                    log("条件符合，发送广播");
                                    getNewMessageContent = "";
                                    getNewMessageUserName = "";
                                    return;
                                }
                            }
                            log("条件不符合，"+getNewMessageUserName+"  "+getNewMessageContent+" "+isChatting +" "+chattingUserName);
                            messageHandler.sendEmptyMessage(0);
                        }else if (resultStr.equals("Success")){
                            log(("请求成功，但是没有新消息"));
                        }else {
                            log("请求失败");
                        }
                    }
                }

                @Override
                public void onFail(String reason) {
                    onResult("Fail");
                }
            });
        }

        public void close(){
            interrupt();
        }
    }
    class MessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            makeMessageRecNotification();
        }
    }
    class CallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                    TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
                    if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING){
                        if (isConnected()){
                            setCallFromContract();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class GetShoesInfoThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (isBluetoothConnect){
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    postEvent(new ShoesEvent());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
