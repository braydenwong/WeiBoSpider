package com.spider;

import com.utils.EncryptUtil;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Braydenwong
 * @date: 2018/09/06
 */
public class WeiBoSpider extends AbstractSpider {

    private static Session session = Requests.session();
    private static String username = "加密后的微博登录用户名";
    private static String password = "原始密码";

    /**
     * 整个登录分三步1.预登录 2.登录 3.重定向 4.获取数据
     */
    public static void main(String[] args) {
        //访问登录页面获取cookie
        session.get("http://weibo.com/login.php").send().readToText();
        //1.开始预登录
        String preJsonStirng = preLogin();
        //2.登录
        String successString = login(preJsonStirng);
        //3.重定向
        redirect(successString);
        //4.获取数据
        String data = getData();
        //输出登录后的后台视频分析数据
        System.out.println(data);

    }

    public static String getData() {
        String rend;
        try {
            String startTime = WeiBoUtil.getNowDate(System.currentTimeMillis() - 60 * 60 * 24 * 1000L * 7);
            String endTime = WeiBoUtil.getNowDate(System.currentTimeMillis() - 60 * 60 * 24 * 1000L);
            Map<String, String> para = new HashMap<>(3);
            para.put("_", String.valueOf(System.currentTimeMillis()));
            para.put("starttime", startTime);
            para.put("endtime", endTime);
            rend = session.get("https://dss.sc.weibo.com/pc/aj/chart/video/videoPlayTrend").params(para).send().readToText();
            return rend;
        } catch (Exception e) {
            rend = "获取数据出错了";
            return rend;
        }
    }

    public static String redirect(String json) {
        String rend;
        try {
            String jsonByRegexEnd = WeiBoUtil.getJsonByRegexEnd(json);
            String ticket = getValueByKeyFromJson(jsonByRegexEnd, "ticket");
            Map<String, String> ssoParams = new HashMap<>(5);
            ssoParams.put("callback", "sinaSSOController.callbackLoginStatus");
            ssoParams.put("client", "ssologin.js(v1.4.18)");
            ssoParams.put("ticket", ticket);
            ssoParams.put("ssosavestate", String.valueOf(System.currentTimeMillis() / 1000));
            ssoParams.put("_", String.valueOf(System.currentTimeMillis()));
            rend = session.get("https://passport.weibo.com/wbsso/login").timeout(15000).params(ssoParams).send().readToText();
            return rend;
        } catch (Exception e) {
            rend = "重定向出错了";
            return rend;
        }
    }

    public static String preLogin() {
        String rend;
        try {
            Map<String, String> paras = new HashMap<>(7);
            paras.put("entry", "weibo");
            paras.put("callback", "sinaSSOController.callbackLoginStatus");
            paras.put("client", "ssologin.js(v1.4.18)");
            paras.put("rsakt", "mod");
            paras.put("checkpin", "1");
            paras.put("su", username);
            paras.put("_", String.valueOf(System.currentTimeMillis()));
            String json1 = session.post("http://login.sina.com.cn/sso/prelogin.php").timeout(15000).params(paras).send().readToText();
            rend = WeiBoUtil.getJsonByRegex(json1);
            return rend;
        } catch (Exception e) {
            rend = "预登录出错了";
            return rend;
        }
    }

    public static String login(String json) {
        String rend;
        try {
            String servertime = String.valueOf(getValueByKeyFromJson(json, "servertime"));
            String nonce = getValueByKeyFromJson(json, "nonce");
            String pubkey = getValueByKeyFromJson(json, "pubkey");
            String rsakv = getValueByKeyFromJson(json, "rsakv");
            String pwd = EncryptUtil.getPassword(password, servertime, nonce, pubkey);
            Map<String, String> postData = new HashMap<>(18);
            postData.put("entry", "weibo");
            postData.put("gateway", "1");
            postData.put("from", "");
            postData.put("savestate", "7");
            postData.put("userticket", "1");
            postData.put("vsnf", "1");
            postData.put("service", "miniblog");
            postData.put("encoding", "UTF-8");
            postData.put("pwencode", "rsa2");
            postData.put("sr", "1280*800");
            postData.put("prelt", "529");
            postData.put("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
            postData.put("rsakv", rsakv);
            postData.put("servertime", servertime);
            postData.put("nonce", nonce);
            postData.put("su", "bmV3bWVkaWElNDB2emhpYm8udHY=");
            postData.put("sp", pwd);
            postData.put("returntype", "TEXT");
            rend = session.post("http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)&_=" + System.currentTimeMillis()).timeout(15000).params(postData).send().readToText();
            return rend;
        } catch (Exception e) {
            rend = "登录出错了";
            return rend;
        }
    }
}
