package com.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: Braydenwong
 * @date: 2018/09/03
 */
public class AbstractSpider {
    /**
     * 1.在开发者工具直接复制请求头信息传入该方法即可，idea会自动添加‘\n’符号，否则使用该方法会出错。
     * 2.以：开头的请求头不必复制
     * 3.代码已过滤accept-encoding请求，不必手动过滤
     */
    public static Map<String, Object> setHeaders(String headers) {
        Map<String, Object> request = new LinkedHashMap<>(16);
        String[] headerList = headers.split("\n");
        for (int k = 0; k < headerList.length; k++) {
            String key = null;
            String value = null;
            if (headerList[k].contains("accept-encoding")) {
                continue;
            }
            char[] charArray = headerList[k].toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                StringBuilder builder = new StringBuilder();
                char ch = charArray[i];
                if (ch == ':') {
                    for (int j = 0; j < i; j++) {
                        builder.append(charArray[j]);
                    }
                    key = builder.toString();
                    builder.setLength(0);
                    for (int j = i + 1; j < charArray.length; j++) {
                        builder.append(charArray[j]);
                    }
                    value = builder.toString();
                    break;
                }
            }
            request.put(key, value);
        }
        return request;
    }

    public static String getValueByKeyFromJson(String json, String oriKey) {
        if (json == null || json == "") {
            return null;
        }
        boolean isJsonString = isJSONValid(json);
        if (!isJsonString) {
            return null;
        }
        String key = oriKey.trim();
        Object object = JSON.parse(json);
        Class<? extends Object> cls = object.getClass();
        if (cls == JSONObject.class) {
            JSONObject jo = (JSONObject) object;
            if (jo.containsKey(key)) {
                return jo.getString(key);
            }
            for (Object o : jo.values()) {
                boolean isJson = isJSONValid(o.toString());
                if (isJson) {
                    String tmp = getValueByKeyFromJson(o.toString(), key);
                    if (tmp == null) {
                        continue;
                    } else {
                        return tmp;
                    }
                } else {
                    continue;
                }
            }

        } else if (cls == JSONArray.class) {
            JSONArray ja = (JSONArray) object;
            int size = ja.size();
            for (int i = 0; i < size; i++) {
                Object o = ja.get(i);
                if (o != null && o != "") {
                    String tmp = getValueByKeyFromJson(o.toString(), key);
                    if (tmp == null) {
                        continue;
                    } else {
                        return tmp;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isJSONValid(String test) {
        if (test == null || "".equals(test)) {
            return false;
        }
        try {
            JSONObject.parseObject(test);
        } catch (Exception ex) {
            try {
                JSONObject.parseArray(test);
            } catch (Exception ex1) {
                return false;
            }
        }
        return true;
    }
}
