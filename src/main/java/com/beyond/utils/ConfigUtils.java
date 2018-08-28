package com.beyond.utils;

import com.beyond.dao.local.impl.LocalDaoXmlImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

    //单例模式
    private static Properties properties;

    public static Properties getProperties(String configPath){

        if (properties!=null) return properties;

        InputStream inputStream = null;
        try {
            properties = new Properties();
            inputStream = new FileInputStream(configPath);
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (properties!=null){
            return properties;
        }else{
            throw new RuntimeException("配置地址不正确!");
        }
    }

    public static Properties getProperties(){
        return getProperties(LocalDaoXmlImpl.class.getResource("/").getPath()+"properties/config.properties");
    }

    public static String getProperty(String key){
        return getProperties().getProperty(key);
    }
}
