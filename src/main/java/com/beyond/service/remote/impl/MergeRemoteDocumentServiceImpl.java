package com.beyond.service.remote.impl;

import com.beyond.controller.MainController;
import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.dao.remote.RemoteDao;
import com.beyond.dao.remote.impl.SimpleRemoteDaoImpl;
import com.beyond.entity.Document;
import com.beyond.f.Config;
import com.beyond.service.remote.RemoteDocumentService;
import com.beyond.utils.ListUtils;
import com.beyond.utils.SortUtils;
import com.beyond.utils.XmlUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class MergeRemoteDocumentServiceImpl implements RemoteDocumentService {

    private LocalDao localDao;
    private RemoteDao remoteDao;

    private String url;
    private String filePath;
    private String downloadTmpPath;

    private static int remoteLockedCount = 0;
    private static int isMergingOrUpdating = 0;
    public static int getIsMergingOrUpdating(){
        return isMergingOrUpdating;
    }
    private synchronized static void setIsMergingOrUpdating(int i){
        isMergingOrUpdating = i;
    }

    public static ObservableList<com.beyond.entity.fx.Document> mergeFxDocuments = null;

    public MergeRemoteDocumentServiceImpl(String url, String filePath){
        this.url = url;
        this.filePath = filePath;
        this.downloadTmpPath = Config.DOWNLOAD_TMP_PATH;
        this.localDao = new LocalDaoXmlImpl(filePath);
        this.remoteDao = new SimpleRemoteDaoImpl(url);
    }

    @Override
    public void synchronize(Callback<Object, Object> callback) {

        SynchronizeEntity synchronizeEntity = getSynchronizeEntity();
        SynchronizeType synchronizeType = synchronizeEntity.getSynchronizeType();

        Config.logger.info(synchronizeEntity);

        if (synchronizeType==SynchronizeType.MERGE){
            remoteDao.setProperty("_lock",1);

            List<Document> localList = getLocalDocumentList();
            List<Document> remoteList = getRemoteDocumentList();
            setIsMergingOrUpdating(1);
            List<Document> mergeList = merge(localDao.getModifiedIds(), localList, remoteList);
            saveLocal(mergeList,synchronizeEntity);
            saveRemote();
            setIsMergingOrUpdating(0);

            //建立一个缓存, 防止刷新页面时都要读取一次xml文件
            mergeFxDocuments=ListUtils.getFxDocumentListFromDocumentList(mergeList);
            SortUtils.sort(mergeFxDocuments,com.beyond.entity.fx.Document.class,"lastModifyTime", MainController.SortType.DESC);
        }

        if (callback!=null){
            callback.call(null);
        }

        Config.logger.info(synchronizeType);
    }

    /**
     * 获取同步信息实体
     * @return 返回同步信息实体, 包含 version lastModifyTimeMills
     */
    private SynchronizeEntity getSynchronizeEntity() {
        long localVersion = localDao.getVersion();
        long localLastModifyTimeMills = localDao.getLastModifyTimeMills();
        Map<String, Object> properties = remoteDao.getProperties();
        Object versionObject = properties.get("_version");
        Object remoteLastModifyTimeMillsObject = properties.get("_lastModifyTimeMills");
        long remoteVersion = versionObject ==null|| StringUtils.equals(versionObject.toString(),"")?-1:Long.parseLong(versionObject.toString());
        long remoteLastModifyTimeMills = remoteLastModifyTimeMillsObject ==null||StringUtils.equals(remoteLastModifyTimeMillsObject.toString(),"")?-1:Long.parseLong(remoteLastModifyTimeMillsObject.toString());


        Object remoteLockObject = properties.get("_lock");
        long remoteLock = remoteLockObject ==null||StringUtils.equals(remoteLockObject.toString(),"")?0:Long.parseLong(remoteLockObject.toString());
        if (remoteLock!=0){
            remoteLockedCount++;
            try {
                Thread.sleep(Config.PERIOD_OF_GET_REMOTE_LOCK);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (remoteLockedCount>Config.MAX_REMOTE_CONNECT_TIME_OUT/Config.PERIOD_OF_GET_REMOTE_LOCK){
                remoteDao.setProperty("_lock",0);
            }
            Config.logger.info(properties);
            return getSynchronizeEntity();
        }


        return new SynchronizeEntity(localVersion,localLastModifyTimeMills,remoteVersion,remoteLastModifyTimeMills);
    }

    private List<Document> getLocalDocumentList() {
        Config.logger.info("localProperties"+localDao.getProperties());
        XStream xStream = XmlUtils.getXStream();
        return (List<Document>)xStream.fromXML(new File(filePath));
    }

    private List<Document> getRemoteDocumentList() {
        try {
            int isSuccess = remoteDao.download(url, downloadTmpPath);
            Map<String, Object> properties = remoteDao.getProperties();
            Config.logger.info("remoteProperties"+properties);
            localDao.setXmlPath(downloadTmpPath);
            localDao.setProperties(properties);
            localDao.setXmlPath(filePath);
            if (isSuccess>0) {
                XStream xStream = XmlUtils.getXStream();
                return (List<Document>) xStream.fromXML(new File(downloadTmpPath));
            }else {
                throw new RuntimeException("下载失败");
            }
        }catch (CannotResolveClassException e){
            e.printStackTrace();
            remoteDao.mkDir();
            remoteDao.upload(new File(filePath));
            return getRemoteDocumentList();
        }
    }

    private List<Document> merge(String[] modifiedIds, List<Document> localList, List<Document> remoteList) {

        Collections.reverse(localList);
        Collections.reverse(remoteList);

        List<String> deletedDocumentIds = new ArrayList<>();
        List<String> modifyDocumentIds = new ArrayList<>();
        List<String> addDocumentIds = new ArrayList<>();

        List<String> remoteDocumentIds = new ArrayList<>();
        List<String> localDocumentIds = new ArrayList<>();
        for (Document remoteDocument: remoteList) {
            remoteDocumentIds.add(remoteDocument.getId());
        }
        for (Document localDocumentId: localList) {
            localDocumentIds.add(localDocumentId.getId());
        }

        for (String modifiedId: modifiedIds) {
            if (localDocumentIds.contains(modifiedId)){
                if (remoteDocumentIds.contains(modifiedId)){
                    modifyDocumentIds.add(modifiedId);
                }else{
                    addDocumentIds.add(modifiedId);
                }
            }else {
                deletedDocumentIds.add(modifiedId);
            }
        }


        //以远程的为主
        List<Document> result = new ArrayList<>();

        for (Document remoteDocument : remoteList) {
            if (deletedDocumentIds.contains(remoteDocument.getId())){//删除的不添加
                continue;
            }
            if (modifyDocumentIds.contains(remoteDocument.getId())){//本地更新的
                Document localDocument = ListUtils.getDocumentById(localList, remoteDocument.getId());
                if (localDocument!=null&&localDocument.getLastModifyTime().compareTo(remoteDocument.getLastModifyTime())<0){
                    if (localDocument.getVersion()<remoteDocument.getVersion()){
                        result.add(remoteDocument);
                    }else{
                        result.add(localDocument);
                    }
                }else {
                    result.add(localDocument);
                }
                continue;
            }
            result.add(remoteDocument);
        }

        for (String addDocumentId : addDocumentIds) {//本地添加的
            result.add(ListUtils.getDocumentById(localList,addDocumentId));
        }

        return result;
    }

    private void saveLocal(List<Document> list, SynchronizeEntity synchronizeEntity){
        XStream xStream = XmlUtils.getXStream();
        String xml = xStream.toXML(list);
        XmlUtils.saveXml(xml,filePath);

        long version = synchronizeEntity.getRemoteVersion();
        long remoteLastModifyTimeMills = synchronizeEntity.getRemoteLastModifyTimeMills();
        long localLastModifyTimeMills = synchronizeEntity.getLocalLastModifyTimeMills();
        localDao.setVersion(version+1L);

        localDao.setLastModifyTimeMills(localLastModifyTimeMills>remoteLastModifyTimeMills?localLastModifyTimeMills:remoteLastModifyTimeMills);
        localDao.setModifiedIds(null);
        localDao.setProperty("_lock",0);
    }

    private void saveRemote(){
        remoteDao.upload(new File(filePath));
        remoteDao.setProperties(localDao.getProperties());
    }

    class SynchronizeEntity{
        private long localVersion;
        private long localLastModifyTimeMills;
        private long remoteVersion;
        private long remoteLastModifyTimeMills;

        private SynchronizeEntity( long localVersion, long localLastModifyTimeMills, long remoteVersion, long remoteLastModifyTimeMills) {
            this.localVersion = localVersion;
            this.localLastModifyTimeMills = localLastModifyTimeMills;
            this.remoteVersion = remoteVersion;
            this.remoteLastModifyTimeMills = remoteLastModifyTimeMills;
        }

        private SynchronizeType getSynchronizeType(){

            if (localLastModifyTimeMills==remoteLastModifyTimeMills){
                return SynchronizeType.NULL;
            }else{
                return SynchronizeType.MERGE;
            }
        }

        public long getLocalVersion() {
            return localVersion;
        }

        public long getLocalLastModifyTimeMills() {
            return localLastModifyTimeMills;
        }


        public long getRemoteVersion() {
            return remoteVersion;
        }

        public long getRemoteLastModifyTimeMills() {
            return remoteLastModifyTimeMills;
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


    public static void main(String[] args){
        MergeRemoteDocumentServiceImpl remoteDocumentService = new MergeRemoteDocumentServiceImpl(Config.DEFAULT_REMOTE_URL,Config.DEFAULT_XML_PATH);
        remoteDocumentService.synchronize(null);
    }
}
