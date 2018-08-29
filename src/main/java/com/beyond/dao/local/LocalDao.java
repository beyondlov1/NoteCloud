package com.beyond.dao.local;

import com.beyond.entity.Document;

import java.util.List;

public interface LocalDao {
    void setXmlPath(String xmlPath);
    int add(Document document);
    int delete(Document document);
    void deleteById(String id);
    int update(Document document);
    <T> List<T> selectAll(Class<T> clazz);
    List<Document> selectAll();
    Document selectById(String id);

    int setVersion(int version);
    int getVersion();
    long getLastModifyTimeMills();
}
