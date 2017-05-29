package creeper_san.myshoes;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import creeper_san.myshoes.base.BaseActivity;
import creeper_san.myshoes.flag.IntentKey;
import creeper_san.myshoes.flag.UserInfoDecoder;
import creeper_san.myshoes.helper.TimeStampHelper;

public class UserActivity extends BaseActivity implements ServiceConnection{
    List<UserInfoBean> userInfoBeenList;
    @BindView(R.id.userToolbar)
    Toolbar toolbar;
    @BindView(R.id.userHeadImage)
    ImageView headImage;
    @BindView(R.id.userNickName)
    TextView nickName;
    @BindView(R.id.userUserName)
    TextView userName;
    @BindView(R.id.userInfoList)
    RecyclerView recyclerView;

    private UserInfoAdapter adapter;
    private ShoesService shoesService;

    private String userNameGet = "";
    private String head="";
    private String nickNameStr = "";
    private String sex = "-1";
    private String bornData="";
    private String location = "-1";
    private String sign = "";
    private boolean isSelf = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStartUpInfo();//获取启动信息
        initActionBar();//初始化标题栏
        toStartBindServer(ShoesService.class,this);
        userInfoBeenList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    private void initStartUpInfo() {
        userNameGet = getIntent().getStringExtra(IntentKey.KEY_USER_NAME);
        if (userName.equals("")){
            toast("没有获取到用户名");
            finish();
        }
        userName.setText("@"+userNameGet);
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("个人信息");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shoesService!=null){
            if (shoesService.isNeedFresh()){
                shoesService.setNeedFresh(false);
                getUserProfile();
            }
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_user;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_toolbar,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (shoesService == null){
            isSelf = false;
            return super.onPrepareOptionsMenu(menu);
        }
        if (shoesService.isLogin()){
            if (shoesService.getUserName().equals(userNameGet)){//如果是本人
                menu.findItem(R.id.menuUserUpdateProfile).setVisible(true);
                menu.findItem(R.id.menuUserChat).setVisible(false);
                isSelf = true;
            }else {//如果不是本人
                menu.findItem(R.id.menuUserUpdateProfile).setVisible(false);
                menu.findItem(R.id.menuUserChat).setVisible(true);
                isSelf = false;
            }
        }else {//如果还没登陆
            menu.findItem(R.id.menuUserUpdateProfile).setVisible(false);
            menu.findItem(R.id.menuUserChat).setVisible(true);
            isSelf = false;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }else if (item.getItemId() == R.id.menuUserUpdateProfile){
            toNextActivity(UpdateProfileActivity.class,new String[]{
                    IntentKey.KEY_USER_NICK_NAME,
                    IntentKey.KEY_USER_SEX,
                    IntentKey.KEY_USER_BORN_DATA,
                    IntentKey.KEY_USER_LOCATION,
                    IntentKey.KEY_USER_SIGN,
            },new String[]{
                    nickNameStr,
                    sex,
                    bornData,
                    location,
                    sign
            },false);
        }else if (item.getItemId() == R.id.menuUserChat){
            toNextActivity(ChatActivity.class,false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        shoesService = ((ShoesService.ShoesServerBinder) binder).getService();
        getUserProfile();
        invalidateOptionsMenu();
    }

    private void getUserProfile(){
        shoesService.getUserProfile(userNameGet, new ShoesService.OnInterServerResultListener() {
            @Override
            public void onResult(String resultStr) {
                try {
                    JSONObject jsonObject = new JSONObject(resultStr);
                    userInfoBeenList = new ArrayList<UserInfoBean>();

                    sex = jsonObject.getString("Sex");
                    userInfoBeenList.add(new UserInfoBean("性别",sex));

                    bornData = jsonObject.getString("BornDate");
                    userInfoBeenList.add(new UserInfoBean("出生日期",bornData));
                    userInfoBeenList.add(new UserInfoBean("年龄",bornData));

                    userInfoBeenList.add(new UserInfoBean("注册时间",jsonObject.getString("TimeStamp")));

                    location = jsonObject.getString("Location");
                    userInfoBeenList.add(new UserInfoBean("位置",location));

                    sign = jsonObject.getString("Sign");
                    userInfoBeenList.add(new UserInfoBean("个性签名",sign));
                    head = jsonObject.getString("Head");
                    nickNameStr = jsonObject.getString("NickName");
                    nickName.setText(nickNameStr);
                    adapter = new UserInfoAdapter();
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                log("收到数据 "+resultStr);
            }

            @Override
            public void onFail(String reason) {
                log("没有找到数据");
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     * 内部类
     */

    class UserInfoBean {
        private String title;
        private String content;

        public UserInfoBean(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
    class UserInfoAdapter extends RecyclerView.Adapter<UserInfoViewHolder> {

        @Override
        public UserInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserInfoViewHolder(layoutInflater.inflate(R.layout.item_user_info_list,parent,false));
        }

        @Override
        public void onBindViewHolder(UserInfoViewHolder holder, int position) {
            UserInfoBean bean = userInfoBeenList.get(position);
            String title = bean.getTitle();
            holder.title.setText(title);
            if (title.equals("性别")){
                holder.content.setText(UserInfoDecoder.decodeUserSex(bean.getContent()));
            }else if (title.equals("出生日期")){
                holder.content.setText(UserInfoDecoder.decodeUserBornData(bean.getContent()));
            }else if (title.equals("年龄")){
                holder.content.setText(UserInfoDecoder.decodeUserAge(bean.getContent()));
            }else if (title.equals("注册时间")){
                holder.content.setText(UserInfoDecoder.decodeRegisterTime(bean.getContent()));
            }else if (title.equals("位置")){
                holder.content.setText(UserInfoDecoder.decodeLocation(bean.getContent(),getResources()));
            }else if (title.equals("个性签名")){
                holder.content.setText(UserInfoDecoder.decodeSign(bean.getContent()));
            }else {
                holder.content.setText(bean.getContent());
            }
        }

        @Override
        public int getItemCount() {
            return (userInfoBeenList==null)?0:userInfoBeenList.size();
        }
    }
    class UserInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.beanUserInfoTitle)
        TextView title;
        @BindView(R.id.beanUserInfoContent)
        TextView content;

        public UserInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
