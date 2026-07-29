package com.nowcoder.community.util;

import io.micrometer.common.util.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.springframework.util.DigestUtils;

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
}
