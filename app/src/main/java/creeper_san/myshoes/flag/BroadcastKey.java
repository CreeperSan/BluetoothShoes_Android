package creeper_san.myshoes.flag;

public class BroadcastKey {
    //蓝牙连接状态发生变化
    public final static String ACTION_CONNECTION_CHANGE = "creeper_san.myshoes.flag.ACTION_CONNECTION_CHANGE";
    public final static String KEY_CONNECTION_CHANGE_CONNECTED = "Status";
    public final static int VALUE_CONNECTION_CHANGE_CONNECTED = 1;
    public final static int VALUE_CONNECTION_CHANGE_DISCONNECT = 0;

    //蓝牙接收到数据
    public final static String ACTION_STEP_DATA = "creeper_san.myshoes.flag.ACTION_STEP_DATA";
    public final static String ACTION_TEMPERATURE_DATA = "creeper_san.myshoes.flag.ACTION_TEMPERATURE_DATA";
    public final static String ACTION_WEIGHT_DATA = "creeper_san.myshoes.flag.ACTION_WEIGHT_DATA";
    public final static String KEY_DATA = "Data";

    //个人资料更新
    public final static String ACTION_SELF_PROFILE_UPDATE = "creeper_san.myshoes.flag.ACTION_SELF_PROFILE_UPDATE";

    //聊天新消息
    public final static String ACTION_NEW_CHAT_MESSAGE = "creeper_san.myshoes.flag.ACTION_SELF_PROFILE_UPDATE";
    public final static String KEY_CONTENT = "Content";

}
