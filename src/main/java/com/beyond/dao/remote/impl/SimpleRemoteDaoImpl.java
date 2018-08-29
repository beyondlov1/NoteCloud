package com.beyond.dao.remote.impl;

import com.beyond.dao.remote.RemoteDao;
import com.beyond.entity.Document;
import com.beyond.f.F;
import com.beyond.utils.HttpUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.apache.jackrabbit.webdav.client.methods.HttpProppatch;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SimpleRemoteDaoImpl implements RemoteDao {

    private String url;

    public SimpleRemoteDaoImpl(String url) {
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
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpPropfind httpPropfind = HttpUtils.getPropfind(url, "version");
        try {
            CloseableHttpResponse response = client.execute(httpPropfind);
            String content = HttpUtils.getContentFromResponse(response);
            client.close();
            return (int) HttpUtils.getInResponseContent(content, "version", 2, "\\d+");
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
            if (httpProppatch == null) throw new RuntimeException("HttpProppatch is null");
            CloseableHttpResponse response = client.execute(httpProppatch);
            client.close();
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void setLastModifyTimeMills(long lastModifyTimeMills) {
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpProppatch httpProppatch = HttpUtils.addProperty(url, "lastModifyTimeMills", lastModifyTimeMills);
        try {
            if (httpProppatch == null) throw new RuntimeException("HttpProppatch is null");
            client.execute(httpProppatch);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getLastModifyTimeMills() {
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpPropfind httpPropfind = HttpUtils.getPropfind(url, "lastModifyTimeMills");
        try {
            CloseableHttpResponse response = client.execute(httpPropfind);
            String content = HttpUtils.getContentFromResponse(response);
            client.close();
            return HttpUtils.getInResponseContent(content, "lastModifyTimeMills", 1, "\\d+");
        } catch (IOException e) {
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

    public static void main(String[] args) {
        String url = "https://yura.teracloud.jp/dav/test.jpg";
        RemoteDao remoteDao = new SimpleRemoteDaoImpl(url);
        remoteDao.setVersion(9);
        int version = remoteDao.getVersion();
        System.out.println(version);
        remoteDao.setLastModifyTimeMills(10000);
        System.out.println(remoteDao.getLastModifyTimeMills());
    }
}
