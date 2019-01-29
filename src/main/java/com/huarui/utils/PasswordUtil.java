package com.huarui.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * Created by lihui on 2019/1/29.
 *  MD5加密工具类
 */
public class PasswordUtil {

    /**
     * @param password 需要加密的次数
     * @param username 盐
     * @param hashIterations 加密次数
     * @return
     */
    public static String saltAndMd5(String password,String username,int hashIterations){
        //加密算法
        String algorithmName = "MD5";
        //盐值
        Object salt = ByteSource.Util.bytes(username);

        SimpleHash hash = new SimpleHash(algorithmName, password, salt, hashIterations);
        return hash.toString();//32位
    }
}
