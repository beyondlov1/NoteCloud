package com.beyond.service.remote;

import com.beyond.service.remote.impl.SimpleDocumentServiceImpl;
import javafx.util.Callback;

public interface DocumentService {
    void synchronize(Callback<SimpleDocumentServiceImpl.SynchronizeType, Object> callback);
}
