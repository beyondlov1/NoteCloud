package com.beyond.utils;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {
    public static CloseableHttpClient getClient(String username, String password) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        //initClient
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultCredentialsProvider(provider);
        return builder.build();
    }

    public static HttpProppatch addProperty(String url, String key, Object value, Namespace namespace) {
        DavPropertySet newProps = new DavPropertySet();
        DavProperty property = new DefaultDavProperty<Object>(key, value, namespace);
        newProps.add(property);
        DavPropertyNameSet removeProperties = new DavPropertyNameSet();
        HttpProppatch httpProppatch = null;
        try {
            httpProppatch = new HttpProppatch(url, newProps, removeProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpProppatch;
    }

    public static HttpProppatch addProperty(String url, String key, Object value) {
        return addProperty(url, key, value, DavConstants.NAMESPACE);
    }

    public static HttpProppatch addProperties(String url, Map<String,Object> properties, Namespace namespace) {
        DavPropertySet newProps = new DavPropertySet();
        Iterator<String> iterator = properties.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            Object value = properties.get(key);
            DavProperty property = new DefaultDavProperty<Object>(key, value, namespace);
            newProps.add(property);
        }
        DavPropertyNameSet removeProperties = new DavPropertyNameSet();
        HttpProppatch httpProppatch = null;
        try {
            httpProppatch = new HttpProppatch(url, newProps, removeProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpProppatch;
    }


    public static HttpPropfind getPropfind(String url, String key, Namespace namespace) {
        DavPropertyNameSet set = new DavPropertyNameSet();
        set.add(key, namespace);
        try {
            return new HttpPropfind(url, DavConstants.PROPFIND_BY_PROPERTY, set, DavConstants.DEPTH_INFINITY);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HttpPropfind getPropfind(String url, String key) {
        return getPropfind(url, key, DavConstants.NAMESPACE);
    }

    public static HttpPropfind getBatchPropfind(String url, Set<String> keys, Namespace namespace) {
        DavPropertyNameSet set = new DavPropertyNameSet();
        for (String key:keys) {
            set.add(key, namespace);
        }
        try {
            return new HttpPropfind(url, DavConstants.PROPFIND_BY_PROPERTY, set, DavConstants.DEPTH_INFINITY);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getContentFromResponse(CloseableHttpResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpEntity entity = response.getEntity();
        try {
            InputStream content = entity.getContent();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = content.read(bytes)) != -1) {
                stringBuilder.append(new String(bytes, 0, len));
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getStringInResponseContent(String content, String targetTag, int splitIndex) {
        targetTag = targetTag+">";
        String[] split = content.split(targetTag);
        try {
            String s = StringUtils.substringBeforeLast(split[splitIndex].trim(), "</");
            return StringUtils.isBlank(s) ? null : s.trim();
        }catch (Exception e){
            return null;
        }

        //Pattern pattern = Pattern.compile(regex);
        //Matcher matcher = pattern.matcher(split[splitIndex]);
        //if (matcher.find()) {
        //    System.out.println(matcher.group());
        //    return matcher.group();
        //} else {
        //    return null;
        //}
    }

    public static String getParentUrl(String url) {
        int index = StringUtils.lastIndexOf(url, "/");
        return StringUtils.substring(url,0,index);
    }
}
