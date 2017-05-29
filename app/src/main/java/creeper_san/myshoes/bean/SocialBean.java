package creeper_san.myshoes.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class SocialBean {
    private String content;
    private String pic;
    private String userName;
    private String nickName;
    private String timeStamp;
    private boolean isHaveImage;

    public SocialBean(JSONObject json) throws JSONException {
        content = json.getString("Content");
        pic = json.getString("Pic");
        userName = json.getString("UserName");
        timeStamp = json.getString("TimeStamp");
        nickName = json.optString("NickName",userName);
        isHaveImage = new Random().nextBoolean();
    }
    public SocialBean(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public SocialBean(String content, String pic, String userName, String nickName, String timeStamp) {
        this.content = content;
        this.pic = pic;
        this.userName = userName;
        this.nickName = nickName;
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }
    public String getPic() {
        return pic;
    }
    public String getUserName() {
        return userName;
    }
    public String getTimeStamp() {
        return timeStamp;
    }

    public boolean isHaveImage() {
        return isHaveImage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
