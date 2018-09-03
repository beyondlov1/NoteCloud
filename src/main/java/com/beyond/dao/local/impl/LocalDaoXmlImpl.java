package com.beyond.dao.local.impl;

import com.beyond.dao.local.LocalDao;
import com.beyond.entity.Document;
import com.beyond.f.Config;
import com.beyond.utils.XmlUtils;
import com.sun.istack.internal.NotNull;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private void init() {

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
            return;
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

        //添加属性
        this.setVersion(0);
        this.setLastModifyTimeMills(new Date().getTime());
        this.setModifiedIds(null);
    }

    @Override
    public String add(Document document) {
        if (document == null) return null;
        Date date = new Date();
        document.setCreateTime(date);
        document.setLastModifyTime(date);
        XStream xStream = XmlUtils.getXStream();
        String xml = xStream.toXML(document);
        XmlUtils.appendInRootXml(xml, xmlPath, ROOT_TAG);
        return document.getId();
    }

    @Override
    public String delete(Document document) {
        if (document == null) return null;
        deleteById(document.getId());
        return document.getId();
    }

    @Override
    public String deleteById(String id) {
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
        return id;
    }

    @Override
    public String update(Document document) {
        if (document == null) return null;
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
        return document.getId();
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
    public void setVersion(long version) {
        setProperty("_version", version);
    }

    @Override
    public long getVersion() {
        String versionString = getProperty("_version");
        try {
            return Long.parseLong(versionString);
        } catch (Exception e) {
            Config.logger.info(e.getMessage());
            return -1;
        }
    }

    @Override
    public void setLastModifyTimeMills(long lastModifyTimeMills) {
        setProperty("_lastModifyTimeMills", lastModifyTimeMills);
    }

    @Override
    public long getLastModifyTimeMills() {
        String versionString = getProperty("_lastModifyTimeMills");
        if (StringUtils.isNotBlank(versionString)) {
            return Long.parseLong(versionString);
        } else {
            return -1;
        }
    }

    @Override
    public void setModifiedIds(String[] ids) {
        if (ids == null) {
            setProperty("_modifiedIds", null);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String id : ids) {
            stringBuilder.append(",");
            stringBuilder.append(id);
        }
        if (StringUtils.isNotBlank(stringBuilder)) {
            String s = stringBuilder.toString();
            setProperty("_modifiedIds", s.substring(1));
        } else {
            setProperty("_modifiedIds", null);
        }
    }

    @Override
    public String[] getModifiedIds() {
        String modifiedIds = getProperty("_modifiedIds");
        return modifiedIds.split(",");
    }

    /**
     * 向xml文件中添加属性
     *
     * @param propertyName
     * @param value        如果为空则保存 ""
     */
    @Override
    public void setProperty(String propertyName, Object value) {
        UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(Paths.get(xmlPath), UserDefinedFileAttributeView.class);
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(com.beyond.utils.StringUtils.getNotNullString(value));
        try {
            userDefinedFileAttributeView.write(propertyName, byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 取出文件属性
     *
     * @param propertyName
     * @return 如果异常返回 ""
     */
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
            Config.logger.info(e.getMessage());
        }
        return "";
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(Paths.get(xmlPath), UserDefinedFileAttributeView.class);
        for (String key : properties.keySet()) {
            Object value = properties.get(key);
            ByteBuffer byteBuffer = Charset.defaultCharset().encode(com.beyond.utils.StringUtils.getNotNullString(value));
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
            for (String key : keys) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(userDefinedFileAttributeView.size(key));
                userDefinedFileAttributeView.read(key, byteBuffer);
                byteBuffer.flip();
                map.put(key, Charset.defaultCharset().decode(byteBuffer).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) {
        LocalDaoXmlImpl localDaoXml = new LocalDaoXmlImpl("F:\\git_repository\\MyGitHub\\NoteCloud\\documents1.xml");
        long version = localDaoXml.getVersion();
        System.out.println(version);
        localDaoXml.setProperty("_version", 10);
        String version1 = localDaoXml.getProperty("_version");
        System.out.println(version1);
    }

}
