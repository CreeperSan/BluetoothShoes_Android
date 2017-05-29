package creeper_san.myshoes.helper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeStampHelper {
    private long time;
    private String resultText;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public TimeStampHelper(long time) {
        this.time = time;
        analyze();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        analyze();
    }

    private void analyze(){
        resultText = simpleDateFormat.format(time);
    }

    public String getYear(){
        return resultText.substring(0,4);
    }
    public String getMonth(){
        return resultText.substring(5,7);
    }
    public String getDay(){
        return resultText.substring(8,10);
    }
    public String getHour(){
        return resultText.substring(11,13);
    }
    public String getMinute(){
        return resultText.substring(14,16);
    }
    public String getSecond(){
        return resultText.substring(17,19);
    }

    public String toCN24String(){
        return getYear()+"年"+getMonth()+"月"+getDay()+"日"+getHour()+"时"+getMinute()+"分"+getSecond()+"秒";
    }
    public String toCN12String(){
        int hour = Integer.parseInt(getHour());
        if (hour>12){
            hour-=12;
        }
        return getYear()+"年"+getMonth()+"月"+getDay()+"日"+hour+"时"+getMinute()+"分"+getSecond()+"秒";
    }

    public static String getUpToNowTimeString(String timeStampStr){
        try {
            long time = Long.parseLong(timeStampStr);
            long currentTime = System.currentTimeMillis();
            long timeDifferent = currentTime - time;
            if (timeDifferent < 1000*60 ){
                return "刚刚";
            }else if (timeDifferent < 1000*60*60 ){
                return (timeDifferent/1000/60) + "分钟前";
            }else if (timeDifferent < 1000*60*60*24 ){
                return (timeDifferent/1000/60/60)+"小时前";
            }else if (timeDifferent < 1000*60*60*24*5 ){
                return (timeDifferent/1000/60/60/24)+"天前";
            }else {
                return new TimeStampHelper(time).toCN24String();
            }
        } catch (NumberFormatException e) {
            return timeStampStr;
        }
    }

}
