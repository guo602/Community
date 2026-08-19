package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import io.micrometer.common.util.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CommunityUtil {
    //generate random string
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll(" ","");
    }

    //MD5加密
    public static String MD5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //生成随机密码（大小写字母+数字）
    public static String generateRandomPassword(int length){
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<length;i++){
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for(String key : map.keySet()){
                json.put(key,map.get(key));

            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }

    public static void main(String[] args){
        Map<String ,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
    }


}
