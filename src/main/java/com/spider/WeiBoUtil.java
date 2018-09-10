package com.spider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author: Braydenwong
 * @Date: 2018/9/7
 * @Description:
 */
public class WeiBoUtil {

    public static String getJsonByRegex(String json) {
        String s = json.replaceAll("\\(|\\)", "");
        String replaceAll = s.replaceAll("sinaSSOController.callbackLoginStatus", "");
        return replaceAll;
    }

    public static String getJsonByRegexEnd(String json) {
        String s = json.replaceAll("\\(|\\)", "");
        String replaceAll = s.replaceAll("parent.sinaSSOController.feedBackUrlCallBack", "");
        String s1 = replaceAll.replaceAll(";", "");
        return s1;
    }

    public static String getNowDate(long times){
        String res = "";
        try {
            Date date = new Date(times);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            res = dateFormat.format(date);
        } catch (Exception var6) {
            var6.printStackTrace();
        }
        return res;
    }
}
