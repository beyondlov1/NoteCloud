package com.beyond.dao.local;

import com.beyond.entity.Document;

import java.util.List;
import java.util.Map;

public interface LocalDao {
    void setXmlPath(String xmlPath);
    int add(Document document);
    int delete(Document document);
    void deleteById(String id);
    int update(Document document);
    <T> List<T> selectAll(Class<T> clazz);
    List<Document> selectAll();
    Document selectById(String id);

    void setVersion(int version);
    String getVersion();
    void setLastModifyTimeMills(long lastModifyTimeMills);
    String getLastModifyTimeMills();
    void setProperty(String propertyName, Object value);
    Object getProperty(String propertyName);
    Map<String, Object> getProperties();
    void setProperties(Map<String,Object> properties);
}
