package com.beyond.dao.local;

import com.beyond.entity.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LocalDao {
    void setXmlPath(String xmlPath);
    String add(Document document);
    String delete(Document document);
    String deleteById(String id);
    String update(Document document);
    <T> List<T> selectAll(Class<T> clazz);
    List<Document> selectAll();
    Document selectById(String id);

    void setVersion(long version);
    long getVersion();
    void setLastModifyTimeMills(long lastModifyTimeMills);
    long getLastModifyTimeMills();
    void setModifiedIds(String[] ids);
    String[] getModifiedIds();

    void setProperty(String propertyName, Object value);
    String getProperty(String propertyName);
    Map<String, Object> getProperties();
    void setProperties(Map<String,Object> properties);


}
