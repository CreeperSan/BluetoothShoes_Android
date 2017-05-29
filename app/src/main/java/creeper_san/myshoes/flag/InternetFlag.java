package creeper_san.myshoes.flag;

public class InternetFlag {
    public static String SERVER_ADDRESS = "http://192.168.1.106:8080/";

    //请求Header(Token)没有则为null
    public final static String KEY_HEADER_TOKEN = "token";
    public final static String VALUE_HEADER_EMPTY_TOKEN = "null";
    public final static String KEY_ACCOUNT = "account";
    public final static String KEY_PASSWORD = "password";

    //请求的事件内容
    public final static String KEY_REQUEST_TYPE = "type";
    public final static String VALUE_REQUEST_TYPE_LOGIN = "login";
    public final static String VALUE_REQUEST_TYPE_REGISTER = "register";

}
