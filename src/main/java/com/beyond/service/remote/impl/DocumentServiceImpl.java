package com.beyond.service.remote.impl;

import com.beyond.controller.MainController;
import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.dao.remote.RemoteDao;
import com.beyond.dao.remote.impl.SimpleRemoteDaoImpl;
import com.beyond.service.remote.DocumentService;
import javafx.util.Callback;

import java.io.File;

public class DocumentServiceImpl implements DocumentService {

    private LocalDao localDao;
    private RemoteDao remoteDao;

    private String url;
    private String filePath;

    public enum SynchronizeType{
        UPLOAD,DOWNLOAD,NULL
    }
    
    public DocumentServiceImpl(String url, String filePath){
        this.url = url;
        this.filePath = filePath;
        this.localDao = new LocalDaoXmlImpl(filePath);
        this.remoteDao = new SimpleRemoteDaoImpl(url);
    }

    @Override
    public void synchronize(Callback<SynchronizeType, Object> callback) {
        int localVersion = localDao.getVersion()==null?-1:Integer.parseInt(localDao.getVersion());
        long localLastModifyTimeMills = localDao.getLastModifyTimeMills()==null?-1:Long.parseLong(localDao.getLastModifyTimeMills());
        int remoteVersion = remoteDao.getVersion()==null?-1:Integer.parseInt(remoteDao.getVersion());
        long remoteLastModifyTimeMills = remoteDao.getLastModifyTimeMills()==null?-1:Long.parseLong(remoteDao.getLastModifyTimeMills());

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
        private int localVersion;
        private long localLastModifyTimeMills;
        private int remoteVersion;
        private long remoteLastModifyTimeMills;

        private SynchronizeEntity(int localVersion, long localLastModifyTimeMills, int remoteVersion, long remoteLastModifyTimeMills) {
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
