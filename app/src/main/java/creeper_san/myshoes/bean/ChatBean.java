package creeper_san.myshoes.bean;


public class ChatBean {
    private String isFromSelf;
    private String content;
    private String timeStamp;

    public ChatBean(String isFromSelf, String content, String timeStamp) {
        this.isFromSelf = isFromSelf;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public String  isFromSelf() {
        return isFromSelf;
    }

    public void setFromSelf(String fromSelf) {
        isFromSelf = fromSelf;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
