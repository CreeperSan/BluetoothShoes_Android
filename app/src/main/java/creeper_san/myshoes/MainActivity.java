package creeper_san.myshoes;

import android.Manifest;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import creeper_san.myshoes.base.BaseActivity;
import creeper_san.myshoes.base.BaseFragment;
import creeper_san.myshoes.bean.SocialBean;
import creeper_san.myshoes.event.NetworkEvent;
import creeper_san.myshoes.event.WeightDataEvent;
import creeper_san.myshoes.flag.BroadcastKey;
import creeper_san.myshoes.flag.IntentKey;
import creeper_san.myshoes.helper.NetworkHelper;
import creeper_san.myshoes.helper.UrlHelper;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener, ServiceConnection {
    @BindView(R.id.mainBottomNavigationView)BottomNavigationBar bottomNavigationBar;
    @BindView(R.id.mainDrawer)NavigationView navigationView;
    @BindView(R.id.mainLayout)DrawerLayout drawerLayout;
    private final static int REQUEST_CODE_PHONE = 1;
    private static String[] BMI_RESULT = {
            "过轻","正常","过重","肥胖","非常肥胖"
    };

    private ConstraintLayout headerLayout;
    private TextView userNameText;
    private TextView userSignText;
    private ImageView userHeadImage;

    private ShoesService shoesService;
    private FragmentManager fragmentManager;
    private MainSocialFragment socialFragment;
    private MainStepFragment stepFragment;
    private MainTemperatureFragment temperatureFragment;
    private MainVibrationFragment vibrationFragment;
    private MainWeightFragment weightFragment;
    private ShoesReceiver receiver;
    private List<BaseFragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();//初始化标题栏
        initDrawerMenu();//初始化滑动菜单
        initBottomNavigationBar();//初始化底栏
        initService();//初始化服务
        initFragments();//初始化fragment
        initBroadcastReceiver();//初始化广播接收器
        initDrawerHeader();//初始化侧滑菜单的头布局
        initPermission();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUserData();//更新头像
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (shoesService!=null){
            shoesService.setNeedFresh(true);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (shoesService!=null){
            shoesService.setNeedFresh(false);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);//取消ShoesServer关联
        unregisterReceiver(receiver);
    }

    /**
     *      各种初始化
     */
    private void initDrawerMenu(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (drawerLayout.isDrawerOpen(Gravity.START)){
                    drawerLayout.closeDrawer(Gravity.START);
                }
                switch (item.getItemId()){
                    case R.id.meNavigationSetting:
                        toNextActivity(SettingActivity.class,false);
                        break;
                    case R.id.meNavigationStep:
                        toNextActivity(LineChartActivity.class,
                                IntentKey.KEY_LINE_CHART_TYPE,IntentKey.VALUE_LINE_CHART_TYPE_STEP,false);
                        break;
                    case R.id.meNavigationWeight:
                        toNextActivity(LineChartActivity.class,
                                IntentKey.KEY_LINE_CHART_TYPE,IntentKey.VALUE_LINE_CHART_TYPE_WEIGHT,false);
                        break;
                    case R.id.meNavigationAbout:
                        toNextActivity(AboutActivity.class,false);
                        break;
                    case R.id.meNavigationMessage:
                        if (shoesService!=null){
                            if (shoesService.isLogin()){
                                toNextActivity(MessageActivity.class,false);
                            }else {
                                toast("你还没登陆，请先登陆哈");
                                toNextActivity(LoginActivity.class,false);
                            }
                        }else {
                            toast("应用程序还没准备好，请等下哈");
                        }
                        break;
                    case R.id.meNavigationBMI:
                        makeBMIDialog();
                        break;
                }
                return true;
            }
        });
    }
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.app_name,R.string.app_name);
            toggle.syncState();
            drawerLayout.addDrawerListener(toggle);
        }
    }
    private void initBottomNavigationBar() {
        //设置风格
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        //添加Tab标签
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_directions_walk_black_24dp,"计步"))
                .addItem(new BottomNavigationItem(R.drawable.ic_temperature_icon,"环境"))
                .addItem(new BottomNavigationItem(R.drawable.ic_vibrate,"按摩"))
                .addItem(new BottomNavigationItem(R.drawable.ic_weight_icon,"称重"))
                .addItem(new BottomNavigationItem(R.drawable.ic_toys_black_24dp,"社区"))
                .initialise();
        bottomNavigationBar.setTabSelectedListener(this);
    }
    private void initService() {
        toStartBindServer(ShoesService.class,this);
    }
    private void initFragments() {
        fragmentManager = getFragmentManager();
        fragmentList = new ArrayList<>();
        //==============================  社交  ===================================================
        socialFragment = new MainSocialFragment();
        socialFragment.setSocialClickListener(new MainSocialFragment.OnSocialClickListener() {
            @Override
            public void onClick(final SocialBean bean) {
                if (shoesService!=null){
                    if (shoesService.isLogin()){
                        dialogList(bean.getNickName(), R.array.socialClickItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    toNextActivity(UserActivity.class,IntentKey.KEY_USER_NAME,bean.getUserName(),false);
                                }else if (which == 1){
                                    log("请求聊天 "+bean.getUserName()+" "+bean.getNickName());
                                    toNextActivity(ChatActivity.class
                                            ,new String[]{IntentKey.KEY_USER_NAME,IntentKey.KEY_USER_NICK_NAME}
                                            ,new String[]{bean.getUserName(),bean.getNickName()}
                                            ,false);
                                }
                            }
                        });
                    }else {
                        toast("还没登陆，请先登录哈");
                        toNextActivity(LoginActivity.class,false);
                    }
                }else {
                    toast("Service还没准备好,请等等哈");
                }
            }
        });
        socialFragment.setSwipeListener(new MainSocialFragment.OnStartRefreshListener() {
            @Override
            public void onRefresh(final SwipeRefreshLayout self) {
                shoesService.getSocial(new ShoesService.OnInterServerResultListener() {
                    @Override
                    public void onResult(String resultStr) {
                        try {
                            JSONArray resultJsonArray = new JSONArray(resultStr);
                            List<SocialBean> socialBeanList = new ArrayList<SocialBean>();
                            for (int i=0;i<resultJsonArray.length();i++){
                                SocialBean socialBean = new SocialBean(new JSONObject(String.valueOf(resultJsonArray.get(i))));
                                socialBeanList.add(socialBean);
                            }
                            socialFragment.setDataAndFresh(socialBeanList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast("返回的数据出错");
                            socialFragment.setRefreshing(false);
                        }finally {
                            self.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        socialFragment.setDataAndFresh(new ArrayList<SocialBean>());
                        socialFragment.setRefreshing(false);
                    }
                });
            }
        });
        socialFragment.setSendSocialListener(new MainSocialFragment.OnSendSocialListener() {
            @Override
            public void onSend() {
                if (shoesService!=null){
                    if (!shoesService.isLogin()){
                        toast("请先登录哈");
                        toNextActivity(LoginActivity.class,false);
                        return;
                    }
                }else {
                    toast("还没连接到Service，请稍等");
                    return;
                }
                dialogSendSocial();
            }
        });
        //=============================  步数  ===================================================
        stepFragment = new MainStepFragment();
        stepFragment.setListener(new MainStepFragment.OnRoundProgressClickListener() {
            @Override
            public void onClick() {
                dialogSimpleInput("请输入目标", "当前目标 " + shoesService.getStepTarget(), 1, InputType.TYPE_CLASS_NUMBER,
                        "确定", new OnDialogInputListener() {
                            @Override
                            public void onClick(TextInputEditText editText, DialogInterface dialog, int which) {
                                String targetStr = editText.getText().toString().trim();
                                try {
                                    int target = Integer.parseInt(targetStr);
                                    shoesService.setStepTarget(target);
                                    setStep(shoesService.getCurrentStepNumber());
                                } catch (NumberFormatException e) {
                                    toast("格式错误");
                                }
                            }
                        },"取消",null);
            }
        });
        //=============================  温度  ===================================================
        temperatureFragment = new MainTemperatureFragment();
        //=============================  振动  ===================================================
        vibrationFragment = new MainVibrationFragment();
        vibrationFragment.setListener(new MainVibrationFragment.OnVibrationChangeListener() {
            @Override
            public void onChange(int oldLevel,int level, boolean isByUser) {
                if (isByUser){
                    if (shoesService!=null){
                        if (shoesService.isConnected()){
                            switch (level){
                                case 1:
                                    shoesService.setMassageLevel(1);
                                    break;
                                case 2:
                                    shoesService.setMassageLevel(2);
                                    break;
                                case 3:
                                    shoesService.setMassageLevel(3);
                                    break;
                                case 4:
                                    shoesService.setMassageLevel(4);
                                    break;
                                case 5:
                                    shoesService.setMassageLevel(5);
                                    break;
                                default:
                                    shoesService.setMassageLevel(0);
                                    break;
                            }
                            if (shoesService!=null){
                                shoesService.setVibrateLevel(level);
                            }
                        }else {
                            toast("还没连接到鞋子呀");
                            vibrationFragment.setLevelWithoutListener(oldLevel);
                            if (shoesService!=null){
                                shoesService.setVibrateLevel(oldLevel);
                            }
                        }
                    }else {
                        toast("还没连接到服务呀");
                        vibrationFragment.setLevelWithoutListener(oldLevel);
                        if (shoesService!=null){
                            shoesService.setVibrateLevel(oldLevel);
                        }
                    }
                }
            }
        });
        //=============================  称重  ===================================================
        weightFragment = new MainWeightFragment();
        weightFragment.setOnRecordListener(new MainWeightFragment.OnRecordListener() {
            @Override
            public void onRecord() {
                final float weightTemp = shoesService.getServerSaveWeight();
                if (shoesService!=null){
                    if (!shoesService.isConnected()){
                        toast("请先连接上鞋子");
                        return;
                    }
                }else {
                    toast("Server未连接，请稍等");
                    return;
                }
                dialogSimple("记录体重", "记录该体重 " + weightTemp + "?", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        log("插入完成后查询的当天体重 "+shoesService.getDataWeightNums(2017,4,4));
                        if (shoesService!=null){
                            if (shoesService.isConnected()){
                                shoesService.insertDataWeightNum((int) weightTemp);
                                postStickyEvent(new WeightDataEvent());
                            }else {
                                toast("请先连接上鞋子哈");
                            }
                        }
                    }
                }, "取消", null);
            }
        });

        fragmentList.add(stepFragment);
        fragmentList.add(temperatureFragment);
        fragmentList.add(vibrationFragment);
        fragmentList.add(weightFragment);
        fragmentList.add(socialFragment);

        fragmentManager.beginTransaction()
                .add(R.id.mainFrameLayoutFragment,fragmentList.get(0))
                .add(R.id.mainFrameLayoutFragment,fragmentList.get(1))
                .add(R.id.mainFrameLayoutFragment,fragmentList.get(2))
                .add(R.id.mainFrameLayoutFragment,fragmentList.get(3))
                .add(R.id.mainFrameLayoutFragment,fragmentList.get(4))
                .hide(fragmentList.get(1))
                .hide(fragmentList.get(2))
                .hide(fragmentList.get(3))
                .hide(fragmentList.get(4))
                .commit();
    }
    private void initBroadcastReceiver() {
        receiver = new ShoesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastKey.ACTION_STEP_DATA);
        intentFilter.addAction(BroadcastKey.ACTION_TEMPERATURE_DATA);
        intentFilter.addAction(BroadcastKey.ACTION_WEIGHT_DATA);
        intentFilter.addAction(BroadcastKey.ACTION_SELF_PROFILE_UPDATE);
        registerReceiver(receiver,intentFilter);
    }
    private void initDrawerHeader() {
        View headerView = navigationView.getHeaderView(0);
        headerLayout = (ConstraintLayout) headerView;
        userNameText = (TextView) headerView.findViewById(R.id.header_me_titleText);
        userSignText = (TextView) headerView.findViewById(R.id.header_me_subText);
        userHeadImage = (ImageView) headerView.findViewById(R.id.header_me_head);
        //接下来是设置点击事件
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shoesService!=null){
                    if (shoesService.isLogin()){
                        toNextActivity(UserActivity.class, IntentKey.KEY_USER_NAME ,shoesService.getUserName(),false);
                    }else {
                        toNextActivity(LoginActivity.class,false);
                    }
                }else {
                    toast("请先连接上Server");
                }
            }
        });
    }
    private void initPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!checkHasPermission(Manifest.permission.READ_PHONE_STATE)){
                dialogSimple("请赋予我们权限", "我们需要读取电话权限以指示鞋子在来电时提醒你", "好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermission(Manifest.permission.READ_PHONE_STATE,REQUEST_CODE_PHONE);
                        }
                    }
                }, "算了吧", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });
            }
        }
    }

    private void makeBMIDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("身体质量指数(BMI)");
        View view = layoutInflater.inflate(R.layout.dialog_bmi,null);
        final TextInputEditText editTextHeight = (TextInputEditText) view.findViewById(R.id.dialogBmiHeight);
        final TextInputEditText editTextWeight = (TextInputEditText) view.findViewById(R.id.dialogBmiWeight);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String heightStr = editTextHeight.getText().toString().trim();
                String weightStr = editTextWeight.getText().toString().trim();
                try {
                    float height = Float.parseFloat(heightStr);
                    float weight = Float.parseFloat(weightStr);
                    float bmi = (float) (weight/Math.pow(height/100,2));
                    int i=0;
                    if (bmi<18.5){
                        i = 0;
                    }else if (bmi < 24){
                        i = 1;
                    }else if (bmi < 28){
                        i = 2;
                    }else if (bmi < 32){
                        i = 3;
                    }else{
                        i = 4;
                    }
                    dialogSimple("身体质量指数(BMI)",
                            "你的身体质量指数为 : "+((float)((int)(bmi*100)))/100+"\n结果为 : "+BMI_RESULT[i]
                            ,"确定",null,"取消",null);
                } catch (NumberFormatException e) {
                    toast("数据格式错误");
                    makeBMIDialog();
                }
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }
    /**
     *      普通设置
     */
    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuMainConnectShoes:
                toNextActivity(ShoesConnectActivity.class,false);
                break;
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(Gravity.START)){
                    drawerLayout.closeDrawer(Gravity.START);
                }else {
                    drawerLayout.openDrawer(Gravity.START);
                }
                break;
            case R.id.menuMainFindShoes:
                toNextActivity(FindShoesActivity.class,false);
                break;
            case R.id.menuMainServerAddress:
                dialogSimpleInput("请输入服务器地址",
                        "端口默认为8080",shoesService.getServerIpAddress(),
                        1, InputType.TYPE_CLASS_TEXT, "确定",
                        new OnDialogInputListener() {
                            @Override
                            public void onClick(TextInputEditText editText, DialogInterface dialog, int which) {
                                String ipAddress = editText.getText().toString().trim();
                                if (shoesService!=null){
                                    if (ipAddress.startsWith("t")) {
                                        String tempValue = ipAddress.substring(1);
                                        if (tempValue.equals("on")){
                                            shoesService.setTempSwitch(true);
                                            return;
                                        }else if (tempValue.equals("off")){
                                            shoesService.setTempSwitch(false);
                                            return;
                                        }else if (tempValue.startsWith("o")){
                                            String tempOffsetValue = tempValue.substring(1);
                                            try {
                                                int value = Integer.parseInt(tempOffsetValue);
                                                shoesService.setTempOffset(value);
                                                return;
                                            } catch (NumberFormatException e) {
                                                toast("数据格式有误");
                                                return;
                                            }
                                        }else {
                                            try {
                                                int value = Integer.parseInt(tempValue);
                                                shoesService.setTemp(value);
                                                return;
                                            } catch (NumberFormatException e) {
                                                toast("数据格式有误");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (!ipAddress.equals("")){
                                    shoesService.setServerIpAddress(ipAddress);
                                }else {
                                    toast("不能为空哦");
                                }
                            }
                        },"取消",null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void dialogSendSocial(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_send_social,null);
        final TextInputEditText editText = (TextInputEditText) dialogView.findViewById(R.id.dialogSendSocialEditText);
        builder.setView(dialogView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String content = editText.getText().toString().trim();
                if (content.equals("")){
                    toast("还没输入内容哇.");
                    return;
                }else if (content.length()>140){
                    toast("不能超过140字哦");
                    return;
                }
                shoesService.postSocial(content, "None", new ShoesService.OnInterServerResultListener() {
                    @Override
                    public void onResult(String resultStr) {
                        toast("发送成功");
                        socialFragment.setRefreshing(true);
                        freshSocialContent();
                    }

                    @Override
                    public void onFail(String reason) {
                        toast("发送失败，请检查你的互联网连接");
                    }
                });
            }
        });
        builder.setNegativeButton("取消",null);
        builder.setView(dialogView);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initPermission();
    }

    /**
     *      Tab切换监听事件
     */
    @Override
    public void onTabSelected(int position) {//标签被选中
        fragmentManager.beginTransaction()
                .show(fragmentList.get(position))
                .commit();
    }//标签选中
    @Override
    public void onTabUnselected(int position) {
        fragmentManager.beginTransaction()
                .hide(fragmentList.get(position))
                .commit();
    }//标签取消选中
    @Override
    public void onTabReselected(int position) {
        if (position==4){// 此处为刷新社交信息
            freshSocialContent();
        }
//        if (shoesService!=null){
//            if (shoesService.isConnected()){
//                if (position == 1){
//                    shoesService.setCallFromContract();
//                }
//            }
//        }
    }//标签重复选中

    /**
     *      与服务进行绑定
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        shoesService = ((ShoesService.ShoesServerBinder)binder).getService();
        //刷新
        setVibrate(shoesService.getVibrateLevel());
        temperatureFragment.setSwitch(shoesService.isAutoWarm() );
//        temperatureFragment.setSeekBarProgress(shoesService.getUserTemperature());
        shoesService.setNeedFresh(true);//提醒服务需要发送广播
        setUserData();//更新头像
        //刷新社区信息
        freshSocialContent();
        //刷新步数
        setStep(shoesService.getCurrentStepNumber());
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     *      更新界面数据显示
     */
    public void setStep(int step){
        stepFragment.setTodaySteps(step);//今天步数
        stepFragment.setTodayProgress( ((float)step) / ((float)shoesService.getStepTarget()));//今天进度
        //一小时150卡里路  步距0.415
        double distance = ((double)shoesService.getUserHeight())*0.415*((double) step)/100000;
        stepFragment.setDistanceHintText(distance);//行走距离
        stepFragment.setEnergyHintText((int)(distance/((double)4)*150));
    }
    public void setTemperature(int temperature){
        temperatureFragment.setTemperature(temperature);
    }
    public void setVibrate(int level){
        vibrationFragment.setLevel(level);
    }
    public void setWeight(int weight){
        weightFragment.setWeight(weight);
    }
    public void setUserData(){
        if (shoesService!=null){
            if (shoesService.isLogin()){
                userSignText.setText("@"+shoesService.getUserName());
                userNameText.setText(shoesService.getNickName());
            }
        }
    }
    public void freshSocialContent(){
        shoesService.getSocial(new ShoesService.OnInterServerResultListener() {
            @Override
            public void onResult(String resultStr) { // 此处解析服务器返回的JSON数据
                log(resultStr);
                try {
                    JSONArray resultJsonArray = new JSONArray(resultStr);
                    List<SocialBean> socialBeanList = new ArrayList<SocialBean>();
                    for (int i=0;i<resultJsonArray.length();i++){
                        SocialBean socialBean = new SocialBean(new JSONObject(String.valueOf(resultJsonArray.get(i))));
                        socialBeanList.add(socialBean);
                    }
                    socialFragment.setDataAndFresh(socialBeanList);
                    socialFragment.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    toast("返回的数据出错");
                    socialFragment.setRefreshing(false);
                }
            }

            @Override
            public void onFail(String reason) {
                socialFragment.setDataAndFresh(new ArrayList<SocialBean>());
                socialFragment.setRefreshing(false);
            }
        });
    }

    /**
     *      内部类
     */
    class ShoesReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastKey.ACTION_STEP_DATA)){
                int step = intent.getIntExtra(BroadcastKey.KEY_DATA,0);
                setStep(step);
            }else if (intent.getAction().equals(BroadcastKey.ACTION_TEMPERATURE_DATA)){
                int temperature = intent.getIntExtra(BroadcastKey.KEY_DATA,0);
                setTemperature(temperature);
            }else if (intent.getAction().equals(BroadcastKey.ACTION_WEIGHT_DATA)){
                int weight = intent.getIntExtra(BroadcastKey.KEY_DATA,0);
                setWeight(weight);
            }else if (intent.getAction().equals(BroadcastKey.ACTION_SELF_PROFILE_UPDATE)){
                if (shoesService!=null){
                    userNameText.setText(shoesService.getNickName());
                    userSignText.setText("@"+shoesService.getUserName());
                }
            }
        }
    }
}
