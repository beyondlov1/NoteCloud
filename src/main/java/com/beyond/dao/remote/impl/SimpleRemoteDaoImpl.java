package com.beyond.dao.remote.impl;

import com.beyond.dao.remote.RemoteDao;
import com.beyond.entity.Document;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

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
    public Integer getVersion() {
        return null;
    }

    @Override
    public Integer setVersion(Integer version) {
        return null;
    }

    @Override
    public int upload(File file) {

        return 0;
    }

    @Override
    public int download(String url) {
        return 0;
    }
}
