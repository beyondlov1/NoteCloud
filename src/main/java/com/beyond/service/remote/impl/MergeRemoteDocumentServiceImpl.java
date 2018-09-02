package com.beyond.service.remote.impl;

import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.dao.remote.RemoteDao;
import com.beyond.dao.remote.impl.SimpleRemoteDaoImpl;
import com.beyond.entity.Document;
import com.beyond.f.Config;
import com.beyond.service.remote.RemoteDocumentService;
import com.beyond.utils.ListUtils;
import com.beyond.utils.XmlUtils;
import com.thoughtworks.xstream.XStream;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.util.*;

public class MergeRemoteDocumentServiceImpl implements RemoteDocumentService {

    private LocalDao localDao;
    private RemoteDao remoteDao;

    private String url;
    private String filePath;
    private String downloadTmpPath;

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
        long localVersion = localDao.getVersion();
        long localLastModifyTimeMills = localDao.getLastModifyTimeMills();
        long remoteVersion = remoteDao.getVersion();
        long remoteLastModifyTimeMills = remoteDao.getLastModifyTimeMills();

        SynchronizeEntity synchronizeEntity = new MergeRemoteDocumentServiceImpl.SynchronizeEntity(localVersion,localLastModifyTimeMills,remoteVersion,remoteLastModifyTimeMills);
        SynchronizeType synchronizeType = synchronizeEntity.getSynchronizeType();

        if (synchronizeType==SynchronizeType.MERGE){
            List<Document> localList = getLocalDocumentList();
            List<Document> remoteList = getRemoteDocumentList();
            List<Document> mergeList = merge(localDao.getModifiedIds(), localList, remoteList);
            saveLocal(mergeList);
            saveRemote();

            mergeFxDocuments=ListUtils.getFxDocumentListFromDocumentList(mergeList);
        }

        if (callback!=null){
            callback.call(null);
        }

        Config.logger.info(synchronizeType);
    }

    private List<Document> getLocalDocumentList() {
        XStream xStream = XmlUtils.getXStream();
        return (List<Document>)xStream.fromXML(new File(filePath));
    }

    private List<Document> getRemoteDocumentList() {
        int isSuccess = remoteDao.download(url, downloadTmpPath);
        Map<String, Object> properties = localDao.getProperties();
        localDao.setXmlPath(downloadTmpPath);
        localDao.setProperties(properties);
        localDao.setXmlPath(filePath);
        if (isSuccess>0) {
            XStream xStream = XmlUtils.getXStream();
            return (List<Document>) xStream.fromXML(new File(downloadTmpPath));
        }else {
            throw new RuntimeException("下载失败");
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
            if (modifyDocumentIds.contains(remoteDocument.getId())){//更新的
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
            if (addDocumentIds.contains(remoteDocument.getId())){//添加的
                result.add(ListUtils.getDocumentById(localList,remoteDocument.getId()));
                continue;
            }
            result.add(remoteDocument);
        }

        if (localList.size()==0){//首次启动, 直接下载远程
            result = remoteList;
        }

        return result;
    }

    private void saveLocal(List<Document> list){
        XStream xStream = XmlUtils.getXStream();
        String xml = xStream.toXML(list);
        XmlUtils.saveXml(xml,filePath);

        localDao.setXmlPath(downloadTmpPath);
        long version = localDao.getVersion();
        localDao.setXmlPath(filePath);
        localDao.setVersion(version+1L);
        localDao.setLastModifyTimeMills(new Date().getTime());
        localDao.setModifiedIds(null);
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

        private SynchronizeEntity(long localVersion, long localLastModifyTimeMills, long remoteVersion, long remoteLastModifyTimeMills) {
            this.localVersion = localVersion;
            this.localLastModifyTimeMills = localLastModifyTimeMills;
            this.remoteVersion = remoteVersion;
            this.remoteLastModifyTimeMills = remoteLastModifyTimeMills;
        }

        private SynchronizeType getSynchronizeType(){
            if (localLastModifyTimeMills==remoteLastModifyTimeMills&&remoteVersion==localVersion){
                return SynchronizeType.NULL;
            }else{
                return SynchronizeType.MERGE;
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


    public static void main(String[] args){
        MergeRemoteDocumentServiceImpl remoteDocumentService = new MergeRemoteDocumentServiceImpl(Config.DEFAULT_REMOTE_URL,Config.DEFAULT_XML_PATH);
        remoteDocumentService.synchronize(null);
    }
}
