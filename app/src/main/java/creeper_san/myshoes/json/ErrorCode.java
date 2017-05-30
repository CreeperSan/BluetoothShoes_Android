package creeper_san.myshoes.json;

import android.support.annotation.StringDef;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ErrorCode {
    public final static String CODE_OK = "ok";//请求完成
    public final static String CODE_KEY_INVALID = "invalid key";//Key错误
    public final static String CODE_UNKNOWN_CITY = "unknown city";//城市没找到
    public final static String CODE_OUT_OF_REQUEST = "no more requests";//超过访问次数
    public final static String CODE_PARAM_INVALID = "param invalid";//参数错误
    public final static String CODE_VIP_OVER = "vip over";//VIP账号到期
    public final static String CODE_ANR = "anr";//请求处理超时
    public final static String CODE_PERMISSION_DENIED = "permission denied";//无访问权限

    public final static String CODE_MISSING = "Miss";//缺少StatusCode
    public final static String CODE_DECODE_ERR = "DecodeErr";//数据解析错误

    @StringDef({CODE_OK,CODE_KEY_INVALID,CODE_UNKNOWN_CITY,CODE_OUT_OF_REQUEST,CODE_PARAM_INVALID,CODE_VIP_OVER,
            CODE_ANR,CODE_PERMISSION_DENIED,CODE_MISSING,CODE_DECODE_ERR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorCode_{};
}
