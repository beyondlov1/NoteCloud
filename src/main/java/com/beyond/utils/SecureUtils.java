package com.beyond.utils;

import sun.plugin2.message.Message;
import sun.security.provider.MD5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureUtils {
    public static String getEncryptedString(String source){
        StringBuffer buffer = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] digest = md5.digest(source.getBytes());
            buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : digest) {
                // 与运算
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }


    public static void main(String[] args){
        String ccc = getEncryptedString("fadf");
        System.out.println(ccc);
    }
}
