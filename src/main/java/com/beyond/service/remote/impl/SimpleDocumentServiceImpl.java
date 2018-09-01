package com.beyond.service.remote.impl;

import com.beyond.controller.MainController;
import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.dao.remote.RemoteDao;
import com.beyond.dao.remote.impl.SimpleRemoteDaoImpl;
import com.beyond.service.remote.DocumentService;
import javafx.util.Callback;

import java.io.File;

/**
 * 远端的服务层
 * 主要任务: 同步数据
 * 实现思路: 判断lastModifyTimeMills和Version, 只保留最新的版本
 * 缺陷: 每隔一段同步一次, 在两次同步间的真空期, 设备1,设备2 同时添加数据, 则会导致下次较快同步的人会上传, 之后版本号+1, 会和下次同步的人
 * 版本号相同,或者比后者版本号要大, 从而会导致丢失更新
 */
public class SimpleDocumentServiceImpl implements DocumentService {

    private LocalDao localDao;
    private RemoteDao remoteDao;

    private String url;
    private String filePath;

    public enum SynchronizeType{
        UPLOAD,DOWNLOAD,NULL
    }
    
    public SimpleDocumentServiceImpl(String url, String filePath){
        this.url = url;
        this.filePath = filePath;
        this.localDao = new LocalDaoXmlImpl(filePath);
        this.remoteDao = new SimpleRemoteDaoImpl(url);
    }

    @Override
    public void synchronize(Callback<SynchronizeType, Object> callback) {
        long localVersion = localDao.getVersion();
        long localLastModifyTimeMills = localDao.getLastModifyTimeMills();
        long remoteVersion = remoteDao.getVersion();
        long remoteLastModifyTimeMills = remoteDao.getLastModifyTimeMills();

        SynchronizeEntity synchronizeEntity = new SynchronizeEntity(localVersion,localLastModifyTimeMills,remoteVersion,remoteLastModifyTimeMills);
        SynchronizeType synchronizeType = synchronizeEntity.getSynchronizeType();

        if (synchronizeType==SynchronizeType.DOWNLOAD){
            remoteDao.download(url,filePath);
            localDao.setProperty("_version",remoteDao.getProperty("_version"));
            localDao.setProperty("_lastModifyTimeMills",remoteDao.getProperty("_lastModifyTimeMills"));
        }
        if (synchronizeType==SynchronizeType.UPLOAD){
            remoteDao.upload(new File(filePath));
            remoteDao.setProperty("_lastModifyTimeMills",localDao.getProperty("_lastModifyTimeMills"));
            remoteDao.setProperty("_version",localDao.getProperty("_version"));
        }

        callback.call(synchronizeType);
    }


    class SynchronizeEntity{
        private long localVersion;
        private long localLastModifyTimeMills;
        private long remoteVersion;
        private long remoteLastModifyTimeMills;

        private SynchronizeEntity(long localVersion, long localLastModifyTimeMills, long remoteVersion, long remoteLastModifyTimeMills) {
            this.localVersion = localVersion;
            this.localLastModifyTimeMills = localLastModifyTimeMills;
            this.remoteVersion = remoteVersion;
            this.remoteLastModifyTimeMills = remoteLastModifyTimeMills;
        }

        private SynchronizeType getSynchronizeType(){
            if (localLastModifyTimeMills>remoteLastModifyTimeMills&&remoteVersion<localVersion){
                return SynchronizeType.UPLOAD;
            }else if(localLastModifyTimeMills<remoteLastModifyTimeMills&&remoteVersion>localVersion){
                return SynchronizeType.DOWNLOAD;
            }else{
                return SynchronizeType.NULL;
            }
        }

        @Override
        public String toString() {
            return "SynchronizeEntity{" +
                    "localVersion=" + localVersion +
                    ", localLastModifyTimeMills=" + localLastModifyTimeMills +
                    ", remoteVersion=" + remoteVersion +
                    ", remoteLastModifyTimeMills=" + remoteLastModifyTimeMills +
                    '}';
        }
    }
}
