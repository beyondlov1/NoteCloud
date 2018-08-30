package com.beyond.dao.local.impl;

import com.beyond.dao.local.LocalDao;
import com.beyond.entity.Document;
import com.beyond.utils.XmlUtils;
import com.thoughtworks.xstream.XStream;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.*;

public class LocalDaoXmlImpl implements LocalDao {

    private final static String ROOT_TAG = "documents";
    private String xmlPath = null;

    public LocalDaoXmlImpl(String xmlPath) {
        setXmlPath(xmlPath);
    }

    @Override
    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
        this.init();
    }

    private int init() {

        //如果文件存在
        File file = new File(xmlPath);
        if (file.exists()) {
            if (file.length() <= 1) {
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(xmlPath);
                    fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + ROOT_TAG + ">");
                    fileWriter.write("</" + ROOT_TAG + ">");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fileWriter != null) {
                            fileWriter.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            return 1;
        }

        //如果不存在 创建
        File parentFile = file.getParentFile();
        boolean isMkdirsSuccess = false;
        boolean isCreateNewFileSuccess = false;
        if (!parentFile.exists()) {
            isMkdirsSuccess = parentFile.mkdirs();
        }
        if (isMkdirsSuccess || parentFile.exists()) {
            try {
                isCreateNewFileSuccess = file.createNewFile();
                FileWriter fileWriter = new FileWriter(xmlPath);
                fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + ROOT_TAG + ">");
                fileWriter.write("</" + ROOT_TAG + ">");
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isCreateNewFileSuccess) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int add(Document document) {
        if (document == null) return 0;
        Date date = new Date();
        document.setCreateTime(date);
        document.setLastModifyTime(date);
        XStream xStream = XmlUtils.getXStream();
        String xml = xStream.toXML(document);
        XmlUtils.appendInRootXml(xml, xmlPath, ROOT_TAG);
        return 1;
    }

    @Override
    public int delete(Document document) {
        if (document == null) return 0;
        deleteById(document.getId());
        return 1;
    }

    @Override
    public void deleteById(String id) {
        XStream xStream = XmlUtils.getXStream();
        List<Document> list = (List) xStream.fromXML(new File(xmlPath));
        List<Document> newList = new ArrayList<>();
        for (Document doc : list) {
            if (!doc.getId().equals(id)) {
                newList.add(doc);
            }
        }
        String xml = xStream.toXML(newList);
        XmlUtils.saveXml(xml, xmlPath);
    }

    @Override
    public int update(Document document) {
        if (document == null) return 0;
        document.setLastModifyTime(new Date());
        XStream xStream = XmlUtils.getXStream();
        List<Document> list = (List) xStream.fromXML(new File(xmlPath));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(document.getId())) {
                document.setCreateTime(list.get(i).getCreateTime());
                list.set(i, document);
            }
        }
        String xml = xStream.toXML(list);
        XmlUtils.saveXml(xml, xmlPath);
        return 1;
    }

    @Override
    public <T> List<T> selectAll(Class<T> clazz) {
        XStream xStream = XmlUtils.getXStream();
        List<Document> documents = (List<Document>) xStream.fromXML(new File(xmlPath));
        List<T> result = new ArrayList<T>();
        for (Document document :
                documents) {
            if (clazz.isAssignableFrom(document.getClass())) {
                T t = (T) document;
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public List<Document> selectAll() {
        XStream xStream = XmlUtils.getXStream();
        return (List<Document>) xStream.fromXML(new File(xmlPath));
    }

    @Override
    public Document selectById(String id) {
//        XPathFactory xPathFactory = XPathFactory.newInstance();
//        XPath xPath = xPathFactory.newXPath();
//        try {
//            XPathExpression compile = xPath.compile("//*[@id]");
//            InputSource is = new InputSource(new FileInputStream(xmlPath));
//            String evaluate = compile.evaluate(is);
//
//            System.out.println(evaluate);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        List<Document> documents = this.selectAll();
        for (Document document :
                documents) {
            if (document.getId().equals(id)) {
                return document;
            }
        }
        return null;
    }

    @Override
    public int setVersion(int version) {
        setProperty("version", version);
        return version;
    }

    @Override
    public int getVersion() {
        String version = getProperty("version");
        return StringUtils.isBlank(version) ? -1 : Integer.parseInt(version);
    }

    @Override
    public long getLastModifyTimeMills() {
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(Paths.get(xmlPath));
            return lastModifiedTime.toMillis();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(Paths.get(xmlPath), UserDefinedFileAttributeView.class);
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(String.valueOf(value));
        try {
            userDefinedFileAttributeView.write(propertyName, byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getProperty(String propertyName) {
        UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(Paths.get(xmlPath), UserDefinedFileAttributeView.class);
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(userDefinedFileAttributeView.size(propertyName));
            userDefinedFileAttributeView.read(propertyName, byteBuffer);
            byteBuffer.flip();
            return Charset.defaultCharset().decode(byteBuffer).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(Paths.get(xmlPath), UserDefinedFileAttributeView.class);
        for (String key : properties.keySet()) {
            Object value = properties.get(key);
            ByteBuffer byteBuffer = Charset.defaultCharset().encode(String.valueOf(value));
            try {
                userDefinedFileAttributeView.write(key, byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>();
        UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(Paths.get(xmlPath), UserDefinedFileAttributeView.class);
        try {
            List<String> keys = userDefinedFileAttributeView.list();
            for (String key :keys) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(userDefinedFileAttributeView.size(key));
                userDefinedFileAttributeView.read(key, byteBuffer);
                byteBuffer.flip();
                map.put(key,Charset.defaultCharset().decode(byteBuffer).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}
