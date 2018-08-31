package com.beyond.dao.remote.impl;

import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.dao.remote.RemoteDao;
import com.beyond.entity.Document;
import com.beyond.f.F;
import com.beyond.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.client.methods.HttpMkcol;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.apache.jackrabbit.webdav.client.methods.HttpProppatch;
import org.apache.jackrabbit.webdav.xml.Namespace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void setVersion(int version) {
        setProperty("_version", version);
    }

    @Override
    public String getVersion() {
        return getProperty("_version");
    }

    @Override
    public void setLastModifyTimeMills(long lastModifyTimeMills) {
        setProperty("_lastModifyTimeMills", lastModifyTimeMills);
    }

    @Override
    public String getLastModifyTimeMills() {
        return getProperty("_lastModifyTimeMills");
    }

    /**
     * 设置属性
     *
     * @param propertyName 属性名称, 自定义的格式为: _xxx, 防止与其他名称重复
     * @param value        属性值
     * @return 1为成功, -1为失败
     */
    @Override
    public int setProperty(String propertyName, Object value) {
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpProppatch httpProppatch = HttpUtils.addProperty(url, propertyName, value);
        try {
            if (httpProppatch == null) throw new RuntimeException("HttpProppatch is null");
            client.execute(httpProppatch);
            client.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取属性值
     *
     * @param propertyName 属性名称
     * @return 返回属性值得字符串
     */
    @Override
    public String getProperty(String propertyName) {
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpPropfind httpPropfind = HttpUtils.getPropfind(url, propertyName);
        try {
            CloseableHttpResponse response = client.execute(httpPropfind);
            String content = HttpUtils.getContentFromResponse(response);
            client.close();
            return HttpUtils.getStringInResponseContent(content, propertyName, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpProppatch httpProppatch = HttpUtils.addProperties(url, properties, DavConstants.NAMESPACE);
        try {
            if (httpProppatch == null) throw new RuntimeException("HttpProppatch is null");
            client.execute(httpProppatch);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getProperties(Set<String> keys) {
        Map<String, Object> result = new HashMap<>();
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpPropfind httpPropfind = HttpUtils.getBatchPropfind(url, keys, DavConstants.NAMESPACE);
        for (String key : keys) {
            try {
                CloseableHttpResponse response = client.execute(httpPropfind);
                String content = HttpUtils.getContentFromResponse(response);
                client.close();
                result.put(key, HttpUtils.getStringInResponseContent(content, key, 1));
            } catch (IOException e) {
                e.printStackTrace();
                result.put(key, null);
            }
        }
        return result;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return -1为上传失败, 1为上传成功
     */
    @Override
    public int upload(File file) {
        mkDir();
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new FileEntity(file));
        try {
            client.execute(httpPut);
            client.close();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 下载文件
     *
     * @param url      地址
     * @param filePath 存入本地的地址
     * @return -1为下载失败, 1为下载成功
     */
    @Override
    public int download(String url, String filePath) {
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpGet httpGet = new HttpGet(url);
        FileOutputStream fileOutputStream = null;
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(EntityUtils.toByteArray(entity));
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isExist(){
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            return statusCode < 300 && statusCode >= 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void mkDir(){
        mkDirs(url);
    }

    private void mkDirs(String url){
        CloseableHttpClient client = HttpUtils.getClient(F.USERNAME, F.PASSWORD);
        String parentUrl = HttpUtils.getParentUrl(url);
        HttpMkcol httpMkcol = new HttpMkcol(parentUrl);
        String root = "https://"+URI.create(url).getHost();
        if (!StringUtils.equalsIgnoreCase(parentUrl,root)){
            mkDirs(parentUrl);
        }
        try {
            client.execute(httpMkcol);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String url = "https://yura.teracloud.jp/dav/test1/test1/test.xml";
        RemoteDao remoteDao = new SimpleRemoteDaoImpl(url);
        //remoteDao.setVersion(9);
        //int version = remoteDao.getVersion();
        //System.out.println(version);
        //remoteDao.setLastModifyTimeMills(10000);
        //remoteDao.setProperty("teststst","_versionl<adfkadf&*(&%%$");
        //String teststst = remoteDao.getProperty("teststst");
        //System.out.println(teststst);
        //System.out.println(remoteDao.getLastModifyTimeMills());
//        String filePath = "F:\\git_repository\\MyGitHub\\NoteCloud\\NoteCloud\\documents\\documents.xml";
//        remoteDao.upload(new File(filePath));
//        remoteDao.setVersion(10);
//        remoteDao.download(url, "F:\\git_repository\\MyGitHub\\NoteCloud\\NoteCloud\\documents\\downloadDocuments.xml");
//        LocalDao localDao = new LocalDaoXmlImpl("F:\\git_repository\\MyGitHub\\NoteCloud\\NoteCloud\\documents\\downloadDocuments.xml");
//        System.out.println(localDao.getVersion());
        ((SimpleRemoteDaoImpl) remoteDao).mkDir();
    }
}
