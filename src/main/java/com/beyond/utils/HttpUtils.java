package com.beyond.utils;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.apache.jackrabbit.webdav.client.methods.HttpProppatch;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.xml.Namespace;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {
    private static CloseableHttpClient client;
    public static CloseableHttpClient getClient(String username, String password){
        if (client==null){
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            provider.setCredentials(AuthScope.ANY,credentials);

            //initClient
            HttpClientBuilder builder = HttpClientBuilder.create();
            builder.setDefaultCredentialsProvider(provider);
            client = builder.build();
        }
        return client;
    }

    public static HttpProppatch addProperty(String url, String key, Object value, Namespace namespace){
        DavPropertySet newProps=new DavPropertySet();
        DavProperty property=new DefaultDavProperty<Object>(key,value,namespace);
        newProps.add(property);
        DavPropertyNameSet removeProperties=new DavPropertyNameSet();
        HttpProppatch httpProppatch = null;
        try {
             httpProppatch = new HttpProppatch(url,newProps,removeProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpProppatch;
    }
    public static HttpProppatch addProperty(String url,String key,Object value){
        return addProperty(url,key,value,DavConstants.NAMESPACE);
    }

    public static HttpPropfind getPropfind(String url,String key, Namespace namespace){
        DavPropertyNameSet set = new DavPropertyNameSet();
        set.add(key,namespace);
        try {
            return new HttpPropfind(url,DavConstants.PROPFIND_BY_PROPERTY,set,DavConstants.DEPTH_INFINITY);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static HttpPropfind getPropfind(String url,String key){
        return getPropfind(url,key,DavConstants.NAMESPACE);
    }

    public static String getContentFromResponse(CloseableHttpResponse response){
        StringBuilder stringBuilder = new StringBuilder();
        HttpEntity entity = response.getEntity();
        try {
            InputStream content = entity.getContent();
            byte[] bytes = new byte[1024];
            int len = 0;
            while((len=content.read(bytes))!=-1){
                stringBuilder.append(new String(bytes,0,len));
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getOnlyVersionInResponseContent(String content){
        String[] versions = content.split("version");
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(versions[2]);
        if (matcher.find()){
            String versionString = matcher.group();
            return Integer.valueOf(versionString);
        }else {
            return -1;
        }
    }
}
