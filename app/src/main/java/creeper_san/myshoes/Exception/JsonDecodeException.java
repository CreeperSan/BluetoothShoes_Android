package creeper_san.myshoes.Exception;


import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class JsonDecodeException extends Exception {
    public final static String TYPE_DECODE_ERR = "JSON数据解析出错";
    public final static String TYPE_MISS = "JSON缺少键值";
    public final static String TYPE_MISS_VALUE = "缺少值";
    public final static String TYPE_CUSTOM = "JSON其他异常";

    private String type;//错误的类型
    private String reason;//错误的原因
    private String errCode;

    public JsonDecodeException(@ErrorType_ String type, String reason, String errCode) {
        this.type = type;
        this.reason = reason;
        this.errCode = errCode;
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public String getErrCode() {
        return errCode;
    }

    @StringDef({TYPE_DECODE_ERR,TYPE_MISS,TYPE_CUSTOM,TYPE_MISS_VALUE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorType_ {}
}
