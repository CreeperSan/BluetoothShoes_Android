package creeper_san.myshoes;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import creeper_san.myshoes.base.BaseActivity;
import creeper_san.myshoes.flag.IntentKey;
import creeper_san.myshoes.flag.UserInfoDecoder;
import creeper_san.myshoes.helper.TimeStampHelper;

public class UpdateProfileActivity extends BaseActivity implements ServiceConnection{
    @BindView(R.id.updateProfileToolbar)
    Toolbar toolbar;
    @BindView(R.id.updateProfileNickName)
    EditText nickNameEdit;
    @BindView(R.id.updateProfileSex)
    EditText sexEdit;
    @BindView(R.id.updateProfileBornData)
    EditText bornDataEdit;
    @BindView(R.id.updateProfileLocation)
    EditText locationEdit;
    @BindView(R.id.updateProfileSign)
    EditText signEdit;

    private static int[] HEAD_ID = new int[]{
            R.drawable.ic_person_gray_24dp,
            R.drawable.ic_head01,
            R.drawable.ic_head02,
            R.drawable.ic_head03,
            R.drawable.ic_head04,
            R.drawable.ic_head05,
            R.drawable.ic_head06,
            R.drawable.ic_head07,
            R.drawable.ic_head08,
            R.drawable.ic_head09,
            R.drawable.ic_head10,
            R.drawable.ic_head11,
            R.drawable.ic_head12,
            R.drawable.ic_head13,
            R.drawable.ic_head14,
            R.drawable.ic_head15,
            R.drawable.ic_head16,
            R.drawable.ic_head17,
            R.drawable.ic_head18,
    };

    private ShoesService shoesService;

    private String nickName;
    private int sex;
    private String bornData;
    private int year;
    private String month;
    private String day;
    private int location;
    private String sign;

    private MenuItem tempMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntentData();
        initActionBar();
        toStartBindServer(ShoesService.class,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }
    private void initIntentData() {
        try {
            Intent intent = getIntent();
            nickName = intent.getStringExtra(IntentKey.KEY_USER_NICK_NAME);
            sex = Integer.parseInt(intent.getStringExtra(IntentKey.KEY_USER_SEX));
            bornData = intent.getStringExtra(IntentKey.KEY_USER_BORN_DATA);
            location = Integer.parseInt(intent.getStringExtra(IntentKey.KEY_USER_LOCATION));
            sign = intent.getStringExtra(IntentKey.KEY_USER_SIGN);

            nickNameEdit.setText(nickName);
            sexEdit.setText(UserInfoDecoder.decodeUserSex(String.valueOf(sex)));
            bornDataEdit.setText(UserInfoDecoder.decodeUserBornData(bornData));
            locationEdit.setText(UserInfoDecoder.decodeLocation(String.valueOf(location),getResources()));
            signEdit.setText(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("修改个人资料");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if (item.getItemId() == R.id.menuUpdateConfigCommit){
            tempMenuItem = item;
            if (shoesService!=null){
                shoesService.updateUserProfile(nickName, year + "-" + month + "-" + day, sex, location, sign, new ShoesService.OnInterServerResultListener() {
                    @Override
                    public void onResult(String resultStr) {
                        shoesService.setNeedFresh(true);
                        toast("更新成功");
                        finish();
                    }

                    @Override
                    public void onFail(String reason) {
                        tempMenuItem.setVisible(true);
                        toast("个人信息更新失败，请检查你的网络连接");
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_frofile,menu);
        return true;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_update_profile;
    }

    @OnClick({R.id.updateProfileNickName, R.id.updateProfileSex, R.id.updateProfileBornData, R.id.updateProfileLocation, R.id.updateProfileSign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateProfileNickName:
                editNickName();
                break;
            case R.id.updateProfileSex:
                editSex();
                break;
            case R.id.updateProfileBornData:
                editBornData();
                break;
            case R.id.updateProfileLocation:
                editLocation();
                break;
            case R.id.updateProfileSign:
                editSign();
                break;
        }
    }

    private void editSign() {
        dialogSimpleInput("更改个性签名", sign, 1, InputType.TYPE_CLASS_TEXT, "确定", new OnDialogInputListener() {
            @Override
            public void onClick(TextInputEditText editText, DialogInterface dialog, int which) {
                String updateSign = editText.getText().toString().trim();
                if (updateSign.equals("")){
                    sign = "这位用户很懒，什么都没有写";
                }else {
                    sign = updateSign;
                }
                signEdit.setText(sign);
            }
        },"取消",null);
    }
    private void editLocation() {
        dialogList("更改位置", R.array.userLocation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == getResources().getStringArray(R.array.userLocation).length - 1){
                    location = -1;
                }else {
                    location = which;
                }
                locationEdit.setText(UserInfoDecoder.decodeLocation(String.valueOf(location),getResources()));
            }
        });
    }
    private void editBornData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("更改出生日期");
        View view = layoutInflater.inflate(R.layout.dialog_date_packer,null);
        final DatePicker picker = (DatePicker) view.findViewById(R.id.dialogDatePickerPicker);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pickYear = picker.getYear();
                int pickMonth = picker.getMonth()+1;
                int pickDay = picker.getDayOfMonth();
                TimeStampHelper helper = new TimeStampHelper(System.currentTimeMillis());
                int currentYear = 0;
                try {
                    currentYear = Integer.valueOf(helper.getYear());
                } catch (NumberFormatException e) {
                    currentYear = 2017;
                }
                //防止超出
                if (pickYear>=currentYear){
                    toast("你...确定?");
                }else if (pickYear-currentYear>100){
                    toast("真的吗...?");
                }else {
                    year = pickYear;
                    month = (pickMonth<10)?"0"+pickMonth:String.valueOf(pickMonth);
                    day = (pickDay<10)?"0"+pickDay:String.valueOf(pickDay);
                    bornDataEdit.setText(year+"-"+month+"-"+day);
                }
            }
        });
        builder.setNegativeButton("恢复默认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    private void editSex() {
        dialogList("更改性别", R.array.userSex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == getResources().getStringArray(R.array.userSex).length - 1){
                    sex = -1;
                }else {
                    sex = which;
                }
                sexEdit.setText(UserInfoDecoder.decodeUserSex(String.valueOf(sex)));
            }
        });
    }
    private void editNickName() {
        dialogSimpleInput("更改昵称", nickName, 1, InputType.TYPE_CLASS_TEXT, "确定", new OnDialogInputListener() {
            @Override
            public void onClick(TextInputEditText editText, DialogInterface dialog, int which) {
                String updateNickName = editText.getText().toString().trim();
                if (updateNickName.equals("")){
                    toast("不能没有名字哦");
                }else {
                    nickName = updateNickName;
                    nickNameEdit.setText(nickName);
                }
            }
        },"取消",null);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        shoesService = ((ShoesService.ShoesServerBinder)binder).getService();
        checkIsLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIsLogin();
    }

    public void checkIsLogin(){
        if (shoesService!=null){
            if (!shoesService.isLogin()){
                toast("请先登录哈");
                toNextActivity(LoginActivity.class,true);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
