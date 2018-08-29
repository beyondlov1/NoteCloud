package com.beyond.dao.remote;

import com.beyond.entity.Document;

import java.io.File;
import java.util.List;

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
    Integer getVersion();
    Integer setVersion(Integer version);
    int upload(File file);
    int download(String url);
}
