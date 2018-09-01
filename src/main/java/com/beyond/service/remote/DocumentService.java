package com.beyond.service.remote;

import com.beyond.controller.MainController;
import com.beyond.service.remote.impl.DocumentServiceImpl;
import javafx.util.Callback;

public interface DocumentService {
    void synchronize(Callback<DocumentServiceImpl.SynchronizeType, Object> callback);
}
