package creeper_san.myshoes.flag;


import android.content.res.Resources;

import java.util.Calendar;

import creeper_san.myshoes.R;
import creeper_san.myshoes.helper.TimeStampHelper;

public class UserInfoDecoder {
    public static String decodeUserSex(String sex){
        if (sex.equals("0")){
            return "男";
        }else if (sex.equals("1")){
            return "女";
        }else {
            return "保密";
        }
    }
    public static String decodeUserBornData(String bornData){
        try {
            int year = Integer.parseInt(bornData.substring(0,4));
            int month = Integer.parseInt(bornData.substring(5,7));
            int day = Integer.parseInt(bornData.substring(8,10));
            return year+"年"+month+"月"+day+"日";
        } catch (NumberFormatException e) {
            return "保密";
        }
    }
    public static String decodeUserAge(String bornData){
        try {
            int year = Integer.parseInt(bornData.substring(0,4));
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            return (currentYear-year)+"岁";
        } catch (NumberFormatException e) {
            return "保密";
        }
    }
    public static String decodeRegisterTime(String registerTime){
        try {
            TimeStampHelper helper = new TimeStampHelper(Long.parseLong(registerTime));
            return helper.toCN24String();
        } catch (NumberFormatException e) {
            return "保密";
        }
    }
    public static String decodeLocation(String location, Resources resources){
        try {
            int locationNum = Integer.parseInt(location);
            if (locationNum == -1){
                return "保密";
            }
            String locationStr = resources.getStringArray(R.array.userLocation)[locationNum];
            return locationStr;
        } catch (NumberFormatException e) {
            return "保密";
        }
    }
    public static String decodeSign(String sign){
        if (!sign.equals("None")){
            return sign;
        }else {
            return "这位用户很懒，什么都没有写";
        }
    }
}
