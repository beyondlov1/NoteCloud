package com.beyond.entity;

import com.beyond.utils.StringUtils;

import javax.print.Doc;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

public class Document {

    private String id;
    private String title;
    private String content;
    private String type;
    private Date createTime;
    private Date lastModifyTime;
    private Date lastSynchronizedTime;
    private Integer version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Date getLastSynchronizedTime() {
        return lastSynchronizedTime;
    }

    public void setLastSynchronizedTime(Date lastSynchronizedTime) {
        this.lastSynchronizedTime = lastSynchronizedTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isValid(){
        return !(StringUtils.isEmpty(title) && StringUtils.isEmpty(content));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Document){
            boolean result = true;
            Document otherDocument = (Document) obj;
            Field[] declaredFields = Document.class.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                try {
                    result = Objects.equals(field.get(this),field.get(otherDocument));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (!result){
                    break;
                }
            }
            return result;
        }else{
            return false;
        }
    }
}
