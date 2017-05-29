package creeper_san.myshoes;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import creeper_san.myshoes.base.BaseActivity;
import creeper_san.myshoes.flag.BroadcastKey;

public class ShoesConnectActivity extends BaseActivity implements ServiceConnection{
    private int REQUEST_BLUETOOTH_CODE = 1;

    @BindView(R.id.shoesConnectRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.shoesConnectConnectingLayout)
    LinearLayout connectLayout;
    @BindView(R.id.shoesConnectSuccessLayout)
    LinearLayout successLayout;
    @BindView(R.id.shoesConnectSuccessDisconnect)
    TextView disconnectText;

    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> bluetoothItemList;
    private ShoesService shoesService;
    private ConnectionReceiver connectionReceiver;
    private boolean isDisconnectFromUser = false;

    /**
     * Activity 一些设置方法
     */
    private void showList(){
        recyclerView.setVisibility(View.VISIBLE);
        connectLayout.setVisibility(View.GONE);
        successLayout.setVisibility(View.GONE);
    }
    private void showLoading(){
        recyclerView.setVisibility(View.GONE);
        connectLayout.setVisibility(View.VISIBLE);
        successLayout.setVisibility(View.GONE);
    }
    private void showSuccess(){
        recyclerView.setVisibility(View.GONE);
        connectLayout.setVisibility(View.GONE);
        successLayout.setVisibility(View.VISIBLE);
    }
    @Override
    protected int getLayoutID() {
        return R.layout.activity_shoes_connect;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("连接到鞋子");
        //打开蓝牙
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_BLUETOOTH_CODE);
            } else {
                initRecycleViewList();
            }
        }
        //连接到服务
        toStartBindServer(ShoesService.class,this);
        //初始化工具栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //注册广播接收器
        connectionReceiver = new ConnectionReceiver();
        IntentFilter intentFilter = new IntentFilter(BroadcastKey.ACTION_CONNECTION_CHANGE);
        registerReceiver(connectionReceiver,intentFilter);
        //设置文本监听
        disconnectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDisconnectFromUser = true;
                shoesService.disconnectDevice();
                showList();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH_CODE) {
            if (resultCode == RESULT_OK) {
                initRecycleViewList();
            } else {
                toast("请允许我们使用蓝牙以连接到鞋子");
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 数据获取处理
     */
    private void initRecycleViewList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bluetoothItemList = getBlueToothItemInfo();
        if (bluetoothItemList.size() > 0) {
            BluetoothListAdapter adapter = new BluetoothListAdapter();
            recyclerView.setAdapter(adapter);
        }

    }

    private List<BluetoothDevice> getBlueToothItemInfo() {
        List<BluetoothDevice> tempItemList = new ArrayList<>();
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        if (deviceSet.size() > 0) {
            for (BluetoothDevice device : deviceSet) {
                tempItemList.add(device);
            }
        }
        return tempItemList;
    }

    /**
     *      管理服务的连接
     */

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        shoesService = ((ShoesService.ShoesServerBinder)binder).getService();
        if (shoesService.isConnected()){
            showSuccess();
        }else {
            showList();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        toast("应用程序运行异常,与服务连接意外断开");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
        unregisterReceiver(connectionReceiver);
    }

    /**
     * 内部类
     */

    private class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListViewHolder> {
        @Override
        public BluetoothListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BluetoothListViewHolder(layoutInflater.inflate(R.layout.item_shoes_connect_list, parent, false));
        }
        @Override
        public void onBindViewHolder(BluetoothListViewHolder holder, int position) {
            final BluetoothDevice item = bluetoothItemList.get(position);
            holder.title.setText("设备 : " + item.getName());
            holder.address.setText("地址 : " + item.getAddress());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (shoesService!=null){
                        showLoading();
                        shoesService.connectToDevice(item);
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return (bluetoothItemList == null) ? 0 : bluetoothItemList.size();
        }
    }
    class BluetoothListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemShoesConnectTextTitle)
        TextView title;
        @BindView(R.id.itemShoesConnectTextAddress)
        TextView address;

        BluetoothListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    class ConnectionReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra(BroadcastKey.KEY_CONNECTION_CHANGE_CONNECTED,BroadcastKey.VALUE_CONNECTION_CHANGE_DISCONNECT)
                    == BroadcastKey.VALUE_CONNECTION_CHANGE_CONNECTED){
                showSuccess();
            }else {
                showList();
                if (isDisconnectFromUser){
                    isDisconnectFromUser = false;
                }else {
                    toast("连接失败，请重试");
                }
            }
        }
    }
}
