package com.beyond.service.remote;

import com.beyond.entity.Document;
import com.beyond.service.remote.impl.SimpleRemoteDocumentServiceImpl;
import javafx.util.Callback;

import java.util.List;

public interface RemoteDocumentService {
    enum SynchronizeType{
        UPLOAD,DOWNLOAD,NULL,MERGE
    }
    void synchronize(Callback<Object,Object> callback);
}
