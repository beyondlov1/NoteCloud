package com.beyond.dao.remote;

import com.beyond.entity.Document;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RemoteDao {
    //对文档的操作
    void setUrl(String url);
    int add(Document document);
    int delete(Document document);
    void deleteById(String id);
    int update(Document document);
    <T> List<T> selectAll(Class<T> clazz);
    List<Document> selectAll();
    Document selectById(String id);

    //对库的整体操作
    List<String> getFileChildren(String dirPath);


    int setProperty(String propertyName,Object value);
    String getProperty(String propertyName);
    void setProperties(Map<String,Object> properties);
    Map<String, Object> getProperties(Set<String> keys);
    int upload(File file);
    int download(String url, String filePath);

    @Deprecated
    String getVersion();
    @Deprecated
    void setVersion(int version);
    @Deprecated
    void setLastModifyTimeMills(long lastModifyTimeMills);
    @Deprecated
    String getLastModifyTimeMills();
}
