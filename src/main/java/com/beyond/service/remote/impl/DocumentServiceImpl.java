package com.beyond.service.remote.impl;

import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.dao.remote.RemoteDao;
import com.beyond.dao.remote.impl.SimpleRemoteDaoImpl;
import com.beyond.service.remote.DocumentService;

import java.io.File;

public class DocumentServiceImpl implements DocumentService {

    private LocalDao localDao;
    private RemoteDao remoteDao;

    private String url;
    private String filePath;

    enum SynchronizeType{
        UPLOAD,DOWNLOAD,NULL
    }
    
    public DocumentServiceImpl(String url, String filePath){
        this.url = url;
        this.filePath = filePath;
        this.localDao = new LocalDaoXmlImpl(filePath);
        this.remoteDao = new SimpleRemoteDaoImpl(url);
    }
    @Override
    public void synchronize() {
        int localVersion = localDao.getVersion();
        long localLastModifyTimeMills = localDao.getLastModifyTimeMills();
        int remoteVersion = remoteDao.getVersion();
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
    }


    class SynchronizeEntity{
        private int localVersion;
        private long localLastModifyTimeMills;
        private int remoteVersion;
        private long remoteLastModifyTimeMills;

        public SynchronizeEntity(int localVersion, long localLastModifyTimeMills, int remoteVersion, long remoteLastModifyTimeMills) {
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

        public int getLocalVersion() {
            return localVersion;
        }

        public void setLocalVersion(int localVersion) {
            this.localVersion = localVersion;
        }

        public long getLocalLastModifyTimeMills() {
            return localLastModifyTimeMills;
        }

        public void setLocalLastModifyTimeMills(long localLastModifyTimeMills) {
            this.localLastModifyTimeMills = localLastModifyTimeMills;
        }

        public int getRemoteVersion() {
            return remoteVersion;
        }

        public void setRemoteVersion(int remoteVersion) {
            this.remoteVersion = remoteVersion;
        }

        public long getRemoteLastModifyTimeMills() {
            return remoteLastModifyTimeMills;
        }

        public void setRemoteLastModifyTimeMills(long remoteLastModifyTimeMills) {
            this.remoteLastModifyTimeMills = remoteLastModifyTimeMills;
        }
    }
}
