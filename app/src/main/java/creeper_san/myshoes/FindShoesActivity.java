package creeper_san.myshoes;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import creeper_san.myshoes.base.BaseActivity;
import creeper_san.myshoes.event.LedEvent;
import creeper_san.myshoes.event.MissingEvent;
import creeper_san.myshoes.event.MissingResultEvent;
import creeper_san.myshoes.flag.BroadcastKey;

public class FindShoesActivity extends BaseActivity {
    @BindView(R.id.findShoesAlreadyFound)
    Button btnFounded;
    @BindView(R.id.findShoesFindShoes)
    Button btnFind;
    @BindView(R.id.findShoesStatus)
    ImageView imgStatus;
    @BindView(R.id.findShoesHint)
    TextView txtHint;

//    private ShoesService shoesService;
    private ConnectionReceiver receiver;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_find_shoes;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new ConnectionReceiver();
        IntentFilter intentFilter = new IntentFilter(BroadcastKey.ACTION_CONNECTION_CHANGE);
        registerReceiver(receiver,intentFilter);
//        toStartBindServer(ShoesService.class,this);
        setTitle("找回我的鞋子");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initStatus();
    }

    private void initStatus() {
        postEvent(new MissingEvent(false,true));
    }

    @Subscribe
    public void onMissingEvent(MissingResultEvent event){
        if (event.isConnected()){
            if (event.getStatus()){
                imgStatus.setImageResource(R.drawable.can_find_shoes_light);
                txtHint.setText("请循光源以及声音寻找你的鞋子");
                hideBtnFind();
                showBtnFound();
            }else {
                imgStatus.setImageResource(R.drawable.cant_find_shoes);
                txtHint.setText("找不到鞋子了吗？请点击下方按钮辅助你寻找鞋子");
                showBtnFind();
                hideBtnFound();
            }
        }else {
            imgStatus.setImageResource(R.drawable.cant_find_shoes_bluetooth);
            txtHint.setText("啊哦，请先连接上蓝牙以便我们识别你的鞋子");
            hideBtnFind();
            hideBtnFound();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
//        unbindService(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onServiceConnected(ComponentName name, IBinder binder) {
//        shoesService = ((ShoesService.ShoesServerBinder) binder).getService();
//        freshShoesStatus();
//    }

    public void freshShoesStatus(){
        postEvent(new MissingEvent(false,false));
//        if (shoesService.isConnected()){
//            if (shoesService.isFinding()){
//                imgStatus.setImageResource(R.drawable.can_find_shoes_light);
//                txtHint.setText("请循光源以及声音寻找你的鞋子");
//                hideBtnFind();
//                showBtnFound();
//            }else {
//                imgStatus.setImageResource(R.drawable.cant_find_shoes);
//                txtHint.setText("找不到鞋子了吗？请点击下方按钮辅助你寻找鞋子");
//                showBtnFind();
//                hideBtnFound();
//            }
//        }else {
//            imgStatus.setImageResource(R.drawable.cant_find_shoes_bluetooth);
//            txtHint.setText("啊哦，请先连接上蓝牙以便我们识别你的鞋子");
//            hideBtnFind();
//            hideBtnFound();
//        }
    }

    public void hideBtnFind(){
        btnFind.setVisibility(View.GONE);
    }
    public void hideBtnFound(){
        btnFounded.setVisibility(View.GONE);
    }
    public void showBtnFind(){
        btnFind.setVisibility(View.VISIBLE);
    }
    public void showBtnFound(){
        btnFounded.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void onServiceDisconnected(ComponentName name) {
//        finish();
//    }

    @OnClick({R.id.findShoesAlreadyFound, R.id.findShoesFindShoes})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.findShoesAlreadyFound:
//                if (shoesService.isConnected()){
//                    shoesService.stopLED();
//                }else {
//                    toast("请先连接上鞋子");
//                }
//                postEvent(new LedEvent(LedEvent.Companion.getSTATUS_OFF()));
                postEvent(new MissingEvent(true,false));
                freshShoesStatus();
                break;
            case R.id.findShoesFindShoes:
//                if (shoesService.isConnected()){
//                    shoesService.setLED();
//                }else {
//                    toast("请先连接上鞋子");
//                }
//                postEvent(new LedEvent(LedEvent.Companion.getSTATUS_TWINKLE()));
                postEvent(new MissingEvent(true,true));
                freshShoesStatus();
                break;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               freshShoesStatus();
            }
        },300);
    }

    class ConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            freshShoesStatus();
        }
    }
}
