package creeper_san.myshoes.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import creeper_san.myshoes.flag.BroadcastKey;

public class ConnectionHelper {
    public String TAG = getClass().getSimpleName();
    final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private MessageListener messageListener;
    private ConnectThread thread;

    private Context context;
    private OnConnectionChangeListener listener;

    public ConnectionHelper(Context context,OnConnectionChangeListener listener) {
        this.context = context;
        this.listener = listener;
    }

    private boolean isConnected = false;//是否连接上鞋子
    private boolean isFinding = false;//是否正在寻找鞋子

    /**
     *      消息通知
     */
    private void notifyShoesConnected(){
        Intent intent = new Intent(BroadcastKey.ACTION_CONNECTION_CHANGE);
        intent.putExtra(BroadcastKey.KEY_CONNECTION_CHANGE_CONNECTED,BroadcastKey.VALUE_CONNECTION_CHANGE_CONNECTED);
        context.sendBroadcast(intent);
        listener.onConnect();
    }
    private void notifyShoesDisconnect(){
        listener.onDisconnect();
        Intent intent = new Intent(BroadcastKey.ACTION_CONNECTION_CHANGE);
        intent.putExtra(BroadcastKey.KEY_CONNECTION_CHANGE_CONNECTED,BroadcastKey.VALUE_CONNECTION_CHANGE_DISCONNECT);
        context.sendBroadcast(intent);
    }


    /**
     *      管理蓝牙连接
     */
    public void connect(BluetoothDevice device){
        thread = new ConnectThread(device);
        thread.start();
    }
    public void disConnect(){
        thread.disconnect();
        thread = null;
    }
    public void send(String msg){
        if (isConnected && thread!=null){
            thread.write(msg);
        }else {
            loge("尚未连接到蓝牙设备");
        }
    }
    public void send(String msg,Runnable onFail){
        if (isConnected && thread!=null){
            thread.write(msg,onFail);
        }else {
            loge("尚未连接到蓝牙设备");
            onFail.run();
        }
    }
    /**
     *      内部类
     */
    class ConnectThread extends Thread{
        private BluetoothSocket socket;
        private boolean isRunning = true;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ConnectThread(BluetoothDevice device) {
            try {
                socket = device.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                e.printStackTrace();
                log("连接失败");
            }
        }
        @Override
        public void run() {
            super.run();
            try {
                socket.connect();
                log("连接成功");
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                isConnected = true;
                notifyShoesConnected();
                log("输入输出流创建成功");
                while (isRunning){
                    readSocket();
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                    log("连接失败，套接字已关闭");
                } catch (IOException e1) {
                    log("连接失败，套接字关闭失败");
                    e1.printStackTrace();
                }
            }
        }
        private void readSocket() {
            byte[] buffer = new byte[1024];
            int length = -1;
            try {
                length = inputStream.read(buffer);
                String receiveData = new String(buffer,0,length,"UTF-8");
                if (messageListener!=null){
                    messageListener.onMessageReceive(receiveData);
                }
            } catch (IOException e) {
                log("读取失败");
                disconnect();
                e.printStackTrace();
            }
        }
        private void write(String msg){
            try {
                outputStream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
            }
        }
        private void write(String msg,Runnable onFail){
            try {
                outputStream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
                onFail.run();
            }
        }


        public void disconnect(){
            isRunning = false;
            isConnected = false;
            try {
                inputStream.close();
                outputStream.close();
                log("输入输出流已关闭");
                socket.close();
                log("socket关闭");
            } catch (IOException e) {
                e.printStackTrace();
            }
            notifyShoesDisconnect();
        }
    }

    /**
     *      Getter & Setter
     */
    public boolean isConnected() {
        return isConnected;
    }
    public MessageListener getMessageListener() {
        return messageListener;
    }
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
    public boolean isFinding() {
        return isFinding;
    }
    public void setFinding(boolean finding) {
        isFinding = finding;
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
     *      一些接口
     */
    public interface MessageListener{
        public void onMessageReceive(String msg);
    }
    public interface OnConnectionChangeListener{
        public void onConnect();
        public void onDisconnect();
    }











}
