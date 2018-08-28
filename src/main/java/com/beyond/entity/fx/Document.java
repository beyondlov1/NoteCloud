package com.beyond.entity.fx;


import javafx.beans.property.*;

import java.util.Date;

public class Document {
    private StringProperty id;
    private StringProperty title;
    private StringProperty content;
    private StringProperty type;
    private ObjectProperty<Date> createTime;
    private ObjectProperty<Date> lastModifyTime;
    private ObjectProperty<Date> lastSynchronizedTime;
    private IntegerProperty version;
    private com.beyond.entity.Document document;

    public Document(com.beyond.entity.Document document) {
        this.document = document;
        this.setDocument(document);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public Date getCreateTime() {
        return createTime.get();
    }

    public ObjectProperty<Date> createTimeProperty() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime.set(createTime);
    }

    public Date getLastModifyTime() {
        return lastModifyTime.get();
    }

    public ObjectProperty<Date> lastModifyTimeProperty() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime.set(lastModifyTime);
    }

    public Date getLastSynchronizedTime() {
        return lastSynchronizedTime.get();
    }

    public ObjectProperty<Date> lastSynchronizedTimeProperty() {
        return lastSynchronizedTime;
    }

    public void setLastSynchronizedTime(Date lastSynchronizedTime) {
        this.lastSynchronizedTime.set(lastSynchronizedTime);
    }

    public int getVersion() {
        return version.get();
    }

    public IntegerProperty versionProperty() {
        return version;
    }

    public void setVersion(int version) {
        this.version.set(version);
    }

    public com.beyond.entity.Document toNormalDocument() {
        return document;
    }

    public void setDocument(com.beyond.entity.Document document) {
        this.id = new SimpleStringProperty(document.getId());
        this.title = new SimpleStringProperty(document.getTitle());
        this.content = new SimpleStringProperty(document.getContent());
        this.type = new SimpleStringProperty(document.getType());
        this.createTime = new SimpleObjectProperty<>(document.getCreateTime());
        this.lastModifyTime = new SimpleObjectProperty<>(document.getLastModifyTime());
        this.lastSynchronizedTime = new SimpleObjectProperty<>(document.getLastModifyTime());
        this.version = new SimpleIntegerProperty(document.getVersion() == null ? 0 : document.getVersion());
    }
}
