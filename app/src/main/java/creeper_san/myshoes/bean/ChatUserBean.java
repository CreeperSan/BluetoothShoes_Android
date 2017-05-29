package creeper_san.myshoes.bean;

public class ChatUserBean {
    private String userName;
    private String nickName;
    private String timeStamp;
    private String needRead;
    private String content;

    public ChatUserBean(String userName, String nickName, String timeStamp, String needRead, String content) {
        this.userName = userName;
        this.nickName = nickName;
        this.timeStamp = timeStamp;
        this.needRead = needRead;
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNeedRead() {
        return needRead;
    }

    public void setNeedRead(String needRead) {
        this.needRead = needRead;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
