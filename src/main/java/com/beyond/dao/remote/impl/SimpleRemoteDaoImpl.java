package com.beyond.dao.remote.impl;

import com.beyond.dao.remote.RemoteDao;
import com.beyond.entity.Document;
import com.beyond.f.F;
import com.beyond.utils.HttpUtils;
import com.beyond.utils.XmlUtils;
import com.thoughtworks.xstream.XStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleRemoteDaoImpl implements RemoteDao {

    private String url;

    public SimpleRemoteDaoImpl(String url){
        this.url = url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int add(Document document) {
        return 0;
    }

    @Override
    public int delete(Document document) {
        return 0;
    }

    @Override
    public void deleteById(String id) {

    }

    @Override
    public int update(Document document) {
        return 0;
    }

    @Override
    public <T> List<T> selectAll(Class<T> clazz) {
        return null;
    }

    @Override
    public List<Document> selectAll() {
        return null;
    }

    @Override
    public Document selectById(String id) {
        return null;
    }

    @Override
    public List<String> getFileChildren(String dirPath) {
        return null;
    }

    @Override
    public int getVersion() {
        String versionString;
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpPropfind httpPropfind = HttpUtils.getPropfind(url, "version");
        try {
            CloseableHttpResponse response = client.execute(httpPropfind);
            String content = HttpUtils.getContentFromResponse(response);
            return HttpUtils.getOnlyVersionInResponseContent(content);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int setVersion(int version) {
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpProppatch httpProppatch = HttpUtils.addProperty(url, "version", version);
        try {
            if (httpProppatch==null) throw new RuntimeException("HttpProppatch is null");
            CloseableHttpResponse response = client.execute(httpProppatch);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int upload(File file) {

        return 0;
    }

    @Override
    public int download(String url) {
        return 0;
    }

    public static void main(String[] args){
        String url = "https://yura.teracloud.jp/dav/test.jpg";
        RemoteDao remoteDao = new SimpleRemoteDaoImpl(url);
        remoteDao.setVersion(9);
        int version = remoteDao.getVersion();
        System.out.println(version);
    }
}
